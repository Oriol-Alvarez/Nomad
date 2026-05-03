package com.example.app.ui.viewmodels

import android.util.Log
import com.example.app.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.Trip
import com.example.app.domain.TripRepository
import com.example.app.domain.ItineraryItemRepository
import com.example.app.domain.ItineraryItem
import com.example.app.domain.AuthRepository
import com.example.app.ui.screens.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TripListViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val itineraryRepository: ItineraryItemRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val TAG = "TripListViewModel"

    @OptIn(ExperimentalCoroutinesApi::class)
    val trips: StateFlow<List<Trip>> = flowOf(authRepository.getCurrentUser()?.uid)
        .flatMapLatest { uid ->
            if (uid != null) tripRepository.getTripsForUser(uid)
            else flowOf(emptyList())
        }
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
        val currentUserId = authRepository.getCurrentUser()?.uid ?: return
        
        if (!Validator.isValidTitle(title) || !Validator.isValidLocation(destination)) return
        
        val imagenFinal = imageUri.ifBlank {
            "android.resource://com.example.app/" + R.drawable.viaje_predefinido
        }

        viewModelScope.launch {
            try {
                val newTripId = UUID.randomUUID().toString()
                val newTrip = Trip(
                    id = newTripId,
                    userId = currentUserId,
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
                updateTripBudget(newTripId)
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

    suspend fun getTripById(id: String): Trip? = tripRepository.getTripById(id)

    fun getActivitiesForTrip(tripId: String) = itineraryRepository.getItineraryItemsForTrip(tripId)

    fun addActivityToTrip(tripId: String, item: ItineraryItem) {
        viewModelScope.launch {
            val trip = tripRepository.getTripById(tripId) ?: return@launch
            itineraryRepository.insertItineraryItem(item.copy(tripId = tripId))
            updateTripBudget(tripId)
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
        try {
            val trip = tripRepository.getTripById(tripId) ?: return
            val activities = itineraryRepository.getItineraryItemsForTrip(tripId).first()
            val newBudget = activities.sumOf { it.precio.toDoubleOrNull() ?: 0.0 }
            
            trip.budget = newBudget
            tripRepository.updateTrip(trip)
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando presupuesto", e)
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            if (Validator.isValidTitle(trip.title)) {
                tripRepository.updateTrip(trip)
            }
        }
    }
}
