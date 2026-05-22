package com.example.app

import com.example.app.domain.*
import com.example.app.ui.viewmodels.TripListViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.*

/**
 * T3.2: Unit tests for trip and itinerary CRUD operations and all validation cases.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TripCRUDTest {

    private lateinit var viewModel: TripListViewModel

    private lateinit var tripRepository: TripRepository
    private lateinit var itineraryRepository: ItineraryItemRepository
    private lateinit var authRepository: AuthRepository

    private val testDispatcher = StandardTestDispatcher()

    // In-memory data stores to emulate Room behavior
    private val tripsList = mutableListOf<Trip>()
    private val itineraryList = mutableListOf<ItineraryItem>()

    private val tripsFlow = MutableStateFlow<List<Trip>>(emptyList())
    private val itineraryFlowMap = mutableMapOf<String, MutableStateFlow<List<ItineraryItem>>>()

    @Before
    fun setup() = runBlocking {
        Dispatchers.setMain(testDispatcher)

        tripRepository = mock()
        itineraryRepository = mock()
        authRepository = mock()

        tripsList.clear()
        itineraryList.clear()
        tripsFlow.value = emptyList()
        itineraryFlowMap.clear()

        // Emulate TripRepository
        whenever(tripRepository.getTripsForUser(any())).thenReturn(tripsFlow)
        
        whenever(tripRepository.getTripById(any())).thenAnswer { invocation ->
            val id = invocation.getArgument<String>(0)
            tripsList.find { it.id == id }
        }

        whenever(tripRepository.insertTrip(any())).thenAnswer { invocation ->
            val trip = invocation.getArgument<Trip>(0)
            tripsList.removeAll { it.id == trip.id }
            tripsList.add(trip)
            tripsFlow.value = tripsList.toList()
            null
        }

        whenever(tripRepository.deleteTrip(any())).thenAnswer { invocation ->
            val id = invocation.getArgument<String>(0)
            tripsList.removeAll { it.id == id }
            tripsFlow.value = tripsList.toList()
            null
        }

        whenever(tripRepository.updateTrip(any())).thenAnswer { invocation ->
            val trip = invocation.getArgument<Trip>(0)
            tripsList.removeAll { it.id == trip.id }
            tripsList.add(trip)
            tripsFlow.value = tripsList.toList()
            null
        }

        // Emulate ItineraryItemRepository
        whenever(itineraryRepository.getItineraryItemsForTrip(any())).thenAnswer { invocation ->
            val tripId = invocation.getArgument<String>(0)
            itineraryFlowMap.getOrPut(tripId) { MutableStateFlow(emptyList()) }
        }

        whenever(itineraryRepository.insertItineraryItem(any())).thenAnswer { invocation ->
            val item = invocation.getArgument<ItineraryItem>(0)
            itineraryList.removeAll { it.id == item.id }
            itineraryList.add(item)
            val flow = itineraryFlowMap.getOrPut(item.tripId) { MutableStateFlow(emptyList()) }
            flow.value = itineraryList.filter { it.tripId == item.tripId }
            null
        }

        whenever(itineraryRepository.updateItineraryItem(any())).thenAnswer { invocation ->
            val item = invocation.getArgument<ItineraryItem>(0)
            itineraryList.removeAll { it.id == item.id }
            itineraryList.add(item)
            val flow = itineraryFlowMap.getOrPut(item.tripId) { MutableStateFlow(emptyList()) }
            flow.value = itineraryList.filter { it.tripId == item.tripId }
            null
        }

        whenever(itineraryRepository.deleteItineraryItem(any())).thenAnswer { invocation ->
            val item = invocation.getArgument<ItineraryItem>(0)
            itineraryList.removeAll { it.id == item.id }
            val flow = itineraryFlowMap.getOrPut(item.tripId) { MutableStateFlow(emptyList()) }
            flow.value = itineraryList.filter { it.tripId == item.tripId }
            null
        }

        whenever(itineraryRepository.deleteItineraryItemsByTripId(any())).thenAnswer { invocation ->
            val tripId = invocation.getArgument<String>(0)
            itineraryList.removeAll { it.tripId == tripId }
            val flow = itineraryFlowMap.getOrPut(tripId) { MutableStateFlow(emptyList()) }
            flow.value = emptyList()
            null
        }

        // Mock AuthRepository and FirebaseUser
        val mockFirebaseUser: FirebaseUser = mock()
        whenever(mockFirebaseUser.uid).thenReturn("default_user")
        
        whenever(authRepository.getCurrentUser()).thenReturn(mockFirebaseUser)
        val authFlow = MutableStateFlow<FirebaseUser?>(mockFirebaseUser)
        whenever(authRepository.getAuthStateFlow()).thenReturn(authFlow)

        viewModel = TripListViewModel(tripRepository, itineraryRepository, authRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test Trip creation all validation cases`() {
        println("\n--- INICIANDO TEST: CASUÍSTICAS DE VALIDACIÓN DE VIAJE ---")

        // 1. Caso Correcto (Éxito esperado)
        val uniqueTitle = "Trip ${UUID.randomUUID().toString().take(8)}" // Título corto y único
        val resOk = viewModel.saveTrip(
            title = uniqueTitle, destination = "Barcelona, Spain",
            dataInici = "01/10/2025", dataFinal = "10/10/2025",
            desc = "Test Description", budget = 100.0, imageUri = "", activitiesFromForm = emptyList()
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        if (!resOk) {
            println("  [ERROR] El caso con DATOS VÁLIDOS falló inesperadamente. Comprueba logs de validación.")
        } else {
            println("  [OK] Registro de viaje con datos válidos correcto.")
        }
        assertTrue("El viaje debería guardarse con datos válidos", resOk)

        // 2. Caso: Fecha de Vuelta ANTERIOR a la de Ida (Fallo esperado)
        val resDates = viewModel.saveTrip(
            title = "Viaje Imposible", destination = "Madrid, Spain",
            dataInici = "10/10/2025", dataFinal = "05/10/2025",
            desc = "", budget = 0.0, imageUri = "", activitiesFromForm = emptyList()
        )
        testDispatcher.scheduler.advanceUntilIdle()

        if (resDates) {
            println("  [ERROR] VALIDACIÓN DE FECHAS FALLIDA: Se permitió un viaje con vuelta (05/10) anterior a la ida (10/10).")
        } else {
            println("  [OK] Validación de fechas detectó correctamente el error (Vuelta < Ida).")
        }
        assertFalse("Debería fallar si la vuelta es anterior a la ida", resDates)

        // 3. Caso: Título demasiado corto (Fallo esperado)
        val resTitle = viewModel.saveTrip(
            title = "Ab", destination = "Paris, France",
            dataInici = "01/12/2025", dataFinal = "05/12/2025",
            desc = "", budget = 0.0, imageUri = "", activitiesFromForm = emptyList()
        )
        testDispatcher.scheduler.advanceUntilIdle()

        if (resTitle) {
            println("  [ERROR] VALIDACIÓN DE TÍTULO FALLIDA: Se permitió un título de solo 2 caracteres ('Ab').")
        } else {
            println("  [OK] Validación de título corto detectó correctamente el error.")
        }
        assertFalse("Debería fallar si el título tiene menos de 3 caracteres", resTitle)

        // 4. Caso: Destino sin formato 'Ciudad, Pais' (Fallo esperado)
        val resLoc = viewModel.saveTrip(
            title = "Destino Malo", destination = "Londres",
            dataInici = "01/12/2025", dataFinal = "05/12/2025",
            desc = "", budget = 0.0, imageUri = "", activitiesFromForm = emptyList()
        )
        testDispatcher.scheduler.advanceUntilIdle()

        if (resLoc) {
            println("  [ERROR] VALIDACIÓN DE UBICACIÓN FALLIDA: Se permitió un destino sin formato 'Ciudad, País' ('Londres').")
        } else {
            println("  [OK] Validación de ubicación sin formato detectó correctamente el error.")
        }
        assertFalse("Debería fallar si el destino no tiene el formato 'Ciudad, País'", resLoc)
    }

    @Test
    fun `test Activity date validation all cases`() {
        println("\n--- INICIANDO TEST: CASUÍSTICAS DE VALIDACIÓN DE ACTIVIDADES ---")
        
        // Creamos un viaje base para las pruebas (Título corto para evitar error de >50 caracteres)
        val tripTitle = "ActTest ${UUID.randomUUID().toString().take(8)}"
        val saveSuccess = viewModel.saveTrip(
            title = tripTitle, destination = "Roma, Italy",
            dataInici = "01/06/2025", dataFinal = "05/06/2025",
            desc = "", budget = 0.0, imageUri = "", activitiesFromForm = emptyList()
        )
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue("El viaje base para el test de actividades debería haberse guardado", saveSuccess)
        
        val trip = viewModel.trips.value.find { it.title == tripTitle }
        assertNotNull("El viaje creado debería encontrarse en la lista", trip)

        // 1. Caso: Actividad ANTES del inicio del viaje (Fallo esperado)
        val actBefore = ItineraryItem(UUID.randomUUID().toString(), trip!!.id, "Pre-vuelo", "31/05/2025", "10:00", 0, "Vuelo", "")
        viewModel.addActivityToTrip(trip.id, actBefore)
        testDispatcher.scheduler.advanceUntilIdle()

        val hasBefore = itineraryList.any { it.nombre == "Pre-vuelo" }
        assertFalse("No debería permitir actividades antes del inicio del viaje", hasBefore)

        // 2. Caso: Actividad DESPUÉS del fin del viaje (Fallo esperado)
        val actAfter = ItineraryItem(UUID.randomUUID().toString(), trip.id, "Post-cena", "06/06/2025", "20:00", 0, "Restaurante", "")
        viewModel.addActivityToTrip(trip.id, actAfter)
        testDispatcher.scheduler.advanceUntilIdle()

        val hasAfter = itineraryList.any { it.nombre == "Post-cena" }
        assertFalse("No debería permitir actividades después del fin del viaje", hasAfter)

        // 3. Caso: Actividad en fecha VÁLIDA (Éxito esperado)
        val actOk = ItineraryItem(UUID.randomUUID().toString(), trip.id, "Visita", "02/06/2025", "11:00", 10, "Museo", "")
        viewModel.addActivityToTrip(trip.id, actOk)
        testDispatcher.scheduler.advanceUntilIdle()

        val hasOk = itineraryList.any { it.nombre == "Visita" }
        assertTrue("Debería permitir actividades dentro del rango", hasOk)

        // 4. Caso: Borrado de viaje (Limpieza)
        viewModel.deleteTrip(trip.id)
        testDispatcher.scheduler.advanceUntilIdle()

        val tripExists = viewModel.trips.value.any { it.id == trip.id }
        if (tripExists) {
            println("  [ERROR] El viaje de prueba NO se eliminó correctamente tras el test.")
        } else {
            println("  [OK] Limpieza de datos (borrado) realizada correctamente.")
        }
        assertNull("El viaje debería haberse eliminado", viewModel.trips.value.find { it.id == trip.id })
    }
}
