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
import androidx.compose.foundation.shape.CircleShape // ⬇️ Nuevo import para el círculo
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.app.ui.theme.AppTheme

@Composable
fun GaleriaViajeScreen2(navController: NavHostController) {
    val albumTitle = "La antigua Roma"

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

            // 1. LA CABECERA
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(modifier = Modifier.padding(bottom = 16.dp)) {
                    CustomHeader(
                        title = albumTitle,
                        showBackButton = true,
                        backgroundImageRes = R.drawable.roma
                    )
                }
            }

            // 2. LAS FOTOS
            itemsIndexed(allImages) { index, imageRes ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // La foto
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Foto del álbum",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { /* Ver en grande */ },
                        contentScale = ContentScale.Crop
                    )

                    // ⬇️ NUEVO BOTÓN DE ELIMINAR (Más pequeño y circular)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp) // Margen desde la esquina
                            .size(22.dp) // Tamaño total del círculo más pequeño
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), // Un poco más opaco para que destaque
                                shape = CircleShape // Círculo perfecto
                            )
                            .clip(CircleShape) // Para que el efecto visual del click (ripple) sea redondo
                            .clickable { allImages.removeAt(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar foto",
                            modifier = Modifier.size(14.dp), // Tamaño de la X reducido
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // 3. EL BOTÓN DE AÑADIR
            item {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        .clickable { /* Acción para añadir foto */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir nueva imagen",
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