package com.example.babymonitorapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity3 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var adapter: CardAdapter

    private val cardItems = listOf(
        CardItem("Daily tasks"),
        CardItem("Calendar"),
        CardItem("Nutrition Tracker")
    )

    private val dailyTasksLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val completedTasks = result.data?.getIntExtra("completed_tasks", 0) ?: 0
            adapter.updateTasks(completedTasks)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        recyclerView = findViewById(R.id.recyclerView)
        bottomNav = findViewById(R.id.bottomNavigationView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CardAdapter(cardItems) { item ->
            when (item.title) {
                "Daily tasks" -> {
                    val intent = Intent(this, DailyTasksView::class.java)
                    dailyTasksLauncher.launch(intent)
                }
                "Calendar" -> {
                    val intent = Intent(this, CalendarView::class.java)
                    startActivity(intent)
                }
                "Nutrition Tracker" -> {


                }
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    true
                }
                R.id.baby -> {
                    true
                }
                R.id.community -> {
                    val intent = Intent(this, Community::class.java)
                    startActivity(intent)
                    true
                }
                R.id.settings -> {
                    true

                    val intent = Intent(this, NutritionTrackerActivity::class.java)
                    startActivity(intent)

                }
                else -> false
            }
        }
        recyclerView.adapter = adapter
    }
}
