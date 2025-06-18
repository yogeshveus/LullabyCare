package com.example.babymonitorapp

import android.text.TextUtils

object Validation {
    fun validPassword1(password: String): Boolean {
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        return (hasSpecialChar)
    }
    fun validPassword2(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        return (hasUppercase)
    }
    fun validPassword3(password: String): Boolean {
        return (password.length>=8)
    }
    fun validPassword4(password: String, cpassword: String): Boolean {
        return (password == cpassword)
    }
    fun validPhone(phone: String): Boolean {
        return (phone.length==10)
    }

    fun checkInput(name: String,user: String,password: String,phone: String): Boolean{
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(user) || TextUtils.isEmpty(password) || phone.isEmpty())
    }
}