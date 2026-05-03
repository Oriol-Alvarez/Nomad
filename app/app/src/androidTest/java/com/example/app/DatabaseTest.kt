package com.example.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app.data.local.*
import com.example.app.domain.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var tripDao: TripDao
    private lateinit var userDao: UserDao
    private lateinit var itineraryDao: ItineraryDao
    private lateinit var accessLogDao: AccessLogDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        tripDao = db.tripDao()
        userDao = db.userDao()
        itineraryDao = db.itineraryDao()
        accessLogDao = db.accessLogDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun tripCrudOperations() = runBlocking {
        val trip = Trip(
            id = "trip_1",
            userId = "user_1",
            title = "Paris Trip",
            country = "France",
            description = "Exploring Paris",
            dataInici = "2024-05-01",
            dataFinal = "2024-05-10",
            imageUri = "",
            isFeatured = false,
            budget = 500.0
        )
        tripDao.insertTrip(trip)

        var trips = tripDao.getTripsForUser("user_1").first()
        assertEquals(1, trips.size)
        assertEquals("Paris Trip", trips[0].title)

        trip.title = "Updated Paris Trip"
        tripDao.updateTrip(trip)
        trips = tripDao.getTripsForUser("user_1").first()
        assertEquals("Updated Paris Trip", trips[0].title)

        tripDao.deleteTripById("trip_1")
        trips = tripDao.getTripsForUser("user_1").first()
        assertEquals(0, trips.size)
    }

    @Test
    fun multiUserTripIsolationTest() = runBlocking {
        val tripUser1 = Trip("T1", "user_A", "My Trip", "Spain", "D", "2024-01-01", "2024-01-05", "", false, 0.0)
        val tripUser2 = Trip("T2", "user_B", "Other Trip", "France", "D", "2024-02-01", "2024-02-05", "", false, 0.0)

        tripDao.insertTrip(tripUser1)
        tripDao.insertTrip(tripUser2)

        val tripsA = tripDao.getTripsForUser("user_A").first()
        assertEquals(1, tripsA.size)
        assertEquals("T1", tripsA[0].id)

        val tripsB = tripDao.getTripsForUser("user_B").first()
        assertEquals(1, tripsB.size)
        assertEquals("T2", tripsB[0].id)
    }

    @Test
    fun userPersistenceAndUniqueCheck() = runBlocking {
        val user = User("u1", "test@example.com", "guillem", "1990-01-01", "BCN", "Spain", "123", true)
        userDao.insertUser(user)

        val retrievedUser = userDao.getUserById("u1")
        assertNotNull(retrievedUser)
        assertEquals("guillem", retrievedUser?.username)

        val byUsername = userDao.getUserByUsername("guillem")
        assertNotNull(byUsername)

        assertNull(userDao.getUserByUsername("unknown"))
    }

    @Test
    fun itineraryPersistenceAndFiltering() = runBlocking {
        // Create trip first for foreign key
        val trip = Trip("trip_1", "u1", "T", "C", "D", "2024-01-01", "2024-01-05", "", false, 0.0)
        tripDao.insertTrip(trip)
        val trip2 = Trip("trip_2", "u1", "T2", "C", "D", "2024-01-01", "2024-01-05", "", false, 0.0)
        tripDao.insertTrip(trip2)

        val item1 = ItineraryItem("i1", "trip_1", "Louvre", "2024-05-02", "10:00", 20, "Museum", "Desc")
        val item2 = ItineraryItem("i2", "trip_2", "Colosseum", "2024-06-01", "12:00", 15, "Museum", "Desc")

        itineraryDao.insertItem(item1)
        itineraryDao.insertItem(item2)

        val itemsTrip1 = itineraryDao.getItemsForTrip("trip_1").first()
        assertEquals(1, itemsTrip1.size)
        assertEquals("Louvre", itemsTrip1[0].nombre)

        // Test Update (T1.4)
        val updatedItem = item1.copy(nombre = "Louvre Museum", precio = 25)
        itineraryDao.updateItem(updatedItem)
        val afterUpdate = itineraryDao.getItemById("i1")
        assertEquals("Louvre Museum", afterUpdate?.nombre)
        assertEquals(25, afterUpdate?.precio)

        itineraryDao.deleteItemsByTripId("trip_1")
        val itemsAfterDelete = itineraryDao.getItemsForTrip("trip_1").first()
        assertTrue(itemsAfterDelete.isEmpty())
    }

    @Test
    fun tripDeletionCascadeTest() = runBlocking {
        // T4.2 & Integrity: Check if deleting trip deletes its itinerary items
        val tripId = "trip_cascade"
        val trip = Trip(tripId, "u1", "T", "C", "D", "2024-01-01", "2024-01-05", "", false, 0.0)
        tripDao.insertTrip(trip)

        val item = ItineraryItem("item_cascade", tripId, "Act", "2024-01-02", "10:00", 0, "Type", "Desc")
        itineraryDao.insertItem(item)

        // Verify it exists
        assertNotNull(itineraryDao.getItemById("item_cascade"))

        // Delete trip
        tripDao.deleteTripById(tripId)

        // Verify item is gone (Cascading delete)
        val items = itineraryDao.getItemsForTrip(tripId).first()
        assertTrue("Itinerary items should be deleted when trip is deleted", items.isEmpty())
        assertNull(itineraryDao.getItemById("item_cascade"))
    }

    @Test
    fun accessLogPersistenceAndOrdering() = runBlocking {
        val uid = "user_1"
        accessLogDao.insertLog(AccessLog(userId = uid, dateTime = 1000L, type = "LOGIN"))
        accessLogDao.insertLog(AccessLog(userId = uid, dateTime = 2000L, type = "LOGOUT"))

        val logs = accessLogDao.getLogsForUser(uid)
        assertEquals(2, logs.size)
        // Check DESC order (dateTime DESC)
        assertEquals("LOGOUT", logs[0].type)
        assertEquals(2000L, logs[0].dateTime)
    }

    @Test
    fun tripUniqueTitlePerUserConstraint() = runBlocking {
        // T5.2: Prevent duplicate trip names for the same user
        val userId = "user_unique"
        val trip1 = Trip("T1", userId, "Unique Trip", "C", "D", "2024-01-01", "2024-01-05", "", false, 0.0)
        val trip2 = Trip("T2", userId, "Unique Trip", "C2", "D2", "2024-02-01", "2024-02-05", "", false, 0.0)

        tripDao.insertTrip(trip1)
        
        // Since we have REPLACE strategy in DAO, it will overwrite T1 with T2 because of the unique (userId, title) constraint.
        // If we want to strictly "prevent", the count should be 1 and ID should be T2 (if replaced) or it should throw exception if strategy is ABORT.
        tripDao.insertTrip(trip2)

        val trips = tripDao.getTripsForUser(userId).first()
        assertEquals("Should only have 1 trip with that title for this user", 1, trips.size)
        assertEquals("T2", trips[0].id) // Verify it was replaced
    }
}
