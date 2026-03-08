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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.app.Routes
import com.example.app.ui.theme.AppTheme

// Modelo de datos para la galería
data class GalleryAlbum(
    val title: String,
    val images: List<Int>
)

@Composable
fun GaleriaViajeScreen(navController: NavHostController) {
    // Simulamos viajes con diferentes cantidades de fotos
    val albums = listOf(
        GalleryAlbum("La antigua Roma", listOf(R.drawable.roma, R.drawable.noruega, R.drawable.londres, R.drawable.roma, R.drawable.noruega, R.drawable.londres, R.drawable.roma)),
        GalleryAlbum("Frío en Noruega", emptyList()),
        GalleryAlbum("Negocios en Londres", listOf(R.drawable.noruega, R.drawable.noruega, R.drawable.londres, R.drawable.noruega)),
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        // El LazyColumn ahora es la raíz de la pantalla y envuelve todo
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 80.dp) // Padding inferior para que no quede tapado por la barra
        ) {

            // 1. LA CABECERA COMO ITEM DEL LAZYCOLUMN
            item {
                CustomHeader("Galería", "Explora tus recuerdos")
            }

            // 2. LA BARRA DE BÚSQUEDA COMO ITEM DEL LAZYCOLUMN
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 32.dp, bottom = 28.dp), // Añadimos bottom aquí en lugar del contentPadding superior que tenías antes
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Barra de texto (Píldora)
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
                            Text(
                                text = "Search...",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Botón circular de la lupa
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.outline,
                        shadowElevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { /* Aquí irá la lógica de búsqueda */ },
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

            // 3. CONTENIDO DE ÁLBUMES
            items(albums) { album ->
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
        // Título del viaje
        Text(
            text = album.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 12.dp)
        )

        // Fila de máximo 3 imágenes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val displayImages = album.images.take(3)
            val extraCount = album.images.size - 3

            // Dibujamos las fotos existentes
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

                    // Capa oscura con el "+X" interactivo
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

            // Si el álbum tiene menos de 3 fotos, mostramos el botón de añadir en el siguiente hueco
            if (displayImages.size < 3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        .clickable { onAddImageClick() }, // <-- Activado el click
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

                // Y si AÚN quedan huecos, metemos Spacers invisibles
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