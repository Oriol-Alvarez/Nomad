package com.example.app.ui.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image // ⬇️ Nuevo import
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box // ⬇️ Nuevo import
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.layout.ContentScale // ⬇️ Nuevo import
import androidx.compose.ui.res.painterResource // ⬇️ Nuevo import
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CustomHeader(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    backgroundImageRes: Int? = null //
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // Degradado original por si no hay imagen
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
        // Usamos un Box para poder apilar la imagen de fondo y el contenido
        Box(modifier = Modifier.fillMaxWidth()) {

            // 1. Capa de fondo (Imagen + Degradado oscuro O Degradado base)
            if (backgroundImageRes != null) {
                // Imagen de fondo
                Image(
                    painter = painterResource(id = backgroundImageRes),
                    contentDescription = "Imagen de fondo de la cabecera",
                    // Usamos matchParentSize para que ocupe exactamente lo que mide la cabecera
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
                // Degradado oscuro para que el texto se lea bien
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
                // Si no hay imagen, aplicamos el fondo original al Box base
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(defaultGradientBrush)
                )
            }

            // 2. Capa de contenido (Textos y Botones)
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

                // Fila superior con botón back opcional
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
                                contentDescription = "Back",
                                tint = Color.White,
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }

                // Título (arriba)
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtítulo (abajo)
                Text(
                    text = subtitle ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

            }
        }
    }
}