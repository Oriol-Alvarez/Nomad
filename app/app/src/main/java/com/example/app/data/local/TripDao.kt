package com.example.app.data.local

import androidx.room.*
import com.example.app.domain.Trip
import com.example.app.domain.TripImage
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE userId = :userId")
    fun getTripsForUser(userId: String): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE id = :id")
    suspend fun getTripById(id: String): Trip?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    @Update
    suspend fun updateTrip(trip: Trip)

    @Query("DELETE FROM trips WHERE id = :id")
    suspend fun deleteTripById(id: String)

    // T3: Métodos de imágenes de viaje
    @Query("SELECT * FROM trip_images WHERE tripId = :tripId")
    fun getImagesForTrip(tripId: String): Flow<List<TripImage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripImage(image: TripImage)

    @Query("DELETE FROM trip_images WHERE id = :id")
    suspend fun deleteTripImageById(id: String)

    @Query("DELETE FROM trip_images WHERE tripId = :tripId")
    suspend fun deleteTripImagesByTripId(tripId: String)
}
