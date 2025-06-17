package com.example.babymonitorapp

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReminderAdapter(private var reminders: List<Reminder>) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {

    private var onItemLongClick: ((Reminder) -> Unit)? = null

    fun setOnItemLongClickListener(listener: (Reminder) -> Unit) {
        onItemLongClick = listener
    }

    fun updateReminders(newReminders: List<Reminder>) {
        reminders = newReminders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val tv = TextView(parent.context).apply {
            textSize = 18f
            setTextColor(Color.parseColor("#2F3D7E"))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(32, 24, 32, 24)
        }
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = reminders[position]
        val rawDate = reminder.date.toString()

        val formattedDate = if (rawDate.length == 8) {
            val year = rawDate.substring(0, 4)
            val month = rawDate.substring(4, 6)
            val day = rawDate.substring(6, 8)
            "$day/$month/$year"
        } else {
            rawDate
        }

        (holder.itemView as TextView).text = "${reminder.text}\n\n📅 $formattedDate"

        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(reminder)
            true
        }
    }


    override fun getItemCount(): Int = reminders.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
