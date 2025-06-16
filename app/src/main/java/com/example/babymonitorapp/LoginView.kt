package com.example.babymonitorapp

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.babymonitorapp.database.UserViewModel
import androidx.core.content.edit

class LoginView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_view)

        val loginButton = findViewById<Button>(R.id.btnLogin)
        val registerButton = findViewById<Button>(R.id.register)
        val usernameEditText = findViewById<EditText>(R.id.etUsername)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            lifecycleScope.launch {
                val user = userViewModel.login(username, password)
                if (user != null) {
                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    sharedPref.edit { putInt("loggedInUserId", user.id) }
                    Toast.makeText(this@LoginView, "Successfully Logged in", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginView, MainActivity3::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginView, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterView::class.java)
            startActivity(intent)
            finish()
        }
    }
}