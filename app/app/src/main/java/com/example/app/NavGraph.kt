package com.example.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.app.ui.screens.DetalleViajeScreen
import com.example.app.ui.screens.HomeScreen
import com.example.app.ui.screens.DetalleViajeScreen2
import com.example.app.ui.screens.GaleriaViajeScreen
import com.example.app.ui.screens.GaleriaViajeScreen2
import com.example.app.ui.screens.PreferenciasScreen
import com.example.app.ui.screens.SobreNosotrosScreen
import com.example.app.ui.screens.SplashScreen
import com.example.app.ui.screens.TerminosCondicionesScreen
import com.example.app.ui.screens.FormularioViaje
import com.example.app.ui.viewmodels.TripListViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    tripViewModel: TripListViewModel,
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
    // Nuevos parámetros Sprint-02
    username: String,
    onUsernameChange: (String) -> Unit,
    birthdate: String,
    onBirthdateChange: (String) -> Unit,
    // Tamaño de letra
    fontSizeScale: Float,
    onFontSizeScaleChange: (Float) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) { SplashScreen(navController) }

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

        // Ruta actualizada para recibir el tripId como String
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
                // Pasar nuevos campos
                username = username,
                onUsernameChange = onUsernameChange,
                birthdate = birthdate,
                onBirthdateChange = onBirthdateChange,
                // Tamaño de letra
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
