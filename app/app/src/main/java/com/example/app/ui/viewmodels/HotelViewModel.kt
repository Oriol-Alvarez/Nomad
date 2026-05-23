package com.example.app.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.BuildConfig
import com.example.app.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HotelViewModel @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val tripRepository: TripRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var city by mutableStateOf("Barcelona")
    var startDate by mutableStateOf("")
    var endDate by mutableStateOf("")

    private val _hotelsState = MutableStateFlow<UiState<List<Hotel>>>(UiState.Empty)
    val hotelsState: StateFlow<UiState<List<Hotel>>> = _hotelsState.asStateFlow()

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Idle)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    val localReservations: StateFlow<List<Trip>> = authRepository.getAuthStateFlow()
        .map { it?.uid }
        .distinctUntilChanged()
        .flatMapLatest { uid ->
            if (uid != null) {
                tripRepository.getTripsForUser(uid).map { list ->
                    list.filter { it.hasReservation }
                }
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun cancelReservation(trip: Trip, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val resId = trip.reservationId
        if (resId.isNullOrBlank()) {
            onError("ID de reserva inválido")
            return
        }
        viewModelScope.launch {
            try {
                hotelRepository.cancelReservationById(resId)
                tripRepository.deleteTrip(trip.id)
                onSuccess()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Error al cancelar la reserva en el servidor")
            }
        }
    }

    init {
        // Pre-populate with dates: today and tomorrow
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val today = Date()
        val tomorrow = Date(today.time + (1000 * 60 * 60 * 24))
        startDate = sdf.format(today)
        endDate = sdf.format(tomorrow)
    }

    fun getCurrentUserEmail(): String = authRepository.getCurrentUser()?.email ?: ""
    fun getCurrentUserName(): String = authRepository.getCurrentUser()?.displayName ?: "Viajero Nomad"

    fun searchAvailability() {
        if (startDate.isBlank() || endDate.isBlank()) {
            _hotelsState.value = UiState.Error("Selecciona fechas de inicio y fin")
            return
        }

        _hotelsState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val apiStart = convertUIDateToApiDate(startDate)
                val apiEnd = convertUIDateToApiDate(endDate)
                val results = hotelRepository.getAvailability(
                    groupId = BuildConfig.GROUP_ID,
                    startDate = apiStart,
                    endDate = apiEnd,
                    city = getCityCode(city)
                )
                _hotelsState.value = UiState.Success(results)
            } catch (e: Exception) {
                _hotelsState.value = UiState.Error(e.localizedMessage ?: "Error de red al buscar hoteles")
            }
        }
    }

    fun bookRoom(
        hotel: Hotel,
        room: Room,
        start: String,
        end: String,
        guestName: String,
        guestEmail: String,
        onSuccess: () -> Unit
    ) {
        _bookingState.value = BookingState.Loading
        viewModelScope.launch {
            try {
                val apiStart = convertUIDateToApiDate(start)
                val apiEnd = convertUIDateToApiDate(end)
                
                val reservation = hotelRepository.reserveRoom(
                    groupId = BuildConfig.GROUP_ID,
                    hotelId = hotel.id,
                    roomId = room.id,
                    startDate = apiStart,
                    endDate = apiEnd,
                    guestName = guestName,
                    guestEmail = guestEmail
                )

                // Save locally in Room as a new Trip
                val currentUserId = authRepository.getCurrentUser()?.uid ?: "anonymous"
                
                val nights = calculateNights(start, end)
                val totalCost = nights * room.price

                val newTrip = Trip(
                    id = UUID.randomUUID().toString(),
                    userId = currentUserId,
                    title = "Hotel en ${hotel.name}",
                    country = city,
                    description = "Estancia en habitación ${room.roomType}. ID: ${reservation.id}",
                    dataInici = start,
                    dataFinal = end,
                    imageUri = hotel.imageUrl,
                    isFeatured = false,
                    budget = totalCost,
                    hasReservation = true,
                    reservationId = reservation.id,
                    hotelId = hotel.id,
                    hotelName = hotel.name,
                    hotelAddress = hotel.address,
                    hotelRating = hotel.rating,
                    hotelImageUrl = hotel.imageUrl,
                    roomId = room.id,
                    roomType = room.roomType,
                    roomPrice = room.price,
                    reservationStartDate = start,
                    reservationEndDate = end,
                    guestName = guestName,
                    guestEmail = guestEmail
                )

                tripRepository.insertTrip(newTrip)
                _bookingState.value = BookingState.Success(reservation)
                onSuccess()
            } catch (e: Exception) {
                _bookingState.value = BookingState.Error(e.localizedMessage ?: "Error al reservar la habitación")
            }
        }
    }

    fun resetBookingState() {
        _bookingState.value = BookingState.Idle
    }

    private fun getCityCode(fullName: String): String {
        return when (fullName.lowercase()) {
            "londres" -> "LON"
            "parís", "paris" -> "PAR"
            "barcelona" -> "BCN"
            else -> fullName.uppercase().take(3)
        }
    }

    private fun convertUIDateToApiDate(uiDate: String): String {
        val parts = uiDate.split("/")
        if (parts.size == 3) {
            return "${parts[2]}-${parts[1]}-${parts[0]}"
        }
        return uiDate
    }

    fun calculateNights(start: String, end: String): Int {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val startDateObj = sdf.parse(start)
            val endDateObj = sdf.parse(end)
            if (startDateObj != null && endDateObj != null) {
                val diff = endDateObj.time - startDateObj.time
                val nights = (diff / (1000 * 60 * 60 * 24)).toInt()
                return if (nights > 0) nights else 1
            }
        } catch (e: Exception) {
            // fallback
        }
        return 1
    }
}

sealed class UiState<out T> {
    object Empty : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

sealed class BookingState {
    object Idle : BookingState()
    object Loading : BookingState()
    data class Success(val reservation: Reservation) : BookingState()
    data class Error(val message: String) : BookingState()
}
