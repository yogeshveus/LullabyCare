package com.example.babymonitorapp

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
import androidx.appcompat.app.AlertDialog
import android.widget.ImageButton

class MainActivity3 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var adapter: CardAdapter
    private lateinit var dbHelper: ReminderDatabaseHelper
    private lateinit var notificationButton: ImageButton
    private lateinit var badgeTextView: TextView

    private val cardItems = listOf(
        CardItem("Daily tasks"),
        CardItem("Calendar"),
        CardItem("Nutrition Tracker")
    )

    private val dailyTasksLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val completedTasks = result.data?.getIntExtra("completed_tasks", 0) ?: 0
            adapter.updateTasks(completedTasks)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        val searchButton: ImageButton = findViewById(R.id.imageButton7)
        val searchEditText: EditText = findViewById(R.id.editTextText)
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

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
                R.id.home -> true
                R.id.baby -> true
                R.id.community -> {
                    startActivity(Intent(this, Community::class.java))
                    true
                }
                R.id.settings -> {
                    startActivity(Intent(this, NutritionTrackerActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun updateNotificationBadge() {
        val count = dbHelper.getAllReminders().size
        if (count > 0) {
            badgeTextView.text = count.toString()
            badgeTextView.visibility = View.VISIBLE
        } else {
            badgeTextView.visibility = View.GONE
        }
    }

    private fun showAllNotifications() {
        val allReminders = dbHelper.getAllReminders()
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
                "📅 $date\n🔔 $text"
            }
            AlertDialog.Builder(this)
                .setTitle("Notifications")
                .setMessage(reminderTexts)
                .setPositiveButton("OK", null)
                .show()
        }
        updateNotificationBadge()  // Refresh the badge after showing
    }
}
