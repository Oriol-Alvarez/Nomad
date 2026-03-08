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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    // Validación de estado de aceptación de términos legales al iniciar la pantalla
    TermsAndConditionsDialog(navController = navController)

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Sección Hero: Bienvenida al usuario y buscador
            CustomHeader("Hola, Oriol", "Busca tu próxima aventura")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Módulo de destinos recomendados con scroll horizontal
                RecomendadosSection(navController)

                Spacer(modifier = Modifier.height(24.dp))

                // Módulo de destinos destacados con tarjetas de menor escala
                DestacadosSection(navController)

                Spacer(modifier = Modifier.height(24.dp))

                // Módulo de contenido editorial y promociones
                OfertasTipsSection(navController)

                // Cierre de la pantalla (Footer decorativo)
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Explora el mundo con Nomad 🌍",
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
fun RecomendadosSection(navController: NavHostController) {
    Text(
        text = "Recomendados para ti",
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
        text = "Más destacados",
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
        text = "Ofertas y tips",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OfertaTipCard(
            image = R.drawable.roma,
            title = "Cómo ahorrar en tu viaje a Roma",
            onClick = { /* Implementar navegación a detalle de artículo */ }
        )

        OfertaTipCard(
            image = R.drawable.equipaje_mano,
            title = "Equipaje de mano: Guía definitiva",
            onClick = { /* Implementar navegación a detalle de artículo */ }
        )
    }
}

@Composable
fun OfertaTipCard(
    image: Int,
    title: String,
    onClick: () -> Unit
) {
    // Tarjeta apaisada optimizada para artículos y listas de consejos
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = image),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
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
fun RecomendadoCard(
    image: Int,
    name: String,
    onClick: () -> Unit
) {
    // Componente de tarjeta de gran formato para destinos sugeridos
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            Image(
                painter = painterResource(id = image),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )

            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun DestacadoCard(
    image: Int,
    name: String,
    onClick: () -> Unit
) {
    // Componente de tarjeta de formato reducido para destinos secundarios
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = image),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )

            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 12.dp)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun TermsAndConditionsDialog(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
    }

    // Persistencia local del estado de aceptación de términos
    var hasAccepted by remember {
        mutableStateOf(sharedPreferences.getBoolean("terms_accepted", false))
    }

    // Sincronización del estado al recuperar el foco de la pantalla
    LaunchedEffect(Unit) {
        hasAccepted = sharedPreferences.getBoolean("terms_accepted", false)
    }

    if (!hasAccepted) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = MaterialTheme.colorScheme.background,
            title = {
                Text(text = "Privacidad y términos", fontWeight = FontWeight.Bold)
            },
            text = {
                // Composición de texto enriquecido con enlace interactivo embebido
                val annotatedText = buildAnnotatedString {
                    append("Para usar Nomad, debes aceptar nuestros ")

                    pushStringAnnotation(tag = "URL", annotation = "terms")
                    withStyle(style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )) {
                        append("términos y condiciones")
                    }
                    pop()

                    append(" antes de comenzar tu aventura.")
                }

                ClickableText(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                            .firstOrNull()?.let {
                                navController.navigate(Routes.TERMINOS_CONDICIONES)
                            }
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        sharedPreferences.edit().putBoolean("terms_accepted", true).apply()
                        hasAccepted = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.inversePrimary
                    )
                ) {
                    Text("Aceptar y continuar")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomePreviewDark() {
    AppTheme {
        HomeScreen(navController = rememberNavController())
    }
}

@Preview(name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HomePreviewLight() {
    AppTheme {
        HomeScreen(navController = rememberNavController())
    }
}