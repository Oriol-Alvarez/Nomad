package com.example.app.ui.screens

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
import java.util.TimeZone

/**
 * Clase de utilidad para manejar textos que pueden ser recursos de Android o Strings directos.
 * Permite que el ViewModel no tenga dependencias de Context.
 */
sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}

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
    val minActual by rememberUpdatedState(fechaMinima)
    val maxActual by rememberUpdatedState(fechaMaxima)

    val state = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                fun inicioDelDia(millis: Long): Long {
                    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    cal.timeInMillis = millis
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
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
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                disabledLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }

    if (mostrar) {
        DatePickerDialog(
            onDismissRequest = { mostrar = false },
            confirmButton = {
                TextButton(onClick = {
                    val formatted = state.selectedDateMillis?.let {
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                    } ?: ""
                    onFechaElegida(formatted)
                    mostrar = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrar = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = state)
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
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                disabledLabelColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
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
            text = { TimePicker(state = state) }
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
                val ciudad = addr.optString("city", addr.optString("town", addr.optString("village", "")))
                val pais = addr.optString("country", "")
                if (ciudad.isNotEmpty() && pais.isNotEmpty()) {
                    res.add("$ciudad, $pais")
                }
            }
        }
        res.distinct()
    } catch (e: Exception) { emptyList() }
}

object CurrencyConverter {
    private val rates = mapOf("EUR(€)" to 1.0, "USD($)" to 1.09, "GBP(£)" to 0.85, "MXN($)" to 18.50)
    fun convert(amount: Double, targetCurrency: String): String {
        val rate = rates[targetCurrency] ?: 1.0
        val converted = amount * rate
        val symbol = targetCurrency.substringAfter("(").substringBefore(")")
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        val df = DecimalFormat("#.##", symbols)
        return "${df.format(converted)} $symbol"
    }
}
