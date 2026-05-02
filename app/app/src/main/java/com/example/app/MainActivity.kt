package com.example.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.navigation.compose.rememberNavController
import com.example.app.data.local.AppDatabase
import com.example.app.data.repository.AuthRepositoryImpl
import com.example.app.data.repository.ItineraryItemRepositoryImpl
import com.example.app.data.repository.TripRepositoryImpl
import com.example.app.data.repository.UserRepositoryImpl
import com.example.app.data.repository.AccessLogRepositoryImpl
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.viewmodels.TripListViewModel
import com.example.app.ui.viewmodels.AuthViewModel
import com.example.app.ui.viewmodels.AppViewModelFactory
import java.util.Locale

class MainActivity : ComponentActivity() {

    // Inicialización de la fábrica única para todos los ViewModels
    private val appViewModelFactory: AppViewModelFactory by lazy {
        val database = AppDatabase.getDatabase(applicationContext)
        AppViewModelFactory(
            tripRepository = TripRepositoryImpl(database.tripDao()),
            itineraryRepository = ItineraryItemRepositoryImpl(database.itineraryDao()),
            authRepository = AuthRepositoryImpl(),
            userRepository = UserRepositoryImpl(database.userDao()),
            accessLogRepository = AccessLogRepositoryImpl(database.accessLogDao())
        )
    }

    private val tripViewModel: TripListViewModel by viewModels { appViewModelFactory }
    private val authViewModel: AuthViewModel by viewModels { appViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
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
            
            var fontSizeScale by rememberSaveable { mutableFloatStateOf(prefs.getFloat("font_size_scale", 1.0f)) }

            AppTheme(useDarkTheme = darkTheme, fontScale = fontSizeScale) {
                val navController = rememberNavController()

                NavGraph(
                    navController = navController,
                    tripViewModel = tripViewModel,
                    authViewModel = authViewModel,
                    isDarkMode = darkTheme,
                    onDarkModeChange = { nuevo ->
                        prefs.edit { putBoolean("dark_mode", nuevo) }
                        darkTheme = nuevo
                    },
                    recordatorioViajes = recordatorioViajes,
                    onRecordatorioChange = { nuevo ->
                        prefs.edit { putBoolean("pref_recordatorio", nuevo) }
                        recordatorioViajes = nuevo
                    },
                    resumenSemanal = resumenSemanal,
                    onResumenChange = { nuevo ->
                        prefs.edit { putBoolean("pref_resumen", nuevo) }
                        resumenSemanal = nuevo
                    },
                    selectedCurrency = selectedCurrency,
                    onCurrencyChange = { nuevaMoneda ->
                        prefs.edit { putString("user_currency", nuevaMoneda) }
                        selectedCurrency = nuevaMoneda
                    },
                    selectedLanguage = selectedLanguage,
                    onLanguageChange = { nuevo ->
                        prefs.edit { putString("user_lang", nuevo) }
                        selectedLanguage = nuevo
                        recreateWithAnimation()
                    },
                    username = username,
                    onUsernameChange = { nuevo ->
                        prefs.edit { putString("username", nuevo) }
                        username = nuevo
                    },
                    birthdate = birthdate,
                    onBirthdateChange = { nueva ->
                        prefs.edit { putString("birthdate", nueva) }
                        birthdate = nueva
                    },
                    fontSizeScale = fontSizeScale,
                    onFontSizeScaleChange = { nuevaEscala ->
                        prefs.edit { putFloat("font_size_scale", nuevaEscala) }
                        fontSizeScale = nuevaEscala
                    }
                )
            }
        }
    }

    private fun updateResourceLocale(language: String) {
        val locale = when (language) {
            "En English" -> Locale.ENGLISH
            "Ca Català" -> Locale("ca")
            else -> Locale("es")
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun recreateWithAnimation() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
        finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent)
    }
}
