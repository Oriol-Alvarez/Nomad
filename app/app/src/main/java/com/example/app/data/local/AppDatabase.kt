package com.example.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.app.domain.Trip
import com.example.app.domain.ItineraryItem
import com.example.app.domain.User
import com.example.app.domain.AccessLog
import com.example.app.domain.TripImage

@Database(
    entities = [Trip::class, ItineraryItem::class, User::class, AccessLog::class, TripImage::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun itineraryDao(): ItineraryDao
    abstract fun userDao(): UserDao
    abstract fun accessLogDao(): AccessLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nomad_database"
                )
                .fallbackToDestructiveMigration() // Para desarrollo, resetea la DB al cambiar versión
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
