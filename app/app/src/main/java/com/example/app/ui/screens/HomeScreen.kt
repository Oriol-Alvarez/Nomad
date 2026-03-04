package com.example.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.Routes
import com.example.app.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nomad",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            Color.White
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {

            // Hero Section
            Text(
                text = "Explora el mundo 🌍",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Encuentra tu próxima aventura con Nomad",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sección de navegación en tarjetas
            NavigationCard(
                title = "Detalle del Viaje",
                icon = Icons.Default.AirplanemodeActive,
                onClick = { navController.navigate(Routes.DETALLE_VIAJE) }
            )

            NavigationCard(
                title = "Galería del Viaje",
                icon = Icons.Default.Image,
                onClick = { navController.navigate(Routes.GALERIA_VIAJE) }
            )

            NavigationCard(
                title = "Preferencias",
                icon = Icons.Default.Settings,
                onClick = { navController.navigate(Routes.PREFERENCIAS) }
            )

            NavigationCard(
                title = "Sobre Nosotros",
                icon = Icons.Default.Info,
                onClick = { navController.navigate(Routes.SOBRE_NOSOTROS) }
            )

            NavigationCard(
                title = "Términos y Condiciones",
                icon = Icons.Default.Description,
                onClick = { navController.navigate(Routes.TERMINOS_CONDICIONES) }
            )
        }
    }
}

@Composable
fun NavigationCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(12.dp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    // Envolvemos la pantalla en AppTheme para ver los colores correctos
    AppTheme {
        // Usamos rememberNavController() para pasar un NavController falso a la vista previa
        HomeScreen(navController = rememberNavController())
    }
}