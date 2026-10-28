package com.playcem.klondikefarm

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.playcem.klondikefarm.data.GameDataManager
import com.playcem.klondikefarm.utils.LocaleHelper

class MainActivity : AppCompatActivity() {

    private lateinit var gameData: GameDataManager
    private val handler = Handler(Looper.getMainLooper())

    // UI Elements
    private lateinit var tvNickname: TextView
    private lateinit var tvLevel: TextView
    private lateinit var tvCoins: TextView
    private lateinit var tvEnergy: TextView
    private lateinit var tvWheatCount: TextView
    private lateinit var tvMilkCount: TextView
    private lateinit var btnSettings: ImageButton

    // Plots UI
    private val plotImageViews = ArrayList<ImageView>()
    private val plotStatusTexts = ArrayList<TextView>()
    private val plotButtons = ArrayList<Button>()

    // Cow UI
    private lateinit var tvCowStatus: TextView
    private lateinit var btnCowAction: Button

    // Truck UI
    private lateinit var tvTruckRequirements: TextView
    private lateinit var btnTruckDeliver: Button

    // Main tick runnable for timer updates and state checks
    private val timerRunnable = object : Runnable {
        override fun run() {
            updatePlotsUI()
            updateCowUI()
            handler.postDelayed(this, 1000)
        }
    }

