package com.example.app.data.repository

import com.example.app.domain.HotelRepository
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.app.data.remote.api.HotelApiService

class HotelRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var repository: HotelRepository

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(HotelApiService::class.java)
        repository = HotelRepositoryImpl(apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test getHotels returns list of hotels on success`() = runBlocking {
        val mockResponseJson = """
            [
              {
                "id": "PAR01",
                "name": "Hotel Louvre",
                "address": "Rue de Rivoli 99, Paris",
                "rating": 4,
                "rooms": [
                  {
                    "id": "R1",
                    "room_type": "single",
                    "price": 80.0,
                    "images": ["/images/PAR01R1.png"]
                  }
                ],
                "image_url": "/images/PAR01.png"
              }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseJson)
        )

        val hotels = repository.getHotels("G15")

        assertNotNull(hotels)
        assertEquals(1, hotels.size)
        val hotel = hotels[0]
        assertEquals("PAR01", hotel.id)
        assertEquals("Hotel Louvre", hotel.name)
        assertEquals(4, hotel.rating)
        assertEquals(1, hotel.rooms.size)
        assertEquals("R1", hotel.rooms[0].id)
        assertEquals(80.0, hotel.rooms[0].price, 0.0)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/hotels/G15/hotels", recordedRequest.path)
        assertEquals("GET", recordedRequest.method)
    }

    @Test
    fun `test checkAvailability returns available hotels list on success`() = runBlocking {
        val mockResponseJson = """
            {
              "available_hotels": [
                {
                  "id": "PAR01",
                  "name": "Hotel Louvre",
                  "address": "Rue de Rivoli 99, Paris",
                  "rating": 4,
                  "image_url": "/images/PAR01.png",
                  "rooms": [
                    {
                      "id": "R1",
                      "room_type": "single",
                      "price": 80.0,
                      "images": ["/images/PAR01R1.png"]
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseJson)
        )

        val hotels = repository.getAvailability(
            groupId = "G15",
            startDate = "2026-05-10",
            endDate = "2026-05-15",
            city = "Paris"
        )

        assertNotNull(hotels)
        assertEquals(1, hotels.size)
        assertEquals("PAR01", hotels[0].id)

        val recordedRequest = mockWebServer.takeRequest()
        assertTrue(recordedRequest.path!!.startsWith("/hotels/G15/availability"))
        assertTrue(recordedRequest.path!!.contains("start_date=2026-05-10"))
        assertTrue(recordedRequest.path!!.contains("end_date=2026-05-15"))
        assertTrue(recordedRequest.path!!.contains("city=Paris"))
        assertEquals("GET", recordedRequest.method)
    }

    @Test
    fun `test reserveRoom returns reserve response on success`() = runBlocking {
        val mockResponseJson = """
            {
              "message": "Reservation confirmed",
              "nights": 5,
              "reservation": {
                "id": "DOWOOG",
                "hotel_id": "PAR01",
                "room_id": "R1",
                "start_date": "2026-05-10",
                "end_date": "2026-05-15",
                "guest_name": "John Doe",
                "guest_email": "john@example.com"
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseJson)
        )

        val reservation = repository.reserveRoom(
            groupId = "G15",
            hotelId = "PAR01",
            roomId = "R1",
            startDate = "2026-05-10",
            endDate = "2026-05-15",
            guestName = "John Doe",
            guestEmail = "john@example.com"
        )

        assertNotNull(reservation)
        assertEquals("DOWOOG", reservation.id)
        assertEquals("PAR01", reservation.hotelId)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/hotels/G15/reserve", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)
    }

    @Test
    fun `test cancelReservation returns cancelled reservation on success`() = runBlocking {
        val mockResponseJson = """
            {
              "message": "Reservation cancelled successfully"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponseJson)
        )

        val apiMessage = repository.cancelReservationById("DOWOOG")

        assertNotNull(apiMessage)
        assertEquals("Reservation cancelled successfully", apiMessage.message)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/reservations/DOWOOG", recordedRequest.path)
        assertEquals("DELETE", recordedRequest.method)
    }

    @Test
    fun `test api failure returns failure result`() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
        )

        val result = runCatching {
            repository.getHotels("G15")
        }

        assertTrue(result.isFailure)
    }
}
