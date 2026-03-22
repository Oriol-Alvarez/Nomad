package com.example.app.ui.viewmodels

import android.util.Log
import com.example.app.R
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

/**
 * Este es el "cerebro" que maneja la lista de viajes y sus actividades.
 * Aquí es donde se guarda, se borra y se actualiza todo lo relacionado con los viajes.
 */
class TripListViewModel(
    private val tripRepository: TripRepository = TripRepositoryImpl(),
    private val itineraryRepository: ItineraryItemRepository = ItineraryItemRepositoryImpl()
) : ViewModel() {

    private val TAG = "TripListViewModel"

    // La lista de viajes que se muestra en la pantalla
    var trips by mutableStateOf(tripRepository.getTrips())
        private set

    /**
     * Guarda un viaje nuevo. Primero mira que el nombre y las fechas estén bien.
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
        Log.d(TAG, "Guardando viaje: $title")

        // Comprobamos que el título y el sitio no estén vacíos
        if (!Validator.isValidTitle(title) || !Validator.isValidLocation(destination)) {
            Log.e(TAG, "Fallo: Título o destino mal puestos.")
            return false
        }

        // Comprobamos que la fecha de vuelta sea después de la de ida
        if (!Validator.areDatesValid(dataInici, dataFinal)) {
            Log.e(TAG, "Fallo: Las fechas no tienen sentido.")
            return false
        }

        // Si no han puesto foto, le ponemos una por defecto
        val imagenFinal = imageUri.ifBlank {
            "android.resource://com.example.app/" + R.drawable.viaje_predefinido
        }

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

            // Guardamos también las paradas/actividades que haya puesto el usuario
            activitiesFromForm.forEach { item ->
                itineraryRepository.insertItineraryItem(item.copy(tripId = newTripId))
            }
            
            refreshTrips() // Actualizamos la lista para que se vea el cambio
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar el viaje", e)
            return false
        }
    }

    // Borra un viaje y todas sus paradas
    fun deleteTrip(id: String) {
        itineraryRepository.deleteItineraryItemsByTripId(id)
        tripRepository.deleteTrip(id)
        refreshTrips()
    }

    // Busca un viaje por su ID
    fun getTripById(id: String): Trip? {
        return tripRepository.getTripById(id)
    }

    // Saca todas las paradas de un viaje concreto
    fun getActivitiesForTrip(tripId: String): List<ItineraryItem> {
        return itineraryRepository.getItineraryItemsForTrip(tripId)
    }

    /**
     * Añade una parada a un viaje. Mira que la fecha caiga dentro de los días del viaje.
     */
    fun addActivityToTrip(tripId: String, item: ItineraryItem): Boolean {
        val trip = tripRepository.getTripById(tripId)
        if (trip == null) return false

        // Comprobamos la fecha
        if (!Validator.isActivityInTripRange(item.dia, trip.dataInici, trip.dataFinal)) {
            Log.e(TAG, "La actividad está fuera de los días del viaje.")
            return false
        }

        itineraryRepository.insertItineraryItem(item.copy(tripId = tripId))
        updateTripBudget(tripId) // Recalculamos el gasto total
        refreshTrips()
        return true
    }

    /**
     * Actualiza una parada que ya existe.
     */
    fun updateActivity(item: ItineraryItem): Boolean {
        val trip = tripRepository.getTripById(item.tripId)

        // Miramos que la nueva fecha siga estando dentro del viaje
        if (trip != null && !Validator.isActivityInTripRange(item.dia, trip.dataInici, trip.dataFinal)) {
            return false
        }

        itineraryRepository.updateItineraryItem(item)
        updateTripBudget(item.tripId)
        refreshTrips()
        return true
    }

    // Borra una parada suelta
    fun deleteActivity(activity: ItineraryItem) {
        itineraryRepository.deleteItineraryItem(activity)
        updateTripBudget(activity.tripId)
        refreshTrips()
    }

    // Suma el precio de todas las paradas para saber cuánto cuesta el viaje
    private fun updateTripBudget(tripId: String) {
        val trip = tripRepository.getTripById(tripId) ?: return
        val activities = itineraryRepository.getItineraryItemsForTrip(tripId)
        val newBudget = activities.sumOf { it.precio.toDoubleOrNull() ?: 0.0 }
        trip.budget = newBudget
    }

    // Cambia los datos de un viaje (ej: el título o la foto)
    fun updateTrip(trip: Trip) {
        if (Validator.isValidTitle(trip.title)) {
            tripRepository.updateTrip(trip)
            refreshTrips()
        }
    }

    // Recarga la lista de viajes desde la base de datos
    fun refreshTrips() {
        trips = tripRepository.getTrips()
    }
}
