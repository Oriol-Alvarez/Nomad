package com.example.app.ui.screens

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Este objeto sirve para revisar que los datos que escribe el usuario estén bien.
 * Por ejemplo, que no deje campos vacíos o que las fechas tengan sentido.
 */
object Validator {

    // Comprueba si el texto no está vacío
    fun isNotEmpty(text: String): Boolean = text.trim().isNotEmpty()
    
    // Comprueba si el texto tiene un mínimo de letras
    fun hasMinLength(text: String, min: Int): Boolean = text.trim().length >= min
    
    // Comprueba si el texto se pasa de largo
    fun hasMaxLength(text: String, max: Int): Boolean = text.trim().length <= max

    // Mira si el texto solo tiene letras y espacios (útil para nombres)
    fun isOnlyLetters(text: String): Boolean {
        return text.isNotEmpty() && text.all { it.isLetter() || it.isWhitespace() }
    }

    // --- REGLAS PARA LOS VIAJES ---

    // El título debe tener entre 3 y 50 letras y ser seguro
    fun isValidTitle(title: String): Boolean {
        return isNotEmpty(title) && hasMinLength(title, 3) && hasMaxLength(title, 50) && isSecureText(title)
    }

    // La ubicación debe tener al menos una coma (ej: "Madrid, España")
    fun isValidLocation(location: String): Boolean {
        return location.contains(",") && location.split(",").size >= 2
    }

    // El precio debe ser un número válido y no ser negativo
    fun isValidPrice(price: String): Boolean {
        if (price.isEmpty()) return false
        val cleanPrice = price.replace(",", ".") 
        val value = cleanPrice.toDoubleOrNull()
        return value != null && value >= 0
    }

    // Filtra caracteres raros que podrían dar problemas de seguridad
    fun isSecureText(text: String): Boolean {
        val forbiddenChars = listOf("<", ">", "{", "}", "[", "]", ";", "$", "http", "www")
        return forbiddenChars.none { text.lowercase().contains(it) }
    }

    // --- MANEJO DE FECHAS ---

    // Intenta entender la fecha sin importar si usa guiones o barras
    private fun parseAnyDate(dateStr: String): java.util.Date? {
        val formats = listOf("dd/MM/yyyy", "yyyy-MM-dd", "yyyy/MM/dd", "dd-MM-yyyy")
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                sdf.isLenient = false
                return sdf.parse(dateStr)
            } catch (e: Exception) { continue }
        }
        return null
    }

    // La fecha de ida no puede ser después de la de vuelta
    fun areDatesValid(fechaIda: String, fechaVuelta: String): Boolean {
        val dateIda = parseAnyDate(fechaIda)
        val dateVuelta = parseAnyDate(fechaVuelta)
        if (dateIda == null || dateVuelta == null) return false
        return !dateIda.after(dateVuelta)
    }

    // Comprueba que la fecha no sea de ayer o antes
    fun isNotPastDate(fecha: String): Boolean {
        val dateInput = parseAnyDate(fecha) ?: return false
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val today = sdf.parse(sdf.format(java.util.Date()))
        return !dateInput.before(today)
    }

    // Mira si una actividad cae dentro de los días que dura el viaje
    fun isActivityInTripRange(activityDate: String, tripStartDate: String, tripEndDate: String): Boolean {
        val dateAct = parseAnyDate(activityDate)
        val dateStart = parseAnyDate(tripStartDate)
        val dateEnd = parseAnyDate(tripEndDate)

        if (dateAct == null || dateStart == null || dateEnd == null) return false

        return !dateAct.before(dateStart) && !dateAct.after(dateEnd)
    }

    // Comprueba que la fecha de nacimiento sea anterior a hoy
    fun isBirthdateValid(birthdate: String): Boolean {
        val dateInput = parseAnyDate(birthdate) ?: return false
        val today = java.util.Date()
        return dateInput.before(today)
    }
}
