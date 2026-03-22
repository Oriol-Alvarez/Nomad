package com.example.app.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.Routes
import com.example.app.domain.ItineraryItem
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.viewmodels.TripListViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.Calendar
import java.util.TimeZone

/**
 * Pantalla que muestra todos los detalles de un viaje concreto.
 * Aquí puedes ver el itinerario día a día, editar el viaje o borrarlo.
 */

// Formatea la fecha para que se vea bonita (ej: 1 Mar)
fun formatDateHeader(dateStr: String): String {
    return try {
        val date = if (dateStr.contains("-")) {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
        } else {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr)
        }
        // Usamos 'd MMM' y quitamos el punto que a veces pone Java en español (abr.)
        val outputSdf = SimpleDateFormat("d MMM", Locale("es", "ES"))
        date?.let { 
            outputSdf.format(it).replace(".", "").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        } ?: dateStr
    } catch (e: Exception) {
        dateStr
    }
}

// Para mostrar el rango de fechas arriba (ej: 1 Ago — 5 Ago)
fun formatTripRange(inicio: String?, fin: String?): String {
    if (inicio.isNullOrEmpty() || fin.isNullOrEmpty()) return ""
    return try {
        val inputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputSdf = SimpleDateFormat("d MMM", Locale("es", "ES"))
        val dateStart = inputSdf.parse(inicio)
        val dateEnd = inputSdf.parse(fin)
        if (dateStart != null && dateEnd != null) {
            val start = outputSdf.format(dateStart).replace(".", "")
            val end = outputSdf.format(dateEnd).replace(".", "")
            "$start — $end"
        } else ""
    } catch (e: Exception) {
        "$inicio — $fin"
    }
}

