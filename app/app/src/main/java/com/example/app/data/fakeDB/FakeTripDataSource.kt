package com.example.app.data.fakeDB

import com.example.app.R
import androidx.compose.runtime.mutableStateListOf
import com.example.app.domain.Trip

object FakeTripDataSource {
    // Usamos mutableStateListOf para que la UI reaccione a cambios (CRUD)
    private val trips = mutableStateListOf<Trip>()

    fun getTrips(): List<Trip> {
        return trips
    }

    fun addTrip(trip: Trip) {
        trips.add(trip)
    }
    fun deleteTrip(id: String) {
        trips.removeAll { it.id == id }
    }

    fun updateTrip(trip: Trip) {
        val index = trips.indexOfFirst { it.id == trip.id }
        if (index != -1) {
            trips[index] = trip
        }
    }

    // Dataset inicial para que la app no aparezca vacía (Fake Dataset)
    init {
        // Convertimos el recurso R.drawable.paris a una URI de texto para que sea compatible
        val parisUri = "android.resource://com.example.app/" + R.drawable.paris

        trips.add(
            Trip(
                id = "1",
                userId = "default_user", // T4.2: Añadido userId
                title = "Viaje a París",
                country = "Francia",
                description = "Visita a la torre Eiffel",
                imageUri = parisUri,
                isFeatured = true,
                budget = 500.0,
                dataInici = "2026-08-01",
                dataFinal = "2026-08-05"
            )
        )
    }
}
