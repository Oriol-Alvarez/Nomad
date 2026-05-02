package com.example.app.ui.viewmodels

import android.util.Log
import com.example.app.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.Trip
import com.example.app.domain.TripRepository
import com.example.app.domain.ItineraryItemRepository
import com.example.app.domain.ItineraryItem
import com.example.app.ui.screens.Validator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class TripListViewModel(
    private val tripRepository: TripRepository,
    private val itineraryRepository: ItineraryItemRepository
) : ViewModel() {

    private val TAG = "TripListViewModel"

    // Convertimos el Flow del repositorio en un StateFlow para la UI (T1.6)
    val trips: StateFlow<List<Trip>> = tripRepository.getTrips()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveTrip(
        title: String,
        destination: String,
        dataInici: String,
        dataFinal: String,
        desc: String,
        budget: Double,
        imageUri: String,
        activitiesFromForm: List<ItineraryItem>
    ) {
        if (!Validator.isValidTitle(title) || !Validator.isValidLocation(destination)) return
        if (!Validator.areDatesValid(dataInici, dataFinal)) return

        val imagenFinal = imageUri.ifBlank {
            "android.resource://com.example.app/" + R.drawable.viaje_predefinido
        }

        viewModelScope.launch {
            try {
                val newTripId = UUID.randomUUID().toString()
                val newTrip = Trip(
                    id = newTripId,
                    title = title,
                    country = destination,
                    description = desc,
                    imageUri = imagenFinal,
                    isFeatured = false,
                    budget = budget,
                    dataInici = dataInici,
                    dataFinal = dataFinal
                )
                
                tripRepository.insertTrip(newTrip)

                activitiesFromForm.forEach { item ->
                    itineraryRepository.insertItineraryItem(item.copy(tripId = newTripId))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al guardar el viaje", e)
            }
        }
    }

    fun deleteTrip(id: String) {
        viewModelScope.launch {
            itineraryRepository.deleteItineraryItemsByTripId(id)
            tripRepository.deleteTrip(id)
        }
    }

    // Nota: getTripById ahora es suspend en el repo
    suspend fun getTripById(id: String): Trip? {
        return tripRepository.getTripById(id)
    }

    fun getActivitiesForTrip(tripId: String) = itineraryRepository.getItineraryItemsForTrip(tripId)

    fun addActivityToTrip(tripId: String, item: ItineraryItem) {
        viewModelScope.launch {
            val trip = tripRepository.getTripById(tripId) ?: return@launch
            if (Validator.isActivityInTripRange(item.dia, trip.dataInici, trip.dataFinal)) {
                itineraryRepository.insertItineraryItem(item.copy(tripId = tripId))
                updateTripBudget(tripId)
            }
        }
    }

    fun updateActivity(item: ItineraryItem) {
        viewModelScope.launch {
            val trip = tripRepository.getTripById(item.tripId)
            if (trip != null && Validator.isActivityInTripRange(item.dia, trip.dataInici, trip.dataFinal)) {
                itineraryRepository.updateItineraryItem(item)
                updateTripBudget(item.tripId)
            }
        }
    }

    fun deleteActivity(activity: ItineraryItem) {
        viewModelScope.launch {
            itineraryRepository.deleteItineraryItem(activity)
            updateTripBudget(activity.tripId)
        }
    }

    private suspend fun updateTripBudget(tripId: String) {
        val trip = tripRepository.getTripById(tripId) ?: return
        // Como getItineraryItemsForTrip devuelve un Flow, aquí tendríamos que obtener el valor actual
        // Para simplificar el ejercicio T1, asumiremos que el repo tiene un método de obtención única o usaremos collect
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            if (Validator.isValidTitle(trip.title)) {
                tripRepository.updateTrip(trip)
            }
        }
    }
}
