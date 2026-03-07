package com.example.app.domain
data class ItineraryType(
    val typeId: String,
    var name: String,
    var icon: String
) {

    /**
     * Obtiene todos los tipos de itinerario disponibles.
     */
    fun getAllItineraryType(): List<ItineraryType> {
        // @TODO Obtener los tipos desde base de datos o API
        return emptyList()
    }

    /**
     * Obtiene el nombre del tipo de itinerario a partir de su ID.
     */
    fun getName(typeId: String): String? {
        // @TODO Buscar el nombre del tipo en base de datos o lista en memoria
        return null
    }

    /**
     * Obtiene el icono asociado al tipo de itinerario según su ID.
     */
    fun getIcon(typeId: String): String? {
        // @TODO Buscar el icono correspondiente al tipo en base de datos o lista en memoria
        return null
    }
}