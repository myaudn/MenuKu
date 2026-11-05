package com.example.menuku.core.utils

object AiPriceEstimator {
    fun estimatePrice(name: String, quantity: Double, unit: String, category: String): Long {
        val lowerName = name.lowercase()
        val basePricePerUnit = when {
            lowerName.contains("daging") || lowerName.contains("sapi") || lowerName.contains("salmon") -> 85000L
            lowerName.contains("ayam") || lowerName.contains("fillet") || lowerName.contains("udang") -> 45000L
            lowerName.contains("keju") || lowerName.contains("susu") || lowerName.contains("butter") -> 30000L
            lowerName.contains("minyak") || lowerName.contains("beras") || lowerName.contains("tepung") -> 20000L
            lowerName.contains("bawang") || lowerName.contains("cabai") || lowerName.contains("brokoli") -> 15000L
            lowerName.contains("buncis") || lowerName.contains("wortel") || lowerName.contains("tahu") || lowerName.contains("tempe") -> 10000L
            category == "protein" -> 50000L
            category == "sayur" -> 12000L
            category == "bumbu" -> 8000L
            else -> 20000L
        }

        val multiplier = when (unit.lowercase()) {
            "kg" -> quantity
            "gram", "gr" -> quantity / 1000.0
            "liter", "l" -> quantity
            "ml" -> quantity / 1000.0
            "pcs", "buah", "butir", "ikat" -> quantity * 0.25
            else -> 1.0
        }.coerceAtLeast(0.1)

        return (basePricePerUnit * multiplier).toLong().coerceIn(3000L, 250000L)
    }
}
