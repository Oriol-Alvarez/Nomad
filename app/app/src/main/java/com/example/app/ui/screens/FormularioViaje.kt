package com.example.app.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.app.R
import com.example.app.domain.ItineraryItem
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.viewmodels.TripListViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla para crear un viaje nuevo paso a paso.
 * Paso 1: Datos básicos (título, destino, fechas).
 * Paso 2: Añadir actividades al plan (itinerario).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioViaje(
    navController: NavHostController,
    previewStep: Int? = null,
    ciudadDestino: String = "",
    viewModel: TripListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // --- DATOS DEL VIAJE ---
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

    // --- BÚSQUEDA DE CIUDADES (OSM) ---
    var expandido by remember { mutableStateOf(false) }
    var sugerencias by remember { mutableStateOf(listOf<String>()) }
    var job by remember { mutableStateOf<Job?>(null) }

    // --- MENSAJES DE ERROR ---
    var errorTitle by remember { mutableStateOf<String?>(null) }
    var errorCountry by remember { mutableStateOf<String?>(null) }
    var errorFechas by remember { mutableStateOf<String?>(null) }

    // --- CONTROL DE LA PANTALLA ---
    var listaItinerarios by remember { mutableStateOf(listOf<ItineraryItem>()) }
    var etapaActual by rememberSaveable { mutableIntStateOf(previewStep ?: 0) }
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    var indiceEdicion by rememberSaveable { mutableIntStateOf(-1) }
    var itemAEliminar by remember { mutableStateOf<Int?>(null) }

    // Escuchar eventos del ViewModel (errores de duplicados, etc)
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Calcula la fecha mínima de vuelta según la de ida
    val minimaVueltaMs = remember(fechaIda) {
        if (fechaIda.isNotEmpty()) {
            try {
                val sdf = SimpleDateFormat(Validator.DATE_FORMAT, Locale.getDefault())
                sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                sdf.parse(fechaIda)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        } else {
            System.currentTimeMillis()
        }
    }

    // Selector de imagen de la galería
    val launcherPrincipal = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    // Si venimos de una sugerencia (ej: "Paris" desde Home), la buscamos oficialmente
    LaunchedEffect(ciudadDestino) {
        val textoLimpio = ciudadDestino.replace("{ciudad}", "").trim()
        if (textoLimpio.length > 2) {
            scope.launch {
                try {
                    val resultados = buscarCiudadesOSM(textoLimpio)
                    if (resultados.isNotEmpty()) {
                        val primeraOpcion = resultados[0]
                        country = TextFieldValue(text = primeraOpcion, selection = TextRange(primeraOpcion.length))
                        if (Validator.isValidLocation(primeraOpcion)) errorCountry = null
                        expandido = false
                    }
                } catch (e: Exception) { }
            }
        }
    }

    val errTitleText = stringResource(id = R.string.form_error_titulo)
    val errDestText = stringResource(id = R.string.form_error_destino)
    val errFechasVaciasText = stringResource(id = R.string.form_error_fechas_vacias)
    val errFechasCoherenciaText = stringResource(id = R.string.form_error_fechas_coherencia)

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            CustomHeader(
                title = if (etapaActual == 0) stringResource(id = R.string.form_nuevo_viaje) else stringResource(id = R.string.form_itinerario),
                subtitle = if (etapaActual == 0) stringResource(id = R.string.form_paso1) else stringResource(id = R.string.form_paso2),
                showBackButton = true
            )

            AnimatedContent(targetState = etapaActual, label = "AnimacionPasos") { targetEtapa ->
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (targetEtapa == 0) {
                        // --- PASO 1: RELLENAR DATOS GENERALES ---
                        
                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                errorTitle = if (Validator.isValidTitle(it)) null else errTitleText
                            },
                            label = { Text(stringResource(id = R.string.form_titulo_label)) },
                            isError = errorTitle != null,
                            supportingText = { errorTitle?.let { Text(it) } },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Buscador de destinos (con autocompletado)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = country,
                                onValueChange = { newValue ->
                                    country = newValue
                                    errorCountry = if (Validator.isValidLocation(newValue.text)) null else errDestText
                                    expandido = newValue.text.length > 2
                                    job?.cancel()
                                    job = scope.launch {
                                        delay(500)
                                        if (newValue.text.length > 2) {
                                            sugerencias = buscarCiudadesOSM(newValue.text)
                                            expandido = sugerencias.isNotEmpty()
                                        }
                                    }
                                },
                                label = { Text(stringResource(id = R.string.form_destino_label)) },
                                isError = errorCountry != null,
                                supportingText = { errorCountry?.let { Text(it) } },
                                leadingIcon = { Icon(imageVector = Icons.Default.Place, contentDescription = null) },
                                trailingIcon = {
                                    if (country.text.isNotEmpty()) {
                                        IconButton(onClick = { country = TextFieldValue(""); expandido = false; sugerencias = emptyList(); errorCountry = null }) {
                                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Borrar")
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().onFocusChanged { isFocused = it.isFocused },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            DropdownMenu(
                                expanded = expandido && sugerencias.isNotEmpty(),
                                onDismissRequest = { expandido = false },
                                properties = PopupProperties(focusable = false),
                                modifier = Modifier.fillMaxWidth(0.9f).background(MaterialTheme.colorScheme.surface)
                            ) {
                                sugerencias.forEach { ciudad ->
                                    DropdownMenuItem(
                                        text = { Text(ciudad) },
                                        onClick = {
                                            country = TextFieldValue(text = ciudad, selection = TextRange(ciudad.length))
                                            expandido = false
                                            errorCountry = null
                                        }
                                    )
                                }
                            }
                        }

                        // Fechas de Ida y Vuelta
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                SelectorFechaModular(
                                    label = stringResource(id = R.string.form_ida_label),
                                    fechaSeleccionada = fechaIda,
                                    onFechaElegida = { 
                                        fechaIda = it
                                        errorFechas = null 
                                    },
                                    fechaMinima = System.currentTimeMillis(),
                                    isError = errorFechas != null,
                                    modifier = Modifier.weight(1f)
                                )
                                SelectorFechaModular(
                                    label = stringResource(id = R.string.form_vuelta_label),
                                    fechaSeleccionada = fechaVuelta,
                                    onFechaElegida = { 
                                        fechaVuelta = it
                                        errorFechas = null 
                                    },
                                    fechaMinima = minimaVueltaMs,
                                    isError = errorFechas != null,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (errorFechas != null) {
                                Text(text = errorFechas!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp))
                            }
                        }

                        // Foto de portada
                        CampoSeleccionImagen(
                            uriSeleccionada = selectedImageUri,
                            label = stringResource(id = R.string.form_portada_label),
                            onBorrar = { selectedImageUri = null },
                            onClick = { launcherPrincipal.launch("image/*") }
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text(stringResource(id = R.string.form_desc_label)) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                val tOk = Validator.isValidTitle(title)
                                val lOk = Validator.isValidLocation(country.text)
                                val fRellenas = fechaIda.isNotEmpty() && fechaVuelta.isNotEmpty()
                                val fCoherentes = if (fRellenas) Validator.areDatesValid(fechaIda, fechaVuelta) else false

                                errorTitle = if (!tOk) errTitleText else null
                                errorCountry = if (!lOk) errDestText else null
                                errorFechas = when {
                                    !fRellenas -> errFechasVaciasText
                                    !fCoherentes -> errFechasCoherenciaText
                                    else -> null
                                }

                                if (tOk && lOk && fRellenas && fCoherentes) etapaActual = 1
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Text(stringResource(id = R.string.form_add_itinerary), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.inversePrimary)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.inversePrimary)
                        }

                    } else {
                        // --- PASO 2: AÑADIR ACTIVIDADES ---
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { etapaActual = 0 }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                            Text(stringResource(id = R.string.form_actividades_para, title), style = MaterialTheme.typography.titleMedium)
                        }

                        if (listaItinerarios.isEmpty()) {
                            Text(text = stringResource(id = R.string.form_no_itinerary), modifier = Modifier.fillMaxWidth().padding(32.dp), color = Color.Gray)
                        } else {
                            listaItinerarios.forEachIndexed { index, act ->
                                ItemItinerario(
                                    act = act,
                                    onEdit = { indiceEdicion = index; mostrarDialogo = true },
                                    onDelete = { itemAEliminar = index }
                                )
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(onClick = { mostrarDialogo = true }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                                Icon(Icons.Default.AddCircle, null); Spacer(Modifier.width(4.dp)); Text(stringResource(id = R.string.form_add_btn))
                            }
                            Button(
                                onClick = {
                                    val budget = listaItinerarios.sumOf { it.precio.toDouble() }
                                    val saved = viewModel.saveTrip(title, country.text, fechaIda, fechaVuelta, description, budget, selectedImageUri?.toString() ?: "", listaItinerarios)
                                    if (saved) {
                                        navController.popBackStack()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.inversePrimary)
                                Spacer(Modifier.width(4.dp))
                                Text(stringResource(id = R.string.form_finalizar_btn), color = MaterialTheme.colorScheme.inversePrimary)
                            }
                        }
                    }
                }
            }
        }
    }

    // Ventana para añadir una actividad nueva o editarla
    if (mostrarDialogo) {
        val sdf = SimpleDateFormat(Validator.DATE_FORMAT, Locale.getDefault())
        val inicioMs = try { sdf.parse(fechaIda)?.time } catch (e: Exception) { null }
        val finMs = try { sdf.parse(fechaVuelta)?.time } catch (e: Exception) { null }

        DialogoNuevaActividad(
            actividadAEditar = if (indiceEdicion != -1) listaItinerarios[indiceEdicion] else null,
            fechaInicioViaje = inicioMs,
            fechaFinViaje = finMs,
            listaExistente = listaItinerarios,
            onDismiss = { mostrarDialogo = false; indiceEdicion = -1 },
            onGuardar = { nuevaAct ->
                val listaMutable = listaItinerarios.toMutableList()
                if (indiceEdicion != -1) listaMutable[indiceEdicion] = nuevaAct
                else listaMutable.add(nuevaAct)
                listaItinerarios = listaMutable
                mostrarDialogo = false
                indiceEdicion = -1
            }
        )
    }

    // Aviso antes de borrar una actividad
    if (itemAEliminar != null) {
        AlertDialog(
            onDismissRequest = { itemAEliminar = null },
            title = { Text(stringResource(id = R.string.detalle2_delete_activity_titulo)) },
            text = { Text(stringResource(id = R.string.detalle2_delete_activity_msg, listaItinerarios[itemAEliminar!!].nombre)) },
            confirmButton = {
                TextButton(onClick = {
                    listaItinerarios = listaItinerarios.toMutableList().apply { removeAt(itemAEliminar!!) }
                    itemAEliminar = null
                }) { Text(stringResource(id = R.string.detalle_eliminar_btn), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { itemAEliminar = null }) { Text(stringResource(id = R.string.detalle_cancelar_btn)) }
            },
            containerColor = MaterialTheme.colorScheme.background
        )
    }
}

