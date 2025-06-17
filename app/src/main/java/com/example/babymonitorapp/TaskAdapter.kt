package com.example.babymonitorapp

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color
import android.widget.CheckBox
import com.example.babymonitorapp.database.Task

class TaskAdapter(private var tasks: MutableList<Task>, private val onTaskCheckedChange: (Task) -> Unit): RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        var tv = CheckBox(parent.context).apply{
            textSize = 18f
            setTextColor(Color.parseColor("#2F3D7E"))
            setPadding(32, 24, 32, 24)
        }
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = tasks[position]
        holder.checkBox.text = item.title
        holder.checkBox.isChecked = item.isCompleted

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = item.isCompleted

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isCompleted = isChecked
            onTaskCheckedChange(item)
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView as CheckBox
    }
    fun setTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}