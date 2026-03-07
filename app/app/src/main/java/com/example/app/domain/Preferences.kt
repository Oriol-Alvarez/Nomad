package com.example.app.domain

data class Preferences(
    val userId: String,
    var notificationsEnabled: Boolean,
    var preferredLanguage: String,
    var theme: String,
    var termsAccepted: Boolean
) {
    /**
     * Devuelve el objeto actual de preferencias.
     */
    fun getPreferences(): Preferences {
        return this
    }

    /**
     * Actualiza los valores de las preferencias del usuario.
     */
    fun updatePreferences(
        notifications: Boolean = this.notificationsEnabled,
        language: String = this.preferredLanguage,
        newTheme: String = this.theme
    ) {
        this.notificationsEnabled = notifications
        this.preferredLanguage = language
        this.theme = newTheme
        // @TODO Guardar en SharedPreferences o Base de Datos local
    }

    /**
     * Restablece las preferencias a sus valores por defecto.
     */
    fun resetToDefault() {
        this.notificationsEnabled = true
        this.preferredLanguage = "es"
        this.theme = "system"
        // termsAccepted no se suele resetear por motivos legales
    }
}