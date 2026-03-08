package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun FormularioViaje(
    navController: NavHostController,
    previewStep: Int? = null,
    ciudadDestino: String = ""
) {
    // Variables de estado para el primer paso (Información general del viaje)
    var title by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf(if (ciudadDestino == "{ciudad}") "" else ciudadDestino) }
    var description by rememberSaveable { mutableStateOf("") }
    var fechaIda by rememberSaveable { mutableStateOf("") }
    var fechaVuelta by rememberSaveable { mutableStateOf("") }

    // Variable de estado para el segundo paso (Lista de actividades programadas)
    var listaItinerarios by rememberSaveable { mutableStateOf(listOf<Map<String, String>>()) }

    // Controladores de navegación interna del formulario y visibilidad de ventanas emergentes
    var etapaActual by rememberSaveable { mutableIntStateOf(previewStep ?: 0) }
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }

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
            // Cabecera dinámica que adapta su título y subtítulo según el paso actual
            CustomHeader(
                if (etapaActual == 0) "Nuevo Viaje" else "Itinerario",
                if (etapaActual == 0) "Paso 1: Detalles" else "Paso 2: Actividades",
                true
            )

            // Contenedor que gestiona las transiciones de entrada y salida entre los pasos del formulario
            AnimatedContent(
                targetState = etapaActual,
                transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                label = "NavigationAnim"
            ) { targetEtapa ->
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (targetEtapa == 0) {
                        // VISTA 1: Formulario de configuración inicial del viaje
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

                        // Agrupación horizontal para los selectores de fecha de inicio y fin
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SelectorFechaModular(
                                label = "Ida",
                                fechaSeleccionada = fechaIda,
                                onFechaElegida = { fechaIda = it },
                                modifier = Modifier.weight(1f)
                            )
                            SelectorFechaModular(
                                label = "Vuelta",
                                fechaSeleccionada = fechaVuelta,
                                onFechaElegida = { fechaVuelta = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Botón de avance al segundo paso del formulario
                        Button(
                            onClick = { etapaActual = 1 },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = Color.White
                            )
                        ) {
                            Text("AÑADIR ITINERARIO", fontWeight = FontWeight.Bold)
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                        }

                    } else {
                        // VISTA 2: Constructor del itinerario y lista de actividades
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { etapaActual = 0 }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                            Text("Actividades para $title", style = MaterialTheme.typography.titleMedium)
                        }

                        // Estado vacío o iteración sobre las actividades añadidas
                        if (listaItinerarios.isEmpty()) {
                            Text(
                                "No hay actividades aún. Haz clic en el botón '+'",
                                modifier = Modifier.padding(vertical = 40.dp).fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        } else {
                            listaItinerarios.forEach { act ->
                                // Representación visual de cada actividad en formato de lista
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = act["hora"] ?: "00:00",
                                        modifier = Modifier.width(60.dp),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray
                                    )
                                    Surface(
                                        modifier = Modifier.size(48.dp),
                                        shape = CircleShape,
                                        color = Color(0xFFE3F2FD)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = when(act["tipo"]) {
                                                    "Vuelo" -> Icons.Default.Flight
                                                    "Restaurante" -> Icons.Default.Restaurant
                                                    "Museo" -> Icons.Default.Museum
                                                    "Hotel" -> Icons.Default.Hotel
                                                    else -> Icons.Default.ConfirmationNumber
                                                },
                                                contentDescription = null,
                                                tint = Color(0xFF1976D2),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(act["nombre"] ?: "", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("${act["tipo"]} • ${act["dia"]}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                        Text("€${act["precio"]}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                    }
                                }
                            }
                        }

                        // Controles inferiores para añadir nuevas paradas o finalizar el viaje
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { mostrarDialogo = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Icon(Icons.Default.AddCircle, null)
                                Text("Añadir parada")
                            }
                            Button(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f),colors = ButtonDefaults.buttonColors( contentColor = Color.White, containerColor= MaterialTheme.colorScheme.surfaceContainer)) {
                                Icon(Icons.Default.CheckCircle, null)
                                Text("Crear viaje")
                            }
                        }
                    }
                }
            }
        }
    }

    // Invocación de la ventana modal al solicitar añadir una nueva actividad
    if (mostrarDialogo) {
        DialogoNuevaActividad(
            onDismiss = { mostrarDialogo = false },
            onGuardar = { nuevaAct ->
                listaItinerarios = listaItinerarios + nuevaAct
                mostrarDialogo = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFechaModular(
    label: String,
    fechaSeleccionada: String,
    onFechaElegida: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Componente auxiliar que envuelve un DatePickerDialog nativo de Material 3
    var mostrar by remember { mutableStateOf(false) }
    val state = rememberDatePickerState()

    // Campo de texto de solo lectura que actúa como disparador del modal
    Box(modifier = modifier.clickable { mostrar = true }) {
        OutlinedTextField(
            value = fechaSeleccionada,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(Icons.Default.DateRange, null) },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }

    if (mostrar) {
        DatePickerDialog(
            onDismissRequest = { mostrar = false },
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
            confirmButton = {
                Button(
                    onClick = {
                        val formatted = state.selectedDateMillis?.let {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                        } ?: ""
                        onFechaElegida(formatted)
                        mostrar = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrar = false }) { Text("Cancelar", color = Color.Gray) }
            }
        ) {
            DatePicker(
                state = state,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    headlineContentColor = Color.Black,
                    dayContentColor = Color.Black,
                    selectedDayContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    selectedDayContentColor = Color.White,
                    todayContentColor = MaterialTheme.colorScheme.surfaceContainer,
                    todayDateBorderColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorHoraModular(
    label: String,
    horaSeleccionada: String,
    onHoraElegida: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Componente auxiliar para la selección de tiempo utilizando un TimePicker nativo
    var mostrar by remember { mutableStateOf(false) }
    val state = rememberTimePickerState()

    // Campo de texto interactivo que despliega el selector horario
    Box(modifier = modifier.clickable { mostrar = true }) {
        OutlinedTextField(
            value = horaSeleccionada,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(Icons.Default.AccessTime, null) },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }

    if (mostrar) {
        AlertDialog(
            onDismissRequest = { mostrar = false },
            containerColor = Color.White,
            confirmButton = {
                Button(
                    onClick = {
                        onHoraElegida(String.format("%02d:%02d", state.hour, state.minute))
                        mostrar = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) { Text("Aceptar", color = Color.White) }
            },
            text = {
                // Configuración visual del reloj para mantener la consistencia con el tema principal
                TimePicker(
                    state = state,
                    colors = TimePickerDefaults.colors(
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContainerColor = Color(0xFFF5F5F5),
                        timeSelectorUnselectedContentColor = Color.Black,
                        clockDialColor = Color.White,
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = Color.Black,
                        selectorColor = MaterialTheme.colorScheme.surfaceContainer,
                        periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        periodSelectorSelectedContentColor = Color.White
                    )
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoNuevaActividad(
    onDismiss: () -> Unit,
    onGuardar: (Map<String, String>) -> Unit
) {
    // Variables de estado del formulario interno de creación de actividades
    var n by rememberSaveable { mutableStateOf("") }
    var d by rememberSaveable { mutableStateOf("") }
    var h by rememberSaveable { mutableStateOf("") }
    var p by rememberSaveable { mutableStateOf("") }
    var t by rememberSaveable { mutableStateOf("Vuelo") }
    var exp by rememberSaveable { mutableStateOf(false) }

    // Cuadro de diálogo modal que contiene los campos de entrada para el itinerario
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        title = { Text("Nueva Actividad", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = n, onValueChange = { n = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())

                SelectorFechaModular(label = "Día", fechaSeleccionada = d, onFechaElegida = { d = it })
                SelectorHoraModular(label = "Hora", horaSeleccionada = h, onHoraElegida = { h = it })

                OutlinedTextField(value = p, onValueChange = { p = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())

                // Selector desplegable para categorizar el tipo de actividad (define el icono a mostrar)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = t, onValueChange = {}, readOnly = true, label = { Text("Tipo") },
                        trailingIcon = { IconButton(onClick = { exp = true }) { Icon(Icons.Default.KeyboardArrowDown, null) } },
                        modifier = Modifier.fillMaxWidth().clickable { exp = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(disabledTextColor = Color.White, disabledBorderColor = MaterialTheme.colorScheme.outline)
                    )
                    DropdownMenu(expanded = exp, onDismissRequest = { exp = false }, containerColor = MaterialTheme.colorScheme.background) {
                        listOf("Vuelo", "Restaurante", "Museo", "Hotel", "Ocio").forEach { opcion ->
                            DropdownMenuItem(text = { Text(opcion) }, onClick = { t = opcion; exp = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(mapOf("nombre" to n, "dia" to d, "hora" to h, "precio" to p, "tipo" to t)) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer, contentColor = Color.White)
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

// Bloque de vistas previas (Previews) para el editor de diseño
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

@Preview(
    name = "Formulario Paso 2 Itinerario",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewFormularioPaso2Black() {
    AppTheme {
        FormularioViaje(
            navController = rememberNavController(),
            previewStep = 1
        )
    }
}

@Preview(
    name = "Preview Unificada Itinerario",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDialogoLimpio() {
    AppTheme {
        DialogoNuevaActividad(
            onDismiss = {},
            onGuardar = {}
        )
    }
}