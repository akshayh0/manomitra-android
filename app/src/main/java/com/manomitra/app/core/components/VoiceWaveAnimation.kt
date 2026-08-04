package com.manomitra.app.core.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/*
 * VoiceWaveAnimation Component
 *
 * Implements a reusable real-time voice indicator wave animation.
 * Features:
 * - 5 vertical cyan/teal bars.
 * - Dynamic height pulsing transitions to simulate vocal activity.
 * - Random/staggered animation durations to feel organic.
 */
@Composable
fun VoiceWaveAnimation(
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF14B8A6)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "voiceWave")

    // Define 5 distinct height animations with staggered durations for organic variance
    val height1 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barHeight1"
    )
    val height2 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barHeight2"
    )
    val height3 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 750, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barHeight3"
    )
    val height4 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barHeight4"
    )
    val height5 by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barHeight5"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val barHeights = listOf(height1, height2, height3, height4, height5)
        barHeights.forEach { height ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(height.dp)
                    .background(color = barColor, shape = RoundedCornerShape(4.dp))
            )
        }
    }
}
