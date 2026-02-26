package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.app.ui.theme.AppTheme
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // contenido full screen

        var isChecking = true
        lifecycleScope.launch {
            delay(3000L) // splash de 3 segundos
            isChecking = false
        }

        installSplashScreen().apply {
            setKeepOnScreenCondition { isChecking }
        }

        setContent {
            AppTheme {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }
}
