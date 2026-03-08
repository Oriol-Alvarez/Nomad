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
import androidx.compose.foundation.lazy.grid.GridItemSpan // Import necesario para la magia del span
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
fun GaleriaViajeScreen(navController: NavHostController) {
    val albumTitle = "La antigua Roma"

    val allImages = listOf(
        R.drawable.roma, R.drawable.noruega, R.drawable.londres,
        R.drawable.roma, R.drawable.noruega, R.drawable.londres,
        R.drawable.roma
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        // Ya no necesitamos la Column externa, el LazyVerticalGrid ocupa todo
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            modifier = Modifier
                .fillMaxSize()
                // Aplicamos el padding inferior del Scaffold aquí
                .padding(bottom = innerPadding.calculateBottomPadding())
                .background(MaterialTheme.colorScheme.background),
            // Quitamos los márgenes superior y laterales para que la cabecera toque los bordes
            contentPadding = PaddingValues(bottom = 80.dp),
            // Mantenemos el espaciado vertical entre filas
            verticalArrangement = Arrangement.spacedBy(8.dp),
            // Reducimos el espaciado horizontal general porque le daremos padding a las fotos
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            // 1. LA CABECERA
            // span = { GridItemSpan(maxLineSpan) } hace que ocupe toda la fila
            item(span = { GridItemSpan(maxLineSpan) }) {
                // Envolvemos en un Box con padding inferior para separarlo de la primera fila de fotos
                Box(modifier = Modifier.padding(bottom = 16.dp)) {
                    CustomHeader(
                        title = albumTitle,
                        showBackButton = true // Asumo que al ser la vista detalle querrás el botón de volver
                    )
                }
            }

            // 2. LAS FOTOS
            items(allImages) { imageRes ->
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Foto del álbum",
                    modifier = Modifier
                        // Le damos el margen lateral a las fotos individualmente
                        .padding(horizontal = 4.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { /* Ver en grande */ },
                    contentScale = ContentScale.Crop
                )
            }

            // 3. EL BOTÓN DE AÑADIR
            item {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp) // Mismo margen que las fotos
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
fun GaleriaViajeScreenPreview() {
    AppTheme {
        GaleriaViajeScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun GaleriaViajeScreenPreviewNight() {
    AppTheme {
        GaleriaViajeScreen(navController = rememberNavController())
    }
}
