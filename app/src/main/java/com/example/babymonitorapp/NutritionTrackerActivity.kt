package com.example.babymonitorapp

import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class NutritionTrackerActivity :AppCompatActivity() {
    private lateinit var ageInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var resultText: TextView
    private lateinit var submitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition_tracker)

        ageInput = findViewById(R.id.ageInput)
        weightInput = findViewById(R.id.weightInput)
        heightInput = findViewById(R.id.heightInput)
        resultText = findViewById(R.id.recommendationText)
        submitBtn = findViewById(R.id.submitBtn)

        submitBtn.setOnClickListener {
            val age = ageInput.text.toString().toIntOrNull()
            val weight = weightInput.text.toString().toDoubleOrNull()
            val height = heightInput.text.toString().toDoubleOrNull()

            if (age == null || weight == null || height == null) {
                Toast.makeText(this, "Please enter valid age, weight, and height", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val babyProfile = BabyProfile(ageInMonths = age, weightKg = weight, heightCm = height)
            val tracker = NutritionTracker(babyProfile)
            val recommendation = tracker.getDietRecommendation()

            val mealsFormatted = recommendation.suggestedMeals.joinToString("\n- ", prefix = "- ")

            val result = getString(
                R.string.nutrition_result,
                recommendation.ageGroup,
                recommendation.dailyCalories,
                recommendation.notes,
                mealsFormatted
            )

            resultText.text = result

        }
    }
}