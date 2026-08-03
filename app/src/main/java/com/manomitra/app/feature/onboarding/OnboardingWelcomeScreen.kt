package com.manomitra.app.feature.onboarding

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.manomitra.app.R
import com.manomitra.app.core.components.IllustrationCard
import com.manomitra.app.core.components.PageIndicatorDots
import com.manomitra.app.core.components.PrimaryGradientButton
import com.manomitra.app.core.components.SecondaryTonalButton
import com.manomitra.app.core.theme.spacing

/*
 * OnboardingWelcomeScreen Composable
 *
 * Implements the first screen of the onboarding flow.
 * Features:
 * - Serene radial background gradient from top-right.
 * - Floating background aura behind illustration.
 * - Reusable components (IllustrationCard, PageIndicatorDots, PrimaryGradientButton, SecondaryTonalButton).
 * - Glassmorphic fixed bottom footer.
 * - Staggered entrance spring transitions.
 * - Accessible touch targets (minimum 48dp).
 */
@Composable
fun OnboardingWelcomeScreen(
    modifier: Modifier = Modifier,
    onContinueClick: () -> Unit,
    onSkipClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()

    // Parallax animation values for the background blur aura
    val infiniteTransition = rememberInfiniteTransition(label = "auraParallax")
    val auraOffsetX by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraOffsetX"
    )
    val auraOffsetY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraOffsetY"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Dynamic top-right radial gradient matching onboarding specifications
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFE2DFFF), MaterialTheme.colorScheme.background),
                        center = Offset(size.width, 0f),
                        radius = size.width * 1.2f
                    )
                )
            }
    ) {
        // Main Content (Scrollable Column)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = spacing.containerMarginMobile)
                .padding(bottom = 240.dp), // Clear the fixed bottom footer height
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.gutter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Manomitra",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                
                // Flat Skip Button with minimum 48dp touch target
                Box(
                    modifier = Modifier
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .clickable(onClick = onSkipClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.stackLg))

            // Illustration Frame Box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                // Animated blurred aura circle in background
                Box(
                    modifier = Modifier
                        .fillMaxSize(1.2f)
                        .offset(x = auraOffsetX.dp, y = auraOffsetY.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Serene Illustration Glass Card
                IllustrationCard(
                    painter = painterResource(id = R.drawable.img_onboarding_welcome),
                    contentDescription = "Serene illustration of a person sitting in a meditative pose on a floating platform with floating teal and violet support structures.",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(spacing.stackLg))

            // Text Typography Cluster
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.stackMd)
            ) {
                Text(
                    text = "Welcome to Manomitra.",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "A safe space to talk, reflect, and grow with AI-powered support.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.15
                )
            }
        }

        // Fixed Glassmorphic Bottom Action Area
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .drawBehind {
                    // Subtle white boundary line at the top of the footer
                    drawLine(
                        color = Color.White.copy(alpha = 0.2f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White.copy(alpha = 0.8f))
        ) {
            // Glassmorphism Blur background layer
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(20.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.containerMarginMobile)
                    .padding(top = 32.dp, bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.stackLg)
            ) {
                // Page Indicator Dots
                PageIndicatorDots(
                    totalPages = 3,
                    currentPage = 0
                )

                // Action Buttons Stack
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrimaryGradientButton(
                        text = "Continue",
                        onClick = onContinueClick
                    )
                    SecondaryTonalButton(
                        text = "Sign in to account",
                        onClick = onSignInClick
                    )
                }
            }
        }
    }
}
