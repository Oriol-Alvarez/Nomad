package com.example.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.app.domain.TripRepository
import com.example.app.domain.ItineraryItemRepository

class TripViewModelFactory(
    private val tripRepository: TripRepository,
    private val itineraryRepository: ItineraryItemRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripListViewModel(tripRepository, itineraryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
