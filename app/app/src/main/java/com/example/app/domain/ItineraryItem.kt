package com.example.app.domain

data class ItineraryItem(
    val itemId: String,
    var activityName: String,
    var schedule: Long, // Usamos Long para fechas/horas (TimeInMillis)
    var locationName: String,
    var cost: Double,
    var isCompleted: Boolean = false
) {
    /**
     * Crea y guarda esta actividad en la base de datos.
     */
    fun createItem() {
        // @TODO Insertar en la base de datos
    }

    /**
     * Modifica los datos principales de la actividad.
     */
    fun updateItem(newName: String, newCost: Double, newTime: Long) {
        this.activityName = newName
        this.cost = newCost
        this.schedule = newTime
        // @TODO Actualizar en la base de datos
    }

    /**
     * Elimina esta actividad del itinerario.
     */
    fun deleteItem() {
        // @TODO Borrar de la base de datos
    }

    /**
     * Marca la actividad como ya realizada.
     */
    fun markAsCompleted() {
        this.isCompleted = true
        // @TODO Actualizar el estado en la base de datos
    }
}