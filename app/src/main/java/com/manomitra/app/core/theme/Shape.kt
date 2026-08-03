package com.manomitra.app.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/*
 * Manomitra Shape Style Configurator
 *
 * Defines standard shapes across components in alignment with Stitch roundness tokens.
 */

val Shapes = Shapes(
    // Extra Small - Used for badges, tooltips, and minor UI markers
    extraSmall = RoundedCornerShape(4.dp),

    // Small - Used for buttons, chips, text inputs, and toggles
    small = RoundedCornerShape(8.dp),

    // Medium - Used for standard cards, dialog popups, and popup menus
    medium = RoundedCornerShape(12.dp),

    // Large - Used for bottom sheets, navigation drawers, and primary cards
    large = RoundedCornerShape(16.dp),

    // Extra Large - Used for large container panels and background overlays
    extraLarge = RoundedCornerShape(24.dp)
)