    // Energy regeneration runnable (1 energy every 10 seconds)
    private val energyRegenRunnable = object : Runnable {
        override fun run() {
            if (gameData.energy < 30) {
                gameData.energy += 1
                updateHeaderUI()
            }
            handler.postDelayed(this, 10000)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("klondike_farm_prefs", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "tr") ?: "tr"
        super.attachBaseContext(LocaleHelper.setLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameData = GameDataManager(this)

        initUIReferences()
        setupListeners()
        refreshAllUI()

        handler.post(timerRunnable)
        handler.postDelayed(energyRegenRunnable, 10000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
        handler.removeCallbacks(energyRegenRunnable)
    }

    private fun initUIReferences() {
        tvNickname = findViewById(R.id.tv_nickname)
        tvLevel = findViewById(R.id.tv_level)
        tvCoins = findViewById(R.id.tv_coins)
        tvEnergy = findViewById(R.id.tv_energy)
        tvWheatCount = findViewById(R.id.tv_wheat_count)
        tvMilkCount = findViewById(R.id.tv_milk_count)
        btnSettings = findViewById(R.id.btn_settings)

        plotImageViews.add(findViewById(R.id.iv_plot_0))
        plotImageViews.add(findViewById(R.id.iv_plot_1))
        plotImageViews.add(findViewById(R.id.iv_plot_2))
        plotImageViews.add(findViewById(R.id.iv_plot_3))

        plotStatusTexts.add(findViewById(R.id.tv_plot_0_status))
        plotStatusTexts.add(findViewById(R.id.tv_plot_1_status))
        plotStatusTexts.add(findViewById(R.id.tv_plot_2_status))
        plotStatusTexts.add(findViewById(R.id.tv_plot_3_status))

        plotButtons.add(findViewById(R.id.btn_plot_0))
        plotButtons.add(findViewById(R.id.btn_plot_1))
        plotButtons.add(findViewById(R.id.btn_plot_2))
        plotButtons.add(findViewById(R.id.btn_plot_3))

        tvCowStatus = findViewById(R.id.tv_cow_status)
        btnCowAction = findViewById(R.id.btn_cow_action)

        tvTruckRequirements = findViewById(R.id.tv_truck_requirements)
        btnTruckDeliver = findViewById(R.id.btn_truck_deliver)
    }

    private fun setupListeners() {
        btnSettings.setOnClickListener {
            showSettingsDialog()
        }

        for (i in 0 until 4) {
            plotButtons[i].setOnClickListener {
                handlePlotClick(i)
            }
        }

        btnCowAction.setOnClickListener {
            handleCowClick()
        }

        btnTruckDeliver.setOnClickListener {
            handleTruckClick()
        }
    }

    private fun refreshAllUI() {
        updateHeaderUI()
        updatePlotsUI()
        updateCowUI()
        updateTruckUI()
    }

    private fun updateHeaderUI() {
        tvNickname.text = gameData.nickname
        tvLevel.text = "${getString(R.string.level)} ${gameData.level}"
        tvCoins.text = gameData.coins.toString()
        tvEnergy.text = gameData.energy.toString()
        tvWheatCount.text = getString(R.string.wheat_count, gameData.wheatCount)
        tvMilkCount.text = getString(R.string.milk_count, gameData.milkCount)
    }

    private fun handlePlotClick(index: Int) {
        val state = gameData.getPlotState(index)
        when (state) {
            0 -> {
                // Empty -> Plant Wheat
                if (gameData.energy >= 1) {
                    gameData.energy -= 1
                    val finishTime = System.currentTimeMillis() + 3000L // 3 seconds grow time
                    gameData.setPlotState(index, 1, finishTime)
                    updatePlotsUI()
                    updateHeaderUI()
                } else {
                    Toast.makeText(this, "Not enough energy!", Toast.LENGTH_SHORT).show()
                }
            }
            2 -> {
                // Ready -> Harvest Wheat
                gameData.setPlotState(index, 0, 0L)
                gameData.wheatCount += 2
                val leveledUp = gameData.addXp(10)
                if (leveledUp) {
                    Toast.makeText(this, "Level Up! Lvl ${gameData.level}", Toast.LENGTH_SHORT).show()
                }
                updatePlotsUI()
                updateHeaderUI()
            }
        }
    }

    private fun updatePlotsUI() {
        val currentTime = System.currentTimeMillis()
        for (i in 0 until 4) {
            val state = gameData.getPlotState(i)
            when (state) {
                0 -> {
                    plotImageViews[i].setImageResource(R.drawable.tarla)
                    plotStatusTexts[i].text = getString(R.string.plot_empty)
                    plotButtons[i].text = getString(R.string.plant_wheat)
                    plotButtons[i].isEnabled = true
                }
                1 -> {
                    val finishTime = gameData.getPlotFinishTime(i)
                    val remainingSeconds = Math.max(0L, (finishTime - currentTime) / 1000L)
                    plotImageViews[i].setImageResource(R.drawable.saman)
                    plotStatusTexts[i].text = "${getString(R.string.plot_growing)} (${remainingSeconds}s)"
                    plotButtons[i].text = getString(R.string.plot_growing)
                    plotButtons[i].isEnabled = false
                }
                2 -> {
                    plotImageViews[i].setImageResource(R.drawable.bugday)
                    plotStatusTexts[i].text = getString(R.string.plot_ready)
                    plotButtons[i].text = getString(R.string.harvest)
                    plotButtons[i].isEnabled = true
                }
            }
        }
    }

    private fun handleCowClick() {
        val state = gameData.cowState
        when (state) {
            0 -> {
                // Hungry -> Feed Wheat
                if (gameData.wheatCount >= 2) {
                    gameData.wheatCount -= 2
                    val finishTime = System.currentTimeMillis() + 4000L // 4 seconds
                    gameData.setCowStateWithTimer(1, finishTime)
                    updateCowUI()
                    updateHeaderUI()
                } else {
                    Toast.makeText(this, "Need 2 Wheat to feed cow!", Toast.LENGTH_SHORT).show()
                }
            }
            2 -> {
                // Ready -> Collect Milk
                gameData.setCowStateWithTimer(0, 0L)
                gameData.milkCount += 1
                val leveledUp = gameData.addXp(15)
                if (leveledUp) {
                    Toast.makeText(this, "Level Up! Lvl ${gameData.level}", Toast.LENGTH_SHORT).show()
                }
                updateCowUI()
                updateHeaderUI()
            }
        }
    }

    private fun updateCowUI() {
        val currentTime = System.currentTimeMillis()
        when (gameData.cowState) {
            0 -> {
                tvCowStatus.text = getString(R.string.cow_hungry)
                btnCowAction.text = getString(R.string.feed_cow)
                btnCowAction.isEnabled = true
            }
            1 -> {
                val finishTime = gameData.getCowFinishTime()
                val remainingSeconds = Math.max(0L, (finishTime - currentTime) / 1000L)
                tvCowStatus.text = "${getString(R.string.cow_eating)} (${remainingSeconds}s)"
                btnCowAction.text = getString(R.string.cow_eating)
                btnCowAction.isEnabled = false
            }
            2 -> {
                tvCowStatus.text = getString(R.string.cow_ready)
                btnCowAction.text = getString(R.string.milk_cow)
                btnCowAction.isEnabled = true
            }
        }
    }

    private fun handleTruckClick() {
        val reqWheat = 2
        val reqMilk = 1
        if (gameData.wheatCount >= reqWheat && gameData.milkCount >= reqMilk) {
            gameData.wheatCount -= reqWheat
            gameData.milkCount -= reqMilk
            gameData.coins += 50
            val leveledUp = gameData.addXp(20)

            Toast.makeText(this, "Order Delivered! +50 Coins, +20 XP", Toast.LENGTH_SHORT).show()
            if (leveledUp) {
                Toast.makeText(this, "Level Up! Lvl ${gameData.level}", Toast.LENGTH_SHORT).show()
            }

            updateHeaderUI()
        } else {
            Toast.makeText(this, "Not enough items for delivery!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTruckUI() {
        tvTruckRequirements.text = getString(R.string.order_requires, 2, 1)
        btnTruckDeliver.text = getString(R.string.deliver_order)
    }

    private fun showSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val etNickname = dialogView.findViewById<EditText>(R.id.et_nickname)
        val spinnerLanguage = dialogView.findViewById<Spinner>(R.id.spinner_language)
        val btnShowManual = dialogView.findViewById<Button>(R.id.btn_show_manual)
        val btnShowLegal = dialogView.findViewById<Button>(R.id.btn_show_legal)
        val tvSettingsContent = dialogView.findViewById<TextView>(R.id.tv_settings_content)

        etNickname.setText(gameData.nickname)

        val languages = arrayOf("Türkçe", "English", "Deutsch", "Español", "Português", "العربية")
        val langCodes = arrayOf("tr", "en", "de", "es", "pt", "ar")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = adapter

        val currentLangIndex = langCodes.indexOf(gameData.language).let { if (it >= 0) it else 0 }
        spinnerLanguage.setSelection(currentLangIndex, false)

        btnShowManual.setOnClickListener {
            tvSettingsContent.text = getString(R.string.manual_text)
        }

        btnShowLegal.setOnClickListener {
            tvSettingsContent.text = getString(R.string.legal_text)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.settings)
            .setView(dialogView)
            .setPositiveButton(R.string.save_nickname) { _, _ ->
                val newName = etNickname.text.toString().trim()
                if (newName.isNotEmpty()) {
                    gameData.nickname = newName
                }

                val selectedIndex = spinnerLanguage.selectedItemPosition
                val selectedLangCode = langCodes[selectedIndex]
                if (selectedLangCode != gameData.language) {
                    gameData.language = selectedLangCode
                    recreate()
                } else {
                    updateHeaderUI()
                }
            }
            .setNegativeButton(R.string.close, null)
            .create()

        dialog.show()
    }
}
