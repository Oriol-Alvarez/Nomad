package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.domain.ItineraryItem
import com.example.app.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun FormularioViaje(
    navController: NavHostController,
    previewStep: Int? = null
) {
    // 1. Estados de la "Página 1" (Datos Generales)
    var title by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // 2. Estados de la "Página 2" (Itinerarios - Guardados como Lista de Mapas)
    // Cada mapa tendrá: "nombre", "hora", "precio", "tipo"
    var listaItinerarios by remember { mutableStateOf(listOf<Map<String, String>>()) }

    // 3. Control de Navegación y Diálogo
    var etapaActual by remember { mutableIntStateOf(previewStep ?: 0) }
    var mostrarDialogo by remember { mutableStateOf(false) }

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
            // Cabecera Dinámica
            HeroTerminosyCondiciones(
                navController,
                if (etapaActual == 0) "Nuevo Viaje" else "Itinerario",
                if (etapaActual == 0) "Paso 1: Detalles" else "Paso 2: Actividades"
            )

            // Animación entre formularios
            AnimatedContent(
                targetState = etapaActual,
                transitionSpec = { fadeIn() with fadeOut() }
            ) { targetEtapa ->
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (targetEtapa == 0) {
                        // --- VISTA 1: FORMULARIO PRINCIPAL ---
                        Text("Detalles del destino", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título del viaje") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            label = { Text("País / Ciudad") },
                            leadingIcon = { Icon(Icons.Default.Place, null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = { etapaActual = 1 },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("AÑADIR ITINERARIO", fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.ArrowForward, null)
                        }

                    } else {
                        // --- VISTA 2: LISTA DE ITINERARIOS ---
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { etapaActual = 0 }) {
                                Icon(Icons.Default.ArrowBack, null)
                            }
                            Text("Actividades para $title", style = MaterialTheme.typography.titleMedium)
                        }

                        if (listaItinerarios.isEmpty()) {
                            Text(
                                "No hay actividades aún. Haz clic en el botón '+'",
                                modifier = Modifier.padding(vertical = 40.dp).align(Alignment.CenterHorizontally),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            // Renderizado de cada mapa en la lista
                            listaItinerarios.forEach { act ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                ) {
                                    ListItem(
                                        headlineContent = { Text(act["nombre"] ?: "", fontWeight = FontWeight.Bold) },
                                        supportingContent = { Text("${act["tipo"]} • ${act["hora"]}") },
                                        trailingContent = { Text("${act["precio"]}€", fontWeight = FontWeight.Bold) },
                                        leadingContent = { Icon(Icons.Default.CheckCircle, tint = MaterialTheme.colorScheme.primary, contentDescription = null) }
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Button(
                                onClick = { mostrarDialogo = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Icon(Icons.Default.AddCircle, null)
                                Text("Añadir parada")
                            }

                            Button(
                                onClick = {
                                    // TODO guardar viaje en base de datos
                                    navController.popBackStack()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, null)
                                Text("Crear viaje")
                            }
                        }
                    }
                }
            }
        }
    }

    // --- MINI FORMULARIO (DIALOGO) ---
    if (mostrarDialogo) {
        var n by remember { mutableStateOf("") }
        var h by remember { mutableStateOf("") }
        var p by remember { mutableStateOf("") }
        var t by remember { mutableStateOf("Vuelo") }
        var exp by remember { mutableStateOf(false) }
        // Dentro del bloque if (mostrarDialogo)
        var mostrarReloj by remember { mutableStateOf(false) }
        val state = rememberTimePickerState(initialHour = 12, initialMinute = 0, is24Hour = true)

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Nueva Actividad") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = n, onValueChange = { n = it }, label = { Text("Nombre") })
                    OutlinedTextField(
                        value = h,
                        onValueChange = { },
                        readOnly = true, // Evita que se abra el teclado
                        label = { Text("Hora") },
                        leadingIcon = { Icon(Icons.Filled.DateRange, null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { mostrarReloj = true }, // Al tocar el campo, se abre el reloj
                        enabled = false, // Lo ponemos en falso para que el click lo gestione el Modifier
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    OutlinedTextField(value = p, onValueChange = { p = it }, label = { Text("Precio") })

                    // Selector simple
                    Box {
                        OutlinedTextField(
                            value = t,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo") },
                            trailingIcon = { IconButton(onClick = { exp = true }) { Icon(Icons.Default.KeyboardArrowDown, null) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = exp, onDismissRequest = { exp = false }) {
                            listOf("Vuelo", "Restaurante", "Museo", "Hotel").forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = { t = opcion; exp = false }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Guardamos como un simple Mapa de Strings
                    val nuevaAct = mapOf("nombre" to n, "hora" to h, "precio" to p, "tipo" to t)
                    listaItinerarios = listaItinerarios + nuevaAct
                    mostrarDialogo = false
                }) { Text("Guardar") }
            }
        )
        if (mostrarReloj) {
            AlertDialog(
                onDismissRequest = { mostrarReloj = false },
                confirmButton = {
                    Button(
                        onClick = {
                            h = String.format("%02d:%02d", state.hour, state.minute)
                            mostrarReloj = false
                        }
                    ) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { mostrarReloj = false }
                    ) {
                        Text("Cancelar")
                    }
                },
                text = {
                    TimePicker(state = state)
                }
            )
        }
    }
}



@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun FormularioViajePreviewDark() {
    AppTheme {
        FormularioViaje(
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
fun FormularioViajePreviewLight() {
    AppTheme {
        FormularioViaje(
            navController = rememberNavController()
        )
    }
}

@Preview(
    name = "Formulario Paso 1",
    showBackground = true
)
@Composable
fun PreviewFormularioPaso1() {
    AppTheme {
        FormularioViaje(
            navController = rememberNavController(),
            previewStep = 0
        )
    }
}

@Preview(
    name = "Formulario Paso 2 Itinerario",
    showBackground = true
)
@Composable
fun PreviewFormularioPaso2() {
    AppTheme {
        FormularioViaje(
            navController = rememberNavController(),
            previewStep = 1
        )
    }
}
