package com.example.app.data.fakeDB

import androidx.compose.runtime.mutableStateListOf
import com.example.app.domain.ItineraryItem

object FakeItineraryItemDataSource {
    private val items = mutableStateListOf<ItineraryItem>()

    init {
        // Actividades iniciales para el "Viaje a París" (tripId = "1")
        // Fechas del viaje: 2026-08-01 al 2026-08-05
        items.addAll(
            listOf(
                ItineraryItem(
                    id = "act1",
                    tripId = "1",
                    nombre = "Llegada y Check-in Hotel",
                    dia = "01/08/2026",
                    hora = "14:00",
                    precio = "0",
                    tipo = "Hotel",
                    descripcion = "Registro en el hotel cerca del centro."
                ),
                ItineraryItem(
                    id = "act2",
                    tripId = "1",
                    nombre = "Cena en Le Marais",
                    dia = "01/08/2026",
                    hora = "20:30",
                    precio = "45",
                    tipo = "Restaurante",
                    descripcion = "Cena tradicional francesa en un barrio histórico."
                ),
                ItineraryItem(
                    id = "act3",
                    tripId = "1",
                    nombre = "Museo del Louvre",
                    dia = "02/08/2026",
                    hora = "10:00",
                    precio = "22",
                    tipo = "Museo",
                    descripcion = "Visita guiada para ver la Mona Lisa y más."
                ),
                ItineraryItem(
                    id = "act4",
                    tripId = "1",
                    nombre = "Subida a la Torre Eiffel",
                    dia = "02/08/2026",
                    hora = "18:00",
                    precio = "28",
                    tipo = "Ocio",
                    descripcion = "Vistas panorámicas de toda la ciudad al atardecer."
                ),
                ItineraryItem(
                    id = "act5",
                    tripId = "1",
                    nombre = "Crucero por el Sena",
                    dia = "03/08/2026",
                    hora = "21:00",
                    precio = "15",
                    tipo = "Ocio",
                    descripcion = "Paseo nocturno en barco por el río."
                ),
                ItineraryItem(
                    id = "act6",
                    tripId = "1",
                    nombre = "Excursión a Versalles",
                    dia = "04/08/2026",
                    hora = "09:30",
                    precio = "20",
                    tipo = "Museo",
                    descripcion = "Visita a los jardines y palacio real."
                )
            )
        )
    }

    fun getItemsForTrip(tripId: String): List<ItineraryItem> {
        return items.filter { it.tripId == tripId }
    }

    fun getItemById(itemId: String): ItineraryItem? {
        return items.find { it.id == itemId }
    }

    fun addItem(item: ItineraryItem) {
        items.add(item)
    }

    fun updateItem(item: ItineraryItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item
        }
    }

    fun deleteItem(item: ItineraryItem) {
        items.removeAll { it.id == item.id }
    }

    fun deleteItemsByTripId(tripId: String) {
        items.removeAll { it.tripId == tripId }
    }
}
