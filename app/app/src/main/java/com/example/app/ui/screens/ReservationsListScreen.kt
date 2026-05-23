package com.example.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app.R
import com.example.app.Routes
import com.example.app.domain.Trip
import com.example.app.ui.viewmodels.HotelViewModel

@Composable
fun ReservationsListScreen(
    navController: NavHostController,
    selectedCurrency: String,
    viewModel: HotelViewModel
) {
    val reservations by viewModel.localReservations.collectAsState()
    val context = LocalContext.current

    var tripToCancel by remember { mutableStateOf<Trip?>(null) }
    var isCancelling by remember { mutableStateOf(false) }

    if (tripToCancel != null) {
        AlertDialog(
            onDismissRequest = { tripToCancel = null },
            title = { Text(stringResource(id = R.string.reservations_cancel_confirm_titulo)) },
            text = { Text(stringResource(id = R.string.reservations_cancel_confirm_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trip = tripToCancel!!
                        tripToCancel = null
                        isCancelling = true
                        viewModel.cancelReservation(
                            trip = trip,
                            onSuccess = {
                                isCancelling = false
                                Toast.makeText(context, context.getString(R.string.reservations_cancel_success), Toast.LENGTH_SHORT).show()
                            },
                            onError = { errorMsg ->
                                isCancelling = false
                                Toast.makeText(context, context.getString(R.string.reservations_cancel_error, errorMsg), Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                ) {
                    Text(stringResource(id = R.string.detalle_eliminar_btn), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { tripToCancel = null }) {
                    Text(stringResource(id = R.string.detalle_cancelar_btn))
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        )
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    CustomHeader(
                        title = stringResource(id = R.string.reservations_titulo)
                    )
                }

                if (reservations.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.reservations_no_reservas),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 50.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                } else {
                    items(reservations) { trip ->
                        Box(modifier = Modifier.padding(horizontal = 25.dp)) {
                            ReservationCard(
                                trip = trip,
                                selectedCurrency = selectedCurrency,
                                onClick = { navController.navigate("${Routes.DETALLE_VIAJE2}/${trip.id}") },
                                onCancel = { tripToCancel = trip }
                            )
                        }
                    }
                }
            }

            if (isCancelling) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF0288D1))
                }
            }
        }
    }
}

@Composable
fun ReservationCard(
    trip: Trip,
    selectedCurrency: String,
    onClick: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Box {
            Column {
                val imageSource = if (!trip.hotelImageUrl.isNullOrEmpty()) {
                    trip.hotelImageUrl
                } else if (!trip.imageUri.isEmpty()) {
                    trip.imageUri
                } else {
                    R.drawable.viaje_predefinido
                }

                AsyncImage(
                    model = imageSource,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )

                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = trip.hotelName ?: trip.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (trip.hotelRating > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        ) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < trip.hotelRating) Color(0xFFFFB300) else Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (!trip.roomType.isNullOrEmpty()) {
                        Text(
                            text = stringResource(id = R.string.reservations_room_type, trip.roomType!!),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    if (!trip.guestName.isNullOrEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = trip.guestName!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Related trip badge
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.reservations_viaje_relacionado, trip.title),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${formatShortDate(trip.dataInici)} - ${formatShortDate(trip.dataFinal)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }

                        val priceToDisplay = if (trip.roomPrice > 0.0) trip.roomPrice else trip.budget
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = CurrencyConverter.convert(priceToDisplay, selectedCurrency),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
