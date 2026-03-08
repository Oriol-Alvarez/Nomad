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
    // 1. Estados de la "Página 1" (Datos Generales)
    var title by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf(if (ciudadDestino == "{ciudad}") "" else ciudadDestino) }
    var description by rememberSaveable { mutableStateOf("") }
    var fechaIda by rememberSaveable { mutableStateOf("") }
    var fechaVuelta by rememberSaveable { mutableStateOf("") }

    // 2. Estados de la "Página 2" (Itinerarios)
    var listaItinerarios by rememberSaveable { mutableStateOf(listOf<Map<String, String>>()) }

    // 3. Control de Navegación y Diálogo
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
            HeroTerminosyCondiciones(
                navController,
                if (etapaActual == 0) "Nuevo Viaje" else "Itinerario",
                if (etapaActual == 0) "Paso 1: Detalles" else "Paso 2: Actividades"
            )

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
                        // --- VISTA 1: DETALLES ---
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
                        // --- VISTA 2: ITINERARIO ---
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { etapaActual = 0 }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                            Text("Actividades para $title", style = MaterialTheme.typography.titleMedium)
                        }

                        if (listaItinerarios.isEmpty()) {
                            Text(
                                "No hay actividades aún. Haz clic en el botón '+'",
                                modifier = Modifier.padding(vertical = 40.dp).fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        } else {
                            listaItinerarios.forEach { act ->
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

// --- COMPONENTE MODULAR DE FECHA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFechaModular(
    label: String,
    fechaSeleccionada: String,
    onFechaElegida: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrar by remember { mutableStateOf(false) }
    val state = rememberDatePickerState()

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
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.background, ),
            confirmButton = {
                Button(
                    onClick = {
                        val formatted = state.selectedDateMillis?.let {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                        } ?: ""
                        onFechaElegida(formatted)
                        mostrar = false
                    },
                    // Aquí configuramos el fondo azul (Primary) y el contenido blanco
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp) // Opcional: para que combine con tus otros campos
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

// --- COMPONENTE MODULAR DE HORA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorHoraModular(
    label: String,
    horaSeleccionada: String,
    onHoraElegida: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrar by remember { mutableStateOf(false) }
    val state = rememberTimePickerState()

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
                TimePicker(
                    state = state,
                    colors = TimePickerDefaults.colors(
// Fondo de los cuadros de hora y minuto (el rectángulo donde sale el número)
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.surfaceContainer, // Azul cuando se elige
                        timeSelectorSelectedContentColor = Color.White,         // Texto blanco
                        timeSelectorUnselectedContainerColor = Color(0xFFF5F5F5), // Gris muy claro si no está seleccionado
                        timeSelectorUnselectedContentColor = Color.Black,       // Texto negro

                        // La esfera del reloj (donde están los números del 1 al 12/24)
                        clockDialColor = Color.White,             // Fondo blanco de la esfera
                        clockDialSelectedContentColor = Color.White, // Número seleccionado en blanco
                        clockDialUnselectedContentColor = Color.Black, // Números normales en negro

                        // El selector (la aguja o círculo que se mueve)
                        selectorColor = MaterialTheme.colorScheme.surfaceContainer, // Color azul de la aguja/círculo

                        // El punto central del reloj
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
    var n by rememberSaveable { mutableStateOf("") }
    var d by rememberSaveable { mutableStateOf("") }
    var h by rememberSaveable { mutableStateOf("") }
    var p by rememberSaveable { mutableStateOf("") }
    var t by rememberSaveable { mutableStateOf("Vuelo") }
    var exp by rememberSaveable { mutableStateOf(false) }

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
        // Simplemente llamamos a la función real
        DialogoNuevaActividad(
            onDismiss = { /* No hace nada en preview */ },
            onGuardar = { /* No hace nada en preview */ }
        )
    }
}
