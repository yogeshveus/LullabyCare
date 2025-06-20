package com.example.babymonitorapp.database

import android.R
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM user")
    fun readUser(): LiveData<List<User>>

    @Query("SELECT * FROM user WHERE user = :username LIMIT 1")
    suspend fun getUserbyUsername(username: String): User?

    @Query("SELECT * FROM user WHERE user = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Query("SELECT * FROM user WHERE user = :username AND password = :password LIMIT 1")
    suspend fun getUserByCredentials(username: String, password: String): User?

    @Query("SELECT * FROM user WHERE user = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM user WHERE id = :userId LIMIT 1")
    suspend fun getUserbyUserId(userId: Int): User?
}
