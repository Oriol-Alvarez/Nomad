package com.example.app.data.repository

import com.example.app.data.local.ItineraryDao
import com.example.app.domain.ItineraryItem
import com.example.app.domain.ItineraryItemRepository
import kotlinx.coroutines.flow.Flow

class ItineraryItemRepositoryImpl(private val itineraryDao: ItineraryDao) : ItineraryItemRepository {

    override suspend fun insertItineraryItem(item: ItineraryItem) {
        itineraryDao.insertItem(item)
    }

    override suspend fun updateItineraryItem(item: ItineraryItem) {
        itineraryDao.updateItem(item)
    }

    override suspend fun deleteItineraryItem(item: ItineraryItem) {
        itineraryDao.deleteItem(item)
    }

    override suspend fun deleteItineraryItemsByTripId(tripId: String) {
        itineraryDao.deleteItemsByTripId(tripId)
    }

    override fun getItineraryItemsForTrip(tripId: String): Flow<List<ItineraryItem>> {
        return itineraryDao.getItemsForTrip(tripId)
    }

    override suspend fun getItineraryItemById(id: String): ItineraryItem? {
        return itineraryDao.getItemById(id)
    }
}