/**
 * Traduce el tipo de actividad para mostrarlo al usuario.
 */
@Composable
fun getTipoTraduccion(tipo: String): String {
    return when (tipo) {
        "Vuelo" -> stringResource(id = R.string.act_tipo_vuelo)
        "Restaurante" -> stringResource(id = R.string.act_tipo_restaurante)
        "Hotel" -> stringResource(id = R.string.act_tipo_hotel)
        "Museo" -> stringResource(id = R.string.act_tipo_museum)
        "Ocio" -> stringResource(id = R.string.act_tipo_ocio)
        "Otros" -> stringResource(id = R.string.act_tipo_otros)
        else -> tipo
    }
}

/**
 * Ventana (Dialog) que sale para rellenar los datos de una actividad.
 */
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
    // Valores de la actividad
    var n by remember { mutableStateOf(actividadAEditar?.nombre ?: "") }
    var d by remember { mutableStateOf(actividadAEditar?.dia ?: "") }
    var h by remember { mutableStateOf(actividadAEditar?.hora ?: "") }
    var p by remember { mutableStateOf(actividadAEditar?.precio?.toString() ?: "") }
    var t by remember { mutableStateOf(actividadAEditar?.tipo ?: "Vuelo") }
    var desc by remember { mutableStateOf(actividadAEditar?.descripcion ?: "") }

    // Estados de error de cada campo
    var errorN by remember { mutableStateOf<String?>(null) }
    var errorD by remember { mutableStateOf<String?>(null) }
    var errorH by remember { mutableStateOf<String?>(null) }
    var errorP by remember { mutableStateOf<String?>(null) }
    var errorDesc by remember { mutableStateOf<String?>(null) }
    var exp by remember { mutableStateOf(false) }

    val monedaSimbolo = "€"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Text(if (actividadAEditar == null) stringResource(id = R.string.act_nueva_parada) else stringResource(id = R.string.act_editar_parada))
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Nombre de la actividad
                Column {
                    OutlinedTextField(
                        value = n,
                        onValueChange = { 
                            n = it
                            errorN = if (it.isNotBlank()) null else "Campo obligatorio" 
                        },
                        label = { Text(stringResource(id = R.string.act_nombre_label)) },
                        isError = errorN != null,
                        supportingText = { errorN?.let { Text(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Selector de día
                Column {
                    SelectorFechaModular(
                        label = stringResource(id = R.string.act_dia_label),
                        fechaSeleccionada = d,
                        fechaMinima = fechaInicioViaje,
                        fechaMaxima = fechaFinViaje,
                        onFechaElegida = { d = it; errorD = null },
                        isError = errorD != null
                    )
                    if (errorD != null) Text(text = errorD!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 12.dp))
                }

                // Selector de hora
                Column {
                    SelectorHoraModular(label = stringResource(id = R.string.act_hora_label), horaSeleccionada = h, onHoraElegida = { h = it; errorH = null }, isError = errorH != null)
                    if (errorH != null) Text(text = errorH!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 12.dp))
                }

                // Precio
                Column {
                    OutlinedTextField(
                        value = p,
                        onValueChange = { input -> 
                            val cleanInput = input.replace(",", ".")
                            if (cleanInput.isEmpty() || cleanInput.matches(Regex("""^\d*[.]?\d{0,2}$"""))) {
                                p = cleanInput
                                errorP = if (Validator.isValidPrice(cleanInput)) null else "Precio inválido"
                            }
                        },
                        label = { Text(stringResource(id = R.string.act_precio_label, monedaSimbolo)) },
                        isError = errorP != null,
                        supportingText = { errorP?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Notas
                OutlinedTextField(
                    value = desc,
                    onValueChange = { 
                        desc = it
                        errorDesc = if (Validator.isSecureText(it)) null else "Caracteres no permitidos"
                    },
                    label = { Text(stringResource(id = R.string.act_notas_label)) },
                    isError = errorDesc != null,
                    supportingText = { errorDesc?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Tipo de actividad (Vuelo, Hotel, etc.)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = getTipoTraduccion(t),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text(stringResource(id = R.string.act_tipo_label)) },
                        trailingIcon = { IconButton(onClick = { exp = true }) { Icon(Icons.Default.KeyboardArrowDown, null) } },
                        modifier = Modifier.fillMaxWidth().clickable { exp = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                    DropdownMenu(expanded = exp, onDismissRequest = { exp = false }, containerColor = MaterialTheme.colorScheme.surface) {
                        listOf("Vuelo", "Restaurante", "Hotel", "Museo", "Ocio", "Otros").forEach { label ->
                            DropdownMenuItem(text = { Text(label) }, onClick = { t = label; exp = false })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val colision = listaExistente.any { it.dia == d && it.hora == h && it.id != actividadAEditar?.id }
                
                errorN = if (n.isBlank()) "Campo obligatorio" else null
                errorD = if (d.isBlank()) "Selecciona un día" else null
                errorH = if (h.isBlank()) "Selecciona una hora" else if (colision) "Hora ya ocupada" else null
                errorDesc = if (!Validator.isSecureText(desc)) "Contenido no seguro" else null
                errorP = if (p.isNotEmpty() && !Validator.isValidPrice(p)) "Precio inválido" else null

                if (errorN == null && errorD == null && errorH == null && errorDesc == null && errorP == null) {
                    onGuardar(ItineraryItem(actividadAEditar?.id ?: java.util.UUID.randomUUID().toString(), "", n, d, h, if (p.isEmpty()) 0 else p.toIntOrNull() ?: 0, t, desc))
                }
            }) { Text(stringResource(id = R.string.act_guardar)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(id = R.string.act_cancelar)) } }
    )
}

/**
 * Campo especial para seleccionar una foto de la galería.
 */
@Composable
fun CampoSeleccionImagen(uriSeleccionada: Uri?, label: String, onBorrar: () -> Unit, onClick: () -> Unit) {
    OutlinedTextField(
        value = if (uriSeleccionada != null) stringResource(id = R.string.form_portada_selected) else "",
        onValueChange = {},
        readOnly = true,
        enabled = false,
        label = { Text(label) },
        leadingIcon = { Icon(if (uriSeleccionada != null) Icons.Default.CheckCircle else Icons.Default.Image, null) },
        trailingIcon = { if (uriSeleccionada != null) IconButton(onBorrar) { Icon(Icons.Default.Clear, null) } },
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

/**
 * Tarjeta que muestra una actividad resumida en el paso 2 del formulario.
 */
@Composable
fun ItemItinerario(act: ItineraryItem, onEdit: () -> Unit, onDelete: () -> Unit) {
    val (icono, colorFondo, colorIcono) = when (act.tipo) {
        "Vuelo" -> Triple(Icons.Default.Flight, Color(0xFFE3F2FD), Color(0xFF1976D2))
        "Hotel" -> Triple(Icons.Default.Hotel, Color(0xFFF3E5F5), Color(0xFF7B1FA2))
        "Restaurante" -> Triple(Icons.Default.Restaurant, Color(0xFFFFF3E0), Color(0xFFE65100))
        "Museo" -> Triple(Icons.Default.Museum, Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "Otros" -> Triple(Icons.Default.Accessibility, Color(0xFFF5F5F5), Color(0xFF616161))
        else -> Triple(Icons.Default.ConfirmationNumber, Color(0xFFFFEBEE), Color(0xFFC62828))
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.clickable { onEdit() }.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = colorFondo) {
                    Icon(imageVector = icono, contentDescription = null, tint = colorIcono, modifier = Modifier.padding(10.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = act.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = "${act.dia} • ${act.hora} (${getTipoTraduccion(act.tipo)})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = act.descripcion, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1, modifier = Modifier.weight(1f))
                        Text(text = "€${act.precio}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color(0xFFE65100))
                    }
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp)) {
                Icon(imageVector = Icons.Rounded.Clear, contentDescription = "Borrar", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
