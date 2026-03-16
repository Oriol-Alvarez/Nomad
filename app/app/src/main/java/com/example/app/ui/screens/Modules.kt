package com.example.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFechaModular(
    label: String,
    fechaSeleccionada: String,
    onFechaElegida: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Componente auxiliar que envuelve un DatePickerDialog nativo de Material 3
    var mostrar by remember { mutableStateOf(false) }
    val state = rememberDatePickerState()

    // Campo de texto de solo lectura que actúa como disparador del modal
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
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
            confirmButton = {
                Button(
                    onClick = {
                        val formatted = state.selectedDateMillis?.let {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                        } ?: ""
                        onFechaElegida(formatted)
                        mostrar = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
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
                    headlineContentColor = MaterialTheme.colorScheme.onPrimary,
                    dayContentColor = MaterialTheme.colorScheme.onPrimary,
                    selectedDayContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    selectedDayContentColor = MaterialTheme.colorScheme.inversePrimary,
                    todayContentColor = MaterialTheme.colorScheme.surfaceContainer,
                    todayDateBorderColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorHoraModular(
    label: String,
    horaSeleccionada: String,
    onHoraElegida: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Componente auxiliar para la selección de tiempo utilizando un TimePicker nativo
    var mostrar by remember { mutableStateOf(false) }
    val state = rememberTimePickerState()

    // Campo de texto interactivo que despliega el selector horario
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
            containerColor = MaterialTheme.colorScheme.background,
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
                // Configuración visual del reloj para mantener la consistencia con el tema principal
                TimePicker(
                    state = state,
                    colors = TimePickerDefaults.colors(
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContainerColor = Color(0xFFF5F5F5),
                        timeSelectorUnselectedContentColor = Color.Black,
                        clockDialColor = MaterialTheme.colorScheme.background,
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                        selectorColor = MaterialTheme.colorScheme.surfaceContainer,
                        periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        periodSelectorSelectedContentColor = Color.White
                    )
                )
            }
        )
    }
}

suspend fun buscarCiudadesOSM(consulta: String): List<String> = withContext(Dispatchers.IO) {
    if (consulta.isBlank()) return@withContext emptyList()
    try {
        val url = URL("https://nominatim.openstreetmap.org/search?q=${consulta.replace(" ", "%20")}&format=json&addressdetails=1&limit=5")
        val conn = url.openConnection() as HttpURLConnection
        conn.setRequestProperty("User-Agent", "NomadApp")

        val json = conn.inputStream.bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        val res = mutableListOf<String>()

        for (i in 0 until array.length()) {
            val addr = array.getJSONObject(i).optJSONObject("address")
            if (addr != null) {
                // Prioridad: ciudad, si no pueblo, si no villa
                val ciudad = addr.optString("city", addr.optString("town", addr.optString("village", "")))
                val pais = addr.optString("country", "")

                if (ciudad.isNotEmpty() && pais.isNotEmpty()) {
                    res.add("$ciudad, $pais")
                }
            }
        }
        res.distinct()
    } catch (e: Exception) {
        emptyList()
    }
}

object CurrencyConverter {
    private val rates = mapOf(
        "EUR(€)" to 1.0,
        "USD($)" to 1.09,
        "GBP(£)" to 0.85,
        "MXN($)" to 18.50
    )

    fun convert(amount: Double, targetCurrency: String): String {
        val rate = rates[targetCurrency] ?: 1.0
        val converted = amount * rate

        // Extraemos el símbolo (ej. "€")
        val symbol = targetCurrency.substringAfter("(").substringBefore(")")

        // Configuración del formato:
        // "#.##" significa: muestra hasta 2 decimales, pero solo si no son cero.
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        val df = DecimalFormat("#.##", symbols)

        val formattedNumber = df.format(converted)

        return "$formattedNumber $symbol"
    }
}