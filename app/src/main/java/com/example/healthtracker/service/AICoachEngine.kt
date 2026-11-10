package com.example.healthtracker.service

import com.example.healthtracker.data.entity.MealEntry
import com.example.healthtracker.data.entity.UserProfile
import com.example.healthtracker.data.entity.WaterEntry

object AICoachEngine {

    /**
     * Generates a context-aware daily tip and review based on the user's logged intake and language.
     */
    fun generateDailyTip(
        profile: UserProfile?,
        todayMeals: List<MealEntry>,
        todayWater: List<WaterEntry>,
        sugarFreeStreak: Int,
        language: String
    ): String {
        val totalCalories = todayMeals.sumOf { it.calories.toDouble() }.toInt()
        val totalWater = todayWater.sumOf { it.amountMl }
        val targetCalories = profile?.dailyCalorieTarget ?: 2000
        val targetWater = profile?.dailyWaterTargetMl ?: 2500

        val lang = language.lowercase()

        return when {
            // High sugar free streak celebration
            sugarFreeStreak >= 5 -> when (lang) {
                "tr" -> "Tebrikler! $sugarFreeStreak gündür ilave şekersiz harika gidiyorsunuz. Enerji seviyelerinizdeki dengeli artışı fark ettiniz mi? 🌱"
                "es" -> "¡Felicitaciones! Llevas $sugarFreeStreak días sin azúcar añadido. Pequeños pasos hacen grandes cambios. 🌱"
                "de" -> "Glückwunsch! Du bist seit $sugarFreeStreak Tagen zuckerfrei. Halte diesen gesunden Schwung aufrecht! 🌱"
                "fr" -> "Félicitations ! Vous avez $sugarFreeStreak jours sans sucre ajouté. Continuez ainsi ! 🌱"
                "pt" -> "Parabéns! Você está há $sugarFreeStreak dias sem açúcar adicionado. Pequenos passos, grandes mudanças! 🌱"
                else -> "Congratulations! You have been added-sugar free for $sugarFreeStreak days. Your steady energy speaks for itself! 🌱"
            }

            // Low water warning
            totalWater < (targetWater * 0.4) -> when (lang) {
                "tr" -> "Bugün su tüketiminiz biraz geride kaldı ($totalWater / $targetWater ml). Başucunuza veya masanıza bir bardak su koymayı deneyebilirsiniz. 💧"
                "es" -> "Tu consumo de agua hoy está bajo ($totalWater / $targetWater ml). Recuerda beber un vaso de agua ahora. 💧"
                "de" -> "Deine Wasserzufuhr ist heute noch gering ($totalWater / $targetWater ml). Gönn dir ein Glas frisches Wasser. 💧"
                "fr" -> "Votre consommation d'eau est faible aujourd'hui ($totalWater / $targetWater ml). Pensez à boire un verre d'eau ! 💧"
                "pt" -> "Seu consumo de água hoje está baixo ($totalWater / $targetWater ml). Que tal um copo d'água agora? 💧"
                else -> "Your hydration is a bit behind today ($totalWater / $targetWater ml). Try keeping a water bottle nearby! 💧"
            }

            // Balanced calories
            totalCalories in (targetCalories - 250)..(targetCalories + 250) -> when (lang) {
                "tr" -> "Harika bir denge! Bugünkü kalori alımınız ($totalCalories kcal) hedefinizle ($targetCalories kcal) tam uyumlu gidiyor. 🥑"
                "es" -> "¡Excelente equilibrio! Tu ingesta de calorías hoy ($totalCalories kcal) está alineada con tu objetivo. 🥑"
                "de" -> "Tolles Gleichgewicht! Deine heutige Kalorienaufnahme ($totalCalories kcal) passt perfekt zu deinem Ziel. 🥑"
                "fr" -> "Super équilibre ! Vos calories du jour ($totalCalories kcal) correspondent bien à votre objectif. 🥑"
                "pt" -> "Ótimo equilíbrio! Suas calorias hoje ($totalCalories kcal) estão bem alinhadas com sua meta. 🥑"
                else -> "Great balance! Your calorie intake today ($totalCalories kcal) aligns nicely with your daily target. 🥑"
            }

            // General friendly welcome/start tip
            else -> when (lang) {
                "tr" -> "Küçük adımlar, büyük değişimler getirir. Öğünlerinizi düzenli kaydederek alışkanlıklarınızı keşfetmeye devam edin. ✨"
                "es" -> "Pequeños pasos llevan a grandes cambios. Registra tus comidas y mantén la constancia. ✨"
                "de" -> "Kleine Schritte führen zu großen Veränderungen. Bleibe dran und erfasse deine Mahlzeiten regelmäßig. ✨"
                "fr" -> "Petits pas, grands changements. Continuez à enregistrer vos repas pour de bonnes habitudes. ✨"
                "pt" -> "Pequenos passos trazem grandes mudanças. Registre suas refeições e mantenha o ritmo. ✨"
                else -> "Small steps lead to big changes. Keep logging your meals to build lasting sustainable habits! ✨"
            }
        }
    }

