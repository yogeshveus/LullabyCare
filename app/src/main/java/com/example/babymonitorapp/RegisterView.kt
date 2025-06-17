package com.example.babymonitorapp

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.babymonitorapp.database.User
import com.example.babymonitorapp.database.UserViewModel
import kotlinx.coroutines.launch

class RegisterView : AppCompatActivity() {
    private lateinit var mUserViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_view)

        val registerButton = findViewById<Button>(R.id.btnLogin)
        val loginButton = findViewById<Button>(R.id.register)
        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        registerButton.setOnClickListener {
            insertDatatoDB()

        }
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginView::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun insertDatatoDB() {
        val name = findViewById<EditText>(R.id.etname).text.toString()
        val user = findViewById<EditText>(R.id.etUsername).text.toString()
        val password = findViewById<EditText>(R.id.etPassword).text.toString()
        val cpassword = findViewById<EditText>(R.id.etConfirmPassword).text.toString()
        val phone = findViewById<EditText>(R.id.etnumber).text.toString()

        if (!checkInput(name,user,password,phone)){
            Toast.makeText(this, "Please fill out all the details.", Toast.LENGTH_LONG).show()
            return
        }
        lifecycleScope.launch {
            val existingUser = mUserViewModel.validUser(user)
            if (existingUser != null) {
                Toast.makeText(this@RegisterView, "Username already exists.", Toast.LENGTH_LONG).show()
            } else if (!validPassword1(password)) {
                Toast.makeText(this@RegisterView, "Password must contain at least 1 special character", Toast.LENGTH_LONG).show()
            } else if (!validPassword2(password)) {
                Toast.makeText(this@RegisterView, "Password must contain at least 1 uppercase character", Toast.LENGTH_LONG).show()
            } else if (!validPassword3(password)) {
                Toast.makeText(this@RegisterView, "Password size must be at least 8", Toast.LENGTH_LONG).show()
            } else if (!validPhone(phone)) {
                Toast.makeText(this@RegisterView, "Please provide a valid phone number.", Toast.LENGTH_LONG).show()
            } else if (!validPassword4(password, cpassword)){
                Toast.makeText(this@RegisterView, "Passwords don't match", Toast.LENGTH_LONG).show()
            }
            else {
                val userObj = User(0, name, user, password, phone)
                mUserViewModel.insertUser(userObj)
                Toast.makeText(this@RegisterView, "Successfully Registered!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@RegisterView, LoginView::class.java))
                finish()
            }
        }
    }

    private fun validPassword1(password: String): Boolean {
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        return (hasSpecialChar)
    }
    private fun validPassword2(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        return (hasUppercase)
    }
    private fun validPassword3(password: String): Boolean {
        return (password.length>=8)
    }
    private fun validPassword4(password: String, cpassword: String): Boolean {
        return (password == cpassword)
    }
    private fun validPhone(phone: String): Boolean {
        return (phone.length==10)
    }

    private fun checkInput(name: String,user: String,password: String,phone: String): Boolean{
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(user) || TextUtils.isEmpty(password) || phone.isEmpty())
    }
}


