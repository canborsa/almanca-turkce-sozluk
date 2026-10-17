package com.maden.demirci.game.data

import android.content.Context
import kotlin.math.pow

class GameManager private constructor() {

    var coins: Double = 0.0
    var copper: Long = 0L
    var silver: Long = 0L
    var gold: Long = 0L

    var processedCopper: Long = 0L
    var processedSilver: Long = 0L

    var pickaxeLevel: Int = 1
    var warehouseLevel: Int = 1
    var forgeLevel: Int = 1
    var depthMeters: Int = 0

    val miners = listOf(
        WorkerState(1, "Çırak Kazmacı", "İşçi 1 (Bakır Kazısı)", 0, "100 Coin", 100.0),
        WorkerState(2, "Kaya Kırıcı", "İşçi 2 (Ağır Kazma)", 0, "İşçi 1 Lv.5 Olmalı", 500.0),
        WorkerState(3, "Derin Kazı Ustası", "İşçi 3 (Gümüş Kazıcı)", 0, "İşçi 2 Lv.5 Olmalı", 2500.0),
        WorkerState(4, "Dinamitçi Cüce", "İşçi 4 (Yüksek Verim)", 0, "İşçi 3 Lv.5 Olmalı", 10000.0),
        WorkerState(5, "Maden Şefi / Golem", "İşçi 5 (Altın Kazıcı)", 0, "İşçi 4 Lv.5 Olmalı", 50000.0)
    )

    val carriers = listOf(
        CarrierState(1, "Sırt Küfeli Cüce", 0, 200.0),
        CarrierState(2, "Buharlı Maden Vagonu", 0, 5000.0)
    )

    companion object {
        @Volatile
        private var instance: GameManager? = null

        fun getInstance(): GameManager {
            return instance ?: synchronized(this) {
                instance ?: GameManager().also { instance = it }
            }
        }
    }

    // Warehouse Capacity Formula: 100 * (1.4 ^ Warehouse_Level)
    fun getMaxWarehouseCapacity(): Long {
        return (100.0 * 1.4.pow(warehouseLevel.toDouble())).toLong()
    }

    fun getCurrentWarehouseStored(): Long {
        return copper + silver + gold
    }

    fun isWarehouseFull(): Boolean {
        return getCurrentWarehouseStored() >= getMaxWarehouseCapacity()
    }

    fun getPickaxeUpgradeCost(): Double {
        return 50.0 * 1.18.pow(pickaxeLevel.toDouble())
    }

    fun getForgeUpgradeCost(): Double {
        return 150.0 * 1.18.pow(forgeLevel.toDouble())
    }

    fun getWarehouseUpgradeCost(): Double {
        return 100.0 * 1.18.pow(warehouseLevel.toDouble())
    }

    // Layer determination
    // Katman 1: 0m - 100m (Copper)
    // Katman 2: 100m - 300m (Silver, req Pickaxe Lv.10 + 500 Copper)
    // Katman 3: 300m+ (Gold, req Pickaxe Lv.25 + 250 Silver)
    fun getCurrentLayer(): Int {
        return when {
            depthMeters >= 300 -> 3
            depthMeters >= 100 -> 2
            else -> 1
        }
    }

    fun canUnlockLayer2(): Boolean {
        return pickaxeLevel >= 10 && processedCopper >= 500
    }

    fun canUnlockLayer3(): Boolean {
        return pickaxeLevel >= 25 && processedSilver >= 250
    }

