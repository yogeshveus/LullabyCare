package com.example.babymonitorapp

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.babymonitorapp.database.User
import com.example.babymonitorapp.database.UserViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RegisterView : AppCompatActivity() {
    private lateinit var mUserViewModel: UserViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String
    private lateinit var otpField: EditText
    private var otpSent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_view)

        val registerButton = findViewById<Button>(R.id.btnLogin)
        val loginButton = findViewById<Button>(R.id.register)
        otpField = findViewById(R.id.otpField)

        val phoneEditText = findViewById<EditText>(R.id.etnumber)

        mUserViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        // 🔔 Auto-send OTP when phone has 10 digits
        phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val phone = s.toString()
                if (phone.length == 10 && !otpSent && validPhone(phone)) {
                    sendVerificationCode(phone)
                    otpSent = true
                }
            }
        })

        registerButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.etname).text.toString()
            val user = findViewById<EditText>(R.id.etUsername).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()
            val cpassword = findViewById<EditText>(R.id.etConfirmPassword).text.toString()
            val phone = phoneEditText.text.toString()

            if (!checkInput(name, user, password, phone)) {
                toast("Please fill out all the details.")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val existingUser = mUserViewModel.validUser(user)
                if (existingUser != null) {
                    toast("Username already exists.")
                } else if (!validPassword1(password)) {
                    toast("Password must contain at least 1 special character")
                } else if (!validPassword2(password)) {
                    toast("Password must contain at least 1 uppercase character")
                } else if (!validPassword3(password)) {
                    toast("Password size must be at least 8")
                } else if (!validPassword4(password, cpassword)) {
                    toast("Passwords don't match")
                } else if (!otpSent) {
                    toast("OTP not sent yet. Please wait.")
                } else {
                    verifyOtpAndRegister(name, user, password, phone)
                }
            }
        }

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginView::class.java))
            finish()
        }
    }

    private fun sendVerificationCode(phone: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Instant verification (rare)
                    toast("Auto OTP Verified.")
                    fillOtpAndRegister(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    toast("OTP send failed: ${e.message}")
                }

                override fun onCodeSent(
                    verifId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verifId, token)
                    verificationId = verifId
                    toast("OTP sent to your number.")
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtpAndRegister(name: String, user: String, password: String, phone: String) {
        val otp = otpField.text.toString()
        if (otp.isEmpty()) {
            toast("Please enter the OTP")
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        fillOtpAndRegister(credential, name, user, password, phone)
    }

    private fun fillOtpAndRegister(
        credential: PhoneAuthCredential,
        name: String? = null,
        user: String? = null,
        password: String? = null,
        phone: String? = null
    ) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (name != null && user != null && password != null && phone != null) {
                    val userObj = User(0, name, user, password, phone)
                    mUserViewModel.insertUser(userObj)
                    toast("OTP Verified. Registered Successfully!")
                    startActivity(Intent(this, LoginView::class.java))
                    finish()
                }
            } else {
                toast("Invalid OTP")
            }
        }
    }

    private fun validPassword1(password: String): Boolean = password.any { !it.isLetterOrDigit() }
    private fun validPassword2(password: String): Boolean = password.any { it.isUpperCase() }
    private fun validPassword3(password: String): Boolean = password.length >= 8
    private fun validPassword4(password: String, cpassword: String): Boolean = password == cpassword
    private fun validPhone(phone: String): Boolean = phone.length == 10
    private fun checkInput(name: String, user: String, password: String, phone: String): Boolean {
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(user) || TextUtils.isEmpty(password) || phone.isEmpty())
    }

    private fun toast(msg: String) {
        Toast.makeText(this@RegisterView, msg, Toast.LENGTH_LONG).show()
    }
}
