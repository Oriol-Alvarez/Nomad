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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TripListViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val itineraryRepository: ItineraryItemRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val TAG_DB = "DatabaseLog"
    private val TAG_VAL = "ValidationLog"

    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents: SharedFlow<String> = _uiEvents

    val trips: StateFlow<List<Trip>> = authRepository.getAuthStateFlow()
        .map { it?.uid }
        .distinctUntilChanged()
        .flatMapLatest { uid ->
            if (uid != null) {
                Log.d(TAG_DB, "Observando viajes para UID: $uid")
                tripRepository.getTripsForUser(uid)
            } else {
                Log.d(TAG_DB, "Sin usuario, lista de viajes vacía")
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
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
    ): Boolean {
        val currentUserId = authRepository.getCurrentUser()?.uid ?: return false
        if (!Validator.isValidTitle(title) || !Validator.isValidLocation(destination)) return false
        if (!Validator.areDatesValid(dataInici, dataFinal)) return false

        viewModelScope.launch {
            try {
                if (trips.value.any { it.title.equals(title, ignoreCase = true) }) {
                    _uiEvents.emit("Ya existe un viaje con este nombre")
                    return@launch
                }
                val imagenFinal = imageUri.ifBlank {
                    "android.resource://com.example.app/" + R.drawable.viaje_predefinido
                }
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
                _uiEvents.emit("¡Viaje creado con éxito!")
            } catch (e: Exception) {
                Log.e(TAG_DB, "Error al guardar viaje", e)
                _uiEvents.emit("Error al guardar en la base de datos")
            }
        }
        return true
    }

    fun deleteTrip(id: String) {
        viewModelScope.launch {
            try {
                itineraryRepository.deleteItineraryItemsByTripId(id)
                tripRepository.deleteTrip(id)
            } catch (e: Exception) {
                Log.e(TAG_DB, "Error al borrar viaje", e)
            }
        }
    }

    suspend fun getTripById(id: String): Trip? = tripRepository.getTripById(id)

    fun getActivitiesForTrip(tripId: String) = itineraryRepository.getItineraryItemsForTrip(tripId)

    fun addActivityToTrip(tripId: String, item: ItineraryItem): Boolean {
        if (item.precio < 0) return false
        viewModelScope.launch {
            try {
                val trip = tripRepository.getTripById(tripId) ?: return@launch
                if (Validator.isActivityInTripRange(item.dia, trip.dataInici, trip.dataFinal)) {
                    itineraryRepository.insertItineraryItem(item.copy(tripId = tripId))
                    updateTripBudget(tripId)
                } else {
                    _uiEvents.emit("Fecha fuera del rango del viaje")
                }
            } catch (e: Exception) {
                Log.e(TAG_DB, "Error al añadir actividad", e)
            }
        }
        return true
    }

    // --- MÉTODOS RESTAURADOS ---
    fun updateActivity(item: ItineraryItem) {
        viewModelScope.launch {
            try {
                itineraryRepository.updateItineraryItem(item)
                updateTripBudget(item.tripId)
            } catch (e: Exception) {
                Log.e(TAG_DB, "Error al actualizar actividad", e)
            }
        }
    }

    fun deleteActivity(activity: ItineraryItem) {
        viewModelScope.launch {
            try {
                itineraryRepository.deleteItineraryItem(activity)
                updateTripBudget(activity.tripId)
            } catch (e: Exception) {
                Log.e(TAG_DB, "Error al borrar actividad", e)
            }
        }
    }

    private suspend fun updateTripBudget(tripId: String) {
        try {
            val trip = tripRepository.getTripById(tripId) ?: return
            val activities = itineraryRepository.getItineraryItemsForTrip(tripId).first()
            val newBudget = activities.sumOf { it.precio.toDouble() }
            trip.budget = newBudget
            tripRepository.updateTrip(trip)
        } catch (e: Exception) {
            Log.e(TAG_DB, "Error presupuesto", e)
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
