package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.ui.theme.AppTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun TerminosCondicionesScreen(navController: NavHostController) {
    val context = LocalContext.current
    // Accedemos a las preferencias compartidas
    val sharedPreferences = remember {
        context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
    }
    var accepted by remember {
        mutableStateOf(sharedPreferences.getBoolean("terms_accepted", false))
    }
    var checkboxChecked by remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // Hero
            CustomHeader("Términos y condiciones", "Ultima actualización 05 de marzo de 2026", true)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = "Términos y Condiciones de Nomad",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = """
Bienvenido a Nomad. Al utilizar esta aplicación aceptas los siguientes términos y condiciones.

1. Uso de la aplicación
Nomad es una herramienta diseñada para ayudar a los usuarios a planificar viajes, organizar itinerarios y descubrir lugares de interés.

2. Funcionalidades
La aplicación permite planificar rutas, buscar lugares cercanos, guardar imágenes de viajes y recibir recomendaciones basadas en inteligencia artificial.

3. Responsabilidad
Nomad proporciona información orientativa. No garantizamos la exactitud o disponibilidad de los lugares o servicios mostrados.

4. Propiedad intelectual
Todo el contenido, diseño y código de la aplicación pertenece a sus desarrolladores y no puede ser copiado o distribuido sin autorización.

5. Cambios en los términos
Los desarrolladores pueden modificar estos términos en futuras actualizaciones de la aplicación.

Equipo de desarrollo:
Oriol Alvarez Arisa
Guillem Talayero Carrasco
                            """.trimIndent()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))



                if (!accepted) {

                    // Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkboxChecked,
                            onCheckedChange = { checkboxChecked = it }
                        )

                        Text("He leído y acepto los Términos y Condiciones")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        OutlinedButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Text("Rechazar")
                        }

                        Button(
                            onClick = {
                                sharedPreferences.edit().putBoolean("terms_accepted", true).apply()
                                accepted = true
                            },
                            enabled = checkboxChecked
                        ) {
                            Text("Aceptar")
                        }
                    }

                } else {

                    // Mensaje cuando ya aceptó
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "✅ Términos aceptados",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Ya has aceptado los Términos y Condiciones de Nomad."
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun TerminosYcondicionesPreviewDark() {
    AppTheme {
        TerminosCondicionesScreen(
            navController = rememberNavController()
        )
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun TerminosYcondicionesPreviewLight() {
    AppTheme {
        TerminosCondicionesScreen(
            navController = rememberNavController()
        )
    }
}
