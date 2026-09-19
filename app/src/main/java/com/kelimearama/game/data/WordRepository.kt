package com.kelimearama.game.data

import com.kelimearama.game.model.Category

object WordRepository {

    val TURKISH_ALPHABET = listOf(
        'A', 'B', 'C', 'Ç', 'D', 'E', 'F', 'G', 'Ğ', 'H', 'I', 'İ', 'J', 'K', 'L', 'M',
        'N', 'O', 'Ö', 'P', 'R', 'S', 'Ş', 'T', 'U', 'Ü', 'V', 'Y', 'Z'
    )

    fun getCategories(): List<Category> {
        return listOf(
            Category(
                id = "animals",
                name = "Hayvanlar",
                iconName = "ic_animals",
                description = "Sevimli dostlarımız ve doğadaki canlılar",
                wordList = listOf(
                    "ASLAN", "KAPLAN", "KEDİ", "KÖPEK", "ZÜRAFA", "FİL", "KARTAL",
                    "MAYMUN", "BALİNA", "YUNUS", "TAVŞAN", "TİLKİ", "AYI", "KURT",
                    "ZERA", "KAPLUMBAĞA", "TİMSAH", "KANGURU", "PENGUEN", "BAYKUŞ",
                    "ŞAHİN", "SERÇE", "GÜVERCİN", "LEYLEK", "SİNCAP", "SERÇE", "ÖRDEK",
                    "HOROZ", "TAVUK", "ZÜRAFA", "AT", "EŞEK", "KEÇİ", "KOYUN", "İNEK"
                )
            ),
            Category(
                id = "fruits",
                name = "Meyveler & Sebzeler",
                iconName = "ic_fruits",
                description = "Lezzetli ve sağlıklı meyveler ile sebzeler",
                wordList = listOf(
                    "ELMA", "ARMUT", "MUZ", "ÇİLEK", "KARPUZ", "KAVUN", "PORTAKAL",
                    "MANDALİNA", "LİMON", "KİVİ", "ANANAS", "ŞEFTALİ", "ERİK", "KAYISI",
                    "ÜZÜM", "KİRAZ", "VİŞNE", "DOMATES", "SALATALIK", "BİBER", "PATLICAN",
                    "PATATES", "SOĞAN", "SARIMSAK", "HAVUÇ", "ISPANAK", "PIRASA", "ENGİNAR"
                )
            ),
            Category(
                id = "cities",
                name = "Şehirler",
                iconName = "ic_cities",
                description = "Türkiye ve dünyadan popüler şehirler",
                wordList = listOf(
                    "İSTANBUL", "ANKARA", "İZMİR", "BURSA", "ANTALYA", "ADANA", "TRABZON",
                    "KONYA", "GAZİANTEP", "ESKİŞEHİR", "SAMSUN", "DENİZLİ", "KAYSERİ", "MUĞLA",
                    "ÇANAKKALE", "LONDRA", "PARİS", "ROMA", "TOKYO", "BERLİN", "MADRİD",
                    "AMSTERDAM", "VİYANA", "PRAG", "SİDNEY", "PEKİN", "MOSKOVA", "TORONTO"
                )
            ),
            Category(
                id = "sports",
                name = "Spor Dalları",
                iconName = "ic_sports",
                description = "Hareket, rekabet ve spor alanları",
                wordList = listOf(
                    "FUTBOL", "BASKETBOL", "VOLEYBOL", "TENNİS", "YÜZME", "GÜREŞ", "HENTBOL",
                    "OKÇULUK", "BOKS", "KARATE", "ATLETİZM", "BİSİKLET", "KÜREK", "JUDO",
                    "TAEKWONDO", "KAYAK", "SURF", "GOLF", "PATEN", "CİRİT", "MASA TENİSİ"
                )
            ),
            Category(
                id = "kitchen",
                name = "Mutfak & Eşyalar",
                iconName = "ic_kitchen",
                description = "Mutfakta ve evde kullandığımız gereçler",
                wordList = listOf(
                    "TABAK", "BARDAK", "KAŞIK", "ÇATAL", "BIÇAK", "TENCERE", "TAVA",
                    "ÇAYDANLIK", "SÜRAHİ", "TEPSİ", "FIRIN", "OCAK", "BUZDOLABI", "MİKSER",
                    "SU Isıtıcı", "KAVANOZ", "KASE", "KEPÇE", "SÜZGEÇ", "RENDE", "MANDAL"
                )
            ),
            Category(
                id = "nature",
                name = "Doğa & Coğrafya",
                iconName = "ic_nature",
                description = "Dağlar, nehirler ve doğa olayları",
                wordList = listOf(
                    "ORMAN", "NEHİR", "DENİZ", "OKYANUS", "GÖL", "DAĞ", "VADİ",
                    "RÜZGAR", "YAĞMUR", "KAR", "BULUT", "GÜNEŞ", "YILDIZ", "GEZEGEN",
                    "ŞELALE", "MAGMA", "YANARDAĞ", "KANYON", "TOPRAK", "YAPRAK", "ÇİÇEK"
                )
            )
        )
    }

    fun getCategoryById(id: String): Category {
        return getCategories().find { it.id == id } ?: getCategories().first()
    }
}
