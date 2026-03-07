package com.example.app.domain

data class MapData(
    val mapId: String,
    var latitude: Double,
    var longitude: Double,
    var zoomLevel: Float,
    var mapStyle: String
) {
    /**
     * Mueve el centro del mapa a una nueva ubicación específica.
     */
    fun showLocation(lat: Double, lng: Double) {
        this.latitude = lat
        this.longitude = lng
        // @TODO Conectar con el controlador de Google Maps/Mapbox
    }

    /**
     * Obtiene una lista de lugares de interés cercanos al radio especificado.
     */
    fun getNearbyPlaces(radius: Double): List<String> {
        // @TODO Llamar a la API de Google Places
        return emptyList()
    }

    /**
     * Calcula la ruta desde un origen hasta un destino.
     */
    fun calculateRoute(origin: String, destination: String): String {
        // @TODO Obtener la polilínea de la ruta de la API de Direcciones
        return "polilinea_de_ruta"
    }

    /**
     * Añade un marcador (pin) visual en el mapa.
     */
    fun addMarker(lat: Double, lng: Double, title: String) {
        // @TODO Dibujar el marcador en el mapa real
    }

    /**
     * Elimina un marcador existente del mapa.
     */
    fun removeMarker(markerId: String) {
        // @TODO Quitar el marcador del mapa usando su ID
    }
}