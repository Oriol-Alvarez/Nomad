package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.viewmodels.TripListViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Image
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.window.PopupProperties // Para el buscador
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioViaje(
    navController: NavHostController,
    previewStep: Int? = null,
    ciudadDestino: String = "",
    viewModel: TripListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // --- 1. ESTADOS DE DATOS ---
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                if (ciudadDestino == "{ciudad}" || ciudadDestino.isEmpty()) "" else ciudadDestino
            )
        )
    }
    var fechaIda by rememberSaveable { mutableStateOf("") }
    var fechaVuelta by rememberSaveable { mutableStateOf("") }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // --- 2. ESTADOS DE BÚSQUEDA OSM ---
    var expandido by remember { mutableStateOf(false) }
    var sugerencias by remember { mutableStateOf(listOf<String>()) }
    var job by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()

    // --- 3. ESTADOS DE ERRORES (VALIDACIÓN) ---
    var errorTitle by remember { mutableStateOf<String?>(null) }
    var errorCountry by remember { mutableStateOf<String?>(null) }
    var errorFechas by remember { mutableStateOf<String?>(null) }

    // --- 4. LÓGICA DE UI E ITINERARIO ---
    var listaItinerarios by rememberSaveable { mutableStateOf(listOf<Map<String, String>>()) }
    var etapaActual by rememberSaveable { mutableIntStateOf(previewStep ?: 0) }
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }

    val launcherPrincipal = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            CustomHeader(
                title = if (etapaActual == 0) "Nuevo Viaje" else "Itinerario",
                subtitle = if (etapaActual == 0) "Paso 1" else "Paso 2",
                showBackButton = true
            )

            AnimatedContent(targetState = etapaActual, label = "AnimacionPasos") { targetEtapa ->
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (targetEtapa == 0) {
                        // -------------------------------------------------------
                        // VISTA: PASO 1 (DATOS GENERALES)
                        // -------------------------------------------------------

                        // Título con Validación
                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                if (Validator.isValidTitle(it)) errorTitle = null
                            },
                            label = { Text("Título del viaje") },
                            isError = errorTitle != null,
                            supportingText = { errorTitle?.let { Text(it) } },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Buscador OSM con Validación
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = country,
                                onValueChange = { newValue ->
                                    country = newValue
                                    if (Validator.isValidLocation(newValue.text)) errorCountry = null

                                    val queryText = newValue.text
                                    expandido = queryText.length > 2

                                    job?.cancel()
                                    job = scope.launch {
                                        delay(500)
                                        if (queryText.length > 2) {
                                            val list = buscarCiudadesOSM(queryText)
                                            if (country.text.length > 2) {
                                                sugerencias = list
                                                expandido = sugerencias.isNotEmpty()
                                            }
                                        } else {
                                            expandido = false
                                        }
                                    }
                                },
                                label = { Text("País / Ciudad") },
                                isError = errorCountry != null,
                                supportingText = { errorCountry?.let { Text(it) } },
                                leadingIcon = { Icon(Icons.Default.Place, null) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            DropdownMenu(
                                expanded = expandido && sugerencias.isNotEmpty(),
                                onDismissRequest = { expandido = false },
                                properties = PopupProperties(focusable = false),
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                sugerencias.forEach { ciudad ->
                                    DropdownMenuItem(
                                        text = { Text(ciudad) },
                                        onClick = {
                                            country = TextFieldValue(
                                                text = ciudad,
                                                selection = TextRange(ciudad.length)
                                            )
                                            expandido = false
                                            sugerencias = emptyList()
                                            errorCountry = null
                                        }
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Selector de Ida
                                SelectorFechaModular(
                                    label = "Ida",
                                    fechaSeleccionada = fechaIda,
                                    onFechaElegida = {
                                        fechaIda = it
                                        errorFechas = null
                                    },
                                    // Pasamos el estado de error al componente (si tu Selector lo soporta)
                                    isError = errorFechas != null,
                                    modifier = Modifier.weight(1f)
                                )

                                // Selector de Vuelta
                                SelectorFechaModular(
                                    label = "Vuelta",
                                    fechaSeleccionada = fechaVuelta,
                                    onFechaElegida = {
                                        fechaVuelta = it
                                        errorFechas = null
                                    },
                                    isError = errorFechas != null,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Mensaje de error en Rojo debajo de los campos
                            if (errorFechas != null) {
                                Text(
                                    text = errorFechas!!,
                                    color = MaterialTheme.colorScheme.error, // Color rojo del tema
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }

                        // Selección de Imagen Portada
                        CampoSeleccionImagen(
                            uriSeleccionada = selectedImageUri,
                            label = "Portada del viaje (Opcional)",
                            onBorrar = { selectedImageUri = null },
                            onClick = { launcherPrincipal.launch("image/*") }
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción (Opcional)") },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp)
                        )



                        Button(
                            onClick = {
                                // 1. Validamos Título y Localización usando tu Validator
                                val tOk = Validator.isValidTitle(title)
                                val lOk = Validator.isValidLocation(country.text)

                                // 2. Nueva lógica de fechas:
                                // Primero comprobamos que NO estén vacías.
                                // Segundo comprobamos que sean válidas entre sí.
                                val fechasRellenas = fechaIda.isNotEmpty() && fechaVuelta.isNotEmpty()
                                val fechasCoherentes = if (fechasRellenas) {
                                    Validator.areDatesValid(fechaIda, fechaVuelta)
                                } else false

                                // 3. Asignación de mensajes de error
                                if (!tOk) errorTitle = "Título requerido (3-50 carac.)"

                                if (!lOk) errorCountry = "Seleccione un destino válido de la lista"

                                if (!fechasRellenas) {
                                    errorFechas = "Debes seleccionar ambas fechas"
                                } else if (!fechasCoherentes) {
                                    errorFechas = "La vuelta no puede ser anterior a la ida"
                                }

                                // 4. Solo pasamos de página si TODO es true
                                if (tOk && lOk && fechasRellenas && fechasCoherentes) {
                                    etapaActual = 1
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        ) {
                            Text("AÑADIR ITINERARIO", fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                        }

                    } else {
                        // -------------------------------------------------------
                        // VISTA: PASO 2 (ITINERARIO)
                        // -------------------------------------------------------
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { etapaActual = 0 }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                            Text("Actividades para $title", style = MaterialTheme.typography.titleMedium)
                        }

                        if (listaItinerarios.isEmpty()) {
                            Text(
                                "No hay paradas todavía.",
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                color = Color.Gray
                            )
                        } else {
                            listaItinerarios.forEach { act -> ItemItinerario(act) }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { mostrarDialogo = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Icon(Icons.Default.AddCircle, null)
                                Spacer(Modifier.width(4.dp))
                                Text("Añadir")
                            }

                            Button(
                                onClick = {
                                    val budget = listaItinerarios.sumOf { it["precio"]?.toDoubleOrNull() ?: 0.0 }
                                    viewModel.saveTrip(
                                        title = title,
                                        destination = country.text,
                                        desc = description,
                                        budget = budget,
                                        imageUri = selectedImageUri?.toString() ?: "",
                                        activitiesFromForm = listaItinerarios
                                    )
                                    navController.popBackStack()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Icon(Icons.Default.CheckCircle, null)
                                Spacer(Modifier.width(4.dp))
                                Text("Finalizar")
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

// ----------------------------------------------------------------------------
// DIÁLOGO CON VALIDACIÓN DE PRECIO Y NOMBRE
// ----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoNuevaActividad(onDismiss: () -> Unit, onGuardar: (Map<String, String>) -> Unit) {
    var n by remember { mutableStateOf("") }
    var d by remember { mutableStateOf("") }
    var h by remember { mutableStateOf("") }
    var p by remember { mutableStateOf("") }
    var t by remember { mutableStateOf("Vuelo") }
    var exp by remember { mutableStateOf(false) }
    var errorN by remember { mutableStateOf(false) }
    var fotoActUri by remember { mutableStateOf<Uri?>(null) }

    val launcherAct = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { fotoActUri = it }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Parada", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = n,
                    onValueChange = { n = it; errorN = false },
                    label = { Text("Nombre *") },
                    isError = errorN,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )


                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio vertical entre los dos
                ) {

                    Box(modifier = Modifier.fillMaxWidth()) {
                        SelectorFechaModular(
                            label = "Día",
                            fechaSeleccionada = d,
                            onFechaElegida = { d = it }
                        )
                    }


                    Box(modifier = Modifier.fillMaxWidth()) {
                        SelectorHoraModular(
                            label = "Hora",
                            horaSeleccionada = h,
                            onHoraElegida = { h = it }
                        )
                    }
                }

                OutlinedTextField(
                    value = p,
                    onValueChange = { if (it.isEmpty() || Validator.isValidPrice(it)) p = it },
                    label = { Text("Precio (€)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = t, onValueChange = {}, readOnly = true, enabled = false,
                        label = { Text("Tipo") },
                        trailingIcon = { IconButton(onClick = { exp = true }) { Icon(Icons.Default.KeyboardArrowDown, null) } },
                        modifier = Modifier.fillMaxWidth().clickable { exp = true },
                        shape = RoundedCornerShape(12.dp)
                    )
                    DropdownMenu(expanded = exp, onDismissRequest = { exp = false }) {
                        listOf("Vuelo", "Restaurante", "Hotel", "Museo", "Ocio").forEach { opcion ->
                            DropdownMenuItem(text = { Text(opcion) }, onClick = { t = opcion; exp = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (Validator.isNotEmpty(n)) {
                    onGuardar(
                        mapOf(
                            "nombre" to n, "dia" to d, "hora" to h,
                            "precio" to if (p.isEmpty()) "0" else p,
                            "tipo" to t, "foto" to (fotoActUri?.toString() ?: "")
                        )
                    )
                } else {
                    errorN = true
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

// ----------------------------------------------------------------------------
// COMPONENTES AUXILIARES
// ----------------------------------------------------------------------------
@Composable
fun CampoSeleccionImagen(uriSeleccionada: Uri?, label: String, onBorrar: () -> Unit, onClick: () -> Unit) {
    OutlinedTextField(
        value = if (uriSeleccionada != null) "Imagen seleccionada" else "",
        onValueChange = {},
        readOnly = true,
        enabled = false,
        label = { Text(label) },
        leadingIcon = { Icon(if (uriSeleccionada != null) Icons.Default.CheckCircle else Icons.Default.Image, null) },
        trailingIcon = { if (uriSeleccionada != null) IconButton(onBorrar) { Icon(Icons.Default.Clear, null) } },
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ItemItinerario(act: Map<String, String>) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(50.dp), shape = CircleShape, color = Color(0xFFE3F2FD)) {
            if (!act["foto"].isNullOrEmpty()) {
                AsyncImage(model = act["foto"], contentDescription = null, contentScale = ContentScale.Crop)
            } else {
                Icon(Icons.Default.Place, null, tint = Color(0xFF1976D2), modifier = Modifier.padding(12.dp))
            }
        }
        Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
            Text(act["nombre"] ?: "", fontWeight = FontWeight.Bold)
            Text("${act["tipo"]} • ${act["hora"]}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text("€${act["precio"]}", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
    }
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
