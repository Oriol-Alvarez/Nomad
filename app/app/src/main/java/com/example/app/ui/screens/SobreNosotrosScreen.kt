package com.example.app.ui.screens

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.ui.theme.AppTheme

@Composable
fun SobreNosotrosScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            HeroHeader(navController)

            Spacer(modifier = Modifier.height(24.dp))

            // Usamos GlassCard definido en Modules.kt
            GlassCard(title = stringResource(id = R.string.sobre_equipo_titulo)) {
                DeveloperRowModern(R.drawable.oriol, "Oriol Alvarez Arisa", stringResource(id = R.string.sobre_rol_oriol))
                Spacer(modifier = Modifier.height(12.dp))
                DeveloperRowModern(R.drawable.guillem, "Guillem Talayero Carrasco", stringResource(id = R.string.sobre_rol_guillem))
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassCard(title = stringResource(id = R.string.sobre_tecnica_titulo)) {
                InfoRowModern(stringResource(id = R.string.sobre_version), "4.0.0")
                InfoRowModern(stringResource(id = R.string.sobre_sprint), "04")
                InfoRowModern(stringResource(id = R.string.sobre_build), "100")
                InfoRowModern(stringResource(id = R.string.sobre_plataforma), "Android")
                InfoRowModern(stringResource(id = R.string.sobre_min_sdk), "24")
                InfoRowModern(stringResource(id = R.string.sobre_estado), stringResource(id = R.string.sobre_produccion))
                InfoRowModern("Android", Build.VERSION.RELEASE)
                InfoRowModern(stringResource(id = R.string.sobre_api_level), Build.VERSION.SDK_INT.toString())
                InfoRowModern(stringResource(id = R.string.sobre_licencia), "Apache License 2.0")
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavigationBar(navController)
        }
    }
}

@Composable
fun HeroHeader(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, start = 16.dp, end = 16.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.sobre_regresar),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .blur(60.dp)
                        .background(Color.White)
                )

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_nomad),
                        contentDescription = "Logo Nomad"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Nomad",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = "v4.0.0 · Sprint 04",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun DeveloperRowModern(image: Int, name: String, role: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(Color(0xFF2D7DFF), Color(0xFF00C6FF)))
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(name, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
            Text(role, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
    }
}

@Composable
fun InfoRowModern(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SobreNosotrosScreenPreviewDark() {
    AppTheme {
        SobreNosotrosScreen(navController = rememberNavController())
    }
}

@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SobreNosotrosScreenPreviewLight() {
    AppTheme {
        SobreNosotrosScreen(navController = rememberNavController())
    }
}
