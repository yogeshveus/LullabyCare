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
}