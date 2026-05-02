package com.example.app.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itinerary_items")
data class ItineraryItem(
    @PrimaryKey
    val id: String,
    val tripId: String,
    val nombre: String,
    val dia: String,
    val hora: String,
    val precio: String,
    val tipo: String,
    val descripcion: String
)
