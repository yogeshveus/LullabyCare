package com.example.babymonitorapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val name: String,
    val user: String,
    val password: String,
    val phone: Int
)
