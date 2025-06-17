package com.example.babymonitorapp.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application): AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        val taskDao = UserDatabase.getInstance(application).taskDao()
        repository = TaskRepository(taskDao)
    }

    fun getTasksForUser(userId: Int): LiveData<List<Task>> {
        return repository.getTasksForUser(userId)
    }

    fun insertTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTask(task)
        }
    }

    fun getCompletedTaskCount(userId: Int): LiveData<Int>{
        return repository.getCompletedTaskCount(userId)
    }

    fun getTotalTasks(userId: Int): LiveData<Int>{
        return repository.getTotalTasks(userId)
    }

    suspend fun deleteTask(task: Task){
        repository.deleteTask(task)
    }
}
