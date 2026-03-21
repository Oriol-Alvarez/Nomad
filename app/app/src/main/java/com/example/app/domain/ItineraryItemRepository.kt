package com.example.app.domain

interface ItineraryItemRepository {
    fun insertItineraryItem(item: ItineraryItem)
    fun updateItineraryItem(item: ItineraryItem)
    fun deleteItineraryItem(item: ItineraryItem)
    fun deleteItineraryItemsByTripId(tripId: String)
    fun getItineraryItemsForTrip(tripId: String): List<ItineraryItem>
    fun getItineraryItemById(id: String): ItineraryItem?
}