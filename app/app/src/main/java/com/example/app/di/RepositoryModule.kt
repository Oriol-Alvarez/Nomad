package com.example.app.di

import com.example.app.data.repository.AccessLogRepositoryImpl
import com.example.app.data.repository.AuthRepositoryImpl
import com.example.app.data.repository.ItineraryItemRepositoryImpl
import com.example.app.data.repository.TripRepositoryImpl
import com.example.app.data.repository.UserRepositoryImpl
import com.example.app.domain.AccessLogRepository
import com.example.app.domain.AuthRepository
import com.example.app.domain.ItineraryItemRepository
import com.example.app.domain.TripRepository
import com.example.app.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTripRepository(
        tripRepositoryImpl: TripRepositoryImpl
    ): TripRepository

    @Binds
    @Singleton
    abstract fun bindItineraryItemRepository(
        itineraryItemRepositoryImpl: ItineraryItemRepositoryImpl
    ): ItineraryItemRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAccessLogRepository(
        accessLogRepositoryImpl: AccessLogRepositoryImpl
    ): AccessLogRepository
}
