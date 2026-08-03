package com.manomitra.app.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
 * Manomitra Spacing Configuration
 *
 * Defines grid spacing, margin, and size tokens in alignment with the Stitch specifications.
 * Uses an 8dp spatial rhythm to ensure visual balance.
 */
data class Spacing(
    // Base layout rhythm
    val unit: Dp = 8.dp,
    val stackSm: Dp = 8.dp,
    val stackMd: Dp = 16.dp,
    val stackLg: Dp = 32.dp,
    val sectionGap: Dp = 64.dp,
    val containerMarginMobile: Dp = 20.dp,
    val containerMarginDesktop: Dp = 48.dp,
    val gutter: Dp = 16.dp,

    // Branding and component dimensions
    val logoSize: Dp = 128.dp,
    val progressWidth: Dp = 128.dp,
    val progressHeight: Dp = 4.dp,
    val footerBottomPadding: Dp = 48.dp,
    val ambientGlowSize: Dp = 600.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
