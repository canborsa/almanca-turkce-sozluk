package com.example.healthtracker

import com.example.healthtracker.service.AICoachEngine
import org.junit.Assert.assertTrue
import org.junit.Test

class AICoachEngineTest {

    @Test
    fun testGenerateDailyTipTurkish() {
        val tip = AICoachEngine.generateDailyTip(null, emptyList(), emptyList(), 5, "tr")
        assertTrue("Should contain streak celebration", tip.contains("5 gündür") || tip.contains("Tebrikler"))
    }

    @Test
    fun testGenerateDailyTipEnglish() {
        val tip = AICoachEngine.generateDailyTip(null, emptyList(), emptyList(), 7, "en")
        assertTrue("Should contain congratulatory message", tip.contains("7 days") || tip.contains("Congratulations"))
    }

    @Test
    fun testAnswerUserQuestionTurkish() {
        val answerWater = AICoachEngine.answerUserQuestion("Günde ne kadar su içmeliyim?", "tr")
        assertTrue(answerWater.contains("Su") && answerWater.contains("litre"))

        val answerSugar = AICoachEngine.answerUserQuestion("Şeker krizini nasıl aşarım?", "tr")
        assertTrue(answerSugar.contains("şeker") || answerSugar.contains("meyve"))
    }

    @Test
    fun testAnswerUserQuestionEnglish() {
        val answerFasting = AICoachEngine.answerUserQuestion("How to do intermittent fasting?", "en")
        assertTrue(answerFasting.contains("fasting") || answerFasting.contains("16:8"))
    }
}
