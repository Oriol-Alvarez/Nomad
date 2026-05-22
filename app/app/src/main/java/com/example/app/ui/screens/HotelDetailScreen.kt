package com.example.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.app.ui.viewmodels.BookingState
import com.example.app.ui.viewmodels.HotelViewModel
import com.example.app.ui.viewmodels.UiState

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (hotel == null) {
                // Cabecera vacía con botón atrás
                CustomHeader(title = stringResource(id = R.string.hotel_detail_cargando), showBackButton = true)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Cabecera con imagen de fondo del hotel
                val fullUrl = if (hotel.imageUrl.startsWith("http")) hotel.imageUrl else "http://15.224.84.148:8090${hotel.imageUrl}"
                CustomHeader(
                    title = hotel.name,
                    subtitle = hotel.address,
                    showBackButton = true,
                    backgroundImageRes = fullUrl
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Resumen de fechas
                    item {
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

                    // Título Habitaciones
                    item {
                        Text(
                            text = stringResource(id = R.string.hotel_detail_habitaciones_disponibles),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Habitaciones
                    if (hotel.rooms.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(stringResource(id = R.string.hotel_detail_no_habitaciones), color = Color.Gray)
                            }
                        }
                    } else {
                        items(hotel.rooms) { room ->
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
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
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
                        viewModel.bookRoom(
                            hotel = hotel,
                            room = room,
                            start = uiStart,
                            end = uiEnd,
                            guestName = guestName,
                            guestEmail = guestEmail
                        ) {}
                    },
                    enabled = bookingState !is BookingState.Loading,
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
