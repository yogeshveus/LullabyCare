package com.example.babymonitorapp

data class DietRecommendation(
    val ageGroup: String,
    val dailyCalories: String,
    val notes: String,
    val suggestedMeals: List<String>
)
