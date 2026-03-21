package com.example.app.ui.screens

import java.text.SimpleDateFormat
import java.util.Locale

object Validator {

    // --- VALIDACIONES DE TEXTO BÁSICAS ---

    // 1. Validar que no esté vacío
    fun isNotEmpty(text: String): Boolean = text.trim().isNotEmpty()

    // 2. Validar longitud mínima
    fun hasMinLength(text: String, min: Int): Boolean = text.trim().length >= min

    // 3. Validar longitud máxima (evita textos infinitos en la base de datos)
    fun hasMaxLength(text: String, max: Int): Boolean = text.trim().length <= max

    // 4. Validar que solo contenga letras (nombres de usuario o etiquetas simples)
    fun isOnlyLetters(text: String): Boolean {
        return text.isNotEmpty() && text.all { it.isLetter() || it.isWhitespace() }
    }

    // --- VALIDACIONES ESPECÍFICAS DE NEGOCIO ---

    // 5. Validar Título del Viaje (Mínimo 3 letras, máximo 50, sin símbolos raros)
    fun isValidTitle(title: String): Boolean {
        return isNotEmpty(title) && hasMinLength(title, 3) && hasMaxLength(title, 50) && isSecureText(title)
    }

    // 6. Validar Ubicación (Debe venir del buscador con formato "Ciudad, País")
    fun isValidLocation(location: String): Boolean {
        return location.contains(",") && location.split(",").size >= 2
    }

    // 7. Validar Formato de Precio
    fun isValidPrice(price: String): Boolean {
        if (price.isEmpty()) return false
        val cleanPrice = price.replace(",", ".") // Soporte para decimales con coma
        val value = cleanPrice.toDoubleOrNull()
        return value != null && value >= 0
    }

    // --- VALIDACIONES DE SEGURIDAD ---

    // 8. Evitar caracteres de inyección de código o scripts
    fun isSecureText(text: String): Boolean {
        val forbiddenChars = listOf("<", ">", "{", "}", "[", "]", ";", "$", "http", "www")
        return forbiddenChars.none { text.lowercase().contains(it) }
    }

    // --- VALIDACIONES DE FECHAS ---

    // 9. Lógica de coherencia temporal (Ida <= Vuelta)
    fun areDatesValid(fechaIda: String, fechaVuelta: String): Boolean {
        if (fechaIda.isEmpty() || fechaVuelta.isEmpty()) return false
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateIda = sdf.parse(fechaIda)
            val dateVuelta = sdf.parse(fechaVuelta)

            if (dateIda == null || dateVuelta == null) return false

            // La vuelta debe ser igual o posterior a la ida
            !dateIda.after(dateVuelta)
        } catch (e: Exception) {
            false
        }
    }

    // 10. Validar que la fecha no sea pasada (No viajar al ayer)
    fun isNotPastDate(fecha: String): Boolean {
        if (fecha.isEmpty()) return false
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateInput = sdf.parse(fecha)
            val today = sdf.parse(sdf.format(java.util.Date())) // Hoy a las 00:00

            dateInput != null && !dateInput.before(today)
        } catch (e: Exception) {
            false
        }
    }


}