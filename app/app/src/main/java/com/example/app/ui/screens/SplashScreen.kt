package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.Routes
import com.example.app.ui.theme.AppTheme
import kotlinx.coroutines.delay

/**
 * Pantalla de carga inicial (Splash).
 * Muestra el logo y una barrita de progreso antes de entrar a la app.
 */
@Composable
fun SplashScreen(navController: NavHostController) {

    // Controlamos cuánto lleva la barra de carga (de 0 a 100%)
    var progress by remember { mutableStateOf(0f) }

    // Para que el logo aparezca poco a poco con una animación
    var visible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Intentamos sacar la versión de la app para ponerla abajo
    val versionName = remember {
        try {
            context.packageManager
                .getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            "2.0" // Por si falla, ponemos una por defecto
        }
    }

    // Al arrancar, esperamos un pelín y mostramos el logo
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    // Simulamos que la app está cargando algo
    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(50L)
            progress += 0.01f
        }
    }

    // Cuando la barra llega al final, nos vamos a la pantalla de inicio
    if (progress >= 1f) {
        LaunchedEffect(Unit) {
            navController.navigate(Routes.HOME) {
                // Borramos esta pantalla del historial para que no se pueda volver atrás
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // El logo de Nomad
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_nomad),
                    contentDescription = "Logo Nomad",
                    modifier = Modifier.size(240.dp)
                )
            }

            Text(
                text = "NOMAD",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            // La barrita azul que se va rellenando
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // El texto que dice el porcentaje (ej: 50%)
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // La versión de la app abajo del todo
        Text(
            text = "v $versionName",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
