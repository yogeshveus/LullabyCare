package com.example.babymonitorapp

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import android.widget.CalendarView
import com.google.android.material.bottomnavigation.BottomNavigationView

class CalendarView : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: com.google.android.material.floatingactionbutton.FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var dbHelper: ReminderDatabaseHelper
    private lateinit var adapter: ReminderAdapter
    private var bottomNav: BottomNavigationView? = null
    private var selectedDate: Long = 0L
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        currentUserId = sharedPref.getInt("loggedInUserId", -1)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_view)

        requestNotificationPermission()
        createNotificationChannel()

        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.reminderRecyclerView)
        addButton = findViewById(R.id.addReminderButton)
        toolbar = findViewById(R.id.toolbar)
        dbHelper = ReminderDatabaseHelper(this)

        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity3::class.java))
            finish()
        }

        adapter = ReminderAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        addRecyclerViewDivider()

        selectedDate = getDateWithoutTime(calendarView.date)
        loadRemindersForDate(selectedDate)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = ymdToLong(year, month + 1, dayOfMonth)
            loadRemindersForDate(selectedDate)
        }

        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
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
                    startActivity(Intent(this, SettingsView::class.java))
                    true
                }
                else -> false
            }
        }

        addButton.setOnClickListener { showAddReminderDialog() }
        adapter.setOnItemLongClickListener { reminder -> showReminderOptions(reminder) }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel", "Reminders", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Channel for reminder notifications" }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun addRecyclerViewDivider() {
        val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider)?.let {
            divider.setDrawable(it)
            recyclerView.addItemDecoration(divider)
        }
    }

    private fun loadRemindersForDate(date: Long) {
        val remindersForDate = dbHelper.getRemindersByDate(date, currentUserId)
        adapter.updateReminders(remindersForDate)
    }

    private fun showAddReminderDialog() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
        }

        val calendar = Calendar.getInstance()

        TimePickerDialog(this, { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                Toast.makeText(this, "Please choose a future time", Toast.LENGTH_SHORT).show()
                return@TimePickerDialog
            }

            AlertDialog.Builder(this)
                .setTitle("Add Reminder")
                .setView(input)
                .setPositiveButton("Add") { _, _ ->
                    val text = input.text.toString().trim()
                    if (text.isNotEmpty()) {
                        val reminder = Reminder(text, selectedDate, calendar.timeInMillis, currentUserId)
                        dbHelper.insertReminder(reminder)
                        scheduleReminderNotification(reminder)
                        loadRemindersForDate(selectedDate)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()

        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
    }

    private fun showReminderOptions(reminder: Reminder) {
        val options = arrayOf("Edit", "Delete")

        AlertDialog.Builder(this)
            .setTitle("Manage Reminder")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(reminder)
                    1 -> {
                        cancelReminderNotification(reminder)
                        if (dbHelper.deleteReminder(reminder)) {
                            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                            loadRemindersForDate(selectedDate)
                        }
                    }
                }
            }
            .show()
    }

    private fun showEditDialog(reminder: Reminder) {
        val input = EditText(this).apply { setText(reminder.text) }

        AlertDialog.Builder(this)
            .setTitle("Edit Reminder")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->
                val newText = input.text.toString().trim()
                if (newText.isNotEmpty()) {
                    if (dbHelper.updateReminder(reminder, newText)) {
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                        loadRemindersForDate(selectedDate)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun ymdToLong(year: Int, month: Int, day: Int): Long {
        return year * 10000L + month * 100L + day
    }

    private fun getDateWithoutTime(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return ymdToLong(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
    }

    private fun scheduleReminderNotification(reminder: Reminder) {
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("text", reminder.text)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, reminder.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder.time, pendingIntent)
    }

    private fun cancelReminderNotification(reminder: Reminder) {
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, reminder.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

}
