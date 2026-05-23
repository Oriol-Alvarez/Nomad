package com.example.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app.R
import com.example.app.Routes
import com.example.app.domain.Trip
import com.example.app.ui.viewmodels.TripListViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasHotelesScreen(
    navController: NavHostController,
    selectedCurrency: String,
    viewModel: TripListViewModel
) {
    val reservations by viewModel.hotelReservations.collectAsState(initial = emptyList())
    var reservationToDelete by remember { mutableStateOf<Trip?>(null) }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            if (reservations.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.HOTEL_SEARCH) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Buscar hotel"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Cabecera universal CustomHeader sin el icono
            CustomHeader(
                content = {
                    Column {
                        Text(
                            text = stringResource(id = R.string.reserva_hotel_titulo),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = stringResource(id = R.string.reserva_hotel_subtitulo),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            )

            if (reservations.isEmpty()) {
                // Estado vacío con un diseño limpio y elegante
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Hotel,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.reserva_no_reservas),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.navigate(Routes.HOTEL_SEARCH) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = stringResource(id = R.string.reserva_buscar_cta),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(reservations) { trip ->
                        ReservaCardItem(
                            trip = trip,
                            selectedCurrency = selectedCurrency,
                            onDeleteClick = { reservationToDelete = trip }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de Confirmación de Cancelación
    if (reservationToDelete != null) {
        AlertDialog(
            onDismissRequest = { reservationToDelete = null },
            title = {
                Text(
                    text = stringResource(id = R.string.reserva_confirm_delete_title),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(
                        id = R.string.reserva_confirm_delete_msg
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        reservationToDelete?.let { viewModel.cancelHotelReservation(it) }
                        reservationToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.reserva_confirm_delete_btn),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { reservationToDelete = null }) {
                    Text(text = stringResource(id = R.string.reserva_cancelar_btn))
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
fun ReservaCardItem(
    trip: Trip,
    selectedCurrency: String,
    onDeleteClick: () -> Unit
) {
    // Resolver URL completa de la imagen del hotel
    val rawImgUrl = trip.hotelImageUrl ?: ""
    val hotelImgUrl = if (rawImgUrl.startsWith("http")) rawImgUrl else "http://15.224.84.148:8090$rawImgUrl"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Miniatura del hotel con borde redondeado y puntuación de estrellas
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = if (rawImgUrl.isNotEmpty()) hotelImgUrl else R.drawable.viaje_predefinido,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Rating de Estrellas
                    if (trip.hotelRating > 0) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(4.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${trip.hotelRating}.0",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Información de la reserva
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = trip.hotelName ?: stringResource(id = R.string.act_tipo_hotel),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.padding(end = 24.dp) // Dejar espacio para el botón de borrar de la esquina
                    )
                    
                    // Tipo de Habitación
                    trip.roomType?.let {
                        Text(
                            text = "Tipo habitación: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // ID Reserva
                    trip.reservationId?.let {
                        Text(
                            text = "ID: #$it",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = Color.Gray
                        )
                    }

                    // Nombre del viaje relacionado
                    Text(
                        text = "Viaje: ${trip.title}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // Fechas
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CalendarMonth,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${trip.reservationStartDate} ➔ ${trip.reservationEndDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Costo en formato Píldora Verde igual que DetalleViajeScreen
                val nightsCount = calculateNights(trip.reservationStartDate, trip.reservationEndDate)
                val hotelCost = nightsCount * trip.roomPrice
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text(
                        text = CurrencyConverter.convert(hotelCost, selectedCurrency),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Botón de Cancelar en la esquina de la tarjeta (Top-Right)
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Cancelar",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun calculateNights(start: String?, end: String?): Int {
    if (start.isNullOrEmpty() || end.isNullOrEmpty()) return 1
    val formats = listOf("dd/MM/yyyy", "yyyy-MM-dd", "d/M/yyyy", "dd-MM-yyyy")
    for (fmt in formats) {
        try {
            val sdf = SimpleDateFormat(fmt, Locale.getDefault())
            sdf.isLenient = false
            val startDate = sdf.parse(start)
            val endDate = sdf.parse(end)
            if (startDate != null && endDate != null) {
                val diff = endDate.time - startDate.time
                val nights = (diff / (1000 * 60 * 60 * 24)).toInt()
                return if (nights > 0) nights else 1
            }
        } catch (e: Exception) {
            continue
        }
    }
    return 1
}

