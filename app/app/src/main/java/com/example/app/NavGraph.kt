package com.example.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
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


@Composable
fun NavGraph(
    navController: NavHostController,
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    recordatorioViajes: Boolean,
    onRecordatorioChange: (Boolean) -> Unit,
    resumenSemanal: Boolean,
    onResumenChange: (Boolean) -> Unit,
    selectedCurrency: String,          // Recibido de MainActivity
    onCurrencyChange: (String) -> Unit, // Recibido de MainActivity
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) { SplashScreen(navController) }

        composable(Routes.HOME) { HomeScreen(navController) }

        composable(Routes.DETALLE_VIAJE) {
            DetalleViajeScreen(
                navController = navController,
                selectedCurrency = selectedCurrency
            )
        }

        composable(Routes.DETALLE_VIAJE2) {
            DetalleViajeScreen2(
                navController = navController,
                selectedCurrency = selectedCurrency
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
                // --- NO OLVIDES ESTO ---
                selectedCurrency = selectedCurrency,
                onCurrencyChange = onCurrencyChange,
                selectedLanguage = selectedLanguage,
                onLanguageChange = onLanguageChange
            )
        }

        composable(Routes.SOBRE_NOSOTROS) { SobreNosotrosScreen(navController) }

        composable(Routes.TERMINOS_CONDICIONES) { TerminosCondicionesScreen(navController) }

        composable(
            route = Routes.FORMVIAJE,
            arguments = listOf(navArgument("ciudad") { defaultValue = "" })
        ) { backStackEntry ->
            val ciudadReal = backStackEntry.arguments?.getString("ciudad") ?: ""
            FormularioViaje(navController = navController, ciudadDestino = ciudadReal)
        }
    }
}
