package com.example.app.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.app.data.repository.ItineraryItemRepositoryImpl
import com.example.app.data.repository.TripRepositoryImpl
import com.example.app.domain.Trip
import com.example.app.domain.TripRepository
import com.example.app.domain.ItineraryItemRepository
import com.example.app.domain.ItineraryItem
import java.util.UUID

class TripListViewModel(
    // Inyectamos AMBOS repositorios
    private val tripRepository: TripRepository = TripRepositoryImpl(),
    private val itineraryRepository: ItineraryItemRepository = ItineraryItemRepositoryImpl()
) : ViewModel() {

    // 1. ESTADO DE LA UI
    var trips by mutableStateOf(tripRepository.getTrips())
        private set

    // 2. OPERACIÓN: Guardar un nuevo viaje y sus actividades
    fun saveTrip(
        title: String,
        destination: String,
        dataInici: String,
        dataFinal: String,
        desc: String,
        budget: Double,
        imageUri: String,
        activitiesFromForm: List<ItineraryItem> // <-- AHORA RECIBE LA LISTA OFICIAL
    ) {
        val newTripId = UUID.randomUUID().toString()

        val newTrip = Trip(
            id = newTripId,
            title = title,
            country = destination,
            description = desc,
            imageUri = imageUri,
            isFeatured = false,
            budget = budget,
            dataInici = dataInici,
            dataFinal = dataFinal
        )
        tripRepository.insertTrip(newTrip)

        // Simplemente le asignamos el Trip ID a cada actividad y la guardamos
        activitiesFromForm.forEach { item ->
            val finalItem = item.copy(tripId = newTripId)
            itineraryRepository.insertItineraryItem(finalItem)
        }

        refreshTrips()
    }

    // 3. OPERACIÓN: Borrar un viaje
    fun deleteTrip(id: String) {
        // Buena práctica: Si borras el viaje, borra también sus actividades
        itineraryRepository.deleteItineraryItemsByTripId(id)
        tripRepository.deleteTrip(id)

        refreshTrips()
    }

    fun getTripById(id: String): Trip? {
        return tripRepository.getTripById(id)
    }

    fun getActivitiesForTrip(tripId: String): List<ItineraryItem> {
        return itineraryRepository.getItineraryItemsForTrip(tripId)
    }

    // 4. AUXILIAR: Refrescar la lista de viajes
    fun refreshTrips() {
        trips = tripRepository.getTrips()
    }
}
