package com.example.app.ui.screens

import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.draw.blur

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.app.Routes

@Composable
fun PreferenciasScreen(navController: NavHostController) {
    // Scaffold es el "esqueleto" oficial para pantallas con barras de navegación
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                // El innerPadding evita que la BottomBar tape el contenido
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Cabecera (Hero) - Ahora fluye con la columna, no se solapa
            HeroPreferencias()

            // 2. Contenido de las tarjetas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // SECCIÓN: IDIOMA
                GlassCard(title = "Idioma y región") {
                    CajasPreferencias(
                        image = R.drawable.earth_americas_solid_full,
                        name = "Idioma",
                        role = "Idioma de la interfaz",
                        value = "Es Español",
                        type = "select"
                    )
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    CajasPreferencias(
                        image = R.drawable.coins_solid_full,
                        name = "Moneda",
                        role = "Escoger moneda",
                        value = "EUR(€)",
                        type = "select"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SECCIÓN: APARIENCIA
                GlassCard(title = "Apariencia") {
                    CajasPreferencias(
                        image = R.drawable.circle_half_stroke_solid_full,
                        name = "Modo oscuro",
                        role = "Escoger un tema",
                        value = "off",
                        type = "slider"
                    )
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    CajasPreferencias(
                        image = R.drawable.text_width_solid_full,
                        name = "Tamaño del texto",
                        role = "Accesibilidad",
                        value = "Normal",
                        type = "select"
                    )
                }

                GlassCard(title = "Notificaciones") {
                    CajasPreferencias(
                        image = R.drawable.bell_solid_full,
                        name = "Recordatorio de viajes",
                        role = "Aviso 24h antes del vuelo",
                        value = "on",
                        type = "slider"
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CajasPreferencias(
                        image = R.drawable.envelope_solid_full,
                        name = "Resumen Semanal",
                        role = "Envio de email",
                        value = "on",
                        type = "slider"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SECCIÓN: INFO
                GlassCard(title = "Más información") {
                    CajasPreferencias(
                        image = R.drawable.circle_info_solid_full,
                        name = "Info de la app",
                        role = "Un poco sobre nosotros",
                        value = "on",
                        type = "nav",
                        navController = navController,
                        onClick = { navController.navigate(Routes.SOBRE_NOSOTROS) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Programado con mucho ☕",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center

                )

                // Espacio extra al final para que no quede pegado al borde
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HeroPreferencias() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 30.dp)
        ) {
            Text(
                text = "Preferencias",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Personaliza la aplicación a tu gusto.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )

        }
    }
}



@Composable
fun CajasPreferencias(
    image: Int,
    name: String,
    role: String,
    value: String,
    type: String,
    navController: NavHostController = rememberNavController(),
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(top = 2.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        // Icono izquierda
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    MaterialTheme.colorScheme.background
                )
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = image),
                contentDescription = "Logo",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Textos
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = role,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }

        if (type == "slider") {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
                    .widthIn(min = 60.dp)
            ) {

                    Icon(
                        painter =
                        if(value == "off"){
                            painterResource(id = R.drawable.toggle_off_solid_full)
                        }else{
                            painterResource(id = R.drawable.toggle_on_solid_full)
                        }
                        ,
                        contentDescription = "toggle off",
                        tint= MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))


            }
        }else if (type=="select"){
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(12.dp)
                    .widthIn(min = 60.dp)
            ) {

                    Text(
                        text = value,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Abrir",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )


            }
        }else{
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
                    .widthIn(min = 60.dp)
                    .clickable {                      // 🔹 aquí manejas el click
                        navController.navigate(Routes.SOBRE_NOSOTROS)  // ruta de tu AboutPage
                    }
            ) {

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Abrir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        // Parte derecha

    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreferenciasPreviewDark() {
    AppTheme {
        PreferenciasScreen(
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
fun PreferenciasPreviewLight() {
    AppTheme {
        PreferenciasScreen(
            navController = rememberNavController()
        )
    }
}
