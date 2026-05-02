package com.example.app.data.local

import androidx.room.*
import com.example.app.domain.ItineraryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryDao {
    @Query("SELECT * FROM itinerary_items WHERE tripId = :tripId")
    fun getItemsForTrip(tripId: String): Flow<List<ItineraryItem>>

    @Query("SELECT * FROM itinerary_items WHERE id = :id")
    suspend fun getItemById(id: String): ItineraryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItineraryItem)

    @Update
    suspend fun updateItem(item: ItineraryItem)

    @Delete
    suspend fun deleteItem(item: ItineraryItem)

    @Query("DELETE FROM itinerary_items WHERE tripId = :tripId")
    suspend fun deleteItemsByTripId(tripId: String)
}
