package com.example.babymonitorapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val query = intent.getStringExtra("query")

        val resultTextView = findViewById<TextView>(R.id.resultTextView)
        resultTextView.text = "Searching for: $query"
    }
}
