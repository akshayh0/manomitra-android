package com.manomitra.app.feature.splash

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.manomitra.app.R
import com.manomitra.app.core.theme.spacing
import kotlinx.coroutines.delay

/*
 * Splash Screen Animation and Timing Mappings
 */
private object SplashConstants {
    const val SPLASH_DURATION = 2000L
    const val PROGRESS_DURATION = 1500
    const val ANIMATION_DURATION = 800

    const val LOGO_DELAY = 200
    const val TITLE_DELAY = 400
    const val TAGLINE_DELAY = 600
    const val PROGRESS_DELAY = 800
    const val FOOTER_DELAY = 1000

    const val TRANSLATION_Y_START = 20f
    const val GLOW_PARALLAX_MAX = 25f
}

/*
 * SplashScreen Composable
 *
 * Implements the Splash screen exactly matching the Stitch design system.
 * Features:
 * - A centered app logo (loaded from resources) with contentDescription for accessibility.
 * - Staggered fade-in and slide-up animations using the premium cubic-bezier easing.
 * - Ambient backdrop radial glow with a gentle floating parallax movement drawn efficiently on canvas.
 * - An M3 Linear Progress bar loading animation (marked as decorative).
 * - Automatic navigation to the login screen after 2.0 seconds.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val spacing = MaterialTheme.spacing

    LaunchedEffect(Unit) {
        // Trigger staggered animations
        startAnimation = true
        // Splash screen duration delay
        delay(SplashConstants.SPLASH_DURATION)
        onNavigateToLogin()
    }

    // Animation transition specs using the cubic-bezier(0.22, 1, 0.36, 1) curve
    val cubicEasing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)

    // Staggered alphas
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = SplashConstants.ANIMATION_DURATION,
            delayMillis = SplashConstants.LOGO_DELAY,
            easing = cubicEasing
        ),
        label = "logoAlpha"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = SplashConstants.ANIMATION_DURATION,
            delayMillis = SplashConstants.TITLE_DELAY,
            easing = cubicEasing
        ),
        label = "titleAlpha"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = SplashConstants.ANIMATION_DURATION,
            delayMillis = SplashConstants.TAGLINE_DELAY,
            easing = cubicEasing
        ),
        label = "taglineAlpha"
    )
    val progressAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = SplashConstants.ANIMATION_DURATION,
            delayMillis = SplashConstants.PROGRESS_DELAY,
            easing = cubicEasing
        ),
        label = "progressAlpha"
    )
    val footerAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = SplashConstants.ANIMATION_DURATION,
            delayMillis = SplashConstants.FOOTER_DELAY,
            easing = cubicEasing
        ),
        label = "footerAlpha"
    )

    // Staggered vertical slide-up offsets
    val logoTranslationY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else SplashConstants.TRANSLATION_Y_START,
        animationSpec = tween(
            durationMillis = SplashConstants.ANIMATION_DURATION,
            delayMillis = SplashConstants.LOGO_DELAY,
            easing = cubicEasing
        ),
        label = "logoTranslationY"
    )
    val titleTranslationY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else SplashConstants.TRANSLATION_Y_START,
        animationSpec = tween(
            durationMillis = SplashConstants.ANIMATION_DURATION,
            delayMillis = SplashConstants.TITLE_DELAY,
            easing = cubicEasing
        ),
        label = "titleTranslationY"
    )
    val taglineTranslationY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else SplashConstants.TRANSLATION_Y_START,
        animationSpec = tween(
            durationMillis = SplashConstants.ANIMATION_DURATION,
            delayMillis = SplashConstants.TAGLINE_DELAY,
            easing = cubicEasing
        ),
        label = "taglineTranslationY"
    )
    val progressTranslationY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else SplashConstants.TRANSLATION_Y_START,
        animationSpec = tween(
            durationMillis = SplashConstants.ANIMATION_DURATION,
            delayMillis = SplashConstants.PROGRESS_DELAY,
            easing = cubicEasing
        ),
        label = "progressTranslationY"
    )
    val footerTranslationY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else SplashConstants.TRANSLATION_Y_START,
        animationSpec = tween(
            durationMillis = SplashConstants.ANIMATION_DURATION,
            delayMillis = SplashConstants.FOOTER_DELAY,
            easing = cubicEasing
        ),
        label = "footerTranslationY"
    )

    // Progress bar fill animation (animates over 1.5s after 200ms delay)
    val progressProgress by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = SplashConstants.PROGRESS_DURATION,
            delayMillis = SplashConstants.LOGO_DELAY,
            easing = LinearOutSlowInEasing
        ),
        label = "progressProgress"
    )

    // Gentle infinite parallax float for ambient glow
    val infiniteTransition = rememberInfiniteTransition(label = "ambientGlow")
    val glowOffsetX by infiniteTransition.animateFloat(
        initialValue = -SplashConstants.GLOW_PARALLAX_MAX,
        targetValue = SplashConstants.GLOW_PARALLAX_MAX,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowOffsetX"
    )
    val glowOffsetY by infiniteTransition.animateFloat(
        initialValue = -SplashConstants.GLOW_PARALLAX_MAX,
        targetValue = SplashConstants.GLOW_PARALLAX_MAX,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowOffsetY"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, MaterialTheme.colorScheme.background)
                )
            )
            .drawBehind {
                // Draw dynamic ambient glow circle in background without child box nodes
                val glowSizePx = spacing.ambientGlowSize.toPx()
                val offsetXPx = glowOffsetX.dp.toPx()
                val opacityPx = glowOffsetY.dp.toPx()
                val glowCenter = center + Offset(offsetXPx, opacityPx)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x0D4F46E5), // ~5% opacity Brand Indigo (#4f46e5)
                            Color.Transparent
                        ),
                        center = glowCenter,
                        radius = glowSizePx / 2f
                    ),
                    radius = glowSizePx / 2f,
                    center = glowCenter
                )
            }
    ) {
        // Main Branding Content (Centered)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = spacing.containerMarginMobile),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.ic_manomitra_logo),
                contentDescription = "Manomitra App Logo",
                modifier = Modifier
                    .size(spacing.logoSize)
                    .graphicsLayer {
                        alpha = logoAlpha
                        translationY = logoTranslationY.dp.toPx()
                    },
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(spacing.stackLg))

            // Brand Title
            Text(
                text = "Manomitra",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = titleAlpha
                    translationY = titleTranslationY.dp.toPx()
                }
            )

            Spacer(modifier = Modifier.height(spacing.stackSm))

            // Tagline
            Text(
                text = "Your Mind's Trusted Friend",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    alpha = taglineAlpha
                    translationY = taglineTranslationY.dp.toPx()
                }
            )

            Spacer(modifier = Modifier.height(spacing.stackLg))

            // Decorative Progress/Loading Indicator
            LinearProgressIndicator(
                progress = { progressProgress },
                modifier = Modifier
                    .width(spacing.progressWidth)
                    .height(spacing.progressHeight)
                    .graphicsLayer {
                        alpha = progressAlpha
                        translationY = progressTranslationY.dp.toPx()
                    }
                    .clearAndSetSemantics { },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }

        // Version Footer
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = spacing.footerBottomPadding)
                .graphicsLayer {
                    alpha = footerAlpha
                    translationY = footerTranslationY.dp.toPx()
                }
        ) {
            Text(
                text = "VERSION 1.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
