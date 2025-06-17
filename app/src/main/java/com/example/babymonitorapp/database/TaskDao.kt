package com.example.babymonitorapp.database
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM task WHERE userId = :userId")
    fun getTasksForUser(userId: Int): LiveData<List<Task>>

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)
}
