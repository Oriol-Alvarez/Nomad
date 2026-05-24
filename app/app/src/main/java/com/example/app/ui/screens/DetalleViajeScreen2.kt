package com.example.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app.R
import com.example.app.Routes
import com.example.app.domain.ItineraryItem
import com.example.app.domain.TripImage
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
        date?.let { d -> 
            outputSdf.format(d).replace(".", "").replaceFirstChar { char -> 
                if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() 
            }
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
    var showCancelReservationDialog by remember { mutableStateOf(false) }

    // Estados para edición
    var editedTitle by remember(trip) { mutableStateOf(trip?.title ?: "") }
    var editedDescription by remember(trip) { mutableStateOf(trip?.description ?: "") }
    var editedImageUri by remember(trip) { mutableStateOf(trip?.imageUri ?: "") }

    val context = androidx.compose.ui.platform.LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> 
        uri?.let { 
            val copied = copyUriToInternalStorage(context, it)
            if (copied != null) {
                editedImageUri = copied.toString()
            }
        }
    }

    val title = trip?.title ?: stringResource(id = R.string.app_name)
    val country = trip?.country ?: ""
    val imageUri = trip?.imageUri ?: ""
    val budget = trip?.budget ?: 0.0

    // Lógica para imagen por defecto (si no hay imagen, usamos R.drawable.viaje_predefinido)
    val displayImage: Any = if (isEditMode) {
        editedImageUri.ifEmpty { R.drawable.viaje_predefinido }
    } else {
        imageUri.ifEmpty { R.drawable.viaje_predefinido }
    }

    val nochesReales = calcularNoches(trip?.dataInici, trip?.dataFinal)
    val rangoFechas = formatTripRange(trip?.dataInici, trip?.dataFinal)

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
                    activityToDelete?.let { item: ItineraryItem -> 
                        viewModel.deleteActivity(item) 
                    }
                    activityToDelete = null
                }) { Text(stringResource(id = R.string.detalle_eliminar_btn), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { activityToDelete = null }) { Text(stringResource(id = R.string.detalle_cancelar_btn)) }
            }
        )
    }

    if (showCancelReservationDialog) {
        AlertDialog(
            onDismissRequest = { showCancelReservationDialog = false },
            title = { Text("Cancelar Reserva de Hotel") },
            text = { Text("¿Estás seguro de que deseas cancelar la reserva del hotel? Se cancelará remotamente, se reembolsará del presupuesto del viaje, pero se mantendrán intactas el resto de actividades y el propio viaje.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        trip?.let { viewModel.cancelHotelReservation(it) }
                        showCancelReservationDialog = false
                        navController.navigate(Routes.DETALLE_VIAJE) {
                            popUpTo(Routes.DETALLE_VIAJE) { inclusive = true }
                        }
                    }
                ) {
                    Text("Cancelar Reserva", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelReservationDialog = false }) {
                    Text("Volver")
                }
            },
            containerColor = MaterialTheme.colorScheme.background
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
                    backgroundImageRes = displayImage,
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

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            containerColor = MaterialTheme.colorScheme.surface
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

            // Fechas del viaje (ej: 1 Ago — 5 Ago)
            if (rangoFechas.isNotEmpty()) {
                Text(
                    text = rangoFechas,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Datos rápidos (Noches, Presupuesto, Actividades)
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    value = nochesReales,
                    label = if (nochesReales == "1") stringResource(id = R.string.detalle2_nights_singular) else stringResource(id = R.string.detalle2_nights),
                    modifier = Modifier.weight(1f)
                )
                HorizontalDivider(
                    modifier = Modifier.height(40.dp).width(1.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )
                StatItem(
                    value = CurrencyConverter.convert(budget, selectedCurrency),
                    label = stringResource(id = R.string.detalle2_budget),
                    modifier = Modifier.weight(1f)
                )
                HorizontalDivider(
                    modifier = Modifier.height(40.dp).width(1.dp),
                    color = Color.LightGray.copy(alpha = 0.5f)
                )
                StatItem(
                    value = activities.size.toString(),
                    label = stringResource(id = R.string.detalle2_activities),
                    modifier = Modifier.weight(1f)
                )
            }

            if (isEditMode) {
                Button(
                    onClick = { showAddActivityDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(id = R.string.detalle2_add_activity))
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

            // Lista del itinerario con diseño de Timeline y Galería del viaje
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // 1. HOTEL RESERVATION CARD (Stay Card at the very top of the list)
                if (trip?.hasReservation == true) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF1A237E), // Deep Indigo
                                                Color(0xFF283593)  // VIP Royal Navy
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFFFFD700), // Gold
                                                Color(0xFFFFA000)  // Amber Gold
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(14.dp)
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Row with Icon & Title & Rating/Price
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.Hotel,
                                                        contentDescription = null,
                                                        tint = Color(0xFFFFD700),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "RESERVA",
                                                color = Color(0xFFFFD700),
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp,
                                                letterSpacing = 1.sp
                                            )
                                        }

                                        // Stars and Price underneath
                                        val nightsCount = calcularNoches(trip.reservationStartDate, trip.reservationEndDate)
                                        val hotelCost = nightsCount.toIntOrNull()?.let { it * trip.roomPrice } ?: trip.roomPrice
                                        Column(horizontalAlignment = Alignment.End) {
                                            if (trip.hotelRating > 0) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                                                ) {
                                                    repeat(trip.hotelRating) {
                                                        Icon(
                                                            imageVector = Icons.Default.Star,
                                                            contentDescription = null,
                                                            tint = Color(0xFFFFD700),
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = CurrencyConverter.convert(hotelCost, selectedCurrency),
                                                color = Color(0xFFFFD700),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }

                                    // Hotel name and address
                                    Text(
                                        text = trip.hotelName ?: "Hotel Reservado",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    trip.hotelAddress?.let {
                                        Text(
                                            text = it,
                                            color = Color.White.copy(alpha = 0.7f),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }

                                    // Check-in / Check-out stay dates
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                Color.White.copy(alpha = 0.08f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "ENTRADA",
                                                color = Color.White.copy(alpha = 0.5f),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp
                                            )
                                            Text(
                                                text = trip.reservationStartDate ?: "",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(14.dp)
                                        )

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "SALIDA",
                                                color = Color.White.copy(alpha = 0.5f),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp
                                            )
                                            Text(
                                                text = trip.reservationEndDate ?: "",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (activities.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(id = R.string.detalle2_no_activities),
                            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                } else {
                    val sdfSort = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val sorted = activities.sortedBy {
                        try {
                            sdfSort.parse("${it.dia} ${it.hora}")?.time ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    }
                    sorted.groupBy { it.dia }.forEach { (dia, acts) ->
                        item { DayHeader(formatDateHeader(dia)) }
                        items(acts) { act ->
                            val iconData = getIconForType(act.tipo)
                            TimelineEvent(
                                time = act.hora,
                                icon = iconData.first,
                                iconBg = iconData.second,
                                iconTint = iconData.third,
                                title = act.nombre,
                                subtitle = act.descripcion,
                                price = if (act.precio == 0) "Gratis" else "${act.precio}€",
                                priceColor = if (act.precio == 0) Color(0xFF2E7D32) else Color(0xFFE65100),
                                isEditMode = true,
                                onDelete = { activityToDelete = act },
                                onClick = { activityToEdit = act }
                            )
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
        "transporte" -> Triple(Icons.Default.DirectionsBus, Color(0xFFE0F7FA), Color(0xFF00838F))
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
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun TimelineEvent(
    time: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    price: String,
    priceColor: Color,
    isEditMode: Boolean,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEditMode, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = time,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.width(50.dp).padding(top = 8.dp)
        )
        Surface(
            shape = CircleShape,
            color = iconBg,
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = subtitle, fontSize = 14.sp, color = Color.Gray)
            Text(text = price, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = priceColor)
        }
        if (isEditMode) {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun parseTripDate(dateString: String?): java.util.Date? {
    if (dateString.isNullOrEmpty()) return null
    val formatosPosibles = listOf("yyyy-MM-dd", "yyyy/MM/dd", "dd/MM/yyyy", "dd-MM-yyyy")
    for (patron in formatosPosibles) {
        try {
            val input = SimpleDateFormat(patron, Locale.getDefault())
            input.isLenient = false
            val date = input.parse(dateString)
            if (date != null) return date
        } catch (e: Exception) {
            continue
        }
    }
    return null
}

private fun copyUriToInternalStorage(context: android.content.Context, uri: android.net.Uri): android.net.Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "trip_img_${System.currentTimeMillis()}.jpg"
        val file = java.io.File(context.filesDir, fileName)
        val outputStream = java.io.FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        android.net.Uri.fromFile(file)
    } catch (e: Exception) {
        android.util.Log.e("StorageUtils", "Error copying URI to internal storage", e)
        null
    }
}
