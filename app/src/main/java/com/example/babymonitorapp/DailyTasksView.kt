package com.example.babymonitorapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babymonitorapp.databinding.ActivityDailyTasksViewBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView


class DailyTasksView : AppCompatActivity() {

    private lateinit var binding: ActivityDailyTasksViewBinding
    private lateinit var adapter: TaskAdapter
    private var allTasks = mutableListOf<Tasks>()
    private var bottomNav: BottomNavigationView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDailyTasksViewBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        adapter = TaskAdapter(allTasks)

        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.taskRecyclerView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider)?.let {
            dividerItemDecoration.setDrawable(it)
            binding.taskRecyclerView.addItemDecoration(dividerItemDecoration)
        }

        binding.toolbar.setNavigationOnClickListener { item ->
            val checkedCount = allTasks.count { it.isChecked }
            val resultIntent = Intent()
            resultIntent.putExtra("completed_tasks", checkedCount)
            setResult(RESULT_OK, resultIntent)
            finish()

        }

        bottomNav = findViewById(R.id.bottomNavigationView)

        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity3::class.java))
                    true
                }
                R.id.baby -> true
                R.id.community -> {
                    startActivity(Intent(this, Community::class.java))
                    true
                }
                R.id.settings -> {

                    true
                }
                else -> false
            }
        }

        binding.addtaskButton.setOnClickListener {showAddTaskDialog()}

    }
    private fun showAddTaskDialog(){
        val input = EditText(this)
        input.hint = "Enter task name"

        AlertDialog.Builder(this)
            .setTitle("New Task")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val taskName = input.text.toString().trim()
                if (taskName.isNotEmpty())
                {
                    adapter.addTask(Tasks(taskName))
                } else {
                    Toast.makeText(this, "Task name can't be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}