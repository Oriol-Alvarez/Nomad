package com.example.app.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
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
    //Comentar TermsAndConditionsDialog para desarrollo
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
            // Hero principal
            CustomHeader("Hola, Oriol", "Busca tu próxima aventura")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Llamada a las secciones modulares
                RecomendadosSection(navController)

                Spacer(modifier = Modifier.height(24.dp))

                DestacadosSection(navController)

                Spacer(modifier = Modifier.height(24.dp))

                OfertasTipsSection(navController)

                // Footer
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Explora el mundo con Nomad 🌍",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray, // Ajustado para visibilidad si el fondo es claro
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
        // Ejemplo de llamadas simples como pediste
        OfertaTipCard(
            image = R.drawable.roma,
            title = "Cómo ahorrar en tu viaje a Roma",
            onClick = { /* Navegar a detalle */ }
        )

        OfertaTipCard(
            image = R.drawable.equipaje_mano,
            title = "Equipaje de mano: Guía definitiva",
            onClick = { /* Navegar a detalle */ }
        )
    }
}

@Composable
fun OfertaTipCard(
    image: Int,
    title: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Gris oscuro según tu diseño
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
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Fondo surface para todo el bloque
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            // Imagen: ocupa la parte superior
            Image(
                painter = painterResource(id = image),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )

            // Contenedor para el texto con padding interno
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp) // Espaciado interno para el nombre
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface // onSurface para mejor contraste
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
    Card(
        modifier = Modifier
            .width(180.dp) // Mantenemos el tamaño más pequeño para jerarquía
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // Gris claro para el contenedor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Sin sombra para un look flat
    ) {
        Column {
            // Imagen
            Image(
                painter = painterResource(id = image),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )

            // Texto con padding dentro de la tarjeta
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1 // Evita que el texto rompa el diseño si es muy largo
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

    // Este es el estado que controla el diálogo
    var hasAccepted by remember {
        mutableStateOf(sharedPreferences.getBoolean("terms_accepted", false))
    }

    // 🔹 ESTO ES LO QUE TE FALTA:
    // Cada vez que esta pantalla se cargue o "vuelva", re-lee el archivo
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
                // Creamos un texto con una parte clicable
                val annotatedText = buildAnnotatedString {
                    append("Para usar Nomad, debes aceptar nuestros ")

                    pushStringAnnotation(tag = "URL", annotation = "terms")
                    withStyle(style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                    ) {
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
                                // Navega a tu ruta de términos
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
                        contentColor = MaterialTheme.colorScheme.inversePrimary // Color del texto adaptado al fondo
                    )

                ) {
                    Text("Aceptar y continuar")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HomePreviewDark() {
    AppTheme {
        HomeScreen(
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
fun HomePreviewLight() {
    AppTheme {
        HomeScreen(
            navController = rememberNavController()
        )
    }
}
