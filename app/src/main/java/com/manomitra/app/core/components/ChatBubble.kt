package com.manomitra.app.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

/*
 * ChatBubble Component
 *
 * Implements a reusable premium chat bubble supporting glassmorphic styling,
 * solid-color background modes, custom side-borders, and drop shadow glows.
 * Perfect for conversational UIs.
 */
@Composable
fun ChatBubble(
    message: String,
    isUser: Boolean,
    modifier: Modifier = Modifier,
    backgroundColor: Color = if (isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.White.copy(alpha = 0.8f)
    },
    contentColor: Color = if (isUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    },
    accentColor: Color = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }
) {
    val bubbleShape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            // 1. Shadow glow (soft-glow-indigo: 0px 4px 24px rgba(79, 70, 229, 0.08))
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        val frameworkPaint = asFrameworkPaint()
                        frameworkPaint.color = Color.Transparent.toArgb()
                        frameworkPaint.setShadowLayer(
                            24.dp.toPx(),
                            0f,
                            4.dp.toPx(),
                            Color(0x144F46E5).toArgb() // 8% opacity brand indigo
                        )
                    }
                    canvas.drawRoundRect(
                        left = 0f,
                        top = 0f,
                        right = size.width,
                        bottom = size.height,
                        radiusX = 12.dp.toPx(),
                        radiusY = 12.dp.toPx(),
                        paint = paint
                    )
                }
            }
            .clip(bubbleShape)
            // 2. Main background color/glass styling
            .then(
                if (!isUser) {
                    Modifier.border(
                        width = 1.dp,
                        color = Color(0xCCE2E8F0), // rgba(226, 232, 240, 0.8)
                        shape = bubbleShape
                    )
                } else Modifier
            )
            .background(backgroundColor)
            // 3. Highlight side border (left for AI, right for user)
            .drawBehind {
                val strokeWidthPx = 4.dp.toPx()
                if (isUser) {
                    drawLine(
                        color = accentColor,
                        start = Offset(size.width - strokeWidthPx / 2, 0f),
                        end = Offset(size.width - strokeWidthPx / 2, size.height),
                        strokeWidth = strokeWidthPx
                    )
                } else {
                    drawLine(
                        color = accentColor,
                        start = Offset(strokeWidthPx / 2, 0f),
                        end = Offset(strokeWidthPx / 2, size.height),
                        strokeWidth = strokeWidthPx
                    )
                }
            }
    ) {
        // Blur background layer for glassmorphic AI bubble
        if (!isUser) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(20.dp)
            )
        }

        // Text content padded, leaving extra space for the vertical accent line
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .padding(
                    start = if (isUser) 0.dp else 4.dp,
                    end = if (isUser) 4.dp else 0.dp
                )
        )
    }
}
