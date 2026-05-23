package com.example.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.app.R
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app.domain.Hotel
import com.example.app.domain.Room
import com.example.app.domain.Trip
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import com.example.app.ui.viewmodels.BookingState
import com.example.app.ui.viewmodels.HotelViewModel
import com.example.app.ui.viewmodels.UiState
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    navController: NavHostController,
    hotelId: String,
    startDate: String,
    endDate: String,
    city: String,
    viewModel: HotelViewModel
) {
    val context = LocalContext.current
    val hotelsState by viewModel.hotelsState.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()

    // Formatear fechas para UI
    val uiStart = startDate.replace("-", "/")
    val uiEnd = endDate.replace("-", "/")
    val nights = viewModel.calculateNights(uiStart, uiEnd)

    var selectedRoomForBooking by remember { mutableStateOf<Room?>(null) }
    var guestName by remember { mutableStateOf("") }
    var guestEmail by remember { mutableStateOf("") }

    // Inicializar datos del usuario logueado
    LaunchedEffect(Unit) {
        guestName = viewModel.getCurrentUserName()
        guestEmail = viewModel.getCurrentUserEmail()
        viewModel.resetBookingState()
    }

    // Buscar hotel en la lista cargada
    val hotel = (hotelsState as? UiState.Success)?.data?.find { it.id == hotelId }

    // Escuchar el estado de la reserva
    LaunchedEffect(bookingState) {
        when (val state = bookingState) {
            is BookingState.Success -> {
                Toast.makeText(context, context.getString(R.string.hotel_detail_exito), Toast.LENGTH_LONG).show()
                viewModel.resetBookingState()
                selectedRoomForBooking = null
                // Navegar de vuelta a la lista de viajes (detalle_viaje)
                navController.navigate("detalle_viaje") {
                    popUpTo("home") { inclusive = false }
                }
            }
            is BookingState.Error -> {
                Toast.makeText(context, context.getString(R.string.auth_error_prefix, state.message), Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (hotel == null) {
                item {
                    // Cabecera vacía con botón atrás
                    CustomHeader(title = stringResource(id = R.string.hotel_detail_cargando), showBackButton = true)
                }
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                val fullUrl = if (hotel.imageUrl.startsWith("http")) hotel.imageUrl else "http://15.224.84.148:8090${hotel.imageUrl}"
                item {
                    // Cabecera con imagen de fondo del hotel
                    CustomHeader(
                        title = hotel.name,
                        subtitle = hotel.address,
                        showBackButton = true,
                        backgroundImageRes = fullUrl
                    )
                }

                // Resumen de fechas
                item {
                    Box(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text(
                                        stringResource(id = R.string.hotel_detail_tu_estancia),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        "$uiStart - $uiEnd ($nights ${stringResource(id = if (nights == 1) R.string.hotel_search_por_noche else R.string.hotel_search_por_noches)})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Título Habitaciones
                item {
                    Text(
                        text = stringResource(id = R.string.hotel_detail_habitaciones_disponibles),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                    )
                }

                // Habitaciones
                if (hotel.rooms.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(id = R.string.hotel_detail_no_habitaciones), color = Color.Gray)
                        }
                    }
                } else {
                    items(hotel.rooms) { room ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            RoomItemCard(room = room, nights = nights) {
                                selectedRoomForBooking = room
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal de Confirmación de Reserva
    if (selectedRoomForBooking != null && hotel != null) {
        val room = selectedRoomForBooking!!
        val totalPrice = nights * room.price
        val activeTrips by viewModel.activeTrips.collectAsState()
        var selectedTrip by remember(activeTrips) { mutableStateOf(activeTrips.firstOrNull()) }
        var dropdownExpanded by remember { mutableStateOf(false) }

        val isDatesValid = selectedTrip?.let { trip ->
            isBookingWithinTripRange(uiStart, uiEnd, trip.dataInici, trip.dataFinal)
        } ?: false

        AlertDialog(
            onDismissRequest = {
                if (bookingState !is BookingState.Loading) {
                    selectedRoomForBooking = null
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth(0.95f),
            title = {
                Text(stringResource(id = R.string.hotel_detail_confirmar_reserva), fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(hotel.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(stringResource(id = R.string.hotel_detail_habitacion, room.roomType), style = MaterialTheme.typography.bodyMedium)
                            Text(stringResource(id = R.string.hotel_detail_fechas, uiStart, uiEnd), style = MaterialTheme.typography.bodyMedium)
                            Text(stringResource(id = R.string.hotel_detail_estancia, nights, stringResource(id = if (nights == 1) R.string.hotel_search_por_noche else R.string.hotel_search_por_noches)), style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(stringResource(id = R.string.hotel_detail_total_pagar), fontWeight = FontWeight.Bold)
                                Text("€${totalPrice.toInt()}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                            }
                        }
                    }

                    // Dropdown Selector para Viajes Activos
                    Column {
                        Text(
                            text = "Asociar a un viaje activo:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (activeTrips.isEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "No tienes ningún viaje activo. Crea un viaje antes de reservar.",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = selectedTrip?.let { "${it.title} (${it.dataInici} - ${it.dataFinal})" } ?: "Selecciona un viaje...",
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        Icon(
                                            imageVector = if (dropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                            contentDescription = null
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable(enabled = bookingState !is BookingState.Loading) {
                                            dropdownExpanded = true
                                        }
                                )
                                DropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .background(MaterialTheme.colorScheme.surface)
                                ) {
                                    activeTrips.forEach { trip ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(trip.title, fontWeight = FontWeight.Bold)
                                                    Text("${trip.dataInici} - ${trip.dataFinal}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                                }
                                            },
                                            onClick = {
                                                selectedTrip = trip
                                                dropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Red Warning Banner if booking is not within range of selected trip
                    if (selectedTrip != null && !isDatesValid) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Las fechas seleccionadas de la reserva deben estar dentro del rango de fechas del viaje (${selectedTrip?.dataInici} a ${selectedTrip?.dataFinal})",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = guestName,
                        onValueChange = { guestName = it },
                        label = { Text(stringResource(id = R.string.hotel_detail_nombre_huesped)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = bookingState !is BookingState.Loading
                    )

                    OutlinedTextField(
                        value = guestEmail,
                        onValueChange = { guestEmail = it },
                        label = { Text(stringResource(id = R.string.hotel_detail_email_contacto)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        enabled = bookingState !is BookingState.Loading
                    )

                    if (bookingState is BookingState.Loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (guestName.isBlank() || guestEmail.isBlank()) {
                            Toast.makeText(context, context.getString(R.string.hotel_detail_error_campos), Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val tripId = selectedTrip?.id ?: return@Button
                        viewModel.bookRoom(
                            hotel = hotel,
                            room = room,
                            start = uiStart,
                            end = uiEnd,
                            guestName = guestName,
                            guestEmail = guestEmail,
                            selectedTripId = tripId
                        ) {}
                    },
                    enabled = bookingState !is BookingState.Loading && selectedTrip != null && isDatesValid,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(id = R.string.hotel_detail_confirmar_reserva), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedRoomForBooking = null },
                    enabled = bookingState !is BookingState.Loading
                ) {
                    Text(stringResource(id = R.string.hotel_detail_cancelar))
                }
            }
        )
    }
}

@Composable
fun RoomItemCard(
    room: Room,
    nights: Int,
    onBookClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
    ) {
        Column {
            // Imagen de la habitación
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val imageUrl = room.images.firstOrNull() ?: ""
                val fullUrl = if (imageUrl.startsWith("http")) imageUrl else "http://15.224.84.148:8090$imageUrl"
                AsyncImage(
                    model = fullUrl,
                    contentDescription = room.roomType,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Badge Tipo de Habitación
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        room.roomType.uppercase(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(id = R.string.hotel_search_precio_noche, room.price.toInt()),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(id = R.string.hotel_detail_total_noches, nights, stringResource(id = if (nights == 1) R.string.hotel_search_por_noche else R.string.hotel_search_por_noches), (nights * room.price).toInt()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = onBookClick,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(id = R.string.hotel_detail_reservar), fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

private fun parseScreenDate(dateString: String): java.util.Date? {
    val formats = listOf("dd/MM/yyyy", "yyyy-MM-dd", "d/M/yyyy")
    for (fmt in formats) {
        try {
            val sdf = SimpleDateFormat(fmt, Locale.getDefault())
            sdf.isLenient = false
            val parsed = sdf.parse(dateString)
            if (parsed != null) return parsed
        } catch (e: Exception) {
            // ignore and try next
        }
    }
    return null
}

private fun isBookingWithinTripRange(
    bookingStart: String,
    bookingEnd: String,
    tripStart: String,
    tripEnd: String
): Boolean {
    val bStart = parseScreenDate(bookingStart) ?: return false
    val bEnd = parseScreenDate(bookingEnd) ?: return false
    val tStart = parseScreenDate(tripStart) ?: return false
    val tEnd = parseScreenDate(tripEnd) ?: return false
    
    return !bStart.before(tStart) && !bEnd.after(tEnd)
}

