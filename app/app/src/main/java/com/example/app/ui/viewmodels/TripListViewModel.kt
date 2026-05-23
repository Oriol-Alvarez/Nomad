package com.example.app.ui.viewmodels

import android.util.Log
import com.example.app.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.Trip
import com.example.app.domain.TripImage
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
    private val authRepository: AuthRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context?
) : ViewModel() {

    // Secondary constructor for compatibility with manual instantiation in factory/tests
    constructor(
        tripRepository: TripRepository,
        itineraryRepository: ItineraryItemRepository,
        authRepository: AuthRepository
    ) : this(tripRepository, itineraryRepository, authRepository, null)

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
        .onEach { list ->
            list.forEach { trip ->
                if (trip.imageUri.startsWith("content://")) {
                    migrateTripImageUriIfNeeded(trip)
                }
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
                tripRepository.deleteTripImagesByTripId(id)
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

    // T3: Métodos de imágenes de viaje
    fun getImagesForTrip(tripId: String): Flow<List<TripImage>> {
        return tripRepository.getImagesForTrip(tripId).onEach { images ->
            images.forEach { image ->
                if (image.imageUri.startsWith("content://")) {
                    migrateTripGalleryImageUriIfNeeded(image)
                }
            }
        }
    }

    fun addImagesToTrip(tripId: String, uris: List<String>) {
        viewModelScope.launch {
            try {
                uris.forEach { uri ->
                    tripRepository.insertTripImage(TripImage(tripId = tripId, imageUri = uri))
                }
            } catch (e: Exception) {
                Log.e(TAG_DB, "Error al añadir imágenes al viaje", e)
            }
        }
    }

    fun deleteTripImage(imageId: String) {
        viewModelScope.launch {
            try {
                tripRepository.deleteTripImage(imageId)
            } catch (e: Exception) {
                Log.e(TAG_DB, "Error al borrar imagen", e)
            }
        }
    }

    private fun migrateTripImageUriIfNeeded(trip: Trip) {
        val uriStr = trip.imageUri
        if (uriStr.startsWith("content://") && context != null) {
            viewModelScope.launch {
                try {
                    val uri = android.net.Uri.parse(uriStr)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val fileName = "trip_img_migrated_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(5)}.jpg"
                        val file = java.io.File(context.filesDir, fileName)
                        val outputStream = java.io.FileOutputStream(file)
                        inputStream.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }
                        val newUriStr = android.net.Uri.fromFile(file).toString()
                        Log.d(TAG_DB, "Exitosamente migrado URI de portada para viaje ${trip.id}: de $uriStr a $newUriStr")
                        tripRepository.updateTrip(trip.copy(imageUri = newUriStr))
                    }
                } catch (e: SecurityException) {
                    // Esperado si no hay permiso todavía (el picker no se ha abierto)
                    Log.d(TAG_DB, "No se pudo migrar URI de portada para viaje ${trip.id} (sin permiso todavía): ${e.message}")
                } catch (e: Exception) {
                    Log.e(TAG_DB, "Error al migrar URI de portada para viaje ${trip.id}", e)
                }
            }
        }
    }

    private fun migrateTripGalleryImageUriIfNeeded(image: TripImage) {
        val uriStr = image.imageUri
        if (uriStr.startsWith("content://") && context != null) {
            viewModelScope.launch {
                try {
                    val uri = android.net.Uri.parse(uriStr)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val fileName = "trip_gallery_migrated_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(5)}.jpg"
                        val file = java.io.File(context.filesDir, fileName)
                        val outputStream = java.io.FileOutputStream(file)
                        inputStream.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }
                        val newUriStr = android.net.Uri.fromFile(file).toString()
                        Log.d(TAG_DB, "Exitosamente migrado URI de galería ${image.id}: de $uriStr a $newUriStr")
                        tripRepository.insertTripImage(image.copy(imageUri = newUriStr))
                    }
                } catch (e: SecurityException) {
                    // Esperado si no hay permiso todavía (el picker no se ha abierto)
                    Log.d(TAG_DB, "No se pudo migrar URI de galería ${image.id} (sin permiso todavía): ${e.message}")
                } catch (e: Exception) {
                    Log.e(TAG_DB, "Error al migrar URI de galería ${image.id}", e)
                }
            }
        }
    }
}
