package com.example.babymonitorapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class NutritionTracker(private val baby: BabyProfile) : AppCompatActivity() {

    private val foodLog = mutableListOf<FoodEntry>()
    private var bottomNav: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition_tracker)

        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity3::class.java))
                    true
                }
                R.id.baby -> {
                    startActivity(Intent(this, YoutubeActivity::class.java))
                    true
                }
                R.id.community -> {
                    startActivity(Intent(this, Community::class.java))
                    true
                }
                R.id.settings -> {
                    startActivity(Intent(this, SettingsView::class.java))
                    true
                }
                else -> false
            }
        }
    }

    fun addFood(name: String, calories: Int, protein: Double, carbs: Double, fats: Double) {
        val entry = FoodEntry(name = name, calories = calories, protein = protein, carbs = carbs, fats = fats)
        foodLog.add(entry)
    }

    fun getSummary(): Map<String, Double> {
        return mapOf(
            "Calories" to foodLog.sumOf { it.calories.toDouble() },
            "Protein (g)" to foodLog.sumOf { it.protein },
            "Carbs (g)" to foodLog.sumOf { it.carbs },
            "Fats (g)" to foodLog.sumOf { it.fats }
        )
    }

    fun getDietRecommendation(): DietRecommendation {
        val heightM = baby.heightCm / 100
        val bmi = baby.weightKg / (heightM * heightM)

        val bmiCategory = when {
            bmi < 14 -> "Underweight"
            bmi > 18 -> "Overweight"
            else -> "Normal"
        }

        return when (baby.ageInMonths) {
            in 0..6 -> DietRecommendation(
                "0–6 months | $bmiCategory",
                "500–700 kcal/day",
                "Exclusive breastfeeding or formula feeding is advised.\n",
                listOf("Breastmilk", "Infant formula")
            )

            in 7..12 -> {
                val meals = when (bmiCategory) {
                    "Underweight" -> listOf("Mashed banana with ghee", "Rice cereal with formula", "Mashed potato", "Full-fat yogurt", "Egg yolk\n")
                    "Overweight" -> listOf("Steamed veggies", "Unsweetened fruit puree", "Light dal water", "Oats porridge (thin)\n")
                    else -> listOf("Rice cereal", "Mashed fruits", "Pureed veggies", "Dal soup", "Soft-boiled egg yolk")
                }
                DietRecommendation(
                    "7–12 months | $bmiCategory",
                    "700–900 kcal/day",
                    "Introduce solids alongside milk feeding.\n",
                    meals
                )
            }

            in 13..24 -> {
                val meals = when (bmiCategory) {
                    "Underweight" -> listOf("Khichdi with ghee", "Paneer cubes", "Ripe banana", "Eggs", "Sweet potato mash")
                    "Overweight" -> listOf("Steamed vegetables", "Thin dal", "Boiled rice (no ghee)", "Fruits")
                    else -> listOf("Chapati with ghee", "Fruit pieces", "Vegetable khichdi", "Boiled egg", "Paneer cubes")
                }
                DietRecommendation(
                    "13–24 months | $bmiCategory",
                    "900–1000 kcal/day",
                    "Balanced meals including protein, iron, and vitamin A.\n",
                    meals
                )
            }

            else -> {
                val meals = when (bmiCategory) {
                    "Underweight" -> listOf("Idli with ghee", "Milk oats with banana", "Boiled sweet potato", "Cheese toast", "Dry fruit powder mix")
                    "Overweight" -> listOf("Vegetable soup", "Steamed idli", "Low sugar fruit snacks", "Roti without ghee")
                    else -> listOf("Idli with sambar", "Milk oats", "Boiled sweet potato", "Roti and dal", "Vegetable soup")
                }
                DietRecommendation(
                    "2+ years | $bmiCategory",
                    "1000–1400 kcal/day (varies by activity)",
                    "Use growth charts and consult a pediatric dietitian if unsure.\n",
                    meals
                )
            }
        }
    }

    fun clearAllEntries() {
        foodLog.clear()
    }

    fun removeEntryById(id: String): Boolean {
        return foodLog.removeIf { it.id == id }
    }

    fun getAllEntries(): List<FoodEntry> = foodLog
}
