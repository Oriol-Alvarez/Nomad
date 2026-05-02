package com.example.app.domain

import kotlinx.coroutines.flow.Flow

interface ItineraryItemRepository {
    suspend fun insertItineraryItem(item: ItineraryItem)
    suspend fun updateItineraryItem(item: ItineraryItem)
    suspend fun deleteItineraryItem(item: ItineraryItem)
    suspend fun deleteItineraryItemsByTripId(tripId: String)
    fun getItineraryItemsForTrip(tripId: String): Flow<List<ItineraryItem>>
    suspend fun getItineraryItemById(id: String): ItineraryItem?
}
