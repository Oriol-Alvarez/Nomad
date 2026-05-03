package com.example.app.domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "itinerary_items",
    foreignKeys = [
        ForeignKey(
            entity = Trip::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class ItineraryItem(
    @PrimaryKey
    val id: String,
    val tripId: String,
    val nombre: String,
    val dia: String,
    val hora: String,
    val precio: Int,
    val tipo: String,
    val descripcion: String
)
