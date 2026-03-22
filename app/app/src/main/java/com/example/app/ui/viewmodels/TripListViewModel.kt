package com.example.app.ui.viewmodels

import android.util.Log
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
import com.example.app.ui.screens.Validator
import java.util.UUID

class TripListViewModel(
    private val tripRepository: TripRepository = TripRepositoryImpl(),
    private val itineraryRepository: ItineraryItemRepository = ItineraryItemRepositoryImpl()
) : ViewModel() {

    private val TAG = "TripListViewModel"

    var trips by mutableStateOf(tripRepository.getTrips())
        private set

    /**
     * T3.1 & T3.5: Validación y Logs.
     * Guarda un nuevo viaje tras validar los campos obligatorios y coherencia de fechas.
     */
    fun saveTrip(
        title: String,
        destination: String,
        dataInici: String,
        dataFinal: String,
        desc: String,
        budget: Double,
        imageUri: String,
        activitiesFromForm: List<ItineraryItem>
    ): Boolean {
        Log.d(TAG, "Intentando guardar viaje: $title en $destination")

        if (!Validator.isValidTitle(title) || !Validator.isValidLocation(destination)) {
            Log.e(TAG, "Error de validación: Título o destino no válidos.")
            return false
        }

        if (!Validator.areDatesValid(dataInici, dataFinal)) {
            Log.e(TAG, "Error de validación: Rango de fechas incoherente ($dataInici - $dataFinal).")
            return false
        }

        try {
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
            Log.i(TAG, "Viaje insertado con ID: $newTripId")

            activitiesFromForm.forEach { item ->
                val finalItem = item.copy(tripId = newTripId)
                itineraryRepository.insertItineraryItem(finalItem)
            }
            
            refreshTrips()
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado al guardar el viaje", e)
            return false
        }
    }

    fun deleteTrip(id: String) {
        Log.d(TAG, "Borrando viaje con ID: $id")
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

    /**
     * T3.1 & T3.5: Validación de actividad y Logs.
     */
    fun addActivityToTrip(tripId: String, item: ItineraryItem): Boolean {
        Log.d(TAG, "Añadiendo actividad '${item.nombre}' al viaje $tripId")

        val trip = tripRepository.getTripById(tripId)
        if (trip == null) {
            Log.e(TAG, "Error: El viaje $tripId no existe.")
            return false
        }

        if (!Validator.isActivityInTripRange(item.dia, trip.dataInici, trip.dataFinal)) {
            Log.e(TAG, "Error: La fecha de la actividad ${item.dia} está fuera del rango del viaje.")
            return false
        }

        itineraryRepository.insertItineraryItem(item.copy(tripId = tripId))
        updateTripBudget(tripId)
        refreshTrips()
        return true
    }

    /**
     * Actualiza una actividad existente validando que siga dentro del rango del viaje.
     */
    fun updateActivity(item: ItineraryItem): Boolean {
        Log.d(TAG, "Actualizando actividad: ${item.id}")
        val trip = tripRepository.getTripById(item.tripId)

        if (trip != null && !Validator.isActivityInTripRange(item.dia, trip.dataInici, trip.dataFinal)) {
            Log.e(TAG, "Error al actualizar: La nueva fecha ${item.dia} está fuera del rango del viaje.")
            return false
        }

        itineraryRepository.updateItineraryItem(item)
        updateTripBudget(item.tripId)
        refreshTrips()
        return true
    }

    fun deleteActivity(activity: ItineraryItem) {
        Log.d(TAG, "Borrando actividad ${activity.id}")
        itineraryRepository.deleteItineraryItem(activity)
        updateTripBudget(activity.tripId)
        refreshTrips()
    }

    private fun updateTripBudget(tripId: String) {
        val trip = tripRepository.getTripById(tripId) ?: return
        val activities = itineraryRepository.getItineraryItemsForTrip(tripId)
        val newBudget = activities.sumOf { it.precio.toDoubleOrNull() ?: 0.0 }
        trip.budget = newBudget
    }

    fun updateTrip(trip: Trip) {
        Log.d(TAG, "Actualizando viaje: ${trip.id}")
        if (Validator.isValidTitle(trip.title)) {
            tripRepository.updateTrip(trip)
            refreshTrips()
        } else {
            Log.e(TAG, "Error al actualizar: El título no es válido.")
        }
    }

    fun refreshTrips() {
        trips = tripRepository.getTrips()
    }
}
