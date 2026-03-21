package com.example.app.data.repository

import com.example.app.data.fakeDB.FakeTripDataSource
import com.example.app.domain.Trip
import com.example.app.domain.TripRepository

class TripRepositoryImpl : TripRepository {
    private val dataSource = FakeTripDataSource

    override fun getTrips(): List<Trip> {
        return dataSource.getTrips()
    }

    override fun insertTrip(trip: Trip) {
        dataSource.addTrip(trip)
    }

    override fun getTripById(id: String): Trip? {
        return dataSource.getTrips().find { it.id == id }
    }

    override fun deleteTrip(id: String) {
        dataSource.deleteTrip(id)
    }
}