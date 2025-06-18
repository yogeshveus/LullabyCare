package com.example.babymonitorapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate // Make sure this is imported
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast
import android.view.LayoutInflater
import android.widget.EditText
import androidx.core.content.edit // For SharedPreferences
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
// Remove redundant import if SettingsView is in the same package
// import com.example.babymonitorapp.SettingsView
import com.example.babymonitorapp.database.User
import com.example.babymonitorapp.database.UserViewModel
import kotlinx.coroutines.launch


class SettingsView : AppCompatActivity() {

    // Define preference keys for theme persistence
    companion object {
        const val THEME_PREFS_NAME = "AppThemePrefs"
        const val KEY_APP_THEME = "selected_app_theme"
    }

    private lateinit var userViewModel: UserViewModel // Made UserViewModel a class member
    private lateinit var bottomNav: BottomNavigationView // Made BottomNav a class member


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // It's best to apply the theme in your Application class or a BaseActivity's onCreate
        // to avoid a flicker. For example:
        // applyThemeFromPreferencesOnLaunch()

        setContentView(R.layout.activity_settings_view)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val accountDetails = findViewById<TextView>(R.id.accountDetailsHeader)
        val editAccount = findViewById<TextView>(R.id.editAccountButton)
        val changeTheme = findViewById<TextView>(R.id.changeThemeButton)
        val logout = findViewById<TextView>(R.id.logoutButton)
        bottomNav = findViewById(R.id.bottomNavigationView)


        accountDetails.setOnClickListener {
            // Your existing accountDetails logic - for brevity, I'll assume it's like this:
            val sharedPrefLogin = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val userId = sharedPrefLogin.getInt("loggedInUserId", -1)
            if (userId != -1) {
                lifecycleScope.launch {
                    val user = userViewModel.getUserbyUserId(userId)
                    user?.let {
                        AlertDialog.Builder(this@SettingsView)
                            .setTitle("Account Details")
                            .setMessage("Name: ${it.name}\nUsername: ${it.user}\nPhone: ${it.phone}")
                            .setPositiveButton("OK", null)
                            .show()
                    } ?: Toast.makeText(this@SettingsView, "Could not load user details.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@SettingsView, "User not logged in.", Toast.LENGTH_SHORT).show()
            }
        }

        editAccount.setOnClickListener {
            val passwordPromptView = layoutInflater.inflate(R.layout.dialog_confirm_password, null)
            val currentPasswordEdit = passwordPromptView.findViewById<EditText>(R.id.currentPassword) // Ensure ID exists

            AlertDialog.Builder(this)
                .setTitle("Confirm Identity")
                .setMessage("Enter your current password to proceed")
                .setView(passwordPromptView)
                .setPositiveButton("Confirm") { _, _ -> } // Will be overridden
                .setNegativeButton("Cancel", null)
                .create().apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            val enteredPassword = currentPasswordEdit.text.toString().trim()
                            val sharedPrefLogin = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                            val userId = sharedPrefLogin.getInt("loggedInUserId", -1)

                            if (userId == -1) {
                                Toast.makeText(this@SettingsView, "User not logged in.", Toast.LENGTH_SHORT).show()
                                dismiss()
                                return@setOnClickListener
                            }

                            lifecycleScope.launch {
                                val user = userViewModel.getUserbyUserId(userId)
                                if (enteredPassword == user?.password && user != null) {
                                    dismiss() // Dismiss password dialog
                                    showEditAccountDialog(user) // Call helper
                                } else {
                                    Toast.makeText(this@SettingsView, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }.show()
        }

        // --- Logic for changing themes ---
        changeTheme.setOnClickListener {
            showThemeSelectionDialog()
        }
        // --- End of theme change logic ---

        logout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    val sharedPrefLogin = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    // Clear only login specific data if theme prefs are separate
                    sharedPrefLogin.edit { remove("loggedInUserId") } // Or clear() if MyAppPrefs is only for login

                    Toast.makeText(this, "Successfully Logged Out", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginView::class.java)
                    //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        bottomNav.setOnItemSelectedListener { item ->
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
                    true // Already in settings
                }
                else -> false
            }
        }
    }

    // Helper function to show the edit account dialog (from original code)
    private fun showEditAccountDialog(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_account, null)
        val nameEdit = dialogView.findViewById<EditText>(R.id.editName)
        val usernameEdit = dialogView.findViewById<EditText>(R.id.editUsername)
        val passwordEdit = dialogView.findViewById<EditText>(R.id.editPassword)
        val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)

        nameEdit.setText(user.name)
        usernameEdit.setText(user.user)
        passwordEdit.setText(user.password) // Pre-fill with current password
        phoneEdit.setText(user.phone)

        val updateDialog = AlertDialog.Builder(this@SettingsView)
            .setTitle("Edit Account")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Update", null) // Will be overridden
            .create()

        updateDialog.setOnShowListener {
            updateDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
                val newName = nameEdit.text.toString().trim()
                val newUsername = usernameEdit.text.toString().trim()
                val newPassword = passwordEdit.text.toString().trim()
                val newPhone = phoneEdit.text.toString().trim()

                lifecycleScope.launch {
                    val existingUserWithNewUsername = if (newUsername != user.user) userViewModel.validUser(newUsername) else null

                    when {
                        existingUserWithNewUsername != null -> {
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

    // --- Functions for Theme Switching ---
    private fun showThemeSelectionDialog() {
        val themes = arrayOf("Light", "Dark", "System Default")
        val sharedPreferences = getSharedPreferences(THEME_PREFS_NAME, MODE_PRIVATE)
        // Default to System Default if no preference is saved
        val currentThemeMode = sharedPreferences.getInt(KEY_APP_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        val checkedItem = when (currentThemeMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> 0 // Light
            AppCompatDelegate.MODE_NIGHT_YES -> 1 // Dark
            else -> 2 // System Default
        }

        AlertDialog.Builder(this)
            .setTitle("Choose Theme")
            .setSingleChoiceItems(themes, checkedItem) { dialog, which ->
                val selectedMode = when (which) {
                    0 -> AppCompatDelegate.MODE_NIGHT_NO  // Light
                    1 -> AppCompatDelegate.MODE_NIGHT_YES // Dark
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // System Default
                }
                applyAndSaveTheme(selectedMode)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun applyAndSaveTheme(themeMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode) // Applies the theme for all activities

        // Save the selected theme to SharedPreferences
        val sharedPreferences = getSharedPreferences(THEME_PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit {
            putInt(KEY_APP_THEME, themeMode)
            apply()
        }
        // Optional: recreate() this activity if you want changes to apply to SettingsView immediately
        // without navigating away and back.
        // recreate()
    }
    // --- End of Theme Switching Functions ---


    override fun onResume() {
        super.onResume()
        // Ensure correct item is selected if user navigates back to settings
        bottomNav.selectedItemId = R.id.settings
    }
}
