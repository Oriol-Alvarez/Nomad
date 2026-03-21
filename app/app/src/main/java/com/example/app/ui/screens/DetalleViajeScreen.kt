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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.app.R
import com.example.app.Routes
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.viewmodels.TripListViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun DetalleViajeScreen(
    navController: NavHostController,
    selectedCurrency: String,
    viewModel: TripListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val tripsFromDB = viewModel.trips
    var tripIdToDelete by remember { mutableStateOf<String?>(null) }

    if (tripIdToDelete != null) {
        AlertDialog(
            onDismissRequest = { tripIdToDelete = null },
            title = { Text("Eliminar viaje") },
            text = { Text("¿Estás seguro de que deseas eliminar este viaje? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        tripIdToDelete?.let { viewModel.deleteTrip(it) }
                        tripIdToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { tripIdToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("form-viaje?ciudad=") },
                containerColor = Color(0xFF0288D1),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
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
                CustomHeader(title = "Mis Viajes")
            }

            if (tripsFromDB.isEmpty()) {
                item {
                    Text(
                        "No tienes viajes planeados.",
                        modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }

            items(tripsFromDB) { trip ->
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



fun formatShortDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    // Lista con los formatos más habituales. El código probará uno a uno hasta acertar.
    val formatosPosibles = listOf("yyyy-MM-dd", "yyyy/MM/dd", "dd/MM/yyyy", "dd-MM-yyyy")

    for (patron in formatosPosibles) {
        try {
            val input = SimpleDateFormat(patron, Locale.getDefault())
            input.isLenient = false

            val date = input.parse(dateString)

            if (date != null) {
                val output = SimpleDateFormat("dd MMM", Locale("es", "ES"))
                return output.format(date).lowercase()
            }
        } catch (e: Exception) {
            // Si el patrón no coincide, ignoramos el error y pasamos al siguiente
            continue
        }
    }

    // Si la fecha viniera con un texto rarísimo que no encaja en nada, devolvemos el original
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

            // Botón de eliminar arriba a la derecha
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
                    contentDescription = "Eliminar viaje",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}