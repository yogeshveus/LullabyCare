package com.example.babymonitorapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.babymonitorapp.database.TaskViewModel

import com.example.babymonitorapp.database.User
import com.example.babymonitorapp.database.UserViewModel
import kotlinx.coroutines.launch
import android.app.AlarmManager
import android.app.PendingIntent
import android.widget.Toast
import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize


class MainActivity3 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var adapter: CardAdapter
    private lateinit var dbHelper: ReminderDatabaseHelper
    private lateinit var notificationButton: ImageButton
    private lateinit var badgeTextView: TextView
    private var currentUserId: Int = -1
    private lateinit var taskViewModel: TaskViewModel
    private var totalTasks = 0
    private var completedTasks = 0
    private lateinit var greetings: TextView
    private lateinit var userViewModel: UserViewModel
    private var userId: Int = -1
    private val cardItems = listOf(
        CardItem("Daily tasks"),
        CardItem("Calendar"),
        CardItem("Nutrition Tracker"),
        CardItem("Lullaby player")
    )

    private val dailyTasksLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val completedTasks = result.data?.getIntExtra("completed_tasks", 0) ?: 0
            adapter.updateTasks(completedTasks, totalTasks)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        currentUserId = sharedPref.getInt("loggedInUserId", -1)

        super.onCreate(savedInstanceState)
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        setContentView(R.layout.activity_main3)
        taskViewModel.getTotalTasks(currentUserId).observe(this) { total ->
            totalTasks = total
            updateProgressIfReady()
        }

        taskViewModel.getCompletedTaskCount(currentUserId).observe(this) { completed ->
            completedTasks = completed
            updateProgressIfReady()
        }
        val searchButton: ImageButton = findViewById(R.id.imageButton7)
        val searchEditText: EditText = findViewById(R.id.editTextText)
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })


        greetings = findViewById<TextView>(R.id.textView)
        var user: User?
        lifecycleScope.launch {
            user = userViewModel.getUserbyUserId(currentUserId)
            greetings.setText("Hello ${user?.name}!")
        }



        searchButton.setOnClickListener {
            val input = EditText(this)
            input.hint = "Enter search term"

            AlertDialog.Builder(this)
                .setTitle("Search")
                .setView(input)
                .setPositiveButton("Search") { dialog, _ ->
                    val query = input.text.toString().trim()
                    if (query.isNotEmpty()) {
                        adapter.filter(query)
                    } else {
                        adapter.filter("") // empty shows everything
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                .setNeutralButton("Reset") { dialog, _ ->
                    adapter.filter("") // show all items again
                    dialog.dismiss()
                }
                .show()
        }



        dbHelper = ReminderDatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerView)
        bottomNav = findViewById(R.id.bottomNavigationView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CardAdapter(cardItems) { item ->
            when (item.title) {

                "Lullaby player" -> startActivity(Intent(this, YoutubeActivity::class.java))
                "Daily tasks" -> dailyTasksLauncher.launch(Intent(this, DailyTasksView::class.java))
                "Calendar" -> startActivity(Intent(this, CalendarView::class.java))
                "Nutrition Tracker" -> startActivity(Intent(this, NutritionTrackerActivity::class.java))
            }
        }
        recyclerView.adapter = adapter

        notificationButton = findViewById(R.id.notificationButton)
        badgeTextView = findViewById(R.id.notification_badge)

        notificationButton.setOnClickListener {
            showAllNotifications()
        }

        updateNotificationBadge()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {

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
                    startActivity(Intent(this, SettingsView::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun updateProgressIfReady() {
        if (totalTasks > 0) {
            adapter.updateTasks(completedTasks, totalTasks)
        }
    }

    private fun updateNotificationBadge() {
        val count = dbHelper.getAllReminders(currentUserId).size
        if (count > 0) {
            badgeTextView.text = count.toString()
            badgeTextView.visibility = View.VISIBLE
        } else {
            badgeTextView.visibility = View.GONE
        }
    }

    private fun showAllNotifications() {
        val allReminders = dbHelper.getAllReminders(currentUserId)
        if (allReminders.isEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Notifications")
                .setMessage("No notifications yet.")
                .setPositiveButton("OK", null)
                .show()
        } else {
            val reminderTexts = allReminders.joinToString("\n\n") {
                val date = it.date
                val text = it.text
                val formattedDate = formatReminderDate(date)
                "📅 $formattedDate\n🔔 $text"
            }
            AlertDialog.Builder(this)
                .setTitle("Notifications")
                .setMessage(reminderTexts)
                .setPositiveButton("OK", null)
                .setNegativeButton("Clear All") { _, _ ->
                    dbHelper.deleteAllReminders(currentUserId)
                }
                .show()
        }
        updateNotificationBadge()
    }
    private fun formatReminderDate(dateLong: Long): String {
        val dateStr = dateLong.toString()
        return if (dateStr.length == 8) {
            val year = dateStr.substring(0, 4)
            val month = dateStr.substring(4, 6)
            val day = dateStr.substring(6, 8)
            "$day/$month/$year"
        } else {
            dateStr // fallback, if something's off
        }
    }


}
