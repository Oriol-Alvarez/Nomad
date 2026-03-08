package com.example.app.ui.screens

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

    // Fondo con degradado sutil
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            // 🔷 HERO HEADER
            HeroHeader(navController)

            Spacer(modifier = Modifier.height(24.dp))

            // 👥 TEAM
            GlassCard(title = "Equipo de desarrollo") {
                DeveloperRowModern(R.drawable.oriol, "Oriol Alvarez Arisa", "Full Stack developer · UI/UX")
                Spacer(modifier = Modifier.height(12.dp))
                DeveloperRowModern(R.drawable.guillem, "Guillem Talayero Carrasco", "Full Stack developer · Data Model")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ℹ️ INFO
            GlassCard(title = "Información técnica") {
                InfoRowModern("Versión", "1.0.0")
                Spacer(modifier = Modifier.height(6.dp))

                InfoRowModern("Sprint", "01")
                Spacer(modifier = Modifier.height(6.dp))

                InfoRowModern("Build", "100")
                Spacer(modifier = Modifier.height(6.dp))

                InfoRowModern("Plataforma", "Android")
                Spacer(modifier = Modifier.height(6.dp))

                InfoRowModern("Min SDK", "24")
                Spacer(modifier = Modifier.height(6.dp))

                InfoRowModern("Estado", "Producción")
                Spacer(modifier = Modifier.height(6.dp))

                InfoRowModern("Android", Build.VERSION.RELEASE)
                Spacer(modifier = Modifier.height(6.dp))

                InfoRowModern("API Level", Build.VERSION.SDK_INT.toString())
                Spacer(modifier = Modifier.height(6.dp))

                InfoRowModern("Licencia", "Apache License 2.0")
                Spacer(modifier = Modifier.height(6.dp))
            }




        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
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

        // Flecha flotante arriba a la izquierda
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Logo + textos centrados
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {

            // Logo con glow
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
                        contentDescription = "Logo"
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

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "v1.0.0 · Sprint 01",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp
            )
        }
    }
}
@Composable
fun GlassCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 4.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            Text(
                text = title.uppercase(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
fun DeveloperRowModern(
    image: Int,
    name: String,
    role: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF2D7DFF), Color(0xFF00C6FF))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit // He quitado el ) extra que había aquí
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
    }
}

// ------------------- PREVIEWS -------------------

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SobreNosotrosPreviewDark() {
    AppTheme {
        SobreNosotrosScreen(
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
fun SobreNosotrosPreviewLight() {
    AppTheme {
        SobreNosotrosScreen(
            navController = rememberNavController()
        )
    }
}
