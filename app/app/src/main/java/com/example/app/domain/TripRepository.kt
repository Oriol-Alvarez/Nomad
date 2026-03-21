package com.example.app.domain

interface TripRepository {
    fun getTrips(): List<Trip>
    fun getTripById(id: String): Trip?
    fun insertTrip(trip: Trip)
    fun deleteTrip(id: String)
    fun updateTrip(trip: Trip)
}
