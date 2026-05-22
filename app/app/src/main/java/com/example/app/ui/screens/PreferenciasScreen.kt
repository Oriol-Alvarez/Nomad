package com.example.app.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
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
    authViewModel: AuthViewModel,
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
    var errorUsername by remember { mutableStateOf<String?>(null) }
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

                GlassCard(title = stringResource(R.string.preferencias_perfil_usuario)) {
                    CajasPreferencias(
                        image = R.drawable.user_solid_full,
                        name = stringResource(R.string.auth_username),
                        role = username,
                        value = "nav",
                        type = "nav",
                        onClick = { 
                            tempUsername = username
                            errorUsername = null
                            showUserDialog = true 
                        }
                    )
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    CajasPreferencias(
                        image = R.drawable.birthday,
                        name = stringResource(R.string.auth_birthdate),
                        role = birthdate,
                        value = "nav",
                        type = "nav",
                        onClick = { showDatePicker = true }
                    )
                    
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    TextButton(
                        onClick = {
                            authViewModel.signOut()
                            // Resetear valores para evitar que el siguiente usuario vea los del anterior
                            onUsernameChange("Viajero")
                            onBirthdateChange("01/01/2000")
                            onResumenChange(true)

                            navController.navigate(Routes.AUTH) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.preferencias_cerrar_sesion),
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
                        0.85f -> stringResource(R.string.preferencias_letra_pequeno)
                        1.15f -> stringResource(R.string.preferencias_letra_grande)
                        1.30f -> stringResource(R.string.preferencias_letra_extra_grande)
                        else -> stringResource(R.string.preferencias_letra_normal)
                    }
                    
                    CajasPreferencias(
                        image = R.drawable.text_width_solid_full,
                        name = stringResource(R.string.preferencias_titulo_tamaño_letra),
                        role = stringResource(R.string.preferencias_role_tamaño_letra),
                        value = fontSizeLabel,
                        options = listOf(
                            stringResource(R.string.preferencias_letra_pequeno),
                            stringResource(R.string.preferencias_letra_normal),
                            stringResource(R.string.preferencias_letra_grande),
                            stringResource(R.string.preferencias_letra_extra_grande)
                        ),
                        type = "select",
                        onCurrencySelect = { selection ->
                            val newScale = when(selection) {
                                context.getString(R.string.preferencias_letra_pequeno) -> 0.85f
                                context.getString(R.string.preferencias_letra_grande) -> 1.15f
                                context.getString(R.string.preferencias_letra_extra_grande) -> 1.30f
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
            title = { Text(stringResource(R.string.preferencias_perfil_usuario)) },
            text = {
                OutlinedTextField(
                    value = tempUsername,
                    onValueChange = { 
                        tempUsername = it
                        errorUsername = if (Validator.hasMinLength(it, 3) && Validator.isOnlyLetters(it)) null else "Nombre inválido (mín. 3 letras)"
                    },
                    label = { Text(stringResource(R.string.auth_username)) },
                    isError = errorUsername != null,
                    supportingText = { errorUsername?.let { Text(it) } },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (Validator.hasMinLength(tempUsername, 3) && Validator.isOnlyLetters(tempUsername)) {
                        onUsernameChange(tempUsername)
                        showUserDialog = false
                    } else {
                        errorUsername = "Nombre inválido (mín. 3 letras)"
                    }
                }) { Text(stringResource(R.string.act_guardar)) }
            },
            dismissButton = {
                TextButton(onClick = { showUserDialog = false }) { Text(stringResource(R.string.act_cancelar)) }
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat(Validator.DATE_FORMAT, Locale.getDefault())
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
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.act_cancelar)) }
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
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = image),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = role,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        when (type) {
            "slider" -> {
                Switch(
                    checked = value == "on",
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }
            "select" -> {
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { expanded = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = 0.dp, y = 4.dp)
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onCurrencySelect(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            "nav" -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}
