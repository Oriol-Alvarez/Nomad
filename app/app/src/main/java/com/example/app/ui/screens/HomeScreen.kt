package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.Routes
import com.example.app.ui.theme.AppTheme
import com.example.app.ui.viewmodels.TripListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController, 
    username: String,
    viewModel: TripListViewModel
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            CustomHeader("${stringResource(id = R.string.home_hola)}, $username", stringResource(id = R.string.home_busca_aventura))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                HotelSearchBanner(navController)

                RecomendadosSection(navController)

                Spacer(modifier = Modifier.height(24.dp))

                DestacadosSection(navController)

                Spacer(modifier = Modifier.height(24.dp))

                OfertasTipsSection(navController)

                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(id = R.string.home_explora_nomad),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HotelSearchBanner(navController: NavHostController) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { navController.navigate(Routes.HOTEL_SEARCH) },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2196F3), // NomadBlue
                            Color(0xFF0D47A1)  // NomadBlueDark
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "¿Buscas estancia?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Encuentra hoteles exclusivos en Barcelona, París o Londres",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Card(
                    shape = RoundedCornerShape(50),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.clickable { navController.navigate(Routes.HOTEL_SEARCH) }
                ) {
                    Text(
                        text = "Buscar",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                }
            }
        }
    }
}

@Composable
fun RecomendadosSection(navController: NavHostController) {
    Text(
        text = stringResource(id = R.string.home_recomendados),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            RecomendadoCard(
                image = R.drawable.newyork,
                name = "New York",
                onClick = { navController.navigate("form-viaje?ciudad=New York") }
            )
        }
        item {
            RecomendadoCard(
                image = R.drawable.paris,
                name = "París",
                onClick = { navController.navigate("form-viaje?ciudad=Paris") }
            )
        }
    }
}

@Composable
fun DestacadosSection(navController: NavHostController) {
    Text(
        text = stringResource(id = R.string.home_mas_destacados),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            DestacadoCard(
                image = R.drawable.bcn,
                name = "Barcelona",
                onClick = { navController.navigate("form-viaje?ciudad=Barcelona") }
            )
        }
        item {
            DestacadoCard(
                image = R.drawable.londres,
                name = "Londres",
                onClick = { navController.navigate("form-viaje?ciudad=Londres") }
            )
        }
        item {
            DestacadoCard(
                image = R.drawable.republica_checa,
                name = "República checa",
                onClick = { navController.navigate("form-viaje?ciudad=República checa") }
            )
        }
    }
}

@Composable
fun OfertasTipsSection(navController: NavHostController) {
    Text(
        text = stringResource(id = R.string.home_ofertas_tips),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OfertaTipCard(
            image = R.drawable.roma,
            title = stringResource(id = R.string.home_ahorrar_roma),
            onClick = { /* Aquí iría la noticia completa */ }
        )

        OfertaTipCard(
            image = R.drawable.equipaje_mano,
            title = stringResource(id = R.string.home_guia_equipaje),
            onClick = { /* Aquí iría la noticia completa */ }
        )
    }
}

@Composable
fun OfertaTipCard(image: Int, title: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().height(90.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = image),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(100.dp).fillMaxHeight()
            )
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun RecomendadoCard(image: Int, name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(260.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            Image(
                painter = painterResource(id = image),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(160.dp)
            )
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun DestacadoCard(image: Int, name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(180.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = image),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(110.dp)
            )
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text(text = name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, maxLines = 1)
            }
        }
    }
}
