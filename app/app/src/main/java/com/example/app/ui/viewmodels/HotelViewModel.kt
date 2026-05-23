package com.example.app.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.BuildConfig
import com.example.app.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

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

    private fun parseTripDate(dateString: String): java.util.Date? {
        val formatos = listOf("yyyy-MM-dd", "dd/MM/yyyy", "d/M/yyyy")
        for (patron in formatos) {
            try {
                val input = SimpleDateFormat(patron, Locale.getDefault())
                input.isLenient = false
                val date = input.parse(dateString)
                if (date != null) return date
            } catch (e: Exception) {
                continue
            }
        }
        return null
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeTrips: StateFlow<List<Trip>> = authRepository.getAuthStateFlow()
        .map { it?.uid }
        .distinctUntilChanged()
        .flatMapLatest { uid ->
            if (uid != null) {
                tripRepository.getTripsForUser(uid)
            } else {
                flowOf(emptyList())
            }
        }
        .map { list ->
            val today = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.time
            list.filter { trip ->
                val end = parseTripDate(trip.dataFinal)
                end != null && !end.before(today)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

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
        selectedTripId: String,
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

                // Update selected Trip in Room
                val trip = tripRepository.getTripById(selectedTripId)
                if (trip != null) {
                    val nights = calculateNights(start, end)
                    val totalCost = nights * room.price
                    
                    val updatedTrip = trip.copy(
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
                        guestEmail = guestEmail,
                        budget = trip.budget + totalCost
                    )

                    tripRepository.updateTrip(updatedTrip)
                }

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
