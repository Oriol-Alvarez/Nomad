package com.example.app.domain

data class ItineraryItem(
    val itemId: String,
    var activityName: String,
    var schedule: Long, // TimeInMillis
    var locationName: String,
    var cost: Double,
    var isCompleted: Boolean = false
)