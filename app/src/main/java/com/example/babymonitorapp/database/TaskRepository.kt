package com.example.babymonitorapp.database

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    fun getTasksForUser(userId: Int): LiveData<List<Task>> {
        return taskDao.getTasksForUser(userId)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }
}