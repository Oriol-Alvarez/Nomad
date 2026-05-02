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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {

    // Estado del progreso de carga (rango 0.0 a 1.0)
    var progress by remember { mutableStateOf(0f) }

    // Control de visibilidad para la animación de entrada del logotipo
    var visible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Obtención de la versión del paquete definida en el manifiesto
    val versionName = remember {
        try {
            context.packageManager
                .getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            "2.0"
        }
    }

    // Disparador de la animación de entrada tras el inflado inicial
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    // Lógica de simulación de carga asíncrona
    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(50L)
            progress += 0.01f
        }
    }

    // Gestión de la transición a la pantalla de login o home al completar el progreso
    if (progress >= 1f) {
        LaunchedEffect(Unit) {
            val auth = FirebaseAuth.getInstance()
            val destination = if (auth.currentUser != null && auth.currentUser?.isEmailVerified == true) {
                Routes.HOME
            } else {
                Routes.AUTH
            }
            
            navController.navigate(destination) {
                // Eliminación de la Splash de la pila de retroceso para evitar re-entradas
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }

    // Interfaz de usuario de la pantalla de bienvenida
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Contenedor central: Branding y progreso
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logotipo con transición de opacidad suave
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

            // Indicador de progreso lineal personalizado
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

            // Etiqueta de porcentaje de carga
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // Información de versión anclada en la parte inferior
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

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SplashScreenPreviewDark() {
    AppTheme {
        SplashScreen(navController = rememberNavController())
    }
}

@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SplashScreenPreviewLight() {
    AppTheme {
        SplashScreen(navController = rememberNavController())
    }
}
