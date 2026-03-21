package com.example.app.domain

data class ItineraryItem(
    val id: String,
    val tripId: String,
    val nombre: String,
    val dia: String,
    val hora: String,
    val precio: String,
    val tipo: String,
    val descripcion: String
)