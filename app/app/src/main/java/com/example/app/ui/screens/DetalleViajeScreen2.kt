package com.example.app.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.ui.theme.AppTheme

@Composable
fun DetalleViajeScreen2(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. CABECERA CON IMAGEN DE ROMA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                // Imagen de fondo
                Image(
                    painter = painterResource(id = R.drawable.roma),
                    contentDescription = "Fondo de Roma",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Degradado oscuro para que el texto se lea bien
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )

                // Botón de Volver
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 16.dp, start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Textos de la cabecera
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Text(
                        text = "La antigua Roma",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Abr 14 - Abr 21",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // 2. BARRA DE ESTADÍSTICAS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(value = "8", label = "NIGHTS", modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray.copy(alpha = 0.5f))
                StatItem(value = "€1,560", label = "BUDGET", modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray.copy(alpha = 0.5f))
                StatItem(value = "14", label = "ACTIVITIES", modifier = Modifier.weight(1f))
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), thickness = 1.dp)


            // 4. LISTA DEL ITINERARIO (TIMELINE)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // DÍA 1
                item { DayHeader("DAY 1 · ABR 14") }

                item {
                    TimelineEvent(
                        time = "08:00",
                        icon = Icons.Default.Flight,
                        iconBg = Color(0xFFE3F2FD),
                        iconTint = Color(0xFF1976D2),
                        title = "Vuelo BCN → FCO",
                        subtitle = "Vueling VY7182 · Terminal 1",
                        price = "€420",
                        priceColor = Color(0xFFE65100)
                    )
                }

                item {
                    TimelineEvent(
                        time = "12:30",
                        icon = Icons.Default.Hotel,
                        iconBg = Color(0xFFFFEBEE),
                        iconTint = Color(0xFFD32F2F),
                        title = "Check-in · Hotel Centro",
                        subtitle = "Centro histórico, Roma · 4★",
                        price = "€120/nit",
                        priceColor = Color(0xFFE65100)
                    )
                }

                // DÍA 2
                item { DayHeader("DAY 2 · ABR 15") }

                item {
                    TimelineEvent(
                        time = "09:00",
                        icon = Icons.Default.AccountBalance,
                        iconBg = Color(0xFFF3E5F5),
                        iconTint = Color(0xFF7B1FA2),
                        title = "Visita al Coliseo",
                        subtitle = "Foro Romano · 3h visita",
                        price = "Entrada pagada",
                        priceColor = Color(0xFF2E7D32)
                    )
                }

                item {
                    TimelineEvent(
                        time = "13:30",
                        icon = Icons.Default.Restaurant,
                        iconBg = Color(0xFFFFF8E1),
                        iconTint = Color(0xFFFBC02D),
                        title = "Comida en Trastevere",
                        subtitle = "Pasta auténtica · Reserva hecha",
                        price = "€35",
                        priceColor = Color(0xFFE65100)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        Text(text = label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
    }
}

@Composable
private fun DayHeader(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun TimelineEvent(
    time: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    price: String,
    priceColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Hora
        Text(
            text = time,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier
                .width(50.dp)
                .padding(top = 8.dp)
        )

        // Icono circular
        Surface(
            shape = CircleShape,
            color = iconBg,
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Textos del evento
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = price,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = priceColor
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DetalleViajeScreen2Preview() {
    AppTheme {
        DetalleViajeScreen2(navController = rememberNavController())
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DetalleViajeScreen2PreviewNight() {
    AppTheme {
        DetalleViajeScreen2(navController = rememberNavController())
    }
}
