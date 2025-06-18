package com.example.babymonitorapp.database

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {
    val readUser: LiveData<List<User>> = userDao.readUser()
    suspend fun insertUser(user: User){
        userDao.insertUser(user)
    }
    suspend fun login(username: String, password: String): User? {
        return userDao.login(username, password)
    }
    suspend fun getUserbyUsername(user: String): User?{
        return userDao.getUserbyUsername(user)
    }
    suspend fun getUserbyUserId(userId: Int): User?{
        return userDao.getUserbyUserId(userId)
    }
    suspend fun updateUser(user: User){
        userDao.updateUser(user)
    }
    suspend fun deleteUser(user: User){
        userDao.deleteUser(user)
    }
}