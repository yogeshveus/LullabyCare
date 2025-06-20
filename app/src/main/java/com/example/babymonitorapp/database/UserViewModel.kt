package com.example.babymonitorapp.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val readUser: LiveData<List<User>>
    private val repository: UserRepository
    init{
        val userDao = UserDatabase.getInstance(application).userDao()
        repository = UserRepository(userDao)
        readUser = repository.readUser
    }

    fun insertUser(user: User){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertUser(user)
        }
    }

    suspend fun validUser(user: String): User?{
        return repository.getUserbyUsername(user)
    }
    suspend fun login(username: String, password: String): User? {
        return repository.login(username, password)
    }
    suspend fun getUserbyUserId(userId: Int): User?{
        return repository.getUserbyUserId(userId)
    }
    suspend fun updateUser(user: User){
        return repository.updateUser(user)
    }
    suspend fun deleteUser(user: User){
        return repository.deleteUser(user)
    }


}