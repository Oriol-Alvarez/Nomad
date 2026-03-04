package com.example.app.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.ui.theme.AppTheme

// Modelo de datos simplificado para la galería
data class GalleryTrip(
    val title: String,
    val image: Int
)

@Composable
fun GaleriaViajesScreen(navController: NavHostController) {
    val trips = listOf(
        GalleryTrip("La antigua Roma", R.drawable.roma),
        GalleryTrip("Frío en Noruega", R.drawable.noruega),
        GalleryTrip("Negocios en Londres", R.drawable.londres)
    )

    Scaffold(
        bottomBar = { PrivateBottomNavigationBar(navController) },
        // El botón flotante "+" abajo a la derecha
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Acción para añadir foto/viaje */ },
                containerColor = Color(0xFF0288D1),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(
                                text = "Galería", // Título cambiado
                                color = Color.White,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // CONTENIDO SCROLLABLE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(trips) { trip ->
                        GalleryCard(trip)
                    }
                }
            }
        }
    }
}

@Composable
fun GalleryCard(trip: GalleryTrip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = trip.image),
                contentDescription = "Imagen de ${trip.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    // Si al ser una galería quieres que la foto se vea más grande,
                    // puedes subir este valor (ej. 180.dp o 200.dp)
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )

            // Información inferior solo con el título
            Column(modifier = Modifier.padding(vertical = 18.dp, horizontal = 16.dp)) {
                Text(
                    text = trip.title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Marcado como 'private' para evitar conflictos con DetalleViajeScreen.kt
@Composable
private fun PrivateBottomNavigationBar(navController: NavHostController) {
    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route ?: "home"

    Surface(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrivateBottomItem(
                icon = painterResource(id = R.drawable.house_solid_full),
                label = "Home",
                selected = currentRoute == "home"
            ) { navController.navigate("home") }

            PrivateBottomItem(
                icon = painterResource(id = R.drawable.plane_solid_full),
                label = "Viajes",
                selected = currentRoute == "viajes"
            ) { navController.navigate("viajes") }

            PrivateBottomItem(
                icon = painterResource(id = R.drawable.image_solid_full),
                label = "Galeria",
                selected = currentRoute == "galeria"
            ) { navController.navigate("galeria") }

            PrivateBottomItem(
                icon = painterResource(id = R.drawable.gear_solid_full),
                label = "Ajustes",
                selected = currentRoute == "ajustes"
            ) { navController.navigate("ajustes") }
        }
    }
}

@Composable
private fun PrivateBottomItem(
    icon: Painter,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GaleriaViajesPreview() {
    AppTheme {
        GaleriaViajesScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun GaleriaViajesPreviewNight() {
    AppTheme {
        GaleriaViajesScreen(navController = rememberNavController())
    }
}