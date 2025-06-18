package com.example.babymonitorapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.babymonitorapp.database.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class SettingsView : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_view)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val accountDetails = findViewById<TextView>(R.id.accountDetailsHeader)
        val editAccount = findViewById<TextView>(R.id.editAccountButton)
        val changeTheme = findViewById<TextView>(R.id.changeThemeButton)
        val logout = findViewById<TextView>(R.id.logoutButton)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        accountDetails.setOnClickListener {
            val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val userId = sharedPref.getInt("loggedInUserId", -1)

            lifecycleScope.launch {
                val user = userViewModel.getUserbyUserId(userId)
                if (user != null) {
                    AlertDialog.Builder(this@SettingsView)
                        .setTitle("Account Details")
                        .setMessage("Username: ${user.user}\nName: ${user.name}\nPhone Number: ${user.phone}")
                        .setPositiveButton("OK", null)
                        .show()
                } else {
                    Toast.makeText(this@SettingsView, "User not found.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        editAccount.setOnClickListener {
            showConfirmPasswordDialog()
        }

        changeTheme.setOnClickListener {
            Toast.makeText(this, "Theme change feature not implemented yet.", Toast.LENGTH_SHORT).show()
        }

        logout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit { clear() }
                    Toast.makeText(this, "Successfully Logged Out", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginView::class.java))
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> startActivity(Intent(this, MainActivity3::class.java))
                R.id.baby -> startActivity(Intent(this, YoutubeActivity::class.java))
                R.id.community -> startActivity(Intent(this, Community::class.java))
                R.id.settings -> {} // Already here
            }
            true
        }
    }

    private fun showConfirmPasswordDialog() {
        val passwordPromptView = layoutInflater.inflate(R.layout.dialog_confirm_password, null)
        val currentPasswordEdit = passwordPromptView.findViewById<EditText>(R.id.currentPassword)

        AlertDialog.Builder(this)
            .setTitle("Confirm Identity")
            .setMessage("Enter your current password to proceed")
            .setView(passwordPromptView)
            .setPositiveButton("Confirm", null)
            .setNegativeButton("Cancel", null)
            .create().apply {
                setOnShowListener {
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val enteredPassword = currentPasswordEdit.text.toString().trim()
                        val userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getInt("loggedInUserId", -1)

                        lifecycleScope.launch {
                            val user = userViewModel.getUserbyUserId(userId)
                            if (user?.password == enteredPassword) {
                                dismiss()
                                showEditAccountDialog(user)
                            } else {
                                Toast.makeText(this@SettingsView, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }.show()
    }

    private fun showEditAccountDialog(user: com.example.babymonitorapp.database.User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_account, null)

        val nameEdit = dialogView.findViewById<EditText>(R.id.editName)
        val usernameEdit = dialogView.findViewById<EditText>(R.id.editUsername)
        val passwordEdit = dialogView.findViewById<EditText>(R.id.editPassword)
        val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)

        nameEdit.setText(user.name)
        usernameEdit.setText(user.user)
        passwordEdit.setText(user.password)
        phoneEdit.setText(user.phone)

        val updateDialog = AlertDialog.Builder(this)
            .setTitle("Edit Account")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Update", null)
            .create()

        updateDialog.setOnShowListener {
            updateDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
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
        updateDialog.show()
    }
}