    // Manual or Auto Mining logic
    fun mineWithPickaxe(): MineResult {
        if (isWarehouseFull()) {
            return MineResult(0, 0, 0, false, "Depo Dolu!")
        }

        var power = pickaxeLevel
        val isCrit = Math.random() < 0.15
        val multiplier = if (isCrit) 2.5 else 1.0
        var amount = (power * multiplier).toInt().coerceAtLeast(1)

        val layer = getCurrentLayer()
        val space = (getMaxWarehouseCapacity() - getCurrentWarehouseStored()).toInt()
        if (space <= 0) return MineResult(0, 0, 0, false, "Depo Dolu!")

        amount = amount.coerceAtMost(space)

        var addCopper = 0L
        var addSilver = 0L
        var addGold = 0L

        when (layer) {
            1 -> {
                addCopper = amount.toLong()
            }
            2 -> {
                // Katman 2: Gümüş + şans eseri Bakır
                val copperPart = (amount * 0.3).toLong()
                val silverPart = (amount - copperPart).toLong()
                addCopper = copperPart
                addSilver = silverPart
            }
            3 -> {
                // Katman 3: Saf Altın
                val silverPart = (amount * 0.2).toLong()
                val goldPart = (amount - silverPart).toLong()
                addSilver = silverPart
                addGold = goldPart
            }
        }

        copper += addCopper
        silver += addSilver
        gold += addGold

        // Increase depth slightly with each pickaxe hit
        depthMeters += 1

        return MineResult(addCopper.toInt(), addSilver.toInt(), addGold.toInt(), isCrit, null)
    }

    // Manual transfer from warehouse to forge
    fun transferToForge(): Long {
        val totalStored = getCurrentWarehouseStored()
        if (totalStored <= 0) return 0L

        // Conversion values: 1 Copper = 1 Coin, 1 Silver = 10 Coins, 1 Gold = 100 Coins
        val forgeMultiplier = 1.0 + (forgeLevel - 1) * 0.2

        val earnedFromCopper = copper * 1.0 * forgeMultiplier
        val earnedFromSilver = silver * 10.0 * forgeMultiplier
        val earnedFromGold = gold * 100.0 * forgeMultiplier

        val totalEarned = earnedFromCopper + earnedFromSilver + earnedFromGold

        processedCopper += copper
        processedSilver += silver

        val transferredCount = totalStored
        copper = 0L
        silver = 0L
        gold = 0L

        coins += totalEarned
        return transferredCount
    }

    // Transfer specific amount (e.g. by carrier workers)
    fun transferAmountToForge(amount: Long): Long {
        var remainingToTransfer = amount
        var transferredCopper = 0L
        var transferredSilver = 0L
        var transferredGold = 0L

        // Take from Copper first, then Silver, then Gold
        if (copper > 0) {
            val take = Math.min(copper, remainingToTransfer)
            copper -= take
            transferredCopper += take
            remainingToTransfer -= take
        }

        if (remainingToTransfer > 0 && silver > 0) {
            val take = Math.min(silver, remainingToTransfer)
            silver -= take
            transferredSilver += take
            remainingToTransfer -= take
        }

        if (remainingToTransfer > 0 && gold > 0) {
            val take = Math.min(gold, remainingToTransfer)
            gold -= take
            transferredGold += take
            remainingToTransfer -= take
        }

        val totalTransferred = transferredCopper + transferredSilver + transferredGold
        if (totalTransferred > 0) {
            val forgeMultiplier = 1.0 + (forgeLevel - 1) * 0.2
            val earned = (transferredCopper * 1.0 + transferredSilver * 10.0 + transferredGold * 100.0) * forgeMultiplier

            processedCopper += transferredCopper
            processedSilver += transferredSilver

            coins += earned
        }

        return totalTransferred
    }

    // Auto-mining tick for worker i
    fun tickWorkerMining(worker: WorkerState, deltaSeconds: Double): Long {
        if (worker.level == 0 || isWarehouseFull()) return 0L

        val speed = worker.getSpeedMultiplier()
        var baseMined = (speed * deltaSeconds).toLong().coerceAtLeast(1L)

        // Rare gem / double / crit bonuses
        if (Math.random() < worker.getDoubleChance()) {
            baseMined *= 2
        }
        if (Math.random() < worker.getCritChance()) {
            baseMined = (baseMined * 2.5).toLong()
        }

        val availableCapacity = (getMaxWarehouseCapacity() - getCurrentWarehouseStored()).coerceAtLeast(0L)
        val actualMined = baseMined.coerceAtMost(availableCapacity)

        if (actualMined <= 0) return 0L

        val layer = getCurrentLayer()
        when (worker.id) {
            1 -> copper += actualMined
            2 -> {
                if (layer >= 2) silver += actualMined else copper += actualMined
            }
            3 -> {
                if (layer >= 2) silver += actualMined else copper += actualMined
            }
            4 -> {
                if (layer >= 3) gold += actualMined else if (layer == 2) silver += actualMined else copper += actualMined
            }
            5 -> {
                if (layer >= 3) gold += actualMined else if (layer == 2) silver += actualMined else copper += actualMined
            }
        }

        return actualMined
    }

