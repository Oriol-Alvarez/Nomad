package com.example.app

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.app.ui.screens.*
import com.example.app.ui.viewmodels.TripListViewModel
import com.example.app.ui.viewmodels.AuthViewModel
import com.example.app.ui.viewmodels.HotelViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    tripViewModel: TripListViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    hotelViewModel: HotelViewModel = hiltViewModel(),
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    recordatorioViajes: Boolean,
    onRecordatorioChange: (Boolean) -> Unit,
    resumenSemanal: Boolean,
    onResumenChange: (Boolean) -> Unit,
    selectedCurrency: String,
    onCurrencyChange: (String) -> Unit,
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    birthdate: String,
    onBirthdateChange: (String) -> Unit,
    fontSizeScale: Float,
    onFontSizeScaleChange: (Float) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) { SplashScreen(navController, authViewModel) }

        composable(Routes.AUTH) { 
            AutentificacionScreen(
                navController = navController,
                vm = authViewModel,
                onUserDataSaved = { newName, newBirth, newResumen ->
                    onUsernameChange(newName)
                    onBirthdateChange(newBirth)
                    onResumenChange(newResumen)
                }
            ) 
        }

        composable(Routes.HOME) { 
            HomeScreen(
                navController = navController, 
                username = username,
                viewModel = tripViewModel
            ) 
        }

        composable(Routes.DETALLE_VIAJE) {
            DetalleViajeScreen(
                navController = navController,
                selectedCurrency = selectedCurrency,
                viewModel = tripViewModel
            )
        }

        composable(Routes.RESERVAS_HOTELES) {
            ReservasHotelesScreen(
                navController = navController,
                selectedCurrency = selectedCurrency,
                viewModel = tripViewModel
            )
        }

        composable(
            route = "${Routes.DETALLE_VIAJE2}/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            DetalleViajeScreen2(
                navController = navController,
                selectedCurrency = selectedCurrency,
                tripId = tripId,
                viewModel = tripViewModel
            )
        }

        composable(Routes.GALERIA_VIAJE) {
            GaleriaViajeScreen(
                navController = navController,
                viewModel = tripViewModel
            )
        }
        composable(
            route = "${Routes.GALERIA_VIAJE_2}/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            GaleriaViajeScreen2(
                navController = navController,
                tripId = tripId,
                viewModel = tripViewModel
            )
        }

        composable(Routes.PREFERENCIAS) {
            PreferenciasScreen(
                navController = navController,
                authViewModel = authViewModel,
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange,
                recordatorioViajes = recordatorioViajes,
                onRecordatorioChange = onRecordatorioChange,
                resumenSemanal = resumenSemanal,
                onResumenChange = onResumenChange,
                selectedCurrency = selectedCurrency,
                onCurrencyChange = onCurrencyChange,
                selectedLanguage = selectedLanguage,
                onLanguageChange = onLanguageChange,
                username = username,
                onUsernameChange = onUsernameChange,
                birthdate = birthdate,
                onBirthdateChange = onBirthdateChange,
                fontSizeScale = fontSizeScale,
                onFontSizeScaleChange = onFontSizeScaleChange
            )
        }

        composable(Routes.SOBRE_NOSOTROS) { SobreNosotrosScreen(navController) }
        composable(Routes.TERMINOS_CONDICIONES) { TerminosCondicionesScreen(navController) }

        composable(
            route = Routes.FORMVIAJE,
            arguments = listOf(navArgument("ciudad") { defaultValue = "" })
        ) { backStackEntry ->
            val ciudadReal = backStackEntry.arguments?.getString("ciudad") ?: ""
            FormularioViaje(
                navController = navController, 
                ciudadDestino = ciudadReal,
                viewModel = tripViewModel
            )
        }

        composable(Routes.HOTEL_SEARCH) {
            HotelSearchScreen(
                navController = navController,
                viewModel = hotelViewModel
            )
        }

        composable(
            route = "${Routes.HOTEL_DETAIL}/{hotelId}/{startDate}/{endDate}/{city}",
            arguments = listOf(
                navArgument("hotelId") { type = NavType.StringType },
                navArgument("startDate") { type = NavType.StringType },
                navArgument("endDate") { type = NavType.StringType },
                navArgument("city") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
            val startDate = backStackEntry.arguments?.getString("startDate") ?: ""
            val endDate = backStackEntry.arguments?.getString("endDate") ?: ""
            val city = backStackEntry.arguments?.getString("city") ?: ""
            HotelDetailScreen(
                navController = navController,
                hotelId = hotelId,
                startDate = startDate,
                endDate = endDate,
                city = city,
                viewModel = hotelViewModel
            )
        }

        composable(Routes.RESERVATIONS_LIST) {
            ReservationsListScreen(
                navController = navController,
                selectedCurrency = selectedCurrency,
                viewModel = hotelViewModel
            )
        }
    }
}
