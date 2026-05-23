package com.example.app.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "trip_images")
data class TripImage(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val tripId: String,
    val imageUri: String
)
