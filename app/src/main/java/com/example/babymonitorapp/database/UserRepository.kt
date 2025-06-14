package com.example.babymonitorapp.database

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {
    val readUser: LiveData<List<User>> = userDao.readUser()
    suspend fun insertUser(user: User){
        userDao.insertUser(user)
    }
}