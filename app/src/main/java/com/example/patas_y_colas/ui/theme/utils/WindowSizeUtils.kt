package com.example.patas_y_colas.ui.theme.utils

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
// ¡AÑADE ESTA LÍNEA QUE FALTABA!
import androidx.compose.material3.windowsizeclass.calculateFromSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // Esta es una implementación simplificada.
    // Para una implementación completa, usa 'androidx.compose.material3:material3-window-size-class'
    // y llama a 'calculateWindowSizeClass(activity)' desde tu MainActivity.
    // Por ahora, esto resolverá el error y funcionará para la mayoría de los casos.

    // ¡CORREGIDO!
    // Esta línea ahora usará el DpSize oficial que importamos.
    return WindowSizeClass.calculateFromSize(DpSize(screenWidth, screenHeight))
}

// ELIMINADO:
// Borramos las clases de ayuda falsas (value class DpSize, fun packFloats, etc.)
// que estaban aquí abajo causando el conflicto.