    /**
     * Answers interactive user queries with helpful, safe lifestyle and nutrition coaching.
     */
    fun answerUserQuestion(question: String, language: String): String {
        val q = question.lowercase()
        val lang = language.lowercase()

        val isTr = lang == "tr"
        val isEs = lang == "es"
        val isDe = lang == "de"
        val isFr = lang == "fr"
        val isPt = lang == "pt"

        return when {
            q.contains("su") || q.contains("water") || q.contains("agua") || q.contains("eau") || q.contains("wasser") -> {
                when {
                    isTr -> "Su içmek metabolizmayı destekler ve tokluk hissi sağlar. Günde 2 - 2.5 litre su içmeyi ve çay/kahve haricinde saf suyu önceliklendirmeyi deneyebilirsiniz."
                    isEs -> "El agua es fundamental para el metabolismo. Intenta beber entre 2 y 2.5 litros al día, priorizando agua pura sobre otras bebidas."
                    isDe -> "Wasser unterstützt den Stoffwechsel. Versuche 2 bis 2,5 Liter pro Tag zu trinken und reines Wasser zu bevorzugen."
                    isFr -> "Boire de l'eau favorise le métabolisme. Visez entre 2 et 2,5 litres par jour en privilégiant l'eau pure."
                    isPt -> "A água é vital para o metabolismo. Tente beber de 2 a 2,5 litros por dia, priorizando água pura."
                    else -> "Staying hydrated supports digestion and energy. Aim for 2 - 2.5 liters daily and prioritize pure water over caffeinated drinks."
                }
            }
            q.contains("şeker") || q.contains("sugar") || q.contains("azucar") || q.contains("sucre") -> {
                when {
                    isTr -> "İlave şekeri azaltmak ani enerji düşüşlerini engeller. Meyvelerden gelen doğal şekeri yasaklamadan, paketli gıdalar ve gazlı içecekleri azaltmak harika bir başlangıçtır."
                    isEs -> "Reducir los azúcares añadidos estabiliza tu energía. Es mejor evitar refrescos y ultraprocesados sin restringir las frutas naturales."
                    isDe -> "Den Verzehr von zugesetztem Zucker zu reduzieren, verhindert Energietiefs. Bevorzuge Obst und meide gezuckerte Getränke."
                    isFr -> "Réduire le sucre ajouté évite les coups de fatigue. Privilégiez les fruits frais plutôt que les boissons sucrées."
                    isPt -> "Reduzir açúcares adicionados evita quedas bruscas de energia. Foque em diminuir refrigerantes e doces processados."
                    else -> "Reducing added sugars prevents afternoon energy crashes. You don't have to cut out whole fruit—just swap sugary drinks and packaged desserts for fresh alternatives."
                }
            }
            q.contains("oruç") || q.contains("fasting") || q.contains("ayuno") || q.contains("jeûne") || q.contains("fasten") -> {
                when {
                    isTr -> "Aralıklı oruç (örneğin 16:8), sindirim sisteminize dinlenme aralığı verir. Oruç süresince bol su ve şekersiz bitki çayları tüketebilirsiniz. Kendinizi yorgun hissederseniz vücudunuzu dinlemeyi unutmayın."
                    isEs -> "El ayuno intermitente (como 16:8) da descanso al sistema digestivo. Bebe suficiente agua durante la ventana de ayuno y escucha siempre a tu cuerpo."
                    isDe -> "Intervallfasten (z. B. 16:8) gibt der Verdauung Zeit zur Erholung. Trinke ausreichend Wasser und achte auf dein Wohlbefinden."
                    isFr -> "Le jeûne intermittent (ex. 16:8) offre une pause digestive. Pensez à bien vous hydrater avec de l'eau et des infusions non sucrées."
                    isPt -> "O jejum intermitente (como 16:8) dá descanso à digestão. Mantenha-se bem hidratado com água e chás sem açúcar."
                    else -> "Intermittent fasting (like 16:8) gives your digestive system a natural rest period. Stay hydrated with water and herbal tea during the fast, and always listen to your body."
                }
            }
            q.contains("protein") -> {
                when {
                    isTr -> "Protein kas onarımını ve tokluk hissini artırır. Yumurta, yoğurt, baklagiller (mercimek, nohut) ve yağsız etler dengeli bir beslenmenin temelidir."
                    isEs -> "La proteína favorece la saciedad y la masa muscular. Huevos, legumbres, pescado y yogur son fuentes excelentes."
                    isDe -> "Protein fördert die Sättigung und den Muskelerhalt. Eier, Hülsenfrüchte, Joghurt und mageres Fleisch sind ideal."
                    isFr -> "Les protéines aident à la satiété et au maintien musculaire. Privilégiez les œufs, légumineuses, yaourts et viandes maigres."
                    isPt -> "A proteína promove saciedade e recuperação muscular. Ovos, iogurte, leguminosas e carnes magras são ótimas fontes."
                    else -> "Protein boosts satiety and muscle maintenance. Great sources include eggs, yogurt, legumes (lentils, chickpeas), fish, and poultry."
                }
            }
            else -> {
                when {
                    isTr -> "Dengeli beslenme, tutarlı su tüketimi ve günlük hareket ile sürdürülebilir sonuçlar elde edebilirsiniz. Size özel bir tavsiye için belirli bir konuyu (su, protein, şeker, oruç) sorabilirsiniz!"
                    isEs -> "La constancia en la nutrición, hidratación y movimiento diario es la clave. ¡Pregúntame sobre agua, proteínas, azúcar o ayuno para más detalles!"
                    isDe -> "Regelmäßige Bewegung, ausreichende Flüssigkeit und ausgewogene Ernährung bringen nachhaltige Erfolge. Frage mich gerne nach Wasser, Protein, Zucker oder Fasten!"
                    isFr -> "La régularité dans vos repas, votre hydratation et vos pas quotidiens fait la différence. N'hésitez pas à poser une question précise sur le sucre, l'eau ou les protéines !"
                    isPt -> "Constância na alimentação, água e movimento diário geram grandes mudanças. Pergunte-me sobre água, proteínas, açúcar ou jejum!"
                    else -> "Consistency in balanced meals, hydration, and daily movement brings sustainable results. Feel free to ask specifically about water, protein, sugar reduction, or fasting!"
                }
            }
        }
    }
}
