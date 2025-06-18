package com.example.babymonitorapp

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.util.Log
import android.widget.Toast

class ReminderReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val text = intent.getStringExtra("text") ?: "Reminder"

        val builder = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.mipmap.ic_launcher) // Make sure this exists
            .setContentTitle("Baby Care Reminder")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())

        Log.d("ReminderReceiver", "Broadcast received with text: $text")
        Toast.makeText(context, "Reminder received: $text", Toast.LENGTH_SHORT).show()

    }
}
