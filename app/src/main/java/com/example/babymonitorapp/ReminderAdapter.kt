package com.example.babymonitorapp

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color

class ReminderAdapter(private var reminders:List<Reminder>): RecyclerView.Adapter<ReminderAdapter.ViewHolder>(){
    fun updateReminders(newReminders: List<Reminder>) {
        reminders = newReminders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
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

    override fun onBindViewHolder(holder: ReminderAdapter.ViewHolder, position: Int) {
        (holder.itemView as TextView).text = reminders[position].text
    }

    override fun getItemCount(): Int {
        return reminders.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}