package com.example.app.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app.R
import com.example.app.Routes
import com.example.app.ui.viewmodels.TripListViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun GaleriaViajeScreen(
    navController: NavHostController,
    viewModel: TripListViewModel
) {
    // 1. Estado para guardar el texto de la búsqueda
    var searchQuery by remember { mutableStateOf("") }

    // Observamos los viajes en tiempo real desde la base de datos
    val tripsFromDB by viewModel.trips.collectAsState()

    val today = remember {
        java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.time
    }

    // Filtramos para mostrar únicamente viajes pasados o actuales (NO futuros)
    val validTrips = remember(tripsFromDB, today) {
        tripsFromDB.filter { trip ->
            val start = parseTripDate(trip.dataInici)
            start != null && !today.before(start)
        }
    }

    // Filtramos la lista basándonos en el texto escrito (ignorando mayúsculas/minúsculas)
    val filteredTrips = remember(validTrips, searchQuery) {
        validTrips.filter {
            it.title.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        // Contenedor principal desplazable que estructura el layout de la pantalla
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // Sección 1: Componente de cabecera principal de la galería
            item {
                CustomHeader(stringResource(id = R.string.galeria_titulo), stringResource(id = R.string.galeria_subtitulo))
            }

            // Sección 2: Fila de controles que contiene la barra de búsqueda interactiva
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 32.dp, bottom = 28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Contenedor visual del campo de texto redondeado (Píldora de búsqueda)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.outline,
                        shadowElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { newText -> searchQuery = newText },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp
                                ),
                                decorationBox = { innerTextField ->
                                    // Mostramos el placeholder solo si está vacío
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = stringResource(id = R.string.galeria_buscar_placeholder),
                                            color = Color.Gray,
                                            fontSize = 16.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Botón de acción flotante auxiliar para la búsqueda
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.outline,
                        shadowElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.galeria_buscar_desc),
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }

            if (filteredTrips.isEmpty()) {
                item {
                    Text(
                        text = if (searchQuery.isEmpty()) "No tienes viajes pasados o actuales todavía" else "No se encontraron álbumes coincidentes",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 24.dp, end = 24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                // Sección 3: Iteración sobre la colección de viajes filtrados
                items(filteredTrips) { trip ->
                    // Observamos las imágenes de este viaje en particular de forma reactiva
                    val images by viewModel.getImagesForTrip(trip.id).collectAsState(initial = emptyList())

                    AlbumSection(
                        tripTitle = trip.title,
                        images = images,
                        onAddImageClick = {
                            navController.navigate("${Routes.GALERIA_VIAJE_2}/${trip.id}")
                        },
                        onMoreImagesClick = {
                            navController.navigate("${Routes.GALERIA_VIAJE_2}/${trip.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AlbumSection(
    tripTitle: String,
    images: List<com.example.app.domain.TripImage>,
    onAddImageClick: () -> Unit,
    onMoreImagesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // Encabezado textual de la sección del álbum
        Text(
            text = tripTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 12.dp)
                .clickable { onMoreImagesClick() }
        )

        // Cuadrícula horizontal fluida restringida a un máximo de 3 elementos visuales
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val displayImages = images.take(3)
            val extraCount = images.size - 3

            // Renderizado de las miniaturas fotográficas del álbum
            displayImages.forEachIndexed { index, tripImage ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = tripImage.imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onMoreImagesClick() },
                        contentScale = ContentScale.Crop
                    )

                    // Capa superpuesta (Overlay) que indica exceso de imágenes y actúa como botón de expansión
                    if (index == 2 && extraCount > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable { onMoreImagesClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+$extraCount",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Contenedor interactivo para la adición de imágenes, visible si la cuadrícula no está llena
            if (displayImages.size < 3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        .clickable { onAddImageClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.galeria_anadir_foto_desc, tripTitle),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Generación de espaciadores flexibles para conservar la alineación y proporción de la Row
                val espaciosRestantes = 3 - displayImages.size - 1
                if (espaciosRestantes > 0) {
                    repeat(espaciosRestantes) {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

private fun parseTripDate(dateString: String?): java.util.Date? {
    if (dateString.isNullOrEmpty()) return null
    val formatosPosibles = listOf("yyyy-MM-dd", "yyyy/MM/dd", "dd/MM/yyyy", "dd-MM-yyyy")
    for (patron in formatosPosibles) {
        try {
            val input = SimpleDateFormat(patron, Locale.getDefault())
            input.isLenient = false
            val date = input.parse(dateString)
            if (date != null) return date
        } catch (e: Exception) {
            continue
        }
    }
    return null
}