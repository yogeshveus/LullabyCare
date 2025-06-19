package com.example.babymonitorapp

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


object NutritionTracker {

    private const val apiKey = BuildConfig.API_KEY_NUT // Replace with your actual API key
    private val client = OkHttpClient()
    private val foodLog = mutableListOf<FoodEntry>()

    suspend fun getDietRecommendation(prompt: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val json = """
                {
                  "contents": [
                    {
                      "role": "user",
                      "parts": [
                        {
                          "text": "$prompt"
                        }
                      ]
                    }
                  ]
                }
                """.trimIndent()

                val body = json.toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=$apiKey")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                Log.d("GeminiResponse", "Response Body: $responseBody")

                val jsonResponse = JSONObject(responseBody)

                if (jsonResponse.has("candidates")) {
                    val text = jsonResponse.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                    text
                } else if (jsonResponse.has("error")) {
                    val errorMessage = jsonResponse.getJSONObject("error").getString("message")
                    Log.e("GeminiError", errorMessage)
                    "Error from Gemini: $errorMessage"
                } else {
                    "No response candidates received."
                }
            } catch (e: Exception) {
                Log.e("GeminiError", "Error fetching suggestion", e)
                "Unable to get recommendation. Please try again."
            }
        }
    }

    fun addFood(entry: FoodEntry) {
        foodLog.add(entry)
    }

    fun getSummary(): Map<String, Double> = mapOf(
        "Calories" to foodLog.sumOf { it.calories.toDouble() },
        "Protein (g)" to foodLog.sumOf { it.protein },
        "Carbs (g)" to foodLog.sumOf { it.carbs },
        "Fats (g)" to foodLog.sumOf { it.fats }
    )

    fun clearAllEntries() {
        foodLog.clear()
    }

    fun removeEntryById(id: String): Boolean = foodLog.removeIf { it.id == id }

    fun getAllEntries(): List<FoodEntry> = foodLog
}
