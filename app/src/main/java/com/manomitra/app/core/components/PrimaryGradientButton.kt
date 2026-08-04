package com.manomitra.app.core.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.manomitra.app.core.theme.spacing

import androidx.compose.ui.graphics.Shape

/*
 * PrimaryGradientButton Component
 *
 * Implements a premium violet-to-indigo gradient button exactly matching the Stitch design.
 * Features:
 * - A linear gradient background.
 * - Staggered press-scale micro-interaction (`scale-95`).
 * - Optional trailing arrow icon.
 */
@Composable
fun PrimaryGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showArrow: Boolean = true,
    shape: Shape = MaterialTheme.shapes.medium
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "buttonScale")

    val spacing = MaterialTheme.spacing
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF4F46E5), Color(0xFF6B38D4))
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(brush = gradient)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Custom scale animation gives sufficient feedback
                onClick = onClick
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
        if (showArrow) {
            Spacer(modifier = Modifier.width(spacing.stackSm))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null, // decorative
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
