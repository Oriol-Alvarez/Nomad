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
    var budget: Double
) {
    @Ignore
    var images: MutableList<Image> = mutableListOf()
}
