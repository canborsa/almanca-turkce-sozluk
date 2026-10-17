package com.maden.demirci.game

import com.maden.demirci.game.data.GameManager
import com.maden.demirci.game.data.NumberFormatter
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameLogicTest {

    private lateinit var manager: GameManager

    @Before
    fun setUp() {
        manager = GameManager.getInstance()
        manager.coins = 0.0
        manager.copper = 0L
        manager.silver = 0L
        manager.gold = 0L
        manager.pickaxeLevel = 1
        manager.warehouseLevel = 1
        manager.forgeLevel = 1
        manager.depthMeters = 0
    }

    @Test
    fun testNumberFormatter() {
        assertEquals("500", NumberFormatter.format(500.0))
        assertEquals("1.50K", NumberFormatter.format(1500.0))
        assertEquals("2.50M", NumberFormatter.format(2500000.0))
        assertEquals("3.10B", NumberFormatter.format(3100000000.0))
        assertEquals("4.20T", NumberFormatter.format(4200000000000.0))
    }

    @Test
    fun testWarehouseCapacity() {
        assertEquals(100L, manager.getMaxWarehouseCapacity())
        manager.warehouseLevel = 2
        assertEquals(140L, manager.getMaxWarehouseCapacity())
    }

    @Test
    fun testMiningAndTransfer() {
        val result = manager.mineWithPickaxe()
        assertTrue(result.copperAdded > 0)
        assertTrue(manager.copper > 0)

        val transferred = manager.transferToForge()
        assertTrue(transferred > 0)
        assertEquals(0L, manager.copper)
        assertTrue(manager.coins > 0)
    }

    @Test
    fun testOfflineGains() {
        manager.miners[0].level = 1 // Miner 1 active
        val gains = manager.processOfflineGains(3600) // 1 hour
        assertTrue(gains.durationSeconds == 3600L)
        assertTrue(manager.getCurrentWarehouseStored() > 0 || manager.coins > 0)
    }
}
