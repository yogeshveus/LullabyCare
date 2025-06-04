package com.example.babymonitorapp

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color
import android.widget.CheckBox

class TaskAdapter(private var tasks: MutableList<Tasks>): RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskAdapter.ViewHolder {
        var tv = CheckBox(parent.context).apply{
            textSize = 18f
            setTextColor(Color.parseColor("#2F3D7E"))
            setPadding(32, 24, 32, 24)
        }
        return TaskAdapter.ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: TaskAdapter.ViewHolder, position: Int) {
        var item = tasks[position]
        holder.checkBox.text = item.label
        holder.checkBox.isChecked = item.isChecked

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = item.isChecked

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView as CheckBox
    }
    fun addTask(task: Tasks) {
        tasks.add(task)
        notifyItemInserted(tasks.size - 1)
    }
}