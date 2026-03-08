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
    isDarkMode: Boolean,           // 1. Declaramos el parámetro aquí
    onDarkModeChange: (Boolean) -> Unit // 2. Necesitamos la función para cambiarlo
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH // 3. Quitamos la declaración errónea de aquí
    ) {
        composable(Routes.SPLASH) { SplashScreen(navController) }
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.DETALLE_VIAJE) { DetalleViajeScreen(navController) }
        composable(Routes.DETALLE_VIAJE2) { DetalleViajeScreen2(navController) }
        composable(Routes.GALERIA_VIAJE) { GaleriaViajeScreen(navController) }
        composable(Routes.GALERIA_VIAJE_2) { GaleriaViajeScreen2(navController) }

        composable(Routes.PREFERENCIAS) {
            PreferenciasScreen(
                navController = navController,
                isDarkMode = isDarkMode,      // Pasamos el valor actual
                onDarkModeChange = onDarkModeChange // Pasamos la acción de cambio
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
