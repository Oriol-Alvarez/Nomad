package com.example.app.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.app.R
import com.example.app.ui.viewmodels.TripListViewModel

@Composable
fun GaleriaViajeScreen2(
    navController: NavHostController,
    tripId: String,
    viewModel: TripListViewModel
) {
    val trips by viewModel.trips.collectAsState()
    val trip = trips.find { it.id == tripId }
    val albumTitle = trip?.title ?: stringResource(id = R.string.app_name)

    // Estado reactivo que gestiona la lista de imágenes guardadas en base de datos
    val images by viewModel.getImagesForTrip(tripId).collectAsState(initial = emptyList())

    val context = androidx.compose.ui.platform.LocalContext.current

    // Selector de múltiples imágenes de la galería del sistema
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            val stringUris = uris.mapNotNull { uri ->
                copyUriToInternalStorage(context, uri)?.toString()
            }
            viewModel.addImagesToTrip(tripId, stringUris)
        }
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
                        showBackButton = true
                    )
                }
            }

            // Renderizado de las celdas de imagen con funcionalidad de eliminación
            itemsIndexed(images) { index, tripImage ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // Visualización de la imagen con AsyncImage
                    AsyncImage(
                        model = tripImage.imageUri,
                        contentDescription = stringResource(id = R.string.galeria2_foto_desc),
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { /* Opcional: pantalla completa */ },
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
                            .clickable { viewModel.deleteTripImage(tripImage.id) },
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
                        .clickable { launcher.launch("image/*") },
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

private fun copyUriToInternalStorage(context: android.content.Context, uri: android.net.Uri): android.net.Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "trip_img_${System.currentTimeMillis()}.jpg"
        val file = java.io.File(context.filesDir, fileName)
        val outputStream = java.io.FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        android.net.Uri.fromFile(file)
    } catch (e: Exception) {
        android.util.Log.e("StorageUtils", "Error copying URI to internal storage", e)
        null
    }
}