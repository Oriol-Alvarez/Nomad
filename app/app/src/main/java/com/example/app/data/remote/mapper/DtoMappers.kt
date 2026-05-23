package com.example.app.data.remote.mapper

import com.example.app.data.remote.dto.*
import com.example.app.domain.*

fun HotelDto.toDomain(): Hotel {
    return Hotel(
        id = id,
        name = name,
        address = address,
        rating = rating,
        imageUrl = imageUrl,
        rooms = rooms?.map { it.toDomain() } ?: emptyList()
    )
}

fun RoomDto.toDomain(): Room {
    return Room(
        id = id,
        roomType = roomType,
        price = price,
        images = images ?: emptyList()
    )
}

fun ReservationDto.toDomain(): Reservation {
    return Reservation(
        id = id,
        hotelId = hotelId,
        roomId = roomId,
        startDate = startDate,
        endDate = endDate,
        guestName = guestName,
        guestEmail = guestEmail,
        hotel = hotel?.toDomain(),
        room = room?.toDomain()
    )
}

fun HotelShortDto.toDomain(): HotelShort {
    return HotelShort(
        id = id,
        name = name,
        address = address,
        rating = rating,
        imageUrl = imageUrl
    )
}

fun RoomShortDto.toDomain(): RoomShort {
    return RoomShort(
        id = id,
        roomType = roomType,
        price = price
    )
}

fun ApiMessageDto.toDomain(): ApiMessage {
    return ApiMessage(
        message = message
    )
}
