package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.app.data.local.AppDatabase
import com.example.app.data.repository.ItineraryItemRepositoryImpl
import com.example.app.data.repository.TripRepositoryImpl
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.viewmodels.TripListViewModel
import com.example.app.ui.viewmodels.TripViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Inicializar Base de Datos y Repositorios
        val database = AppDatabase.getDatabase(this)
        val tripRepo = TripRepositoryImpl(database.tripDao())
        val itineraryRepo = ItineraryItemRepositoryImpl(database.itineraryDao())
        val factory = TripViewModelFactory(tripRepo, itineraryRepo)
        
        // Obtener el ViewModel una sola vez para toda la app
        val tripViewModel: TripListViewModel by viewModels { factory }

        val prefs = getSharedPreferences("config_nomad", MODE_PRIVATE)
        val savedLang = prefs.getString("user_lang", "Es Español") ?: "Es Español"
        updateResourceLocale(savedLang)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var darkTheme by rememberSaveable { mutableStateOf(prefs.getBoolean("dark_mode", false)) }
            var recordatorioViajes by rememberSaveable { mutableStateOf(prefs.getBoolean("pref_recordatorio", true)) }
            var resumenSemanal by rememberSaveable { mutableStateOf(prefs.getBoolean("pref_resumen", true)) }
            var selectedCurrency by rememberSaveable { mutableStateOf(prefs.getString("user_currency", "EUR(€)") ?: "EUR(€)") }
            var selectedLanguage by rememberSaveable { mutableStateOf(savedLang) }
            
            var username by rememberSaveable { mutableStateOf(prefs.getString("username", "Viajero") ?: "Viajero") }
            var birthdate by rememberSaveable { mutableStateOf(prefs.getString("birthdate", "01/01/2000") ?: "01/01/2000") }
            
            var fontSizeScale by rememberSaveable { mutableStateOf(prefs.getFloat("font_size_scale", 1.0f)) }

            AppTheme(useDarkTheme = darkTheme, fontScale = fontSizeScale) {
                val navController = rememberNavController()

                NavGraph(
                    navController = navController,
                    tripViewModel = tripViewModel, // Pasamos el ViewModel persistente
                    isDarkMode = darkTheme,
                    onDarkModeChange = { nuevo ->
                        prefs.edit().putBoolean("dark_mode", nuevo).apply()
                        darkTheme = nuevo
                    },
                    recordatorioViajes = recordatorioViajes,
                    onRecordatorioChange = { nuevo ->
                        prefs.edit().putBoolean("pref_recordatorio", nuevo).apply()
                        recordatorioViajes = nuevo
                    },
                    resumenSemanal = resumenSemanal,
                    onResumenChange = { nuevo ->
                        prefs.edit().putBoolean("pref_resumen", nuevo).apply()
                        resumenSemanal = nuevo
                    },
                    selectedCurrency = selectedCurrency,
                    onCurrencyChange = { nuevaMoneda ->
                        prefs.edit().putString("user_currency", nuevaMoneda).apply()
                        selectedCurrency = nuevaMoneda
                    },
                    selectedLanguage = selectedLanguage,
                    onLanguageChange = { nuevo ->
                        prefs.edit().putString("user_lang", nuevo).apply()
                        selectedLanguage = nuevo
                        recreateWithAnimation()
                    },
                    username = username,
                    onUsernameChange = { nuevo ->
                        prefs.edit().putString("username", nuevo).apply()
                        username = nuevo
                    },
                    birthdate = birthdate,
                    onBirthdateChange = { nueva ->
                        prefs.edit().putString("birthdate", nueva).apply()
                        birthdate = nueva
                    },
                    fontSizeScale = fontSizeScale,
                    onFontSizeScaleChange = { nuevaEscala ->
                        prefs.edit().putFloat("font_size_scale", nuevaEscala).apply()
                        fontSizeScale = nuevaEscala
                    }
                )
            }
        }
    }

    private fun updateResourceLocale(language: String) {
        val locale = when (language) {
            "En English" -> java.util.Locale.ENGLISH
            "Ca Català" -> java.util.Locale("ca")
            else -> java.util.Locale("es")
        }
        java.util.Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun recreateWithAnimation() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent)
    }
}
