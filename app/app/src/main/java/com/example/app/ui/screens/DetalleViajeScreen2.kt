package com.example.app.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
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
import com.example.app.R
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.viewmodels.TripListViewModel

@Composable
fun DetalleViajeScreen2(
    navController: NavHostController,
    selectedCurrency: String,
    tripId: String,
    viewModel: TripListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val trip = viewModel.trips.find { it.id == tripId }

    val title = trip?.title ?: "Detalle del Viaje"
    val subtitle = trip?.country ?: "Sin destino"
    val budget = trip?.budget ?: 0.0

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Nota: Cambia R.drawable.roma si tu CustomHeader soporta URIs dinámicas
            CustomHeader(
                title = title,
                subtitle = subtitle,
                showBackButton = true,
                backgroundImageRes = R.drawable.roma
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(value = "8", label = "NIGHTS", modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray.copy(alpha = 0.5f))
                StatItem(value = CurrencyConverter.convert(budget, selectedCurrency), label = "BUDGET", modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray.copy(alpha = 0.5f))
                StatItem(value = trip?.activities?.size?.toString() ?: "0", label = "ACTIVITIES", modifier = Modifier.weight(1f))
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), thickness = 1.dp)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                val activities = trip?.activities ?: emptyList()

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
                    item { DayHeader("TU ITINERARIO") }

                    items(activities) { activity ->
                        TimelineEvent(
                            time = "Prog.", // Placeholder para la hora
                            icon = Icons.Default.AccountBalance,
                            iconBg = Color(0xFFF3E5F5),
                            iconTint = Color(0xFF7B1FA2),
                            title = activity.activityName,
                            subtitle = activity.locationName,
                            price = CurrencyConverter.convert(activity.cost, selectedCurrency),
                            priceColor = if (activity.cost > 0) Color(0xFFE65100) else Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
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