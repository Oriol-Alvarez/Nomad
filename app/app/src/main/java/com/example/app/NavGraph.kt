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

@Composable
fun NavGraph(
    navController: NavHostController,
    tripViewModel: TripListViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
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

        composable(Routes.GALERIA_VIAJE) { GaleriaViajeScreen(navController) }
        composable(Routes.GALERIA_VIAJE_2) { GaleriaViajeScreen2(navController) }

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
    }
}