// Calcula cuántas noches dura el viaje restando las fechas
fun calcularNoches(inicio: String?, fin: String?): String {
    if (inicio.isNullOrEmpty() || fin.isNullOrEmpty()) return "0"
    val formatosPosibles = listOf("yyyy-MM-dd", "dd/MM/yyyy", "d/M/yyyy", "yyyy-M-d", "yyyy/MM/dd", "yyyy/M/d", "dd-MM-yyyy")
    for (patron in formatosPosibles) {
        try {
            val sdf = SimpleDateFormat(patron, Locale.getDefault())
            sdf.isLenient = false
            val startDate = sdf.parse(inicio)
            val endDate = sdf.parse(fin)
            if (startDate != null && endDate != null) {
                val diffInMillis = endDate.time - startDate.time
                val noches = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
                if (noches >= 0) return noches.toString()
            }
        } catch (e: Exception) { continue }
    }
    return "0"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleViajeScreen2(
    navController: NavHostController,
    selectedCurrency: String,
    tripId: String,
    viewModel: TripListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val trip = viewModel.getTripById(tripId)
    var activities by remember { mutableStateOf(viewModel.getActivitiesForTrip(tripId)) }

    // Control de diálogos y modo edición
    var isEditMode by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteTripDialog by remember { mutableStateOf(false) }
    var activityToDelete by remember { mutableStateOf<ItineraryItem?>(null) }
    var activityToEdit by remember { mutableStateOf<ItineraryItem?>(null) }
    var showAddActivityDialog by remember { mutableStateOf(false) }

    // Valores temporales mientras editamos el viaje
    var editedTitle by remember(trip) { mutableStateOf(trip?.title ?: "") }
    var editedDescription by remember(trip) { mutableStateOf(trip?.description ?: "") }
    var editedImageUri by remember(trip) { mutableStateOf(trip?.imageUri ?: "") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { editedImageUri = it.toString() } }

    val title = trip?.title ?: stringResource(id = R.string.app_name)
    val country = trip?.country ?: ""
    val budget = trip?.budget ?: 0.0
    val imageUri = trip?.imageUri ?: ""
    val description = trip?.description ?: ""

    val nochesReales = calcularNoches(trip?.dataInici, trip?.dataFinal)
    val rangoFechas = formatTripRange(trip?.dataInici, trip?.dataFinal)

    // Mezclamos el título con el país en el mismo texto
    val annotatedTitle = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(title) }
        withStyle(style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))) { append(",  $country") }
    }

    // Diálogo para confirmar borrar el viaje entero
    if (showDeleteTripDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteTripDialog = false },
            title = { Text(stringResource(id = R.string.detalle_eliminar_titulo)) },
            text = { Text(stringResource(id = R.string.detalle_eliminar_msg)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTrip(tripId)
                    showDeleteTripDialog = false
                    navController.navigate(Routes.DETALLE_VIAJE) { popUpTo(Routes.DETALLE_VIAJE) { inclusive = true } }
                }) { Text(stringResource(id = R.string.detalle_eliminar_btn), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteTripDialog = false }) { Text(stringResource(id = R.string.detalle_cancelar_btn)) }
            },
            containerColor = MaterialTheme.colorScheme.background
        )
    }

    // Diálogo para confirmar borrar una actividad suelta
    if (activityToDelete != null) {
        AlertDialog(
            onDismissRequest = { activityToDelete = null },
            title = { Text(stringResource(id = R.string.detalle2_delete_activity_titulo)) },
            text = { Text(stringResource(id = R.string.detalle2_delete_activity_msg, activityToDelete?.nombre ?: "")) },
            confirmButton = {
                TextButton(onClick = {
                    activityToDelete?.let { viewModel.deleteActivity(it); activities = viewModel.getActivitiesForTrip(tripId) }
                    activityToDelete = null
                }) { Text(stringResource(id = R.string.detalle_eliminar_btn), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { activityToDelete = null }) { Text(stringResource(id = R.string.detalle_cancelar_btn)) }
            }
        )
    }

    // Ventana para añadir o cambiar una actividad
    if (showAddActivityDialog || activityToEdit != null) {
        fun getCleanMillis(dateStr: String): Long? {
            val formatos = listOf("yyyy-MM-dd", "dd/MM/yyyy")
            for (formato in formatos) {
                try {
                    val sdf = SimpleDateFormat(formato, Locale.getDefault())
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    val date = sdf.parse(dateStr); if (date != null) return date.time
                } catch (e: Exception) {}
            }
            return null
        }

        DialogoNuevaActividad(
            actividadAEditar = activityToEdit,
            fechaInicioViaje = getCleanMillis(trip?.dataInici ?: ""),
            fechaFinViaje = getCleanMillis(trip?.dataFinal ?: ""),
            listaExistente = activities,
            onDismiss = { showAddActivityDialog = false; activityToEdit = null },
            onGuardar = { nuevaAct ->
                val act = nuevaAct.copy(tripId = tripId)
                if (activityToEdit != null) viewModel.updateActivity(act)
                else viewModel.addActivityToTrip(tripId, act)
                activities = viewModel.getActivitiesForTrip(tripId)
                showAddActivityDialog = false; activityToEdit = null
            }
        )
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).background(MaterialTheme.colorScheme.background)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                CustomHeader(
                    backgroundImageRes = if (isEditMode) editedImageUri else imageUri,
                    showBackButton = !isEditMode,
                    content = {
                        Column {
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (isEditMode) {
                                    OutlinedTextField(
                                        value = editedTitle,
                                        onValueChange = { editedTitle = it },
                                        textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color.White),
                                        modifier = Modifier.fillMaxWidth().offset(x = (-16).dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.White.copy(alpha = 0.5f),
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                            cursorColor = Color.White
                                        )
                                    )
                                } else {
                                    Text(text = annotatedTitle, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (isEditMode) {
                                    OutlinedTextField(
                                        value = editedDescription,
                                        onValueChange = { editedDescription = it },
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                                        modifier = Modifier.fillMaxWidth().offset(x = (-16).dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.White.copy(alpha = 0.5f),
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                            cursorColor = Color.White
                                        )
                                    )
                                } else {
                                    Text(text = description, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                )

                // Botón para cambiar la foto si estamos editando
                if (isEditMode) {
                    IconButton(onClick = { launcher.launch("image/*") }, modifier = Modifier.align(Alignment.TopStart).padding(top = 24.dp, start = 8.dp)) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Cambiar foto", tint = Color.White)
                    }
                }

                // Botones de guardar/cancelar o menú de 3 puntos
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = if (isEditMode) 16.dp else 24.dp, end = 8.dp)) {
                    if (isEditMode) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { 
                                // Resetear campos al cancelar para volver al estado anterior
                                editedTitle = trip?.title ?: ""
                                editedDescription = trip?.description ?: ""
                                editedImageUri = trip?.imageUri ?: ""
                                isEditMode = false 
                            }) {
                                Text(stringResource(id = R.string.act_cancelar), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Button(onClick = {
                                trip?.let { viewModel.updateTrip(it.copy(title = editedTitle, description = editedDescription, imageUri = editedImageUri)) }
                                isEditMode = false
                            }) { Text(stringResource(id = R.string.act_guardar), fontWeight = FontWeight.Bold) }
                        }
                    } else {
                        IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White) }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, containerColor = MaterialTheme.colorScheme.surface) {
                            DropdownMenuItem(text = { Text(stringResource(id = R.string.detalle2_edit_trip)) }, leadingIcon = { Icon(Icons.Default.Edit, null) }, onClick = { isEditMode = true; showMenu = false })
                            DropdownMenuItem(text = { Text(stringResource(id = R.string.detalle2_delete_trip), color = MaterialTheme.colorScheme.error) }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }, onClick = { showDeleteTripDialog = true; showMenu = false })
                        }
                    }
                }
            }

            // Fechas del viaje (ej: 1 Ago — 5 Ago)
            if (rangoFechas.isNotEmpty()) {
                Text(text = rangoFechas, modifier = Modifier.fillMaxWidth().padding(top = 16.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }

            // Datos rápidos (Noches, Presupuesto, Actividades)
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                StatItem(value = nochesReales, label = if (nochesReales == "1") stringResource(id = R.string.detalle2_nights_singular) else stringResource(id = R.string.detalle2_nights), modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray.copy(alpha = 0.5f))
                StatItem(value = CurrencyConverter.convert(budget, selectedCurrency), label = stringResource(id = R.string.detalle2_budget), modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray.copy(alpha = 0.5f))
                StatItem(value = activities.size.toString(), label = stringResource(id = R.string.detalle2_activities), modifier = Modifier.weight(1f))
            }

            if (isEditMode) {
                Button(onClick = { showAddActivityDialog = true }, modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)) {
                    Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text(stringResource(id = R.string.detalle2_add_activity))
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

            // Lista del itinerario
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
                if (activities.isEmpty()) {
                    item { Text(text = stringResource(id = R.string.detalle2_no_activities), modifier = Modifier.fillMaxWidth().padding(top = 40.dp), textAlign = TextAlign.Center, color = Color.Gray) }
                } else {
                    val sdfSort = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val sorted = activities.sortedBy { try { sdfSort.parse("${it.dia} ${it.hora}")?.time ?: 0L } catch(e: Exception) { 0L } }
                    sorted.groupBy { it.dia }.forEach { (dia, acts) ->
                        item { DayHeader(formatDateHeader(dia)) }
                        items(acts) { act ->
                            val iconData = getIconForType(act.tipo)
                            TimelineEvent(act.hora, iconData.first, iconData.second, iconData.third, act.nombre, act.descripcion, CurrencyConverter.convert(act.precio.toDoubleOrNull() ?: 0.0, selectedCurrency), if ((act.precio.toDoubleOrNull() ?: 0.0) > 0) Color(0xFFE65100) else Color(0xFF2E7D32), isEditMode, { activityToDelete = act }, { if (isEditMode) activityToEdit = act })
                        }
                    }
                }
            }
        }
    }
}

