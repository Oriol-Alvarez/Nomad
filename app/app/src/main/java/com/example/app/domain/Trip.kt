package com.example.app.domain

import android.media.Image

data class Trip(
    val id: String,
    var title: String,
    var country: String,
    var description: String,
    var imageRes: Int,
    var isFeatured: Boolean,
    var budget: Double,
    val activities: MutableList<ItineraryItem> = mutableListOf(),
    val images: MutableList<Image> = mutableListOf()
) {
    /**
     * Crea y guarda el viaje en la base de datos.
     */
    fun createTrip() {
        // @TODO Insertar este objeto en Room Database (TripDao)
    }

    /**
     * Actualiza los datos del viaje.
     */
    fun updateTrip(newTitle: String, newDescription: String) {
        this.title = newTitle
        this.description = newDescription
        // @TODO Actualizar en la base de datos
    }

    /**
     * Elimina el viaje actual.
     */
    fun deleteTrip() {
        // @TODO Eliminar este viaje y sus actividades asociadas de la DB
    }

    /**
     * Añade una nueva actividad al itinerario del viaje.
     */
    fun addItineraryItem(item: ItineraryItem) {
        activities.add(item)
        // @TODO Guardar la nueva actividad en la base de datos
    }

    /**
     * Añade una imagen a la galería del viaje.
     */
    fun addImage(image: Image) {
        images.add(image)
        // @TODO Guardar la referencia de la imagen
    }

    /**
     * Genera un enlace o formato para compartir el viaje con amigos.
     */
    fun shareTrip(): String {
        // @TODO Generar un Deep Link o texto formateado para compartir
        return "¡Mira mi viaje a $title en Nomad! [Enlace]"
    }

    /**
     * Calcula el presupuesto restante descontando las actividades planificadas.
     */
    fun getRemainingBudget(): Double {
        val totalActivityCost = activities.sumOf { it.cost }
        return budget - totalActivityCost
    }

    /**
     * Optimiza la distribución del presupuesto restante por día.
     */
    fun optimizeBudgetDistribution() {
        // @TODO Implementar el algoritmo inteligente de distribución
    }
}