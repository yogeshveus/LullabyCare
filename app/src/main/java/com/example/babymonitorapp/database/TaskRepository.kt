package com.example.babymonitorapp.database

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    fun getTasksForUser(userId: Int): LiveData<List<Task>> {
        return taskDao.getTasksForUser(userId)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    fun getCompletedTaskCount(userId: Int): LiveData<Int>{
        return taskDao.getCompletedTaskCount(userId)
    }

    fun getTotalTasks(userId: Int): LiveData<Int>{
        return taskDao.getTotalTasks(userId)
    }
}