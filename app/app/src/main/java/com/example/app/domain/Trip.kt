package com.example.app.domain

import android.media.Image

data class Trip(
    val id: String,
    var title: String,
    var country: String,
    var description: String,
    var dataInici: String,
    var dataFinal: String,
    var imageUri: String,
    var isFeatured: Boolean,
    var budget: Double,
    val activities: MutableList<ItineraryItem> = mutableListOf(),
    val images: MutableList<Image> = mutableListOf()
)
