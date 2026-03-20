package com.example.app.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.app.data.repository.TripRepositoryImpl
import com.example.app.domain.Trip
import com.example.app.domain.TripRepository
import com.example.app.domain.ItineraryItem // Asegúrate de tener este import
import java.util.UUID

class TripListViewModel(
    private val repository: TripRepository = TripRepositoryImpl()
) : ViewModel() {

    // 1. ESTADO DE LA UI: Lista de viajes observable por Compose
    var trips by mutableStateOf(repository.getTrips())
        private set

    // 2. OPERACIÓN: Guardar un nuevo viaje
    fun saveTrip(
        title: String,
        destination: String,
        dataInici: String,
        dataFinal: String,
        desc: String,
        budget: Double,
        imageUri: String,
        activitiesFromForm: List<Map<String, String>> // Los datos del DialogoNuevaActividad
    ) {
        // Convertimos la lista de Maps a objetos ItineraryItem
        val itineraryItems = activitiesFromForm.map { map ->
            ItineraryItem(
                itemId = UUID.randomUUID().toString(),
                activityName = map["nombre"] ?: "",
                // Convertimos el String de la hora/fecha a Long (o usa 0L por ahora si no tienes el conversor)
                schedule = System.currentTimeMillis(),
                locationName = destination,
                cost = map["precio"]?.toDoubleOrNull() ?: 0.0,
                isCompleted = false
            )
        }

        val newTrip = Trip(
            id = UUID.randomUUID().toString(),
            title = title,
            country = destination,
            description = desc,
            imageUri = imageUri,
            isFeatured = false,
            budget = budget,
            dataInici = dataInici,
            dataFinal = dataFinal
        ).apply {
            // Añadimos los items creados a la lista del viaje
            this.activities.addAll(itineraryItems)
        }

        repository.insertTrip(newTrip)
        refreshTrips()
    }

    // 3. OPERACIÓN: Borrar un viaje
    fun deleteTrip(id: String) {
        repository.deleteTrip(id)
        refreshTrips()
    }

    // 4. AUXILIAR: Refrescar la lista de viajes
    fun refreshTrips() {
        trips = repository.getTrips()
    }
}