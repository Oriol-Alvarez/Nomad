package com.example.app.di

import android.content.Context
import com.example.app.data.local.AppDatabase
import com.example.app.data.local.TripDao
import com.example.app.data.local.ItineraryDao
import com.example.app.data.local.UserDao
import com.example.app.data.local.AccessLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideTripDao(database: AppDatabase): TripDao = database.tripDao()

    @Provides
    fun provideItineraryDao(database: AppDatabase): ItineraryDao = database.itineraryDao()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideAccessLogDao(database: AppDatabase): AccessLogDao = database.accessLogDao()
}
