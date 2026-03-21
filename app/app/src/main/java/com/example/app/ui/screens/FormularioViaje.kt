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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.test.isFocused
import coil.compose.AsyncImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.window.PopupProperties // Para el buscador
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import com.example.app.domain.ItineraryItem
import java.text.SimpleDateFormat
import java.util.Locale


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
    // Reemplaza la línea antigua de listaItinerarios por esta:
    var listaItinerarios by remember { mutableStateOf(listOf<ItineraryItem>()) }
    var etapaActual by rememberSaveable { mutableIntStateOf(previewStep ?: 0) }
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    var indiceEdicion by rememberSaveable { mutableIntStateOf(-1) }
    val minimaVueltaMs = remember(fechaIda) {
        if (fechaIda.isNotEmpty()) {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                // OBLIGATORIO PARA QUE COMPOSE NO SE EQUIVOQUE DE DÍA
                sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                sdf.parse(fechaIda)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        } else {
            System.currentTimeMillis()
        }
    }
    val launcherPrincipal = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }
    LaunchedEffect(ciudadDestino) {
        val textoLimpio = ciudadDestino.replace("{ciudad}", "").trim()

        if (textoLimpio.length > 2) {
            scope.launch {
                try {
                    // Llamamos a tu función de búsqueda OSM
                    val resultados = buscarCiudadesOSM(textoLimpio)

                    if (resultados.isNotEmpty()) {
                        val primeraOpcion = resultados[0]

                        // Actualizamos el estado con la primera sugerencia oficial
                        country = TextFieldValue(
                            text = primeraOpcion,
                            selection = TextRange(primeraOpcion.length)
                        )

                        // Al ser un valor de OSM, debería pasar tu Validator
                        if (Validator.isValidLocation(primeraOpcion)) {
                            errorCountry = null
                        }

                        // Cerramos el menú ya que hemos auto-seleccionado
                        expandido = false
                        sugerencias = emptyList()
                    }
                } catch (e: Exception) {
                    // Manejo de error silencioso o log
                }
            }
        }
    }
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
                                    if (Validator.isValidLocation(newValue.text)) errorCountry =
                                        null

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
                                supportingText = {
                                    if (errorCountry != null) {
                                        Text(
                                            text = errorCountry!!,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = if (errorCountry != null) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                // --- BOTÓN X PARA BORRAR TODO ---
                                trailingIcon = {
                                    if (country.text.isNotEmpty()) {
                                        IconButton(
                                            onClick = {
                                                country = TextFieldValue("")
                                                expandido = false
                                                sugerencias = emptyList()
                                                errorCountry = null
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Limpiar campo",
                                                tint = if (errorCountry != null) MaterialTheme.colorScheme.error
                                                else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        // --- LÓGICA AL SALIR DEL FOCO ---
                                        if (isFocused && !focusState.isFocused) {
                                            val currentText = country.text
                                            if (currentText.length > 2 && !Validator.isValidLocation(
                                                    currentText
                                                )
                                            ) {
                                                scope.launch {
                                                    val resultados = buscarCiudadesOSM(currentText)
                                                    if (resultados.isNotEmpty()) {
                                                        val mejorCoincidencia = resultados[0]
                                                        country = TextFieldValue(
                                                            text = mejorCoincidencia,
                                                            selection = TextRange(mejorCoincidencia.length)
                                                        )
                                                        errorCountry = null
                                                        expandido = false
                                                    }
                                                }
                                            }
                                        }
                                        isFocused = focusState.isFocused
                                    },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    errorBorderColor = MaterialTheme.colorScheme.error,
                                    errorLabelColor = MaterialTheme.colorScheme.error,
                                    errorSupportingTextColor = MaterialTheme.colorScheme.error
                                )
                            )

                            // Menú de sugerencias
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
                                    fechaMinima = System.currentTimeMillis(),
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
                                    // --- AQUÍ APLICAMOS LA LÓGICA ---
                                    fechaMinima = minimaVueltaMs,
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
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
                                val fechasRellenas =
                                    fechaIda.isNotEmpty() && fechaVuelta.isNotEmpty()
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
                            Text(
                                "AÑADIR ITINERARIO",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.inversePrimary
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                null,
                                tint = MaterialTheme.colorScheme.inversePrimary
                            )
                        }

                    } else {
                        // -------------------------------------------------------
                        // VISTA: PASO 2 (ITINERARIO)
                        // -------------------------------------------------------
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { etapaActual = 0 }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                            Text(
                                "Actividades para $title",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        if (listaItinerarios.isEmpty()) {
                            Text(
                                text = "No hay itinerario todavía.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                color = Color.Gray
                            )
                        } else {
                            listaItinerarios.forEachIndexed { index, act ->
                                ItemItinerario(
                                    act = act,
                                    onEdit = {
                                        indiceEdicion = index
                                        mostrarDialogo = true
                                    },
                                    onDelete = {
                                        // Filtramos la lista para quitar el elemento actual
                                        listaItinerarios = listaItinerarios.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
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
                                    val budget =
                                        listaItinerarios.sumOf { it.precio.toDoubleOrNull() ?: 0.0 }
                                    viewModel.saveTrip(
                                        title = title,
                                        destination = country.text,
                                        dataInici = fechaIda,
                                        dataFinal = fechaVuelta,
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
                                Icon(
                                    Icons.Default.CheckCircle,
                                    null,
                                    tint = MaterialTheme.colorScheme.inversePrimary
                                )
                                Spacer(Modifier.width(4.dp))
                                Text("Finalizar", color = MaterialTheme.colorScheme.inversePrimary)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        // Ejemplo de conversión rápida (asegúrate de que el formato coincida con tu SelectorFecha)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val inicioMs = try {
            sdf.parse(fechaIda)?.time
        } catch (e: Exception) {
            null
        }
        val finMs = try {
            sdf.parse(fechaVuelta)?.time
        } catch (e: Exception) {
            null
        }

        DialogoNuevaActividad(
            actividadAEditar = if (indiceEdicion != -1) listaItinerarios[indiceEdicion] else null,
            fechaInicioViaje = inicioMs,
            fechaFinViaje = finMs,
            listaExistente = listaItinerarios,
            onDismiss = {
                mostrarDialogo = false
                indiceEdicion = -1
            },
            onGuardar = { nuevaAct ->
                val listaMutable = listaItinerarios.toMutableList()
                if (indiceEdicion != -1) {
                    listaMutable[indiceEdicion] = nuevaAct
                } else {
                    listaMutable.add(nuevaAct)
                }
                // Ordenar por día y hora automáticamente sería un puntazo aquí
                listaItinerarios = listaMutable
                mostrarDialogo = false
                indiceEdicion = -1
            }
        )
    }
}

// ----------------------------------------------------------------------------
// DIÁLOGO CON VALIDACIÓN DE PRECIO Y NOMBRE
// ----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoNuevaActividad(
    actividadAEditar: ItineraryItem? = null,
    fechaInicioViaje: Long? = null,
    fechaFinViaje: Long? = null,
    listaExistente: List<ItineraryItem>,
    onDismiss: () -> Unit,
    onGuardar: (ItineraryItem) -> Unit
) {
    // Si editamos, cogemos sus valores. Si no, vacío.
    var n by remember { mutableStateOf(actividadAEditar?.nombre ?: "") }
    var d by remember { mutableStateOf(actividadAEditar?.dia ?: "") }
    var h by remember { mutableStateOf(actividadAEditar?.hora ?: "") }
    var p by remember { mutableStateOf(actividadAEditar?.precio ?: "") }
    var t by remember { mutableStateOf(actividadAEditar?.tipo ?: "Vuelo") }
    var desc by remember { mutableStateOf(actividadAEditar?.descripcion ?: "") }

    var errorN by remember { mutableStateOf(false) }
    var errorD by remember { mutableStateOf(false) }
    var errorH by remember { mutableStateOf(false) }
    var errorDesc by remember { mutableStateOf(false) }
    var errorHoraRepetida by remember { mutableStateOf(false) }
    var exp by remember { mutableStateOf(false) }

    val monedaSimbolo = "€"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Text(if (actividadAEditar == null) "Nueva Parada" else "Editar Parada")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // --- 1. Nombre ---
                Column {
                    OutlinedTextField(
                        value = n,
                        onValueChange = {
                            n = it
                            errorN = false
                        },
                        label = { Text("Nombre de la actividad *") },
                        isError = errorN,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (errorN) {
                        Text(
                            text = "El nombre es obligatorio",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    }
                }

                // --- 2. Día ---
                Column {
                    SelectorFechaModular(
                        label = "Día *",
                        fechaSeleccionada = d,
                        fechaMinima = fechaInicioViaje,
                        fechaMaxima = fechaFinViaje,
                        onFechaElegida = {
                            d = it
                            errorD = false
                        },
                        isError = errorD
                    )
                    if (errorD) {
                        Text(
                            text = "Selecciona un día",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    }
                }

                // --- 3. Hora ---
                Column {
                    SelectorHoraModular(
                        label = "Hora *",
                        horaSeleccionada = h,
                        onHoraElegida = {
                            h = it
                            errorH = false
                            errorHoraRepetida = false
                        },
                        isError = errorH
                    )
                    if (errorH) {
                        Text(
                            text = "Selecciona una hora",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    } else if (errorHoraRepetida) {
                        Text(
                            text = "Esta hora ya está ocupada",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    }
                }

                // --- 4. Precio ---
                OutlinedTextField(
                    value = p,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.matches(Regex("""^\d*[.,]?\d{0,2}$"""))) {
                            p = input.replace(",", ".")
                        }
                    },
                    label = { Text("Precio ($monedaSimbolo)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // --- 5. Notas ---
                OutlinedTextField(
                    value = desc,
                    onValueChange = {
                        desc = it
                        errorDesc = false
                    },
                    label = { Text("Notas / Descripción *") },
                    isError = errorDesc,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    supportingText = {
                        if (errorDesc) {
                            Text("La descripción es obligatoria")
                        }
                    }
                )

                // --- 6. Tipo ---
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = t,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Tipo") },
                        trailingIcon = {
                            IconButton(onClick = { exp = true }) {
                                Icon(Icons.Default.KeyboardArrowDown, null)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { exp = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    DropdownMenu(
                        expanded = exp,
                        onDismissRequest = { exp = false },
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        listOf(
                            "Vuelo",
                            "Restaurante",
                            "Hotel",
                            "Museo",
                            "Ocio",
                            "Otros"
                        ).forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    t = opcion
                                    exp = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Comprobamos colisión usando el id de la actividad
                    val colision =
                        listaExistente.any { it.dia == d && it.hora == h && it.id != actividadAEditar?.id }

                    errorN = n.isBlank()
                    errorD = d.isBlank()
                    errorH = h.isBlank()
                    errorDesc = desc.isBlank()
                    errorHoraRepetida = !errorH && colision

                    if (!errorN && !errorD && !errorH && !errorDesc && !errorHoraRepetida) {
                        onGuardar(
                            ItineraryItem(
                                // Si estamos editando, mantenemos el ID. Si es nueva, generamos uno nuevo.
                                id = actividadAEditar?.id ?: java.util.UUID.randomUUID().toString(),
                                tripId = "", // Todavía no sabemos el Trip ID, se lo ponemos en el ViewModel
                                nombre = n,
                                dia = d,
                                hora = h,
                                precio = if (p.isEmpty()) "0" else p,
                                tipo = t,
                                descripcion = desc
                            )
                        )
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// ----------------------------------------------------------------------------
// COMPONENTES AUXILIARES
// ----------------------------------------------------------------------------
@Composable
fun CampoSeleccionImagen(
    uriSeleccionada: Uri?,
    label: String,
    onBorrar: () -> Unit,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = if (uriSeleccionada != null) "Imagen seleccionada" else "",
        onValueChange = {},
        readOnly = true,
        enabled = false,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                if (uriSeleccionada != null) Icons.Default.CheckCircle else Icons.Default.Image,
                null
            )
        },
        trailingIcon = {
            if (uriSeleccionada != null) IconButton(onBorrar) {
                Icon(
                    Icons.Default.Clear,
                    null
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = OutlinedTextFieldDefaults.colors(
            // Colores cuando está deshabilitado (nuestro caso)
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,

            ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ItemItinerario(
    act: ItineraryItem, // <-- Usamos el modelo real
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val (icono, colorFondo, colorIcono) = when (act.tipo) {
        "Vuelo" -> Triple(Icons.Default.Flight, Color(0xFFE3F2FD), Color(0xFF1976D2))
        "Hotel" -> Triple(Icons.Default.Hotel, Color(0xFFF3E5F5), Color(0xFF7B1FA2))
        "Restaurante" -> Triple(Icons.Default.Restaurant, Color(0xFFFFF3E0), Color(0xFFE65100))
        "Museo" -> Triple(Icons.Default.Museum, Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "Otros" -> Triple(Icons.Default.Accessibility, Color(0xFFF5F5F5), Color(0xFF616161))
        else -> Triple(Icons.Default.ConfirmationNumber, Color(0xFFFFEBEE), Color(0xFFC62828))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier
            .clickable { onEdit() }
            .padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = colorFondo) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = colorIcono,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = act.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${act.dia} • ${act.hora} (${act.tipo})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = act.descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "€${act.precio}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFE65100)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp)
                    .size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Clear,
                    contentDescription = "Borrar",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
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
            onGuardar = {},
            listaExistente = emptyList()
        )
    }
}

// ----------------------------------------------------------------------------
// PREVIEWS DE LOS ITEMS (COPIAR Y PEGAR)
// ----------------------------------------------------------------------------

// ----------------------------------------------------------------------------
// PREVIEWS DE LOS ITEMS
// ----------------------------------------------------------------------------

@Preview(
    name = "Item Completo - Modo Claro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PreviewItemCompletoLight() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ItemItinerario(
                act = ItineraryItem(
                    id = "preview_1",
                    tripId = "trip_preview",
                    nombre = "Cena Romántica",
                    dia = "22 Mar",
                    hora = "21:30",
                    precio = "65.50",
                    tipo = "Restaurante",
                    descripcion = "Reserva a nombre de Juan. Mesa cerca de la ventana con vistas al río."
                ),
                onEdit = { /* No hace nada en el preview */ },
                onDelete = { /* No hace nada en el preview */ }
            )
        }
    }
}

@Preview(
    name = "Item Simple - Modo Noche",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewItemSimpleDark() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) { // Añadido un Box con padding para que se vea mejor
            ItemItinerario(
                act = ItineraryItem(
                    id = "preview_2",
                    tripId = "trip_preview",
                    nombre = "Vuelo de Ida",
                    dia = "20 Mar",
                    hora = "08:00",
                    precio = "145.00",
                    tipo = "Vuelo",
                    descripcion = "" // Probamos cómo queda sin descripción
                ),
                onEdit = { },
                onDelete = { }
            )
        }
    }
}

@Preview(
    name = "Lista de Ejemplo",
    showBackground = true
)
@Composable
fun PreviewListaItems() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ItemItinerario(
                act = ItineraryItem(
                    id = "preview_3",
                    tripId = "trip_preview",
                    nombre = "Hotel Palace",
                    dia = "20 Mar",
                    hora = "14:00",
                    precio = "200.00",
                    tipo = "Hotel",
                    descripcion = "Check-in temprano solicitado."
                ),
                onEdit = {},
                onDelete = {}
            )
            ItemItinerario(
                act = ItineraryItem(
                    id = "preview_4",
                    tripId = "trip_preview",
                    nombre = "Museo del Prado",
                    dia = "21 Mar",
                    hora = "10:30",
                    precio = "15.00",
                    tipo = "Museo",
                    descripcion = "Entradas digitales en el correo."
                ),
                onEdit = {},
                onDelete = {}
            )
        }
    }
}