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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.babymonitorapp.database.Task
import com.example.babymonitorapp.database.TaskViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class DailyTasksView : AppCompatActivity() {

    private lateinit var binding: ActivityDailyTasksViewBinding
    private lateinit var adapter: TaskAdapter
    private lateinit var taskViewModel: TaskViewModel
    private var bottomNav: BottomNavigationView? = null
    private var userId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDailyTasksViewBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        userId = sharedPref.getInt("loggedInUserId", -1)
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        adapter = TaskAdapter(mutableListOf()){
            updatedTask -> taskViewModel.updateTask(updatedTask)
        }
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.taskRecyclerView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider)?.let {
            dividerItemDecoration.setDrawable(it)
            binding.taskRecyclerView.addItemDecoration(dividerItemDecoration)
        }

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        taskViewModel.getTasksForUser(userId).observe(this) { tasks ->
            adapter.setTasks(tasks)
        }

        binding.toolbar.setNavigationOnClickListener {
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
        binding.addtaskButton.setOnClickListener {
            showAddTaskDialog()
        }

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
                    val newTask = Task(userId = userId, title = taskName, isCompleted = false)
                    taskViewModel.insertTask(newTask)
                } else {
                    Toast.makeText(this, "Task name can't be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}