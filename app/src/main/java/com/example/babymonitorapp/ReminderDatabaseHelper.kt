package com.example.babymonitorapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ReminderDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "reminders.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "reminders"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TEXT = "text"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIME = "time"

    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TEXT TEXT,
                $COLUMN_DATE INTEGER,
                $COLUMN_TIME INTEGER
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertReminder(reminder: Reminder) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TEXT, reminder.text)
            put(COLUMN_DATE, reminder.date)
            put(COLUMN_TIME, reminder.time)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getRemindersByDate(date: Long): List<Reminder> {
        val reminders = mutableListOf<Reminder>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_TEXT, COLUMN_DATE, COLUMN_TIME),
            "$COLUMN_DATE = ?",
            arrayOf(date.toString()),
            null, null, null
        )
        with(cursor) {
            while (moveToNext()) {
                val text = getString(getColumnIndexOrThrow(COLUMN_TEXT))
                val reminderDate = getLong(getColumnIndexOrThrow(COLUMN_DATE))
                val time = getLong(getColumnIndexOrThrow(COLUMN_TIME))
                reminders.add(Reminder(text, reminderDate, time))
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
            "$COLUMN_TEXT = ? AND $COLUMN_DATE = ? AND $COLUMN_TIME = ?",
            arrayOf(reminder.text, reminder.date.toString(), reminder.time.toString())
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
            "$COLUMN_TEXT = ? AND $COLUMN_DATE = ? AND $COLUMN_TIME = ?",
            arrayOf(oldReminder.text, oldReminder.date.toString(), oldReminder.time.toString())
        )
        db.close()
        return result > 0
    }




}