    // Check unlocking conditions for workers
    fun isWorkerUnlockable(workerId: Int): Boolean {
        val worker = miners.find { it.id == workerId } ?: return false
        if (worker.level > 0) return false

        return when (workerId) {
            1 -> true
            2 -> (miners.find { it.id == 1 }?.level ?: 0) >= 5
            3 -> (miners.find { it.id == 2 }?.level ?: 0) >= 5
            4 -> (miners.find { it.id == 3 }?.level ?: 0) >= 5
            5 -> (miners.find { it.id == 4 }?.level ?: 0) >= 5
            else -> false
        }
    }

    fun isCarrierUnlockable(carrierId: Int): Boolean {
        val carrier = carriers.find { it.id == carrierId } ?: return false
        if (carrier.level > 0) return false

        return when (carrierId) {
            1 -> true
            2 -> (carriers.find { it.id == 1 }?.level ?: 0) >= 5
            else -> false
        }
    }

    // Process Offline Gains (up to 8 hours = 28800 seconds)
    fun processOfflineGains(offlineSeconds: Long): OfflineGainsResult {
        val cappedSeconds = offlineSeconds.coerceAtMost(8 * 3600L)
        if (cappedSeconds <= 10) { // Less than 10s ignored
            return OfflineGainsResult(0, 0, 0, 0.0, 0)
        }

        var totalMinedCopper = 0L
        var totalMinedSilver = 0L
        var totalMinedGold = 0L
        var initialCoins = coins

        // Simulate mining and carrying over time in steps of 1 second
        val steps = cappedSeconds
        for (s in 0 until steps) {
            // Miners mine
            for (miner in miners) {
                if (miner.level > 0 && !isWarehouseFull()) {
                    val rate = miner.getSpeedMultiplier().toLong().coerceAtLeast(1L)
                    val space = (getMaxWarehouseCapacity() - getCurrentWarehouseStored()).coerceAtLeast(0L)
                    val toAdd = rate.coerceAtMost(space)

                    if (toAdd > 0) {
                        val layer = getCurrentLayer()
                        when (miner.id) {
                            1 -> { copper += toAdd; totalMinedCopper += toAdd }
                            2 -> {
                                if (layer >= 2) { silver += toAdd; totalMinedSilver += toAdd }
                                else { copper += toAdd; totalMinedCopper += toAdd }
                            }
                            3 -> {
                                if (layer >= 2) { silver += toAdd; totalMinedSilver += toAdd }
                                else { copper += toAdd; totalMinedCopper += toAdd }
                            }
                            4, 5 -> {
                                if (layer >= 3) { gold += toAdd; totalMinedGold += toAdd }
                                else if (layer == 2) { silver += toAdd; totalMinedSilver += toAdd }
                                else { copper += toAdd; totalMinedCopper += toAdd }
                            }
                        }
                    }
                }
            }

            // Carriers transport
            for (carrier in carriers) {
                if (carrier.level > 0 && s % carrier.getIntervalSeconds().toLong().coerceAtLeast(1L) == 0L) {
                    transferAmountToForge(carrier.getCapacity())
                }
            }
        }

        val coinsEarned = coins - initialCoins
        return OfflineGainsResult(totalMinedCopper, totalMinedSilver, totalMinedGold, coinsEarned, cappedSeconds)
    }
}

data class MineResult(
    val copperAdded: Int,
    val silverAdded: Int,
    val goldAdded: Int,
    val isCrit: Boolean,
    val warning: String?
)

data class OfflineGainsResult(
    val copperMined: Long,
    val silverMined: Long,
    val goldMined: Long,
    val coinsEarned: Double,
    val durationSeconds: Long
)
