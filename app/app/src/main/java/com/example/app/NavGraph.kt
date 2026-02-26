package com.example.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.app.*
import com.example.app.ui.screens.HomeScreen
import com.example.app.ui.screens.DetalleViajeScreen
import com.example.app.ui.screens.GaleriaViajeScreen
import com.example.app.ui.screens.PreferenciasScreen
import com.example.app.ui.screens.SobreNosotrosScreen
import com.example.app.ui.screens.TerminosCondicionesScreen


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.DETALLE_VIAJE) { DetalleViajeScreen(navController) }
        composable(Routes.GALERIA_VIAJE) { GaleriaViajeScreen(navController) }
        composable(Routes.PREFERENCIAS) { PreferenciasScreen(navController) }
        composable(Routes.SOBRE_NOSOTROS) { SobreNosotrosScreen(navController) }
        composable(Routes.TERMINOS_CONDICIONES) { TerminosCondicionesScreen(navController) }
    }
}