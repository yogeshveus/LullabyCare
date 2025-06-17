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



class SettingsView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_view)

        val accountDetails = findViewById<TextView>(R.id.accountDetailsHeader)
        val editAccount = findViewById<TextView>(R.id.editAccountButton)
        val changeTheme = findViewById<TextView>(R.id.changeThemeButton)
        val logout = findViewById<TextView>(R.id.logoutButton)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        accountDetails.setOnClickListener {
            // TODO: Display account details pls
        }
        editAccount.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_edit_account, null)

            val nameEdit = dialogView.findViewById<EditText>(R.id.editName)
            val usernameEdit = dialogView.findViewById<EditText>(R.id.editUsername)
            val passwordEdit = dialogView.findViewById<EditText>(R.id.editPassword)
            val phoneEdit = dialogView.findViewById<EditText>(R.id.editPhone)

            AlertDialog.Builder(this)
                .setTitle("Edit Account")
                .setView(dialogView)
                .setPositiveButton("Update") { _, _ ->
                    val newName = nameEdit.text.toString().trim()
                    val newUsername = usernameEdit.text.toString().trim()
                    val newPassword = passwordEdit.text.toString().trim()
                    val newPhone = phoneEdit.text.toString().trim()

                    // TODO: Save updates pls
                    // Example:
                    // userViewModel.updateUser(currentUserId, newName, newUsername, newPassword, newPhone)

                    AlertDialog.Builder(this)
                        .setMessage("Account updated successfully.")
                        .setPositiveButton("OK", null)
                        .show()
                }
                .setNegativeButton("Cancel", null)
                .show()
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
                    // TODO: Do logout pls

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
