package com.example.babymonitorapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NutritionTrackerActivity : AppCompatActivity() {

    private lateinit var ageInput: EditText
    private lateinit var weightInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var resultText: TextView
    private lateinit var submitBtn: Button
    private var bottomNav: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition_tracker)

        ageInput = findViewById(R.id.ageInput)
        weightInput = findViewById(R.id.weightInput)
        heightInput = findViewById(R.id.heightInput)
        resultText = findViewById(R.id.recommendationText)
        submitBtn = findViewById(R.id.submitBtn)

        bottomNav = findViewById(R.id.bottomNavigationView)
        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> startActivity(Intent(this, MainActivity3::class.java))
                R.id.baby -> startActivity(Intent(this, YoutubeActivity::class.java))
                R.id.community -> startActivity(Intent(this, Community::class.java))
                R.id.settings -> startActivity(Intent(this, SettingsView::class.java))
            }
            true
        }

        submitBtn.setOnClickListener {
            val age = ageInput.text.toString().toIntOrNull()
            val weight = weightInput.text.toString().toDoubleOrNull()
            val height = heightInput.text.toString().toDoubleOrNull()

            if (age == null || weight == null || height == null) {
                Toast.makeText(this, "Please enter valid age, weight, and height", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            resultText.text = "Getting recommendations..."

            val prompt = """
                Could you kindly provide a detailed nutrition plan for a baby aged $age months, 
                weighing $weight kg and measuring $height cm in height, living in India? 
                Please include the recommended daily calorie intake, suggested meals, and appropriate portion sizes.
            """.trimIndent()


            CoroutineScope(Dispatchers.IO).launch {
                val suggestion = NutritionTracker.getDietRecommendation(prompt)
                withContext(Dispatchers.Main) {
                    resultText.text = suggestion
                }
            }
        }
    }
}
