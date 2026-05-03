package com.example.app.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.Routes
import com.example.app.ui.viewmodels.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenciasScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel, // Eliminado = viewModel() para evitar el crash
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    recordatorioViajes: Boolean,
    onRecordatorioChange: (Boolean) -> Unit,
    resumenSemanal: Boolean,
    onResumenChange: (Boolean) -> Unit,
    selectedCurrency: String,
    onCurrencyChange: (String) -> Unit,
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    username: String = "Viajero",
    onUsernameChange: (String) -> Unit = {},
    birthdate: String = "01/01/2000",
    onBirthdateChange: (String) -> Unit = {},
    fontSizeScale: Float = 1.0f,
    onFontSizeScaleChange: (Float) -> Unit = {}
) {
    var showUserDialog by remember { mutableStateOf(false) }
    var tempUsername by remember { mutableStateOf(username) }
    val context = LocalContext.current
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

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
            CustomHeader(
                stringResource(R.string.preferencias_titulo),
                stringResource(R.string.preferencias_subtitulo),
                false
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                GlassCard(title = "Perfil de Usuario") {
                    CajasPreferencias(
                        image = R.drawable.user_solid_full,
                        name = "Nombre de Usuario",
                        role = username,
                        value = "nav",
                        type = "nav",
                        onClick = { 
                            tempUsername = username
                            showUserDialog = true 
                        }
                    )
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    CajasPreferencias(
                        image = R.drawable.birthday,
                        name = "Fecha de Nacimiento",
                        role = birthdate,
                        value = "nav",
                        type = "nav",
                        onClick = { showDatePicker = true }
                    )
                    
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    TextButton(
                        onClick = {
                            authViewModel.signOut()
                            navController.navigate(Routes.AUTH) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Cerrar sesión",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlassCard(title = stringResource(R.string.preferencias_idioma_region)) {
                    CajasPreferencias(
                        image = R.drawable.earth_americas_solid_full,
                        name = stringResource(R.string.preferencias_idioma_titulo),
                        role = stringResource(R.string.preferencias_idioma_role),
                        options = listOf("Es Español", "Ca Català", "En English"),
                        value = selectedLanguage,
                        type = "select",
                        onCurrencySelect = { onLanguageChange(it) }
                    )
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    CajasPreferencias(
                        image = R.drawable.coins_solid_full,
                        name = stringResource(R.string.preferencias_titulo_moneda),
                        role = stringResource(R.string.preferencias_subtitulo_moneda),
                        value = selectedCurrency,
                        options = listOf("EUR(€)", "USD($)", "GBP(£)", "MXN($)"),
                        type = "select",
                        onCurrencySelect = { onCurrencyChange(it) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlassCard(title = stringResource(R.string.preferencias_titulo_seccion_apariencia)) {
                    CajasPreferencias(
                        image = R.drawable.circle_half_stroke_solid_full,
                        name = stringResource(R.string.preferencias_titulo_modo_oscuro),
                        role = stringResource(R.string.preferencias_role_modo_oscuro),
                        value = if (isDarkMode) "on" else "off",
                        type = "slider",
                        onCheckedChange = onDarkModeChange
                    )
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    
                    val fontSizeLabel = when(fontSizeScale) {
                        0.85f -> "Pequeño"
                        1.15f -> "Grande"
                        1.30f -> "Extra Grande"
                        else -> "Normal"
                    }
                    
                    CajasPreferencias(
                        image = R.drawable.text_width_solid_full,
                        name = stringResource(R.string.preferencias_titulo_tamaño_letra),
                        role = stringResource(R.string.preferencias_role_tamaño_letra),
                        value = fontSizeLabel,
                        options = listOf("Pequeño", "Normal", "Grande", "Extra Grande"),
                        type = "select",
                        onCurrencySelect = { selection ->
                            val newScale = when(selection) {
                                "Pequeño" -> 0.85f
                                "Grande" -> 1.15f
                                "Extra Grande" -> 1.30f
                                else -> 1.0f
                            }
                            onFontSizeScaleChange(newScale)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlassCard(title = stringResource(R.string.preferencias_titulo_seccion_notificaciones)) {
                    CajasPreferencias(
                        image = R.drawable.bell_solid_full,
                        name = stringResource(R.string.preferencias_notif_viajes),
                        role = stringResource(R.string.preferencias_notif_viajes_role),
                        value = if (recordatorioViajes) "on" else "off",
                        type = "slider",
                        onCheckedChange = onRecordatorioChange
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CajasPreferencias(
                        image = R.drawable.envelope_solid_full,
                        name = stringResource(R.string.preferencias_notif_resumen),
                        role = stringResource(R.string.preferencias_notif_resumen_role),
                        value = if (resumenSemanal) "on" else "off",
                        type = "slider",
                        onCheckedChange = onResumenChange
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlassCard(title = stringResource(R.string.preferencias_seccion_mas_info)) {
                    CajasPreferencias(
                        image = R.drawable.circle_info_solid_full,
                        name = stringResource(R.string.preferencias_info_app),
                        role = stringResource(R.string.preferencias_info_app_role),
                        value = "on",
                        type = "nav",
                        onClick = { navController.navigate(Routes.SOBRE_NOSOTROS) }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CajasPreferencias(
                        image = R.drawable.clipboard_list_solid_full,
                        name = stringResource(R.string.preferencias_terminos),
                        role = stringResource(R.string.preferencias_terminos_role),
                        value = "on",
                        type = "nav",
                        onClick = { navController.navigate(Routes.TERMINOS_CONDICIONES) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showUserDialog) {
        AlertDialog(
            onDismissRequest = { showUserDialog = false },
            title = { Text("Editar Usuario") },
            text = {
                OutlinedTextField(
                    value = tempUsername,
                    onValueChange = { tempUsername = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (tempUsername.isNotBlank()) {
                        onUsernameChange(tempUsername)
                        showUserDialog = false
                    } else {
                        Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showUserDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val formattedDate = sdf.format(Date(it))
                        if (Validator.isBirthdateValid(formattedDate)) {
                            onBirthdateChange(formattedDate)
                            showDatePicker = false
                        } else {
                            Toast.makeText(context, "La fecha debe ser anterior a hoy", Toast.LENGTH_LONG).show()
                        }
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CajasPreferencias(
    image: Int,
    name: String,
    role: String,
    value: String,
    options: List<String> = emptyList(),
    type: String,
    navController: NavHostController = rememberNavController(),
    onClick: () -> Unit = {},
    onCheckedChange: (Boolean) -> Unit = {},
    onCurrencySelect: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(if (type == "nav") Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = image),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = role,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }

        when (type) {
            "slider" -> {
                Switch(
                    checked = value == "on",
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
            }
            "select" -> {
                var expanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                            .clickable { expanded = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .widthIn(min = 60.dp)
                    ) {
                        Text(
                            text = value,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.rotate(if (expanded) 180f else 0f)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = 0.dp, y = 4.dp),
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    ) {
                        if (options.isEmpty()) {
                            DropdownMenuItem(text = { Text("No hay opciones") }, onClick = { expanded = false })
                        } else {
                            options.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(text = opcion) },
                                    onClick = {
                                        expanded = false
                                        onCurrencySelect(opcion)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Acceder",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
