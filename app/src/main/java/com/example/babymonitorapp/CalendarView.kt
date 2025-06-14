package com.example.babymonitorapp

import android.content.BroadcastReceiver
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Build
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar
import kotlin.random.Random
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.babymonitorapp.R

class CalendarView : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var toolbar: Toolbar
    private lateinit var dbHelper: ReminderDatabaseHelper

    private lateinit var adapter: ReminderAdapter
    private var selectedDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel", // channel ID
                "Reminders", // name
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for reminder notifications"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }




        calendarView = findViewById(R.id.calendarView)
        recyclerView = findViewById(R.id.reminderRecyclerView)
        addButton = findViewById(R.id.addReminderButton)
        toolbar = findViewById(R.id.toolbar)
        dbHelper = ReminderDatabaseHelper(this)

        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity3::class.java))
        }

        adapter = ReminderAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider)?.let {
            dividerItemDecoration.setDrawable(it)
            recyclerView.addItemDecoration(dividerItemDecoration)
        }

        selectedDate = getDateWithoutTime(calendarView.date)
        loadRemindersForDate(selectedDate)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = ymdToLong(year, month + 1, dayOfMonth)
            loadRemindersForDate(selectedDate)
        }

        addButton.setOnClickListener { showAddReminderDialog() }

        adapter.setOnItemLongClickListener { reminder ->
            showReminderOptions(reminder)
        }
    }

    private fun loadRemindersForDate(date: Long) {
        val remindersForDate = dbHelper.getRemindersByDate(date)
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

            AlertDialog.Builder(this)
                .setTitle("Add Reminder")
                .setView(input)
                .setPositiveButton("Add") { _, _ ->
                    val text = input.text.toString().trim()
                    if (text.isNotEmpty()) {
                        val reminder = Reminder(text, selectedDate, calendar.timeInMillis)
                        dbHelper.insertReminder(reminder)
                        scheduleReminderNotification(reminder)
                        loadRemindersForDate(selectedDate)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }


    private fun showReminderOptions(reminder: Reminder) {
        val options = arrayOf("Edit", "Delete")

        AlertDialog.Builder(this)
            .setTitle("Manage Reminder")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(reminder)
                    1 -> {
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
        val input = EditText(this).apply {
            setText(reminder.text)
        }

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
            this,
            reminder.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminder.time,
            pendingIntent
        )
    }

}
