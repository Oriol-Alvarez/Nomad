package com.example.app.ui.screens

import android.net.Uri
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
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app.R
import com.example.app.Routes
import com.example.app.domain.ItineraryItem
import com.example.app.ui.viewmodels.TripListViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.TimeZone

/**
 * Pantalla que muestra todos los detalles de un viaje concreto.
 */

fun formatDateHeader(dateStr: String): String {
    return try {
        val date = if (dateStr.contains("-")) {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr)
        } else {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr)
        }
        val outputSdf = SimpleDateFormat("d MMM", Locale("es", "ES"))
        date?.let { 
            outputSdf.format(it).replace(".", "").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        } ?: dateStr
    } catch (e: Exception) {
        dateStr
    }
}

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
    viewModel: TripListViewModel
) {
    // T1.6: Observamos la lista de viajes como un estado de Compose
    val trips by viewModel.trips.collectAsState()
    val trip = trips.find { it.id == tripId }
    
    // Observamos las actividades (que ahora también vienen de un Flow en el repo)
    val activities by viewModel.getActivitiesForTrip(tripId).collectAsState(initial = emptyList())

    var isEditMode by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteTripDialog by remember { mutableStateOf(false) }
    var activityToDelete by remember { mutableStateOf<ItineraryItem?>(null) }
    var activityToEdit by remember { mutableStateOf<ItineraryItem?>(null) }
    var showAddActivityDialog by remember { mutableStateOf(false) }

    // Estados para edición
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

    val nochesReales = calcularNoches(trip?.dataInici, trip?.dataFinal)

    val annotatedTitle = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(title) }
        withStyle(style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.9f))) { append(",  $country") }
    }

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

    if (activityToDelete != null) {
        AlertDialog(
            onDismissRequest = { activityToDelete = null },
            title = { Text(stringResource(id = R.string.detalle2_delete_activity_titulo)) },
            text = { Text(stringResource(id = R.string.detalle2_delete_activity_msg, activityToDelete?.nombre ?: "")) },
            confirmButton = {
                TextButton(onClick = {
                    activityToDelete?.let { viewModel.deleteActivity(it) }
                    activityToDelete = null
                }) { Text(stringResource(id = R.string.detalle_eliminar_btn), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { activityToDelete = null }) { Text(stringResource(id = R.string.detalle_cancelar_btn)) }
            }
        )
    }

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
                                        modifier = Modifier.fillMaxWidth().offset(x = (-16).dp),
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.White.copy(alpha = 0.5f),
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                            cursorColor = Color.White
                                        )
                                    )
                                } else {
                                    Text(text = trip?.description ?: "", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                )

                if (isEditMode) {
                    Row(
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White) }

                        IconButton(
                            onClick = {
                                trip?.let {
                                    viewModel.updateTrip(it.copy(title = editedTitle, description = editedDescription, imageUri = editedImageUri))
                                }
                                isEditMode = false
                            },
                            modifier = Modifier.background(Color(0xFF4CAF50), CircleShape)
                        ) { Icon(Icons.Default.Check, contentDescription = null, tint = Color.White) }

                        IconButton(
                            onClick = { isEditMode = false; editedTitle = title; editedDescription = trip?.description ?: ""; editedImageUri = imageUri },
                            modifier = Modifier.background(Color(0xFFF44336), CircleShape)
                        ) { Icon(Icons.Rounded.Close, contentDescription = null, tint = Color.White) }
                    }
                } else {
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        ) { Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White) }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.detalle2_edit_trip)) },
                                onClick = { isEditMode = true; showMenu = false },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.detalle_eliminar_btn), color = Color.Red) },
                                onClick = { showDeleteTripDialog = true; showMenu = false },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = stringResource(id = R.string.detalle2_activities), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(text = "$nochesReales ${stringResource(id = R.string.detalle2_nights)}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                    Button(
                        onClick = { showAddActivityDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(id = R.string.detalle2_add_activity))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (activities.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(id = R.string.detalle2_no_activities), color = Color.Gray, textAlign = TextAlign.Center)
                    }
                } else {
                    val groupedActivities = activities.groupBy { it.dia }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 32.dp)) {
                        groupedActivities.forEach { (dia, activitiesInDay) ->
                            item {
                                Text(
                                    text = formatDateHeader(dia),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(activitiesInDay) { item ->
                                ItineraryItemCard(
                                    item = item,
                                    onEdit = { activityToEdit = it },
                                    onDelete = { activityToDelete = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItineraryItemCard(item: ItineraryItem, onEdit: (ItineraryItem) -> Unit, onDelete: (ItineraryItem) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEdit(item) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFE3F2FD), CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = getIconForType(item.tipo), contentDescription = null, tint = Color(0xFF0288D1), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.nombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = "${item.hora} • ${item.tipo}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(text = if (item.precio == "0") "Gratis" else "${item.precio}€", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (item.precio == "0") Color(0xFF2E7D32) else Color.Black)
            IconButton(onClick = { onDelete(item) }) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
            }
        }
    }
}

fun getIconForType(tipo: String): ImageVector {
    return when (tipo.lowercase()) {
        "vuelo" -> Icons.Default.Flight
        "hotel" -> Icons.Default.Hotel
        "restaurante" -> Icons.Default.Restaurant
        "museo" -> Icons.Default.Museum
        "ocio" -> Icons.Default.LocalActivity
        "transporte" -> Icons.Default.DirectionsBus
        else -> Icons.Default.ConfirmationNumber
    }
}
