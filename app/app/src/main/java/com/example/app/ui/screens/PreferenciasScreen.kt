package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.Routes
import com.example.app.ui.theme.AppTheme
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun PreferenciasScreen(
    navController: NavHostController,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    recordatorioViajes: Boolean,
    onRecordatorioChange: (Boolean) -> Unit,
    resumenSemanal: Boolean,
    onResumenChange: (Boolean) -> Unit,
    selectedCurrency: String,
    onCurrencyChange: (String) -> Unit,
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit
) {
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

                // --- IDIOMA Y REGIÓN ---
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

                // --- APARIENCIA ---
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
                    CajasPreferencias(
                        image = R.drawable.text_width_solid_full,
                        name = stringResource(R.string.preferencias_titulo_tamaño_letra),
                        role = stringResource(R.string.preferencias_role_tamaño_letra),
                        value = "Normal",
                        options = listOf("Pequeño", "Normal", "Grande", "Extra Grande"),
                        type = "select"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- NOTIFICACIONES ---
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

                // --- MÁS INFORMACIÓN ---
                GlassCard(title = stringResource(R.string.preferencias_seccion_mas_info)) {
                    CajasPreferencias(
                        image = R.drawable.circle_info_solid_full,
                        name = stringResource(R.string.preferencias_info_app),
                        role = stringResource(R.string.preferencias_info_app_role),
                        value = "on",
                        type = "nav",
                        navController = navController,
                        onClick = { navController.navigate(Routes.SOBRE_NOSOTROS) }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CajasPreferencias(
                        image = R.drawable.clipboard_list_solid_full,
                        name = stringResource(R.string.preferencias_terminos),
                        role = stringResource(R.string.preferencias_terminos_role),
                        value = "on",
                        type = "nav",
                        navController = navController,
                        onClick = { navController.navigate(Routes.TERMINOS_CONDICIONES) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.preferencias_footer),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
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
    // Fila que conforma cada ítem de ajuste individual
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(if (type == "nav") Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Bloque de icono identificativo de la preferencia
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

        // Bloque de descripción textual
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

        // Renderizado condicional del componente de interacción según el tipo de dato
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

                    // Menú desplegable para la selección entre múltiples opciones
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
                // Indicador visual para elementos de navegación simple
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



@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreferenciasPreviewDark() {
    AppTheme(useDarkTheme = true) {
        PreferenciasScreen(
            navController = rememberNavController(),
            isDarkMode = true,
            onDarkModeChange = {},
            recordatorioViajes = true,
            onRecordatorioChange = {},
            resumenSemanal = true,
            onResumenChange = {},
            // Parámetros faltantes añadidos:
            selectedCurrency = "EUR(€)",
            onCurrencyChange = {},
            selectedLanguage = "Es Español",
            onLanguageChange = {}
        )
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PreferenciasPreviewLight() {
    AppTheme(useDarkTheme = false) {
        PreferenciasScreen(
            navController = rememberNavController(),
            isDarkMode = false,
            onDarkModeChange = {},
            recordatorioViajes = false,
            onRecordatorioChange = {},
            resumenSemanal = false,
            onResumenChange = {},
            // Parámetros faltantes añadidos:
            selectedCurrency = "USD($)",
            onCurrencyChange = {},
            selectedLanguage = "Es Español",
            onLanguageChange = {}
        )
    }
}