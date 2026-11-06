package com.antigravity.survival.data.calculator

data class RationCalculationResult(
    val totalWaterLiters: Double,
    val totalCalories: Double,
    val whiteRiceKg: Double,
    val dryBeansLentilsKg: Double,
    val rolledOatsKg: Double,
    val cookingOilKg: Double,
    val saltKg: Double,
    val sugarOrHoneyKg: Double,
    val totalDryFoodKg: Double
)

object RationCalculator {
    const val ADULT_WATER_LITERS_DAY = 3.5
    const val CHILD_WATER_LITERS_DAY = 2.2
    const val ADULT_CALORIES_DAY = 2200.0
    const val CHILD_CALORIES_DAY = 1500.0

    // Daily breakdown grams per person
    const val WHITE_RICE_GRAMS = 250.0
    const val DRY_BEANS_GRAMS = 120.0
    const val ROLLED_OATS_GRAMS = 80.0
    const val COOKING_OIL_GRAMS = 30.0
    const val SALT_GRAMS = 10.0
    const val SUGAR_HONEY_GRAMS = 35.0

    fun calculate(adults: Int, children: Int, days: Int): RationCalculationResult {
        val totalWater = (adults * ADULT_WATER_LITERS_DAY + children * CHILD_WATER_LITERS_DAY) * days
        val totalCalories = (adults * ADULT_CALORIES_DAY + children * CHILD_CALORIES_DAY) * days
        val totalPersons = adults + children

        fun toKg(gramsPerDay: Double): Double {
            return (totalPersons * gramsPerDay * days) / 1000.0
        }

        val rice = toKg(WHITE_RICE_GRAMS)
        val beans = toKg(DRY_BEANS_GRAMS)
        val oats = toKg(ROLLED_OATS_GRAMS)
        val oil = toKg(COOKING_OIL_GRAMS)
        val salt = toKg(SALT_GRAMS)
        val sugar = toKg(SUGAR_HONEY_GRAMS)

        val totalFood = rice + beans + oats + oil + salt + sugar

        return RationCalculationResult(
            totalWaterLiters = totalWater,
            totalCalories = totalCalories,
            whiteRiceKg = rice,
            dryBeansLentilsKg = beans,
            rolledOatsKg = oats,
            cookingOilKg = oil,
            saltKg = salt,
            sugarOrHoneyKg = sugar,
            totalDryFoodKg = totalFood
        )
    }
}
