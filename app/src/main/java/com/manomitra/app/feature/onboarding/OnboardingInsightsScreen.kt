package com.manomitra.app.feature.onboarding

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.manomitra.app.R
import com.manomitra.app.core.components.IllustrationCard
import com.manomitra.app.core.components.PageIndicatorDots
import com.manomitra.app.core.components.PrimaryGradientButton
import com.manomitra.app.core.theme.spacing

/*
 * OnboardingInsightsScreen Composable
 *
 * Implements the second screen of the onboarding flow.
 * Features:
 * - Dual radial gradient mesh background (indigo top-left, teal top-right).
 * - Floating background aura behind illustration card.
 * - Reusable components (IllustrationCard, PageIndicatorDots, PrimaryGradientButton).
 * - Fixed fading gradient bottom footer.
 * - Gentle continuous 3D card tilt animation & decorative floating bubble animations.
 * - Accessible touch targets (minimum 48dp).
 */
@Composable
fun OnboardingInsightsScreen(
    modifier: Modifier = Modifier,
    onContinueClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()

    // 1. Continuous Parallax Angle for background aura
    val infiniteTransition = rememberInfiniteTransition(label = "onboardingInsightsAnim")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auraAngle"
    )
    val auraOffsetX = kotlin.math.sin(angle) * 10f
    val auraOffsetY = kotlin.math.cos(angle) * 10f

    // 2. Pulse Opacity for top-right cyan bubble
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // 3. Vertical Bounce for bottom-left indigo bubble
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceOffset"
    )

    // 4. Continuous gentle 3D Card Tilt Animation
    val cardAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cardAngle"
    )
    val tiltRotationX = kotlin.math.sin(cardAngle) * 3f
    val tiltRotationY = kotlin.math.cos(cardAngle) * 3f

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Dual radial gradient mesh background (5% opacity at corners, fading to transparent)
                val radius = size.width * 0.8f
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x0D4F46E5), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = radius
                    )
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x0D14B8A6), Color.Transparent),
                        center = Offset(size.width, 0f),
                        radius = radius
                    )
                )
            }
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Pinned Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = spacing.containerMarginMobile, vertical = spacing.gutter),
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

        // Scrollable Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = spacing.containerMarginMobile)
                .padding(top = 96.dp)
                .padding(bottom = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Illustration Container Box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                // Secondary-tinted background aura (blurred)
                Box(
                    modifier = Modifier
                        .fillMaxSize(1.2f)
                        .offset(x = auraOffsetX.dp, y = auraOffsetY.dp)
                        .blur(64.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
                            shape = CircleShape
                        )
                )

                // Top-Right floating decorative cyan/teal blur bubble
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 12.dp, y = (-12).dp)
                        .blur(24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = pulseAlpha),
                            shape = CircleShape
                        )
                )

                // Bottom-Left floating decorative indigo/primary blur bubble
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-16).dp, y = (16 + bounceOffset).dp)
                        .blur(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.10f),
                            shape = CircleShape
                        )
                )

                // Serene Illustration Glass Card (80% size, 32dp corners, gentle auto-tilt rotation)
                IllustrationCard(
                    painter = painterResource(id = R.drawable.img_onboarding_insights),
                    contentDescription = "Minimalist digital illustration representing mood tracking and emotional discovery.",
                    cornerRadius = 32.dp,
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                        .graphicsLayer {
                            rotationX = tiltRotationX
                            rotationY = tiltRotationY
                            cameraDistance = 8 * density
                        }
                )
            }

            Spacer(modifier = Modifier.height(spacing.stackLg))

            // Typography Cluster
            Column(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.stackMd)
            ) {
                Text(
                    text = "Understand Your Mind",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.025).em // -2.5% letter spacing
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Track your mood, journal your thoughts, and discover emotional patterns.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 26.sp // leading-relaxed (1.625x)
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(spacing.sectionGap))

            // Page Indicator Dots (Step 2 of 3)
            PageIndicatorDots(
                totalPages = 3,
                currentPage = 1
            )
        }

        // Fixed Fading Gradient Bottom Footer Action Area
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(horizontal = spacing.containerMarginMobile)
                .padding(top = 24.dp, bottom = 48.dp)
        ) {
            PrimaryGradientButton(
                text = "Continue",
                onClick = onContinueClick,
                shape = CircleShape
            )
        }
    }
}
