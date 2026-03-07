package com.example.app.domain

data class Authentication(
    val authId: String,
    var passwordHash: String,
    var sessionToken: String?,
    var isEmailVerified: Boolean
) {
    /**
     * Inicia sesión comprobando el email y la contraseña.
     */
    fun login(emailAttempt: String, passwordAttempt: String): Boolean {
        // @TODO Implementar la lógica de verificación (Firebase/Backend)
        this.sessionToken = "nuevo_token_de_sesion"
        return true
    }

    /**
     * Cierra la sesión del usuario invalidando su token.
     */
    fun logout() {
        this.sessionToken = null
        // @TODO Limpiar datos de sesión locales
    }

    /**
     * Envía un enlace para recuperar la contraseña.
     */
    fun resetPassword(email: String) {
        // @TODO Llamar a la API para enviar el correo de recuperación
    }

    /**
     * Cambia la contraseña actual por una nueva.
     */
    fun changePassword(old: String, new: String): Boolean {
        // @TODO Validar la antigua, generar el hash de la nueva y guardar
        this.passwordHash = "nuevo_hash_generado"
        return true
    }

    /**
     * Verifica la dirección de correo electrónico del usuario.
     */
    fun verifyEmail() {
        this.isEmailVerified = true
        // @TODO Registrar en el backend que el correo está verificado
    }

    /**
     * Refresca el token de sesión para que no caduque.
     */
    fun refreshToken() {
        // @TODO Solicitar un nuevo token al servidor utilizando un refresh token
        this.sessionToken = "token_refrescado"
    }
}