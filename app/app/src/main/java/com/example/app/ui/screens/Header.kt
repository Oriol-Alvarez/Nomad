package com.example.app.ui.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Esta es la cabecera que usamos en casi todas las pantallas.
 * Puede tener una imagen de fondo o un degradado de colores.
 */
@Composable
fun CustomHeader(
    title: String? = null,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    backgroundImageRes: String? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null 
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // Colores por defecto si no hay imagen
    val defaultGradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.primaryContainer
        )
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            // Ponemos el fondo: imagen si hay una, o el degradado si no
            if (!backgroundImageRes.isNullOrEmpty()) {
                AsyncImage(
                    model = backgroundImageRes,
                    contentDescription = "Imagen de fondo",
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
                // Oscurecemos un poco la imagen para que el texto se lea bien
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(defaultGradientBrush)
                )
            }

            // Aquí va el texto y el botón de volver
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 48.dp,
                        bottom = 32.dp
                    )
            ) {

                if (showBackButton) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { backDispatcher?.onBackPressed() },
                            modifier = Modifier.offset(x = (-16).dp, y = (-24).dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver atrás",
                                tint = Color.White,
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }

                // Si pasamos contenido personalizado lo usamos, si no, mostramos título y subtítulo normales
                if (content != null) {
                    content()
                } else {
                    Text(
                        text = title ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = subtitle ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
