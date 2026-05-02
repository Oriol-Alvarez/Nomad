package com.example.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app.domain.TripRepository
import com.example.app.domain.ItineraryItemRepository
import com.example.app.domain.AuthRepository
import com.example.app.domain.UserRepository
import com.example.app.domain.AccessLogRepository

class AppViewModelFactory(
    private val tripRepository: TripRepository? = null,
    private val itineraryRepository: ItineraryItemRepository? = null,
    private val authRepository: AuthRepository? = null,
    private val userRepository: UserRepository? = null,
    private val accessLogRepository: AccessLogRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TripListViewModel::class.java) -> {
                TripListViewModel(tripRepository!!, itineraryRepository!!) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository!!, userRepository!!, accessLogRepository!!) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
