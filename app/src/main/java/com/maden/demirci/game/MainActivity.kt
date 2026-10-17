package com.maden.demirci.game

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.maden.demirci.game.data.GameManager
import com.maden.demirci.game.data.NumberFormatter
import com.maden.demirci.game.data.SaveSystem
import com.maden.demirci.game.databinding.ActivityMainBinding
import com.maden.demirci.game.manager.SoundManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val manager = GameManager.getInstance()
    private lateinit var saveSystem: SaveSystem
    private lateinit var soundManager: SoundManager

    private val handler = Handler(Looper.getMainLooper())
    private var gameLoopRunnable: Runnable? = null

    private var currentUpgradeTab = 0 // 0: Pickaxe, 1: Miners, 2: Carriers, 3: Forge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveSystem = SaveSystem(this)
        soundManager = SoundManager(this)

        // Load save data and check offline gains
        val lastSaveTime = saveSystem.loadGame(manager)
        if (lastSaveTime > 0) {
            val offlineSeconds = (System.currentTimeMillis() - lastSaveTime) / 1000
            if (offlineSeconds > 10) {
                val gains = manager.processOfflineGains(offlineSeconds)
                showOfflineGainsDialog(gains.durationSeconds, gains.copperMined, gains.silverMined, gains.goldMined, gains.coinsEarned)
            }
        }

        setupUI()
        setupGameLoop()
    }

    private fun setupUI() {
        // Top HUD & Transfer Button
        binding.btnTransfer.setOnClickListener {
            val transferred = manager.transferToForge()
            if (transferred > 0) {
                soundManager.playCoinSound()
                Toast.makeText(this, "$transferred cevher demirciye aktarıldı ve paraya çevrildi!", Toast.LENGTH_SHORT).show()
                updateHUD()
                binding.forgeView.triggerHammerHit()
            } else {
                Toast.makeText(this, "Depoda aktarılacak cevher yok!", Toast.LENGTH_SHORT).show()
            }
        }

        // View Tabs (Mine ↔ Forge)
        binding.btnTabMine.setOnClickListener {
            binding.mineView.visibility = View.VISIBLE
            binding.forgeView.visibility = View.GONE
            binding.btnTabMine.setTextColor(resources.getColor(R.color.white, null))
            binding.btnTabForge.setTextColor(resources.getColor(R.color.silver_gray, null))
        }

        binding.btnTabForge.setOnClickListener {
            binding.mineView.visibility = View.GONE
            binding.forgeView.visibility = View.VISIBLE
            binding.btnTabMine.setTextColor(resources.getColor(R.color.silver_gray, null))
            binding.btnTabForge.setTextColor(resources.getColor(R.color.white, null))
        }

        // Mine & Forge interactive click listeners
        binding.mineView.onMineClickListener = {
            val res = manager.mineWithPickaxe()
            if (res.warning != null) {
                Toast.makeText(this, res.warning, Toast.LENGTH_SHORT).show()
            } else {
                soundManager.playPickaxeHit()
                binding.mineView.triggerHitAnimation(res.isCrit, manager.getCurrentLayer())
            }
            updateHUD()
        }

        binding.forgeView.onForgeClickListener = {
            val transferred = manager.transferToForge()
            if (transferred > 0) {
                soundManager.playAnvilHit()
                binding.forgeView.triggerHammerHit()
            } else {
                Toast.makeText(this, "Aktarılacak cevher yok!", Toast.LENGTH_SHORT).show()
            }
            updateHUD()
        }

        // Upgrade Category Tabs
        val categories = listOf("⛏️ Kazma", "⛏️👷 Madenciler", "🚚👷 Taşıyıcılar", "🔨 Demirci")
        for (cat in categories) {
            binding.tabUpgradeCategories.addTab(binding.tabUpgradeCategories.newTab().setText(cat))
        }

        binding.tabUpgradeCategories.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentUpgradeTab = tab?.position ?: 0
                renderUpgradePanel()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        updateHUD()
        renderUpgradePanel()
    }

    private fun setupGameLoop() {
        var lastTickTime = System.currentTimeMillis()

        gameLoopRunnable = object : Runnable {
            override fun run() {
                val now = System.currentTimeMillis()
                val deltaSec = (now - lastTickTime) / 1000.0
                lastTickTime = now

                // Auto Mining Ticks
                for (miner in manager.miners) {
                    if (miner.level > 0) {
                        manager.tickWorkerMining(miner, deltaSec)
                    }
                }

                // Carrier Ticks
                for (carrier in manager.carriers) {
                    if (carrier.level > 0) {
                        manager.transferAmountToForge((carrier.getCapacity() * deltaSec).toLong().coerceAtLeast(1L))
                    }
                }

                updateHUD()
                handler.postDelayed(this, 500)
            }
        }
        handler.post(gameLoopRunnable!!)
    }

    private fun updateHUD() {
        binding.tvCoins.text = "🪙 ${NumberFormatter.format(manager.coins)}"
        binding.tvOresCount.text = "🟤 ${NumberFormatter.formatLong(manager.copper)} | ⚪ ${NumberFormatter.formatLong(manager.silver)} | 🟡 ${NumberFormatter.formatLong(manager.gold)}"

        val stored = manager.getCurrentWarehouseStored()
        val maxCap = manager.getMaxWarehouseCapacity()
        val percent = ((stored.toDouble() / maxCap) * 100).toInt().coerceIn(0, 100)

        binding.pbWarehouse.progress = percent
        if (manager.isWarehouseFull()) {
            binding.tvWarehouseCapacity.text = "Depo: $stored / $maxCap kg (DEPO DOLU!)"
            binding.tvWarehouseCapacity.setTextColor(resources.getColor(R.color.forge_red, null))
        } else {
            binding.tvWarehouseCapacity.text = "Depo: $stored / $maxCap kg (%$percent)"
            binding.tvWarehouseCapacity.setTextColor(resources.getColor(R.color.silver_gray, null))
        }

        binding.mineView.invalidate()
        binding.forgeView.invalidate()
    }

    private fun renderUpgradePanel() {
        binding.upgradeContentLayout.removeAllViews()

        when (currentUpgradeTab) {
            0 -> renderPickaxeAndWarehouseUpgrades()
            1 -> renderMinersUpgrades()
            2 -> renderCarriersUpgrades()
            3 -> renderForgeUpgrades()
        }
    }

    private fun renderPickaxeAndWarehouseUpgrades() {
        // Pickaxe Upgrade Card
        val pickaxeCost = manager.getPickaxeUpgradeCost()
        val pickaxeView = createUpgradeCardView(
            title = "Kazma Gücü (Lv.${manager.pickaxeLevel})",
            subtitle = "Kazma Vuruş Gücü: ${manager.pickaxeLevel}\nDerinlik: ${manager.depthMeters}m",
            costText = "🪙 ${NumberFormatter.format(pickaxeCost)}",
            canAfford = manager.coins >= pickaxeCost,
            onAction = {
                if (manager.coins >= pickaxeCost) {
                    manager.coins -= pickaxeCost
                    manager.pickaxeLevel++
                    updateHUD()
                    renderUpgradePanel()
                }
            }
        )
        binding.upgradeContentLayout.addView(pickaxeView)

        // Warehouse Upgrade Card
        val warehouseCost = manager.getWarehouseUpgradeCost()
        val warehouseView = createUpgradeCardView(
            title = "Depo Kapasitesi (Lv.${manager.warehouseLevel})",
            subtitle = "Kapasite: ${manager.getMaxWarehouseCapacity()} kg",
            costText = "🪙 ${NumberFormatter.format(warehouseCost)}",
            canAfford = manager.coins >= warehouseCost,
            onAction = {
                if (manager.coins >= warehouseCost) {
                    manager.coins -= warehouseCost
                    manager.warehouseLevel++
                    updateHUD()
                    renderUpgradePanel()
                }
            }
        )
        binding.upgradeContentLayout.addView(warehouseView)
    }

    private fun renderMinersUpgrades() {
        for (miner in manager.miners) {
            val cost = miner.getUpgradeCost()
            val isUnlocked = miner.level > 0 || manager.isWorkerUnlockable(miner.id)

            val statusText = if (miner.level == 0) {
                if (isUnlocked) "Kilidi Açılabilir!" else "Gereksinim: ${miner.unlockRequirementText}"
            } else {
                "Lv.${miner.level}/5 - Hız: %${(miner.getSpeedMultiplier() * 100).toInt()}"
            }

            val actionText = if (miner.level == 0) "Aç\n🪙 ${NumberFormatter.format(cost)}"
            else if (miner.isMaxLevel()) "MAX"
            else "Yükselt\n🪙 ${NumberFormatter.format(cost)}"

            val card = createUpgradeCardView(
                title = miner.name,
                subtitle = "${miner.title}\n$statusText",
                costText = actionText,
                canAfford = isUnlocked && !miner.isMaxLevel() && manager.coins >= cost,
                onAction = {
                    if (isUnlocked && !miner.isMaxLevel() && manager.coins >= cost) {
                        manager.coins -= cost
                        miner.level++
                        updateHUD()
                        renderUpgradePanel()
                    }
                }
            )
            binding.upgradeContentLayout.addView(card)
        }
    }

    private fun renderCarriersUpgrades() {
        for (carrier in manager.carriers) {
            val cost = carrier.getUpgradeCost()
            val isUnlocked = carrier.level > 0 || manager.isCarrierUnlockable(carrier.id)

            val statusText = if (carrier.level == 0) {
                if (isUnlocked) "Kilidi Açılabilir!" else "Önceki taşıyıcı Lv.5 olmalı"
            } else {
                "Lv.${carrier.level}/5 - Kapasite: ${carrier.getCapacity()} / ${carrier.getIntervalSeconds()}sn"
            }

            val actionText = if (carrier.level == 0) "Aç\n🪙 ${NumberFormatter.format(cost)}"
            else if (carrier.isMaxLevel()) "MAX"
            else "Yükselt\n🪙 ${NumberFormatter.format(cost)}"

            val card = createUpgradeCardView(
                title = carrier.name,
                subtitle = statusText,
                costText = actionText,
                canAfford = isUnlocked && !carrier.isMaxLevel() && manager.coins >= cost,
                onAction = {
                    if (isUnlocked && !carrier.isMaxLevel() && manager.coins >= cost) {
                        manager.coins -= cost
                        carrier.level++
                        updateHUD()
                        renderUpgradePanel()
                    }
                }
            )
            binding.upgradeContentLayout.addView(card)
        }
    }

    private fun renderForgeUpgrades() {
        val forgeCost = manager.getForgeUpgradeCost()
        val mult = 1.0 + (manager.forgeLevel - 1) * 0.2

        val forgeView = createUpgradeCardView(
            title = "Demirci & Örs (Lv.${manager.forgeLevel})",
            subtitle = String.format("Dönüşüm Çarpanı: x%.1f (+%%20/seviye)", mult),
            costText = "🪙 ${NumberFormatter.format(forgeCost)}",
            canAfford = manager.coins >= forgeCost,
            onAction = {
                if (manager.coins >= forgeCost) {
                    manager.coins -= forgeCost
                    manager.forgeLevel++
                    updateHUD()
                    renderUpgradePanel()
                }
            }
        )
        binding.upgradeContentLayout.addView(forgeView)
    }

    private fun createUpgradeCardView(
        title: String,
        subtitle: String,
        costText: String,
        canAfford: Boolean,
        onAction: () -> Unit
    ): View {
        val card = layoutInflater.inflate(R.layout.item_upgrade_card, binding.upgradeContentLayout, false)
        val tvTitle = card.findViewById<TextView>(R.id.tvUpgradeTitle)
        val tvSubtitle = card.findViewById<TextView>(R.id.tvUpgradeSubtitle)
        val btnAction = card.findViewById<Button>(R.id.btnUpgradeAction)

        tvTitle.text = title
        tvSubtitle.text = subtitle
        btnAction.text = costText

        btnAction.isEnabled = canAfford
        btnAction.alpha = if (canAfford) 1.0f else 0.5f

        btnAction.setOnClickListener { onAction() }
        return card
    }

    private fun showOfflineGainsDialog(seconds: Long, copper: Long, silver: Long, gold: Long, coins: Double) {
        val hours = seconds / 3600
        val mins = (seconds % 3600) / 60

        val msg = "Sen yokken madenlerin çalıştı! ($hours saat $mins dakika)\n\n" +
                "Kazanılan Cevherler:\n" +
                "🟤 Bakır: $copper\n" +
                "⚪ Gümüş: $silver\n" +
                "🟡 Altın: $gold\n\n" +
                "🪙 Üretilen Toplam Altın: ${NumberFormatter.format(coins)}"

        AlertDialog.Builder(this)
            .setTitle("Çevrimdışı İlerleme")
            .setMessage(msg)
            .setPositiveButton("Harika!") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    override fun onPause() {
        super.onPause()
        saveSystem.saveGame(manager)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
        gameLoopRunnable?.let { handler.removeCallbacks(it) }
    }
}
