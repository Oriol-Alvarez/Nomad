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
        // 1. LEER IDIOMA ANTES DE SUPER.ONCREATE
        // Esto asegura que la app cargue los strings correctos desde el segundo 1
        val prefs = getSharedPreferences("config_nomad", MODE_PRIVATE)
        val savedLang = prefs.getString("user_lang", "Es Español") ?: "Es Español"
        updateResourceLocale(savedLang)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // --- ESTADOS PERSISTENTES ---
            var darkTheme by rememberSaveable { mutableStateOf(prefs.getBoolean("dark_mode", false)) }
            var recordatorioViajes by rememberSaveable { mutableStateOf(prefs.getBoolean("pref_recordatorio", true)) }
            var resumenSemanal by rememberSaveable { mutableStateOf(prefs.getBoolean("pref_resumen", true)) }
            var selectedCurrency by rememberSaveable { mutableStateOf(prefs.getString("user_currency", "EUR(€)") ?: "EUR(€)") }
            var selectedLanguage by rememberSaveable { mutableStateOf(savedLang) }

            AppTheme(useDarkTheme = darkTheme) {
                val navController = rememberNavController()

                NavGraph(
                    navController = navController,
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
                        // Guardamos el nuevo idioma
                        prefs.edit().putString("user_lang", nuevo).apply()
                        selectedLanguage = nuevo

                        // REINICIO CON ANIMACIÓN
                        recreateWithAnimation()
                    }
                )
            }
        }
    }

    // Método para forzar la Locale en el contexto de la actividad
    private fun updateResourceLocale(language: String) {
        val locale = when (language) {
            "En English" -> java.util.Locale.ENGLISH
            "Ca Català" -> java.util.Locale("ca")
            else -> java.util.Locale("es")
        }
        java.util.Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        // Actualizamos los recursos base para que stringResource() funcione
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    // Método para reiniciar la actividad con un fundido suave
    private fun recreateWithAnimation() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent)
    }
}
