package com.example.babymonitorapp

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.CheckBox
import com.example.babymonitorapp.database.Task

class TaskAdapter(private var tasks: MutableList<Task>, private val onTaskCheckedChange: (Task) -> Unit): RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    private var onItemLongClick: ((Task) -> Unit)? = null

    fun setOnItemLongClickListener(listener: (Task) -> Unit) {
        onItemLongClick = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)
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
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(item)
            true
        }
        holder.checkBox.setOnLongClickListener {
            onItemLongClick?.invoke(item)
            true
        }

    }

    override fun getItemCount(): Int {
        return tasks.size
    }
    fun setTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}