// Icono y colores según sea un vuelo, hotel, restaurante...
private fun getIconForType(type: String): Triple<ImageVector, Color, Color> {
    return when (type.lowercase()) {
        "vuelo" -> Triple(Icons.Default.Flight, Color(0xFFE3F2FD), Color(0xFF1976D2))
        "hotel" -> Triple(Icons.Default.Hotel, Color(0xFFF3E5F5), Color(0xFF7B1FA2))
        "restaurante" -> Triple(Icons.Default.Restaurant, Color(0xFFFFF3E0), Color(0xFFE65100))
        "museo" -> Triple(Icons.Default.Museum, Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "ocio" -> Triple(Icons.Default.LocalActivity, Color(0xFFE1F5FE), Color(0xFF0288D1))
        "otros" -> Triple(Icons.Default.Accessibility, Color(0xFFF5F5F5), Color(0xFF616161))
        else -> Triple(Icons.Default.ConfirmationNumber, Color(0xFFFFEBEE), Color(0xFFC62828))
    }
}

@Composable
private fun StatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DayHeader(text: String) {
    Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), shape = RoundedCornerShape(50), modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(text = text, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
    }
}

@Composable
private fun TimelineEvent(time: String, icon: ImageVector, iconBg: Color, iconTint: Color, title: String, subtitle: String, price: String, priceColor: Color, isEditMode: Boolean, onDelete: () -> Unit, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(enabled = isEditMode, onClick = onClick).padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.Top) {
        Text(text = time, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.width(50.dp).padding(top = 8.dp))
        Surface(shape = CircleShape, color = iconBg, modifier = Modifier.size(42.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp)) }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = subtitle, fontSize = 14.sp, color = Color.Gray)
            Text(text = price, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = priceColor)
        }
        if (isEditMode) {
            IconButton(onClick = onDelete) { Icon(imageVector = Icons.Rounded.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp)) }
        }
    }
}
