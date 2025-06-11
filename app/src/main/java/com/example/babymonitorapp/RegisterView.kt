package com.example.babymonitorapp

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

class RegisterView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_view)

        val registerButton = findViewById<Button>(R.id.btnLogin)
        val loginButton = findViewById<Button>(R.id.register)

        registerButton.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
            finish()
        }
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginView::class.java)
            startActivity(intent)
            finish()
        }
    }
}