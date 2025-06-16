package com.example.babymonitorapp

import java.util.UUID

data class FoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fats: Double
)