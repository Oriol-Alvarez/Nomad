package com.example.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Home") }
            )
        },
        content = { innerPadding ->
            // Contenido de la pantalla
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenido a la HomeScreen",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de ejemplo para navegar
                Button(
                    onClick = { navController.navigate(Routes.DETALLE_VIAJE) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir a Detalle del Viaje")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate(Routes.GALERIA_VIAJE) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir a Galería del Viaje")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate(Routes.PREFERENCIAS) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir a Preferencias")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate(Routes.SOBRE_NOSOTROS) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir a Sobre Nosotros")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate(Routes.TERMINOS_CONDICIONES) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir a Términos y Condiciones")
                }
            }
        }
    )
}



