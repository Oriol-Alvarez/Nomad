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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.Routes
import com.example.app.ui.theme.AppTheme

// Estructura de datos que representa un álbum fotográfico vinculado a un destino
data class GalleryAlbum(
    val title: String,
    val images: List<Int>
)

@Composable
fun GaleriaViajeScreen(navController: NavHostController) {
    // 1. Estado para guardar el texto de la búsqueda
    var searchQuery by remember { mutableStateOf("") }

    // Conjunto de datos de prueba para renderizar diferentes estados de los álbumes
    val albums = listOf(
        GalleryAlbum("La antigua Roma", listOf(R.drawable.roma, R.drawable.roma, R.drawable.roma, R.drawable.roma, R.drawable.roma, R.drawable.roma, R.drawable.roma)),
        GalleryAlbum("Frío en Noruega", emptyList()),
        GalleryAlbum("Negocios en Londres", listOf(R.drawable.londres, R.drawable.londres, R.drawable.londres, R.drawable.londres)),
    )

    // 2. Filtramos la lista basándonos en el texto escrito (ignorando mayúsculas/minúsculas)
    val filteredAlbums = albums.filter {
        it.title.contains(searchQuery, ignoreCase = true)
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
                CustomHeader("Galería", "Explora tus recuerdos")
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
                            // 3. Componente de texto interactivo
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
                                            text = "Buscar viaje...",
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
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { /* Espacio para añadir acción extra si lo necesitas */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }

            // Sección 3: Iteración sobre la colección de álbumes filtrada
            items(filteredAlbums) { album ->
                AlbumSection(
                    album = album,
                    onAddImageClick = {
                        navController.navigate(Routes.GALERIA_VIAJE_2)
                    },
                    onMoreImagesClick = {
                        navController.navigate(Routes.GALERIA_VIAJE_2)
                    }
                )
            }
        }
    }
}

@Composable
fun AlbumSection(
    album: GalleryAlbum,
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
            text = album.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 12.dp)
        )

        // Cuadrícula horizontal fluida restringida a un máximo de 3 elementos visuales
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val displayImages = album.images.take(3)
            val extraCount = album.images.size - 3

            // Renderizado de las miniaturas fotográficas del álbum
            displayImages.forEachIndexed { index, imageRes ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
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
                            contentDescription = "Añadir imagen a ${album.title}",
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

@Preview(showBackground = true)
@Composable
fun GaleriaViajePreview() {
    AppTheme {
        GaleriaViajeScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun GaleriaViajePreviewNight() {
    AppTheme {
        GaleriaViajeScreen(navController = rememberNavController())
    }
}