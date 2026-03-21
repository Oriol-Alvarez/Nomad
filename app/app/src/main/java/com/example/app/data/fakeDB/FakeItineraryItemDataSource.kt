package com.example.app.data.fakeDB

import androidx.compose.runtime.mutableStateListOf
import com.example.app.domain.ItineraryItem

object FakeItineraryItemDataSource {
    private val items = mutableStateListOf<ItineraryItem>()

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