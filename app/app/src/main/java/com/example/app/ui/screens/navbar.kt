package com.example.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.app.R

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    // Observador del estado de la pila de navegación para identificar la ruta activa
    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route ?: "home"

    // Contenedor de la barra con el color de superficie definido en el tema
    Surface(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
    ) {
        // Disposición horizontal de los destinos de navegación
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Ítem: Pantalla principal
            BottomItem(
                icon = painterResource(id = R.drawable.house_solid_full),
                label = "Home",
                selected = currentRoute == "home"
            ) { navController.navigate("home") }

            // Ítem: Listado y detalles de itinerarios
            BottomItem(
                icon = painterResource(id = R.drawable.plane_solid_full),
                label = "Viajes",
                selected = currentRoute == "detalle_viaje"
            ) { navController.navigate("detalle_viaje") }

            // Ítem: Álbumes de fotos y recuerdos
            BottomItem(
                icon = painterResource(id = R.drawable.gallery_solid_full),
                label = "Galería",
                selected = currentRoute == "galeria_viaje"
            ) { navController.navigate("galeria_viaje") }

            // Ítem: Configuración y perfil de usuario
            BottomItem(
                icon = painterResource(id = R.drawable.settings_solid_full),
                label = "Ajustes",
                selected = currentRoute == "preferencias"
            ) { navController.navigate("preferencias") }
        }
    }
}

@Composable
fun BottomItem(
    icon: Painter,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    // Celda individual con estados visuales (color primario para activo, variante para inactivo)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {

        // Representación gráfica del destino
        Icon(
            painter = icon,
            contentDescription = label,
            tint = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

        // Etiqueta descriptiva inferior
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}