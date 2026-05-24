package com.example.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.app.R
import com.example.app.Routes

/**
 * Menú de navegación de la parte de abajo de la pantalla.
 */
@Composable
fun BottomNavigationBar(navController: NavHostController) {

    // Miramos en qué pantalla estamos para saber qué botón iluminar
    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route ?: "home"

    Surface(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
    ) {
        // Ponemos los botones repartidos por el ancho de la pantalla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Botón de Inicio
            BottomItem(
                icon = painterResource(id = R.drawable.house_solid_full),
                label = stringResource(id = R.string.nav_home),
                selected = currentRoute == "home"
            ) { 
                if (currentRoute != "home") {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }

            // Botón de Reservas - Unificado
            BottomItem(
                icon = rememberVectorPainter(Icons.Default.Hotel),
                label = stringResource(id = R.string.nav_reservas),
                selected = currentRoute == Routes.RESERVATIONS_LIST
            ) { 
                if (currentRoute != Routes.RESERVATIONS_LIST) {
                    navController.navigate(Routes.RESERVATIONS_LIST) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }

            // Botón de Mis Viajes
            BottomItem(
                icon = painterResource(id = R.drawable.plane_solid_full),
                label = stringResource(id = R.string.nav_viajes),
                selected = currentRoute == "detalle_viaje"
            ) { 
                if (currentRoute != "detalle_viaje") {
                    navController.navigate("detalle_viaje") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }

            // Botón de Galería
            BottomItem(
                icon = painterResource(id = R.drawable.gallery_solid_full),
                label = stringResource(id = R.string.nav_galeria),
                selected = currentRoute == "galeria_viaje"
            ) { 
                if (currentRoute != "galeria_viaje") {
                    navController.navigate("galeria_viaje") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }

            // Botón de Ajustes
            BottomItem(
                icon = painterResource(id = R.drawable.settings_solid_full),
                label = stringResource(id = R.string.nav_ajustes),
                selected = currentRoute == "preferencias"
            ) { 
                if (currentRoute != "preferencias") {
                    navController.navigate("preferencias") {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }
}

/**
 * Diseño de cada botón individual del menú de abajo.
 */
@Composable
fun BottomItem(
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

        // Dibujamos el icono (se pinta de color azul si está seleccionado)
        Icon(
            painter = icon,
            contentDescription = label,
            tint = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        // El texto debajo del icono
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
