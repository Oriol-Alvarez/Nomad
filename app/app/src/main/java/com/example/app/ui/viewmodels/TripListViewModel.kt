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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
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

    // T4.2: Solo mostramos los viajes del usuario logueado actualmente
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
    ): Boolean {

        val currentUserId = authRepository.getCurrentUser()?.uid ?: return false

        // Validaciones Síncronas
        if (!Validator.isValidTitle(title)) {
            Log.e(TAG_VAL, "Título inválido: $title")
            return false
        }
        if (!Validator.isValidLocation(destination)) {
            Log.e(TAG_VAL, "Ubicación inválida: $destination")
            return false
        }
        if (!Validator.areDatesValid(dataInici, dataFinal)) {
            Log.e(TAG_VAL, "Fechas inconsistentes: $dataInici - $dataFinal")
            return false
        }

        viewModelScope.launch {
            try {
                // Prevenir nombres de viaje duplicados para el mismo usuario
                val currentTrips = trips.value
                if (currentTrips.any { it.title.equals(title, ignoreCase = true) }) {
                    Log.w(TAG_VAL, "El usuario ya tiene un viaje llamado: $title")
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
                _uiEvents.emit("Viaje guardado correctamente")
            } catch (e: Exception) {
                Log.e(TAG_DB, "Error crítico al guardar viaje", e)
                _uiEvents.emit("Error al guardar el viaje")
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

    /**
     * T5.2: Añade una actividad validando el rango de fechas.
     */
    fun addActivityToTrip(tripId: String, item: ItineraryItem): Boolean {
        // Validación síncrona del precio
        if (item.precio < 0) return false

        viewModelScope.launch {
            try {
                val trip = tripRepository.getTripById(tripId) ?: return@launch
                if (Validator.isActivityInTripRange(item.dia, trip.dataInici, trip.dataFinal)) {
                    itineraryRepository.insertItineraryItem(item.copy(tripId = tripId))
                    updateTripBudget(tripId)
                    _uiEvents.emit("Actividad añadida")
                } else {
                    _uiEvents.emit("La fecha de la actividad está fuera del rango del viaje")
                }
            } catch (e: Exception) {
                Log.e(TAG_DB, "Error al añadir actividad", e)
            }
        }
        return true // Retornamos true indicando que se ha iniciado el proceso de guardado
    }

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
            Log.e(TAG_DB, "Error actualizando presupuesto", e)
        }
    }

    fun updateTrip(trip: Trip) {
        viewModelScope.launch {
            if (Validator.isValidTitle(trip.title)) {
                tripRepository.updateTrip(trip)
            }
        }
    }

    // Para compatibilidad con tests que no usan DB real
    fun refreshTrips() {
        // En una implementación real con Room, el StateFlow se actualiza solo.
    }
}
