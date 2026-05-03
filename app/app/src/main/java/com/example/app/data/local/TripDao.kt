package com.example.app.data.local

import androidx.room.*
import com.example.app.domain.Trip
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
}
