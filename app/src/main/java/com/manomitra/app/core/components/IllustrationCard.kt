package com.manomitra.app.core.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.Dp

/*
 * IllustrationCard Component
 *
 * Implements a premium glassmorphic image container with a spring entry zoom transition.
 * Features:
 * - Transparent white outline border.
 * - Backdrop blurring.
 * - Dynamic custom primary-tinted soft depth shadow.
 * - Fully reusable by accepting any Painter.
 */
@Composable
fun IllustrationCard(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp
) {
    var startScale by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        startScale = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (startScale) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        val frameworkPaint = asFrameworkPaint()
                        frameworkPaint.color = Color.Transparent.toArgb()
                        frameworkPaint.setShadowLayer(
                            20.dp.toPx(),
                            0f,
                            4.dp.toPx(),
                            Color(0x0D4F46E5).toArgb() // 5% opacity brand indigo
                        )
                    }
                    canvas.drawRoundRect(
                        left = 0f,
                        top = 0f,
                        right = size.width,
                        bottom = size.height,
                        radiusX = cornerRadius.toPx(),
                        radiusY = cornerRadius.toPx(),
                        paint = paint
                    )
                }
            }
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.4f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(Color.White.copy(alpha = 0.8f))
    ) {
        // Blur layer for glassmorphism
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp)
        )
        
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
