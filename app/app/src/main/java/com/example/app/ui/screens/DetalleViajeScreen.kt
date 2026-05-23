package com.example.app.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app.R
import com.example.app.Routes
import com.example.app.ui.viewmodels.TripListViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleViajeScreen(
    navController: NavHostController,
    selectedCurrency: String,
    viewModel: TripListViewModel
) {
    // T1.6: Observamos los cambios en la base de datos a través del StateFlow del ViewModel
    val tripsFromDB by viewModel.trips.collectAsState()
    var tripIdToDelete by remember { mutableStateOf<String?>(null) }

    if (tripIdToDelete != null) {
        AlertDialog(
            onDismissRequest = { tripIdToDelete = null },
            title = { Text(stringResource(id = R.string.detalle_eliminar_titulo)) },
            text = { Text(stringResource(id = R.string.detalle_eliminar_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        tripIdToDelete?.let { viewModel.deleteTrip(it) }
                        tripIdToDelete = null
                    }
                ) {
                    Text(stringResource(id = R.string.detalle_eliminar_btn), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { tripIdToDelete = null }) {
                    Text(stringResource(id = R.string.detalle_cancelar_btn))
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        )
    }

    val today = remember {
        java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.time
    }

    val currentTrips = remember(tripsFromDB, today) {
        tripsFromDB.filter { trip ->
            val start = parseTripDate(trip.dataInici)
            val end = parseTripDate(trip.dataFinal)
            start != null && end != null && !today.before(start) && !today.after(end)
        }
    }

    val futureTrips = remember(tripsFromDB, today) {
        tripsFromDB.filter { trip ->
            val start = parseTripDate(trip.dataInici)
            start != null && today.before(start)
        }
    }

    val pastTrips = remember(tripsFromDB, today) {
        tripsFromDB.filter { trip ->
            val end = parseTripDate(trip.dataFinal)
            end != null && today.after(end)
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("form-viaje?ciudad=") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir viaje")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                CustomHeader(title = stringResource(id = R.string.detalle_mis_viajes))
            }

            if (tripsFromDB.isEmpty()) {
                item {
                    Text(
                        stringResource(id = R.string.detalle_no_viajes),
                        modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color.Gray
                    )
                }
            } else {
                // 1. VIAJE ACTUAL
                if (currentTrips.isNotEmpty()) {
                    item {
                        SectionSeparator(title = "Viaje actual", isGreen = true)
                    }
                    items(currentTrips) { trip ->
                        Box(modifier = Modifier.padding(horizontal = 25.dp)) {
                            TripCardModule(
                                title = trip.title,
                                date = "${formatShortDate(trip.dataInici)} - ${formatShortDate(trip.dataFinal)}",
                                price = trip.budget,
                                imageUri = trip.imageUri,
                                selectedCurrency = selectedCurrency,
                                onClick = { navController.navigate("${Routes.DETALLE_VIAJE2}/${trip.id}") },
                                onDelete = { tripIdToDelete = trip.id }
                            )
                        }
                    }
                }

                // 2. FUTUROS VIAJES
                if (futureTrips.isNotEmpty()) {
                    item {
                        SectionSeparator(title = "Futuros viajes", isGreen = false)
                    }
                    items(futureTrips) { trip ->
                        Box(modifier = Modifier.padding(horizontal = 25.dp)) {
                            TripCardModule(
                                title = trip.title,
                                date = "${formatShortDate(trip.dataInici)} - ${formatShortDate(trip.dataFinal)}",
                                price = trip.budget,
                                imageUri = trip.imageUri,
                                selectedCurrency = selectedCurrency,
                                onClick = { navController.navigate("${Routes.DETALLE_VIAJE2}/${trip.id}") },
                                onDelete = { tripIdToDelete = trip.id }
                            )
                        }
                    }
                }

                // 3. VIAJES PASADOS
                if (pastTrips.isNotEmpty()) {
                    item {
                        SectionSeparator(title = "Viajes pasados", isGreen = false)
                    }
                    items(pastTrips) { trip ->
                        Box(modifier = Modifier.padding(horizontal = 25.dp)) {
                            TripCardModule(
                                title = trip.title,
                                date = "${formatShortDate(trip.dataInici)} - ${formatShortDate(trip.dataFinal)}",
                                price = trip.budget,
                                imageUri = trip.imageUri,
                                selectedCurrency = selectedCurrency,
                                onClick = { navController.navigate("${Routes.DETALLE_VIAJE2}/${trip.id}") },
                                onDelete = { tripIdToDelete = trip.id }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun parseTripDate(dateString: String?): java.util.Date? {
    if (dateString.isNullOrEmpty()) return null
    val formatosPosibles = listOf("yyyy-MM-dd", "yyyy/MM/dd", "dd/MM/yyyy", "dd-MM-yyyy")
    for (patron in formatosPosibles) {
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

@Composable
fun SectionSeparator(title: String, isGreen: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 8.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.5.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isGreen) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onBackground
        )
    }
}

fun formatShortDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    val formatosPosibles = listOf("yyyy-MM-dd", "yyyy/MM/dd", "dd/MM/yyyy", "dd-MM-yyyy")

    for (patron in formatosPosibles) {
        try {
            val input = SimpleDateFormat(patron, Locale.getDefault())
            input.isLenient = false

            val date = input.parse(dateString)

            if (date != null) {
                val output = SimpleDateFormat("dd MMM", Locale.getDefault())
                return output.format(date).lowercase()
            }
        } catch (e: Exception) {
            continue
        }
    }

    return dateString
}

@Composable
fun TripCardModule(
    title: String,
    date: String,
    price: Double,
    imageUri: String,
    selectedCurrency: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick() }
    ) {
        Box {
            Column {
                if (imageUri.isNotEmpty()) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.viaje_predefinido),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

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
                                text = date,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }

                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = CurrencyConverter.convert(price, selectedCurrency),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onDelete,
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
