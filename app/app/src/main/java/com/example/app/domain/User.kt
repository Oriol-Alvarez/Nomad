package com.example.app.domain

data class User(
    val userId: String,
    var name: String,
    var email: String,
    var profilePicture: String
) {
    /**
     * Registra un nuevo usuario en el sistema.
     */
    fun register() {
        // @TODO Llamar al servicio de autenticación/API para registrar al usuario
    }

    /**
     * Actualiza el perfil del usuario (nombre, foto, etc.).
     */
    fun updateProfile(newName: String, newPhoto: String) {
        this.name = newName
        this.profilePicture = newPhoto
        // @TODO Guardar los cambios en la base de datos (UserRepository)
    }

    /**
     * Elimina la cuenta del usuario y todos sus datos asociados.
     */
    fun deleteAccount() {
        this.name = "Usuario Eliminado"
        this.email = ""
        this.profilePicture = ""
        // @TODO Llamar a la API para eliminar la cuenta permanentemente
    }

    /**
     * Obtiene todos los viajes que pertenecen a este usuario.
     */
    fun getTrips(): List<Trip> {
        // @TODO Implementar la llamada al repositorio para obtener los viajes
        return emptyList()
    }
}