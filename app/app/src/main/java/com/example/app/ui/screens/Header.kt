package com.example.app.ui.screens

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CustomHeader(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    backgroundImageRes: Int? = null
) {
    // Gestor para la navegación hacia atrás del sistema
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // Definición del degradado por defecto cuando no se proporciona una imagen de fondo
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
        // Contenedor principal para superponer capas de fondo y contenido textual
        Box(modifier = Modifier.fillMaxWidth()) {

            // Capa 1: Lógica de fondo (Imagen con máscara de legibilidad o degradado sólido)
            if (backgroundImageRes != null) {
                // Renderizado de la imagen de recurso con recorte adaptativo
                Image(
                    painter = painterResource(id = backgroundImageRes),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
                // Superposición de degradado vertical oscuro para garantizar el contraste del texto blanco
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
                // Aplicación del degradado de marca si no existe imagen de fondo
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(defaultGradientBrush)
                )
            }

            // Capa 2: Estructura de contenido (Botón de retroceso, título y subtítulo)
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

                // Área de control superior: Botón de navegación opcional
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
                                contentDescription = "Regresar",
                                tint = Color.White,
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }

                // Título principal con énfasis tipográfico
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtítulo secundario con opacidad reducida para jerarquía visual
                Text(
                    text = subtitle ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

            }
        }
    }
}