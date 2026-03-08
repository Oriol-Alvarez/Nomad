package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.PermissionChecker
import androidx.navigation.compose.rememberNavController
import com.example.app.ui.theme.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // 1. Estado que sobrevive a la rotación
            var darkThemeState by rememberSaveable { mutableStateOf(false) }

            // 2. Pasamos el estado al Theme para cambiar los colores de TODA la app
            AppTheme(useDarkTheme = darkThemeState) {
                val navController = rememberNavController()

                // 3. Pasamos el estado y la función al NavGraph
                NavGraph(
                    navController = navController,
                    isDarkMode = darkThemeState,
                    onDarkModeChange = { darkThemeState = it }
                )
            }
        }
    }
}
