package com.example.app.domain

data class AIRecommendation(
    val recId: String,
    var score: Float,
    val reason: String,
    val generatedAt: Long // Fecha de generación en milisegundos
) {
    /**
     * Genera una lista de sugerencias basadas en las preferencias del usuario.
     */
    fun generateSuggestions(preferences: Preferences): List<AIRecommendation> {
        // @TODO Conectar con el modelo Gemini o el backend de IA para obtener recomendaciones
        return emptyList()
    }

    /**
     * Actualiza la lista de recomendaciones para obtener sugerencias más frescas.
     */
    fun refreshRecommendations() {
        // @TODO Volver a solicitar datos al motor de recomendación
    }

    /**
     * Permite al usuario darle una puntuación a la sugerencia para entrenar a la IA.
     */
    fun rateRecommendation(newScore: Float) {
        this.score = (this.score + newScore) / 2
        // @TODO Enviar el feedback de vuelta al modelo de aprendizaje automático
    }

    /**
     * Convierte esta recomendación de IA en un viaje real o actividad para el usuario.
     */
    fun applyToTrip(tripId: String) {
        // @TODO Convertir esta recomendación y añadirla a la tabla de Trips o ItineraryItems
    }
}