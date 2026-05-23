package com.example.app.domain

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import android.media.Image

@Entity(
    tableName = "trips",
    indices = [Index(value = ["userId", "title"], unique = true)]
)
data class Trip(
    @PrimaryKey
    val id: String,
    val userId: String,
    var title: String,
    var country: String,
    var description: String,
    var dataInici: String,
    var dataFinal: String,
    var imageUri: String,
    var isFeatured: Boolean,
    var budget: Double,
    
    // Campos opcionales para la integración de reservas de hotel
    var hasReservation: Boolean = false,
    var reservationId: String? = null,
    var hotelId: String? = null,
    var hotelName: String? = null,
    var hotelAddress: String? = null,
    var hotelRating: Int = 0,
    var hotelImageUrl: String? = null,
    var roomId: String? = null,
    var roomType: String? = null,
    var roomPrice: Double = 0.0,
    var reservationStartDate: String? = null,
    var reservationEndDate: String? = null,
    var guestName: String? = null,
    var guestEmail: String? = null
) {
    @Ignore
    var images: MutableList<Image> = mutableListOf()
}
