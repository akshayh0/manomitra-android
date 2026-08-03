package com.manomitra.app.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
 * Manomitra Elevation Configuration
 *
 * Defines Material 3 tonal elevation and custom shadows.
 * Includes a soft primary-tinted ambient glow shadow as specified by the Stitch design.
 */
data class Elevation(
    val level0: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp,
    val level3: Dp = 6.dp,
    val level4: Dp = 8.dp,
    val level5: Dp = 12.dp,
    
    // Soft depth primary-tinted ambient shadow: 0px 4px 20px rgba(79, 70, 229, 0.05)
    val softShadow: Shadow = Shadow(
        color = Color(0x0D4F46E5), // 0x0D is ~5% opacity (79, 70, 229)
        offset = androidx.compose.ui.geometry.Offset(0f, 4f),
        blurRadius = 20f
    )
)

val LocalElevation = staticCompositionLocalOf { Elevation() }
