package com.example.app.data.repository

import com.example.app.data.fakeDB.FakeItineraryItemDataSource
import com.example.app.domain.ItineraryItem
import com.example.app.domain.ItineraryItemRepository

class ItineraryItemRepositoryImpl : ItineraryItemRepository {

    // Nos conectamos a tus datos falsos en memoria
    private val dataSource = FakeItineraryItemDataSource

    // ------------------------------------------------------------------------
    // MÉTODOS DE ESCRITURA
    // ------------------------------------------------------------------------

    override fun insertItineraryItem(item: ItineraryItem) {
        dataSource.addItem(item)
    }

    override fun updateItineraryItem(item: ItineraryItem) {
        dataSource.updateItem(item)
    }

    override fun deleteItineraryItem(item: ItineraryItem) {
        dataSource.deleteItem(item)
    }

    override fun deleteItineraryItemsByTripId(tripId: String) {
        dataSource.deleteItemsByTripId(tripId)
    }

    // ------------------------------------------------------------------------
    // MÉTODOS DE LECTURA
    // ------------------------------------------------------------------------

    override fun getItineraryItemsForTrip(tripId: String): List<ItineraryItem> {
        return dataSource.getItemsForTrip(tripId)
    }

    override fun getItineraryItemById(id: String): ItineraryItem? {
        return dataSource.getItemById(id)
    }
}