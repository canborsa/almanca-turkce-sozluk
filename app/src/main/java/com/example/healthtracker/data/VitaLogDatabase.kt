package com.example.healthtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.healthtracker.data.dao.VitaLogDao
import com.example.healthtracker.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        FoodItem::class,
        MealEntry::class,
        WaterEntry::class,
        WeightRecord::class,
        ActivityRecord::class,
        FastingSession::class,
        HabitChallenge::class,
        HabitLog::class,
        Badge::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VitaLogDatabase : RoomDatabase() {
    abstract fun vitaLogDao(): VitaLogDao

    companion object {
        @Volatile
        private var INSTANCE: VitaLogDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): VitaLogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VitaLogDatabase::class.java,
                    "vitalog_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(VitaLogDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class VitaLogDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.vitaLogDao())
                }
            }
        }

        suspend fun populateInitialData(dao: VitaLogDao) {
            // Default user profile
            dao.insertOrUpdateProfile(UserProfile())

            // Default Badges
            val initialBadges = listOf(
                Badge(code = "first_meal", title = "First Fuel", description = "Logged your first meal", category = "nutrition", iconName = "ic_badge_meal", tier = "bronze"),
                Badge(code = "sugar_free_3", title = "Sugar Detour", description = "3 consecutive sugar-free days", category = "habit", iconName = "ic_badge_sugar", tier = "bronze"),
                Badge(code = "sugar_free_7", title = "Sugar Liberator", description = "7 consecutive sugar-free days", category = "habit", iconName = "ic_badge_sugar", tier = "silver"),
                Badge(code = "sugar_free_30", title = "Pure Energy Master", description = "Completed 30 days without added sugar", category = "habit", iconName = "ic_badge_sugar", tier = "gold"),
                Badge(code = "water_champion", title = "Hydration Star", description = "Reached daily water goal 7 times", category = "hydration", iconName = "ic_badge_water", tier = "silver"),
                Badge(code = "flour_free_7", title = "Whole Grain Pioneer", description = "7 days reducing refined flours", category = "habit", iconName = "ic_badge_flour", tier = "silver"),
                Badge(code = "walker_pro", title = "Step Master", description = "Reached 10,000 steps or logged 5 walks", category = "fitness", iconName = "ic_badge_walk", tier = "bronze")
            )
            dao.insertBadges(initialBadges)

            // Default Habit Challenges
            dao.insertChallenge(
                HabitChallenge(
                    type = "sugar_free",
                    title = "Sugar-Free Habit",
                    description = "Eliminate added sugars and sugary drinks for sustainable energy.",
                    startDate = "2026-01-01",
                    targetDays = 30,
                    currentStreak = 0,
                    bestStreak = 0
                )
            )
            dao.insertChallenge(
                HabitChallenge(
                    type = "flour_free",
                    title = "Refined Flour Reduction",
                    description = "Swap white bread and pastries for complex carbohydrates and whole foods.",
                    startDate = "2026-01-01",
                    targetDays = 21,
                    currentStreak = 0,
                    bestStreak = 0
                )
            )

            // Seed comprehensive international & Turkish food database
            val foodList = listOf(
                // Turkish Traditional & Common Foods
                FoodItem(name = "Mercimek Çorbası (Lentil Soup)", servingDescription = "1 kase / bowl (250ml)", servingSizeGrams = 250f, calories = 180f, protein = 10.5f, carbs = 26f, fat = 4.2f, sugar = 2.1f, fiber = 6.5f, sodiumMg = 450f, category = "Turkish"),
                FoodItem(name = "Menemen", servingDescription = "1 porsiyon / portion (200g)", servingSizeGrams = 200f, calories = 210f, protein = 12f, carbs = 7.5f, fat = 14.5f, sugar = 4.2f, fiber = 2.8f, sodiumMg = 380f, category = "Turkish"),
                FoodItem(name = "Simit", servingDescription = "1 adet / piece (100g)", servingSizeGrams = 100f, calories = 280f, protein = 9f, carbs = 54f, fat = 3.5f, sugar = 3.2f, fiber = 3.1f, sodiumMg = 420f, category = "Turkish"),
                FoodItem(name = "Kuru Fasulye (White Bean Stew)", servingDescription = "1 porsiyon / portion (200g)", servingSizeGrams = 200f, calories = 245f, protein = 13.5f, carbs = 34f, fat = 6.2f, sugar = 3f, fiber = 9.8f, sodiumMg = 510f, category = "Turkish"),
                FoodItem(name = "Pirinç Pilavı (Rice Pilaf)", servingDescription = "1 porsiyon / portion (150g)", servingSizeGrams = 150f, calories = 260f, protein = 4.5f, carbs = 42f, fat = 8.5f, sugar = 0.5f, fiber = 1.2f, sodiumMg = 320f, category = "Turkish"),
                FoodItem(name = "Bulgur Pilavı (Bulgur Pilaf)", servingDescription = "1 porsiyon / portion (150g)", servingSizeGrams = 150f, calories = 195f, protein = 5.2f, carbs = 36f, fat = 4.0f, sugar = 1.1f, fiber = 4.6f, sodiumMg = 280f, category = "Turkish"),
                FoodItem(name = "Lahmacun", servingDescription = "1 adet / piece (120g)", servingSizeGrams = 120f, calories = 240f, protein = 11f, carbs = 32f, fat = 7.5f, sugar = 2f, fiber = 2.5f, sodiumMg = 460f, category = "Turkish"),
                FoodItem(name = "Tavuk Döner (Chicken Doner)", servingDescription = "1 porsiyon / portion (150g)", servingSizeGrams = 150f, calories = 275f, protein = 32f, carbs = 3.5f, fat = 15f, sugar = 1f, fiber = 0.8f, sodiumMg = 580f, category = "Turkish"),
                FoodItem(name = "Et Döner (Beef Doner)", servingDescription = "1 porsiyon / portion (150g)", servingSizeGrams = 150f, calories = 330f, protein = 30f, carbs = 2.5f, fat = 22f, sugar = 0.8f, fiber = 0.5f, sodiumMg = 620f, category = "Turkish"),
                FoodItem(name = "Kıymalı Börek", servingDescription = "1 dilim / slice (120g)", servingSizeGrams = 120f, calories = 310f, protein = 11.5f, carbs = 28f, fat = 17f, sugar = 1.5f, fiber = 1.6f, sodiumMg = 490f, category = "Turkish"),
                FoodItem(name = "Ispanaklı Börek (Spinach Borek)", servingDescription = "1 dilim / slice (120g)", servingSizeGrams = 120f, calories = 260f, protein = 8.5f, carbs = 29f, fat = 12.5f, sugar = 2.2f, fiber = 2.8f, sodiumMg = 440f, category = "Turkish"),
                FoodItem(name = "Süzme Yoğurt (Strained Yogurt)", servingDescription = "100g", servingSizeGrams = 100f, calories = 115f, protein = 8f, carbs = 4.5f, fat = 7.5f, sugar = 4f, fiber = 0f, sodiumMg = 60f, category = "Turkish"),
                FoodItem(name = "Beyaz Peynir (Feta / White Cheese)", servingDescription = "1 dilim / slice (30g)", servingSizeGrams = 30f, calories = 85f, protein = 5.5f, carbs = 0.8f, fat = 6.8f, sugar = 0.6f, fiber = 0f, sodiumMg = 270f, category = "Turkish"),
                FoodItem(name = "Kaşar Peyniri (Kashar Cheese)", servingDescription = "1 dilim / slice (30g)", servingSizeGrams = 30f, calories = 105f, protein = 7.5f, carbs = 0.5f, fat = 8.5f, sugar = 0.3f, fiber = 0f, sodiumMg = 210f, category = "Turkish"),
                FoodItem(name = "Siyah Zeytin (Black Olives)", servingDescription = "5 adet / pieces (20g)", servingSizeGrams = 20f, calories = 45f, protein = 0.3f, carbs = 0.8f, fat = 4.5f, sugar = 0f, fiber = 0.6f, sodiumMg = 180f, category = "Turkish"),
                FoodItem(name = "Ayran", servingDescription = "1 bardak / glass (200ml)", servingSizeGrams = 200f, calories = 76f, protein = 4f, carbs = 5.6f, fat = 4.2f, sugar = 5f, fiber = 0f, sodiumMg = 160f, category = "Turkish"),

                // International Essentials & Healthy Staples
                FoodItem(name = "Yumurta (Boiled Egg)", servingDescription = "1 adet / piece (50g)", servingSizeGrams = 50f, calories = 78f, protein = 6.3f, carbs = 0.6f, fat = 5.3f, sugar = 0.5f, fiber = 0f, sodiumMg = 62f, category = "General"),
                FoodItem(name = "Izgara Tavuk Göğsü (Grilled Chicken Breast)", servingDescription = "150g", servingSizeGrams = 150f, calories = 248f, protein = 46.5f, carbs = 0f, fat = 5.4f, sugar = 0f, fiber = 0f, sodiumMg = 110f, category = "General"),
                FoodItem(name = "Somon Balığı (Grilled Salmon)", servingDescription = "150g", servingSizeGrams = 150f, calories = 312f, protein = 34f, carbs = 0f, fat = 19f, sugar = 0f, fiber = 0f, sodiumMg = 85f, category = "General"),
                FoodItem(name = "Tam Buğday Ekmeği (Whole Wheat Bread)", servingDescription = "1 dilim / slice (35g)", servingSizeGrams = 35f, calories = 85f, protein = 3.8f, carbs = 15f, fat = 1.2f, sugar = 1.5f, fiber = 2.4f, sodiumMg = 135f, category = "General"),
                FoodItem(name = "Beyaz Ekmek (White Bread)", servingDescription = "1 dilim / slice (35g)", servingSizeGrams = 35f, calories = 95f, protein = 2.8f, carbs = 18.2f, fat = 1.1f, sugar = 1.8f, fiber = 0.9f, sodiumMg = 170f, category = "General"),
                FoodItem(name = "Yulaf Ezmesi (Rolled Oats)", servingDescription = "1 porsiyon / portion (50g)", servingSizeGrams = 50f, calories = 185f, protein = 6.8f, carbs = 33f, fat = 3.5f, sugar = 0.5f, fiber = 5.2f, sodiumMg = 2f, category = "General"),
                FoodItem(name = "Muz (Banana)", servingDescription = "1 orta boy / medium (118g)", servingSizeGrams = 118f, calories = 105f, protein = 1.3f, carbs = 27f, fat = 0.4f, sugar = 14.4f, fiber = 3.1f, sodiumMg = 1f, category = "General"),
                FoodItem(name = "Elma (Apple)", servingDescription = "1 orta boy / medium (150g)", servingSizeGrams = 150f, calories = 78f, protein = 0.4f, carbs = 21f, fat = 0.3f, sugar = 15.5f, fiber = 3.6f, sodiumMg = 1f, category = "General"),
                FoodItem(name = "Avokado (Avocado)", servingDescription = "Yarım / half (75g)", servingSizeGrams = 75f, calories = 120f, protein = 1.5f, carbs = 6.4f, fat = 11f, sugar = 0.5f, fiber = 5f, sodiumMg = 5f, category = "General"),
                FoodItem(name = "Zeytinyağı (Olive Oil)", servingDescription = "1 yemek kaşığı / tbsp (14g)", servingSizeGrams = 14f, calories = 119f, protein = 0f, carbs = 0f, fat = 13.5f, sugar = 0f, fiber = 0f, sodiumMg = 0f, category = "General"),
                FoodItem(name = "Badem (Raw Almonds)", servingDescription = "1 avuç / handful (28g)", servingSizeGrams = 28f, calories = 164f, protein = 6f, carbs = 6.1f, fat = 14.2f, sugar = 1.2f, fiber = 3.5f, sodiumMg = 1f, category = "General"),
                FoodItem(name = "Ceviz (Walnuts)", servingDescription = "1 avuç / handful (28g)", servingSizeGrams = 28f, calories = 185f, protein = 4.3f, carbs = 3.9f, fat = 18.5f, sugar = 0.7f, fiber = 1.9f, sodiumMg = 1f, category = "General"),
                FoodItem(name = "Süt (Semi-skimmed Milk)", servingDescription = "1 bardak / cup (200ml)", servingSizeGrams = 200f, calories = 94f, protein = 6.8f, carbs = 9.8f, fat = 3.2f, sugar = 9.8f, fiber = 0f, sodiumMg = 88f, category = "Beverage"),
                FoodItem(name = "Yeşil Çay (Green Tea, unsweetened)", servingDescription = "1 fincan / cup (200ml)", servingSizeGrams = 200f, calories = 2f, protein = 0f, carbs = 0f, fat = 0f, sugar = 0f, fiber = 0f, sodiumMg = 2f, category = "Beverage")
            )
            dao.insertFoods(foodList)
        }
    }
}
