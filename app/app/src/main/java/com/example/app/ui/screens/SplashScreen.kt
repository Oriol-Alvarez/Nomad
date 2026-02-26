package com.example.app.ui.screens

import android.content.res.Configuration
import android.media.browse.MediaBrowser
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import kotlinx.coroutines.delay
import com.example.app.Routes
import com.example.app.ui.theme.AppTheme


@Composable
fun SplashScreen(navController: NavHostController) {

    var progress by remember { mutableStateOf(0f) } // 0f = 0%, 1f = 100%
    val context = LocalContext.current

    val versionName = remember {
        try {
            context.packageManager
                .getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            "1.0"
        }
    }

    // Simulación de carga
    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(50L) // cada 50ms aumenta el progreso
            progress += 0.01f // incrementa 1%
        }
    }

    // Navegación cuando termine
    if (progress >= 1f) {
        LaunchedEffect(Unit) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // CONTENIDO CENTRAL
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo_nomad),
                contentDescription = "Logo",
                modifier = Modifier.size(240.dp)
            )


            Text(modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "NOMAD",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary)
            Spacer(modifier = Modifier.height(32.dp))
            // BARRA HORIZONTAL CON PORCENTAJE
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)), // bordes redondeados
                color = MaterialTheme.colorScheme.primary, // color de la barra
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) // color del fondo de la barra
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TEXTO CON PORCENTAJE
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // VERSIÓN ABAJO
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

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SplashScreenPreviewDark() {
    AppTheme {
        SplashScreen(
            navController = rememberNavController()
        )
    }
}


@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun SplashScreenPreviewLight() {
    AppTheme {
        SplashScreen(
            navController = rememberNavController()
        )
    }
}

