package com.example.app.ui.screens
import androidx.compose.foundation.background
import androidx.compose.material3.SelectableDates
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
import androidx.compose.material3.DatePickerColors
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
import androidx.compose.runtime.rememberUpdatedState
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
import java.util.Calendar

/**
 * Aquí tenemos componentes que usamos en varias pantallas,
 * como el selector de fecha, el de hora o el buscador de ciudades.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFechaModular(
    label: String,
    fechaSeleccionada: String,
    onFechaElegida: (String) -> Unit,
    fechaMinima: Long? = null,
    fechaMaxima: Long? = null,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    var mostrar by remember { mutableStateOf(false) }

    // Guardamos los límites de fecha para que el calendario no deje elegir días fuera de rango
    val minActual by rememberUpdatedState(fechaMinima)
    val maxActual by rememberUpdatedState(fechaMaxima)

    val state = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Ponemos la fecha a las 00:00 para comparar solo el día
                fun inicioDelDia(millis: Long): Long {
                    val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                    cal.timeInMillis = millis
                    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    cal.set(java.util.Calendar.MINUTE, 0)
                    cal.set(java.util.Calendar.SECOND, 0)
                    cal.set(java.util.Calendar.MILLISECOND, 0)
                    return cal.timeInMillis
                }

                val minDia = minActual?.let { inicioDelDia(it) }
                val maxDia = maxActual?.let { inicioDelDia(it) }

                val despuesDeMin = minDia == null || utcTimeMillis >= minDia
                val antesDeMax = maxDia == null || utcTimeMillis <= maxDia

                return despuesDeMin && antesDeMax
            }
        }
    )

    // Al pulsar en el campo, se abre el calendario
    Box(modifier = modifier.clickable { mostrar = true }) {
        OutlinedTextField(
            value = fechaSeleccionada,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            isError = isError,
            leadingIcon = { Icon(Icons.Default.DateRange, null) },
            modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.background),
            enabled = false,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                // Colores cuando está deshabilitado (nuestro caso)
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor =  if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                disabledLabelColor =  if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor =  if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )
    }

    if (mostrar) {
        DatePickerDialog(
            onDismissRequest = { mostrar = false },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            confirmButton = {
                TextButton(onClick = {
                    val formatted = state.selectedDateMillis?.let {
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date(it))
                    } ?: ""
                    onFechaElegida(formatted)
                    mostrar = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrar = false }) { Text("Cancelar") }
            },

        ) {
            DatePicker(state = state,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background
                ))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorHoraModular(
    label: String,
    horaSeleccionada: String,
    onHoraElegida: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    var mostrar by remember { mutableStateOf(false) }
    val state = rememberTimePickerState()

    // Al pulsar en el campo, se abre el reloj
    Box(modifier = modifier.clickable { mostrar = true }) {
        OutlinedTextField(
            value = horaSeleccionada,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(Icons.Default.AccessTime, null) },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                // Colores cuando está deshabilitado (nuestro caso)
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                disabledLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor =  if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )
    }

    if (mostrar) {
        AlertDialog(
            onDismissRequest = { mostrar = false },
            confirmButton = {
                Button(onClick = {
                    onHoraElegida(String.format("%02d:%02d", state.hour, state.minute))
                    mostrar = false
                }) { Text("Aceptar") }
            },
            text = {
                TimePicker(state = state,
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
                    ))
            },
            containerColor = MaterialTheme.colorScheme.background
        )
    }
}

/**
 * Busca ciudades en internet usando el servicio de OpenStreetMap.
 * Le pasas un texto (ej: "Mad") y te devuelve una lista (ej: ["Madrid, España", ...])
 */
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
                // Buscamos el nombre de la ciudad o pueblo
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

/**
 * Pasa los precios de Euros a otras monedas (Dólares, Libras, etc.)
 */
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
        val symbol = targetCurrency.substringAfter("(").substringBefore(")")

        val symbols = DecimalFormatSymbols(Locale.getDefault())
        val df = DecimalFormat("#.##", symbols)
        val formattedNumber = df.format(converted)

        return "$formattedNumber $symbol"
    }
}
