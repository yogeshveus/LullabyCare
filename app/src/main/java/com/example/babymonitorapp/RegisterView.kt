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
import com.example.babymonitorapp.database.User
import com.example.babymonitorapp.database.UserViewModel

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
        val phone = findViewById<EditText>(R.id.etnumber).text.toString()
        if (checkInput(name,user,password,phone)){
            val user = User(0, name, user, password, phone.toString())
            mUserViewModel.insertUser(user)
            Toast.makeText(this, "Successfully Registered!", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginView::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "Please fill out all the details.", Toast.LENGTH_LONG).show()
        }
    }
    private fun checkInput(name: String,user: String,password: String,phone: String): Boolean{
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(user) || TextUtils.isEmpty(password) || phone.isEmpty())
    }
}


