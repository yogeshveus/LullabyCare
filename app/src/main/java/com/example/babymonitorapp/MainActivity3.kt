package com.example.babymonitorapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import android.widget.ImageButton

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
        val searchButton: ImageButton = findViewById(R.id.imageButton7)
        val searchEditText: EditText = findViewById(R.id.editTextText)
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter(s.toString())
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        searchButton.setOnClickListener {
            val input = EditText(this)
            input.hint = "Enter search term"

            AlertDialog.Builder(this)
                .setTitle("Search")
                .setView(input)
                .setPositiveButton("Search") { dialog, _ ->
                    val query = input.text.toString().trim()
                    if (query.isNotEmpty()) {
                        adapter.filter(query)
                    } else {
                        adapter.filter("") // empty shows everything
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                .setNeutralButton("Reset") { dialog, _ ->
                    adapter.filter("") // show all items again
                    dialog.dismiss()
                }
                .show()
        }



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }


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
                    val intent = Intent(this, NutritionTrackerActivity::class.java)
                    startActivity(intent)

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
                    val intent = Intent(this, NutritionTrackerActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        recyclerView.adapter = adapter
    }
}
