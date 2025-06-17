package com.example.babymonitorapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.ALARM_SERVICE
import java.text.SimpleDateFormat
import java.util.Locale



class ReminderDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "reminders.db"
        private const val DATABASE_VERSION = 5
        private const val TABLE_NAME = "reminders"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TEXT = "text"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIME = "time"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createRemindersTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TEXT TEXT,
                $COLUMN_DATE INTEGER,
                $COLUMN_TIME INTEGER,
                userId INTEGER
            )
        """.trimIndent()
        db.execSQL(createRemindersTable)

        val createNotificationsTable = """
            CREATE TABLE notifications (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                message TEXT,
                timestamp INTEGER
            )
        """.trimIndent()
        db.execSQL(createNotificationsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS notifications")
        onCreate(db)
    }

    fun insertReminder(reminder: Reminder) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TEXT, reminder.text)
            put(COLUMN_DATE, reminder.date)
            put(COLUMN_TIME, reminder.time)
            put("userId", reminder.userId)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getRemindersByDate(date: Long, userId: Int): List<Reminder> {
        val reminders = mutableListOf<Reminder>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_TEXT, COLUMN_DATE, COLUMN_TIME, "userId"),
            "$COLUMN_DATE = ? AND userId = ?",
            arrayOf(date.toString(), userId.toString()),
            null, null, null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val text = getString(getColumnIndexOrThrow(COLUMN_TEXT))
                val reminderDate = getLong(getColumnIndexOrThrow(COLUMN_DATE))
                val time = getLong(getColumnIndexOrThrow(COLUMN_TIME))
                val fetchedUserId = getInt(getColumnIndexOrThrow("userId"))

                reminders.add(Reminder(text, reminderDate, time, fetchedUserId, id))
            }
            close()
        }
        db.close()
        return reminders
    }


    fun getAllReminders(userId: Int): List<Reminder> {
        val reminders = mutableListOf<Reminder>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_TEXT, COLUMN_DATE, COLUMN_TIME, "userId"),
            "userId = ?",
            arrayOf(userId.toString()),
            null, null,
            "$COLUMN_DATE DESC, $COLUMN_TIME DESC"
        )
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_ID))
                val text = getString(getColumnIndexOrThrow(COLUMN_TEXT))
                val date = getLong(getColumnIndexOrThrow(COLUMN_DATE))
                val time = getLong(getColumnIndexOrThrow(COLUMN_TIME))
                val uId = getInt(getColumnIndexOrThrow("userId"))
                reminders.add(Reminder(text, date, time, uId, id))
            }
            close()
        }
        db.close()
        return reminders
    }

    fun deleteReminder(reminder: Reminder): Boolean {
        val db = writableDatabase
        val result = db.delete(
            TABLE_NAME,
            "$COLUMN_TEXT = ? AND $COLUMN_DATE = ? AND $COLUMN_TIME = ? AND userId = ?",
            arrayOf(
                reminder.text,
                reminder.date.toString(),
                reminder.time.toString(),
                reminder.userId.toString()
            )
        )
        db.close()
        return result > 0
    }


    fun updateReminder(oldReminder: Reminder, newText: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TEXT, newText)
        }
        val result = db.update(
            TABLE_NAME,
            values,
            "$COLUMN_TEXT = ? AND $COLUMN_DATE = ? AND $COLUMN_TIME = ? AND userId = ?",
            arrayOf(
                oldReminder.text,
                oldReminder.date.toString(),
                oldReminder.time.toString(),
                oldReminder.userId.toString()
            )
        )
        db.close()
        return result > 0
    }


    fun insertNotificationMessage(message: String) {
        val formattedMessage = formatDateInText(message)

        val db = writableDatabase
        val values = ContentValues().apply {
            put("message", formattedMessage)
            put("timestamp", System.currentTimeMillis())
        }
        db.insert("notifications", null, values)
        db.close()
    }

    fun getAllNotifications(): List<String> {
        val notifications = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT message FROM notifications ORDER BY timestamp DESC", null)
        while (cursor.moveToNext()) {
            notifications.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return notifications
    }
    fun formatDateInText(message: String): String {
        val regex = Regex("""\b\d{8}\b""")  // matches 8-digit date
        return regex.replace(message) { matchResult ->
            val dateStr = matchResult.value
            val year = dateStr.substring(0, 4).toInt()
            val month = dateStr.substring(4, 6).toInt()
            val day = dateStr.substring(6, 8).toInt()
            "%02d/%02d/%04d".format(day, month, year)
        }
    }

    fun deleteAllReminders(userId: Int): Boolean {
        val db = this.writableDatabase
        val rows = db.delete("reminders", "userId = ?", arrayOf(userId.toString()))
        db.close()
        return rows > 0
    }

}
