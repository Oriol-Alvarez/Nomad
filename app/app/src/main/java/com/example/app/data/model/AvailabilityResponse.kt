package com.example.app.data.model

import com.google.gson.annotations.SerializedName

data class AvailabilityResponse(
    @SerializedName("available_hotels") val availableHotels: List<Hotel>
)
