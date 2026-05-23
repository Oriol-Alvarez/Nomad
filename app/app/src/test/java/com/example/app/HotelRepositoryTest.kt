package com.example.app

import com.example.app.data.remote.api.HotelApiService
import com.example.app.data.remote.dto.*
import com.example.app.data.repository.HotelRepositoryImpl
import com.example.app.domain.HotelRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HotelRepositoryTest {

    private lateinit var fakeApi: FakeHotelApiService
    private lateinit var repository: HotelRepository

    @Before
    fun setup() {
        fakeApi = FakeHotelApiService()
        repository = HotelRepositoryImpl(fakeApi)
    }

    @Test
    fun `test getHotels returns successfully mapped hotels`() = runBlocking {
        val hotels = repository.getHotels("G15")
        
        assertEquals(2, hotels.size)
        assertEquals("BCN01", hotels[0].id)
        assertEquals("Hotel Ramblas", hotels[0].name)
        assertEquals("La Rambla 33, Barcelona", hotels[0].address)
        assertEquals(4, hotels[0].rating)
        assertEquals("/images/BCN01.png", hotels[0].imageUrl)
        
        assertEquals(1, hotels[0].rooms.size)
        assertEquals("R1", hotels[0].rooms[0].id)
        assertEquals("single", hotels[0].rooms[0].roomType)
        assertEquals(80.0, hotels[0].rooms[0].price, 0.0)
        assertEquals(listOf("/images/BCN01R1.png"), hotels[0].rooms[0].images)
    }

    @Test
    fun `test getAvailability filtering options and response mapping`() = runBlocking {
        // Querying availability for G15 BCN
        val available = repository.getAvailability(
            groupId = "G15",
            startDate = "2025-05-01",
            endDate = "2025-05-03",
            hotelId = "BCN01",
            city = "BCN"
        )
        
        assertEquals(1, available.size)
        assertEquals("BCN01", available[0].id)
        assertEquals(1, available[0].rooms.size)
    }

    @Test
    fun `test reserveRoom maps response reservation correctly`() = runBlocking {
        val reservation = repository.reserveRoom(
            groupId = "G15",
            hotelId = "BCN01",
            roomId = "R1",
            startDate = "2025-05-01",
            endDate = "2025-05-03",
            guestName = "Oriol",
            guestEmail = "oriol@example.com"
        )
        
        assertEquals("RES123", reservation.id)
        assertEquals("BCN01", reservation.hotelId)
        assertEquals("R1", reservation.roomId)
        assertEquals("2025-05-01", reservation.startDate)
        assertEquals("2025-05-03", reservation.endDate)
        assertEquals("Oriol", reservation.guestName)
        assertEquals("oriol@example.com", reservation.guestEmail)
    }

    @Test
    fun `test cancelReservation returns successful message`() = runBlocking {
        val result = repository.cancelReservation(
            groupId = "G15",
            hotelId = "BCN01",
            roomId = "R1",
            startDate = "2025-05-01",
            endDate = "2025-05-03",
            guestName = "Oriol",
            guestEmail = "oriol@example.com"
        )
        
        assertEquals("Reserva cancelada con éxito", result.message)
    }

    @Test
    fun `test getReservations maps list properly`() = runBlocking {
        val list = repository.getReservations("G15", "oriol@example.com")
        
        assertEquals(1, list.size)
        assertEquals("RES123", list[0].id)
        assertNotNull(list[0].hotel)
        assertEquals("Hotel Ramblas", list[0].hotel?.name)
        assertNotNull(list[0].room)
        assertEquals("single", list[0].room?.roomType)
    }

    @Test
    fun `test cancelReservationById deletes and returns confirmation`() = runBlocking {
        val result = repository.cancelReservationById("RES123")
        assertEquals("Reserva RES123 cancelada con éxito", result.message)
    }

    // --- Fake Service Implementation ---
    class FakeHotelApiService : HotelApiService {
        
        override suspend fun getHotels(groupId: String): List<HotelDto> {
            return listOf(
                HotelDto(
                    id = "BCN01",
                    name = "Hotel Ramblas",
                    address = "La Rambla 33, Barcelona",
                    rating = 4,
                    imageUrl = "/images/BCN01.png",
                    rooms = listOf(
                        RoomDto("R1", "single", 80.0, listOf("/images/BCN01R1.png"))
                    )
                ),
                HotelDto(
                    id = "PAR01",
                    name = "Hotel Louvre",
                    address = "Rue de Rivoli 99, Paris",
                    rating = 4,
                    imageUrl = "/images/PAR01.png",
                    rooms = listOf(
                        RoomDto("R1", "single", 80.0, listOf("/images/PAR01R1.png"))
                    )
                )
            )
        }

        override suspend fun getAvailability(
            groupId: String,
            startDate: String,
            endDate: String,
            hotelId: String?,
            city: String?
        ): AvailabilityResponseDto {
            return AvailabilityResponseDto(
                availableHotels = listOf(
                    HotelDto(
                        id = "BCN01",
                        name = "Hotel Ramblas",
                        address = "La Rambla 33, Barcelona",
                        rating = 4,
                        imageUrl = "/images/BCN01.png",
                        rooms = listOf(
                            RoomDto("R1", "single", 80.0, listOf("/images/BCN01R1.png"))
                        )
                    )
                )
            )
        }

        override suspend fun reserveRoom(groupId: String, request: ReserveRequestDto): ReservationResponseDto {
            return ReservationResponseDto(
                message = "Reserva confirmada",
                nights = 2,
                reservation = ReservationDto(
                    id = "RES123",
                    hotelId = request.hotelId,
                    roomId = request.roomId,
                    startDate = request.startDate,
                    endDate = request.endDate,
                    guestName = request.guestName,
                    guestEmail = request.guestEmail
                )
            )
        }

        override suspend fun cancelReservation(groupId: String, request: ReserveRequestDto): ApiMessageDto {
            return ApiMessageDto("Reserva cancelada con éxito")
        }

        override suspend fun getReservations(groupId: String, guestEmail: String?): ReservationsResponseDto {
            return ReservationsResponseDto(
                reservations = listOf(
                    ReservationDto(
                        id = "RES123",
                        hotelId = "BCN01",
                        roomId = "R1",
                        startDate = "2025-05-01",
                        endDate = "2025-05-03",
                        guestName = "Oriol",
                        guestEmail = guestEmail ?: "oriol@example.com",
                        hotel = HotelShortDto("BCN01", "Hotel Ramblas", "La Rambla 33, Barcelona", 4, "/images/BCN01.png"),
                        room = RoomShortDto("R1", "single", 80.0)
                    )
                )
            )
        }

        override suspend fun getReservationById(resId: String): ReservationDto {
            return ReservationDto(
                id = resId,
                hotelId = "BCN01",
                roomId = "R1",
                startDate = "2025-05-01",
                endDate = "2025-05-03",
                guestName = "Oriol",
                guestEmail = "oriol@example.com"
            )
        }

        override suspend fun cancelReservationById(resId: String): ApiMessageDto {
            return ApiMessageDto("Reserva $resId cancelada con éxito")
        }
    }
}
