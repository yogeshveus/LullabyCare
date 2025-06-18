package com.example.babymonitorapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast
import android.view.LayoutInflater
import android.widget.EditText
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.babymonitorapp.SettingsView
import com.example.babymonitorapp.database.User
import com.example.babymonitorapp.database.UserViewModel
import kotlinx.coroutines.launch


class SettingsView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_view)
        var userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val accountDetails = findViewById<TextView>(R.id.accountDetailsHeader)
        val editAccount = findViewById<TextView>(R.id.editAccountButton)
        val changeTheme = findViewById<TextView>(R.id.changeThemeButton)
        val logout = findViewById<TextView>(R.id.logoutButton)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        accountDetails.setOnClickListener {
            // TODO: Display account details pls
        }
        editAccount.setOnClickListener {
            val passwordPromptView = layoutInflater.inflate(R.layout.dialog_confirm_password, null)
            val currentPasswordEdit = passwordPromptView.findViewById<EditText>(R.id.currentPassword)

            AlertDialog.Builder(this)
                .setTitle("Confirm Identity")
                .setMessage("Enter your current password to proceed")
                .setView(passwordPromptView)
                .setPositiveButton("Confirm") { _, _ -> }
                .setNegativeButton("Cancel", null)
                .create().apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            val enteredPassword = currentPasswordEdit.text.toString().trim()
                            val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            val userId = sharedPref.getInt("loggedInUserId", -1)

                            lifecycleScope.launch {
                                val user = userViewModel.getUserbyUserId(userId)
                                val actualPassword = user?.password
                                if (enteredPassword == actualPassword) {
                                    dismiss()

                                    // Show Edit Dialog
                                    val dialogView = layoutInflater.inflate(R.layout.dialog_edit_account, null)
                                    val nameEdit = dialogView.findViewById<EditText>(R.id.editName)
                                    val usernameEdit = dialogView.findViewById<EditText>(R.id.editUsername)
                                    val passwordEdit = dialogView.findViewById<EditText>(R.id.editPassword)
                                    val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)

                                    nameEdit.setText(user.name)
                                    usernameEdit.setText(user.user)
                                    passwordEdit.setText(user.password)
                                    phoneEdit.setText(user.phone)

                                    val updateDialog = AlertDialog.Builder(this@SettingsView)
                                        .setTitle("Edit Account")
                                        .setView(dialogView)
                                        .setNegativeButton("Cancel", null)
                                        .create()

                                    updateDialog.setOnShowListener {
                                        updateDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                                            val newName = nameEdit.text.toString().trim()
                                            val newUsername = usernameEdit.text.toString().trim()
                                            val newPassword = passwordEdit.text.toString().trim()
                                            val newPhone = phoneEdit.text.toString().trim()

                                            lifecycleScope.launch {
                                                val existingUser = userViewModel.validUser(newUsername)

                                                when {
                                                    existingUser != null && newUsername != user.user -> {
                                                        Toast.makeText(this@SettingsView, "Username already exists.", Toast.LENGTH_LONG).show()
                                                    }
                                                    !Validation.validPassword1(newPassword) -> {
                                                        Toast.makeText(this@SettingsView, "Password must contain at least 1 special character", Toast.LENGTH_LONG).show()
                                                    }
                                                    !Validation.validPassword2(newPassword) -> {
                                                        Toast.makeText(this@SettingsView, "Password must contain at least 1 uppercase character", Toast.LENGTH_LONG).show()
                                                    }
                                                    !Validation.validPassword3(newPassword) -> {
                                                        Toast.makeText(this@SettingsView, "Password size must be at least 8", Toast.LENGTH_LONG).show()
                                                    }
                                                    !Validation.validPhone(newPhone) -> {
                                                        Toast.makeText(this@SettingsView, "Please provide a valid phone number.", Toast.LENGTH_LONG).show()
                                                    }
                                                    else -> {
                                                        user.name = newName
                                                        user.user = newUsername
                                                        user.password = newPassword
                                                        user.phone = newPhone

                                                        userViewModel.updateUser(user)
                                                        updateDialog.dismiss()

                                                        AlertDialog.Builder(this@SettingsView)
                                                            .setMessage("Account updated successfully.")
                                                            .setPositiveButton("OK", null)
                                                            .show()
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    updateDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Update") { _, _ -> }
                                    updateDialog.show()
                                } else {
                                    Toast.makeText(this@SettingsView, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }.show()
        }



        // Change Theme Click Handler (replace with your own theme logic)
        changeTheme.setOnClickListener {
            // TODO: Change theme pls
        }

        // Logout Click Handler
        logout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    sharedPref.edit { clear() }
                    Toast.makeText(this, "Successfully Logged Out", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginView::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity3::class.java))
                    true
                }
                R.id.baby -> {
                    startActivity(Intent(this, YoutubeActivity::class.java))
                    true
                }
                R.id.community -> {
                    startActivity(Intent(this, Community::class.java))
                    true
                }
                R.id.settings -> {

                    true
                }
                else -> false
            }
        }
    }
}
