package com.example.app.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String, // Firebase UID
    val email: String,
    val username: String,
    val birthdate: String,
    val address: String = "",
    val country: String = "",
    val phoneNumber: String = "",
    val acceptEmails: Boolean = true
)
