package com.example.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.domain.ItineraryItem
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.viewmodels.TripListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// 1. Funciones para formatear texto legible
fun formatDateHeader(dateStr: String): String {
    return try {
        val inputSdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputSdf = SimpleDateFormat("dd MMM", Locale("es", "ES"))
        val date = inputSdf.parse(dateStr)
        date?.let { outputSdf.format(it).uppercase() } ?: dateStr
    } catch (e: Exception) {
        dateStr
    }
}

fun calcularNoches(inicio: String?, fin: String?): String {
    if (inicio.isNullOrEmpty() || fin.isNullOrEmpty()) return "0"

    val formatosPosibles = listOf(
        "dd/MM/yyyy", "d/M/yyyy",
        "yyyy-MM-dd", "yyyy-M-d",
        "yyyy/MM/dd", "yyyy/M/d",
        "dd-MM-yyyy"
    )

    for (patron in formatosPosibles) {
        try {
            val sdf = SimpleDateFormat(patron, Locale.getDefault())
            sdf.isLenient = false

            val startDate = sdf.parse(inicio)
            val endDate = sdf.parse(fin)

            if (startDate != null && endDate != null) {
                val diffInMillis = endDate.time - startDate.time
                val noches = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)

                if (noches > 0) return noches.toString()
            }
        } catch (e: Exception) {
            continue
        }
    }

    return "0"
}

@Composable
fun DetalleViajeScreen2(
    navController: NavHostController,
    selectedCurrency: String,
    tripId: String,
    viewModel: TripListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val trip = viewModel.getTripById(tripId)
    val activities = viewModel.getActivitiesForTrip(tripId)

    val title = trip?.title ?: "Detalle del Viaje"
    val subtitle = trip?.country ?: "Sin destino"
    val budget = trip?.budget ?: 0.0
    val imageUri = trip?.imageUri ?: ""

    val nochesReales = calcularNoches(trip?.dataInici, trip?.dataFinal)

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            CustomHeader(
                title = title,
                subtitle = subtitle,
                showBackButton = true,
                backgroundImageRes = imageUri
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(value = nochesReales, label = "NIGHTS", modifier = Modifier.weight(1f))

                HorizontalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray.copy(alpha = 0.5f))
                StatItem(value = CurrencyConverter.convert(budget, selectedCurrency), label = "BUDGET", modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray.copy(alpha = 0.5f))
                StatItem(value = activities.size.toString(), label = "ACTIVITIES", modifier = Modifier.weight(1f))
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), thickness = 1.dp)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (activities.isEmpty()) {
                    item {
                        Text(
                            text = "Aún no has añadido actividades a este viaje.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp, start = 24.dp, end = 24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    // 2. ORDENAMOS Y AGRUPAMOS
                    val sdfSort = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val sortedActivities = activities.sortedBy { 
                        try { sdfSort.parse("${it.dia} ${it.hora}")?.time ?: 0L } catch(e: Exception) { 0L }
                    }

                    // Agrupamos por la fecha formateada para el header
                    val groupedActivities = sortedActivities.groupBy { it.dia }

                    // Iteramos sobre cada grupo de días
                    groupedActivities.forEach { (diaOriginal, activitiesForDay) ->

                        // Pintamos la cabecera del día
                        item { DayHeader(formatDateHeader(diaOriginal)) }

                        // Pintamos todas las actividades de ese día concreto
                        items(activitiesForDay) { activity ->
                            val iconData = getIconForType(activity.tipo)
                            val precioDouble = activity.precio.toDoubleOrNull() ?: 0.0
                            
                            TimelineEvent(
                                time = activity.hora,
                                icon = iconData.first,
                                iconBg = iconData.second,
                                iconTint = iconData.third,
                                title = activity.nombre,
                                subtitle = activity.descripcion,
                                price = CurrencyConverter.convert(precioDouble, selectedCurrency),
                                priceColor = if (precioDouble > 0) Color(0xFFE65100) else Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getIconForType(type: String): Triple<ImageVector, Color, Color> {
    return when (type.lowercase()) {
        "transporte", "vuelo" -> Triple(Icons.Default.Flight, Color(0xFFE3F2FD), Color(0xFF1976D2))
        "alojamiento", "hotel" -> Triple(Icons.Default.Hotel, Color(0xFFE8F5E9), Color(0xFF388E3C))
        "comida", "restaurante" -> Triple(Icons.Default.Fastfood, Color(0xFFFFF3E0), Color(0xFFF57C00))
        "actividad", "ocio" -> Triple(Icons.Default.LocalActivity, Color(0xFFF3E5F5), Color(0xFF7B1FA2))
        "bus", "tren" -> Triple(Icons.Default.DirectionsBus, Color(0xFFEFEBE9), Color(0xFF5D4037))
        else -> Triple(Icons.Default.AccountBalance, Color(0xFFF5F5F5), Color(0xFF616161))
    }
}

@Composable
private fun StatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        Text(text = label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
    }
}

@Composable
private fun DayHeader(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun TimelineEvent(
    time: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    price: String,
    priceColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = time,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.width(50.dp).padding(top = 8.dp)
        )

        Surface(shape = CircleShape, color = iconBg, modifier = Modifier.size(42.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = price, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = priceColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleViajeScreen2Preview() {
    AppTheme {
        DetalleViajeScreen2(navController = rememberNavController(), selectedCurrency = "EUR(€)", tripId = "1")
    }

}
