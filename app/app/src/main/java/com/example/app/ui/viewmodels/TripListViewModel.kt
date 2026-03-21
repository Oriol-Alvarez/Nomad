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
import com.example.app.R


class TripListViewModel(
    private val tripRepository: TripRepository = TripRepositoryImpl(),
    private val itineraryRepository: ItineraryItemRepository = ItineraryItemRepositoryImpl()
) : ViewModel() {

    var trips by mutableStateOf(tripRepository.getTrips())
        private set

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
        val newTripId = UUID.randomUUID().toString()
        val imagenFinal = imageUri.ifBlank {
            "android.resource://com.example.app/" + R.drawable.viaje_predefinido
        }
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
            val finalItem = item.copy(tripId = newTripId)
            itineraryRepository.insertItineraryItem(finalItem)
        }

        refreshTrips()
    }

    fun deleteTrip(id: String) {
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

    fun deleteActivity(activity: ItineraryItem) {
        itineraryRepository.deleteItineraryItem(activity)
        updateTripBudget(activity.tripId)
        refreshTrips()
    }

    fun addActivityToTrip(tripId: String, item: ItineraryItem) {
        // Aseguramos que el item tenga el ID del viaje correcto
        val activityConId = item.copy(tripId = tripId)
        itineraryRepository.insertItineraryItem(activityConId)
        updateTripBudget(tripId)
        refreshTrips()
    }

    fun updateActivity(item: ItineraryItem) {
        // Al actualizar, el objeto ya trae su tripId desde la pantalla de detalle
        itineraryRepository.updateItineraryItem(item)
        updateTripBudget(item.tripId)
        refreshTrips()
    }

    private fun updateTripBudget(tripId: String) {
        val trip = tripRepository.getTripById(tripId) ?: return
        val activities = itineraryRepository.getItineraryItemsForTrip(tripId)
        val newBudget = activities.sumOf { it.precio.toDoubleOrNull() ?: 0.0 }
        
        // Creamos una copia del viaje con el nuevo presupuesto para que la UI reaccione
        val updatedTrip = trip.copy(budget = newBudget)
        tripRepository.updateTrip(updatedTrip)
    }

    fun updateTrip(trip: Trip) {
        tripRepository.updateTrip(trip)
        refreshTrips()
    }

    fun refreshTrips() {
        trips = tripRepository.getTrips()
    }
}
