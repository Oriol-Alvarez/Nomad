package com.example.app.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.ui.theme.AppTheme

@Composable
fun GaleriaViajeScreen2(navController: NavHostController) {
    val albumTitle = "La antigua Roma"

    // Estado reactivo que gestiona la lista de imágenes para permitir la eliminación en tiempo real
    val allImages = remember {
        mutableStateListOf(
            R.drawable.roma, R.drawable.roma, R.drawable.roma,
            R.drawable.roma, R.drawable.roma, R.drawable.roma,
            R.drawable.roma
        )
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        // Grid adaptativo que ajusta el número de columnas según el ancho del dispositivo
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            // Cabecera: Se configura para ocupar el ancho total de la cuadrícula (span completo)
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(modifier = Modifier.padding(bottom = 16.dp)) {
                    CustomHeader(
                        title = albumTitle,
                        showBackButton = true,

                    )
                }
            }

            // Renderizado de las celdas de imagen con funcionalidad de eliminación
            itemsIndexed(allImages) { index, imageRes ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // Visualización de la imagen con ajuste de recorte (Crop)
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = stringResource(id = R.string.galeria2_foto_desc),
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { /* Implementar visualización a pantalla completa */ },
                        contentScale = ContentScale.Crop
                    )

                    // Control de eliminación: Botón circular posicionado en la esquina superior derecha
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(22.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .clickable { allImages.removeAt(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.galeria2_eliminar_desc),
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Celda de acción: Botón para importar o capturar nuevas imágenes
            item {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        .clickable { /* Lógica de selección de archivos */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.galeria2_anadir_desc),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GaleriaViajeScreen2Preview() {
    AppTheme {
        GaleriaViajeScreen2(navController = rememberNavController())
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun GaleriaViajeScreen2PreviewNight() {
    AppTheme {
        GaleriaViajeScreen2(navController = rememberNavController())
    }
}