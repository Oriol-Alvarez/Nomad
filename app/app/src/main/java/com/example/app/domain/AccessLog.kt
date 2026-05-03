package com.example.app.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "access_logs")
data class AccessLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val dateTime: Long, // Timestamp
    val type: String // "LOGIN" o "LOGOUT"
)
