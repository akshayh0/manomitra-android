package com.manomitra.app.feature.onboarding

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.manomitra.app.R
import com.manomitra.app.core.components.ChatBubble
import com.manomitra.app.core.components.PageIndicatorDots
import com.manomitra.app.core.components.PrimaryGradientButton
import com.manomitra.app.core.components.SecondaryTonalButton
import com.manomitra.app.core.components.VoiceWaveAnimation
import com.manomitra.app.core.theme.spacing

/*
 * OnboardingAIScreen Composable
 *
 * Implements the third screen of the onboarding flow ("Onboarding: AI Companion").
 * Features:
 * - Ambient radial background cursor glow visual.
 * - Center floating visual orb container with downloaded Stitch AI avatar orb asset.
 * - Dynamic core component reuse for custom pill-shaped CTA buttons.
 * - Reusable PageIndicatorDots (Step 3 of 3).
 * - Floating user and AI chat bubbles with glassmorphism/tonal accents.
 * - Real-time VoiceWaveAnimation.
 * - Pinned layout structure and edge-to-edge support.
 */
@Composable
fun OnboardingAIScreen(
    modifier: Modifier = Modifier,
    onGetStartedClick: () -> Unit,
    onSkipClick: () -> Unit,
    onLearnMoreClick: () -> Unit = {}
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()

    // 1. Organic Float Animation for the visual orb container
    val infiniteTransition = rememberInfiniteTransition(label = "onboardingAIFloat")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Static premium primary-tinted cursor/ambient glow drawn at center
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x0D4F46E5), Color.Transparent),
                        center = center,
                        radius = 200.dp.toPx()
                    ),
                    radius = 200.dp.toPx(),
                    center = center
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
                .padding(bottom = 220.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Floating Illustration Container
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f)
                    .offset(y = floatOffset.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background blur glow element
                Box(
                    modifier = Modifier
                        .fillMaxSize(1.1f)
                        .blur(80.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            shape = CircleShape
                        )
                )

                // Drop shadow Box (soft-glow-indigo: 0px 4px 24px rgba(79, 70, 229, 0.08))
                Box(
                    modifier = Modifier
                        .size(spacing.avatarSize)
                        .offset(y = 4.dp)
                        .blur(24.dp)
                        .background(
                            color = Color(0x144F46E5),
                            shape = CircleShape
                        )
                )

                // Central Visual AI Avatar Orb
                Box(
                    modifier = Modifier
                        .size(spacing.avatarSize)
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(1.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    // AI Companion Orb Image
                    Image(
                        painter = painterResource(id = R.drawable.img_onboarding_ai),
                        contentDescription = "AI companion glowing orb representation",
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.9f),
                        contentScale = ContentScale.Crop
                    )

                    // Real-time Voice Waves overlay at the bottom
                    VoiceWaveAnimation(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                    )
                }

                // User chat bubble (floating bottom-left)
                ChatBubble(
                    message = "I'm feeling a bit overwhelmed...",
                    isUser = true,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-16).dp, y = (-48).dp)
                        .width(180.dp)
                )

                // AI chat bubble (floating top-right)
                ChatBubble(
                    message = "How are you feeling today?",
                    isUser = false,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 16.dp, y = 48.dp)
                        .width(180.dp)
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
                    text = "Your AI Mind Companion.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.025).em // -2.5% letter spacing
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Chat naturally, use your voice, and receive personalized wellness support whenever you need it.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 26.sp // leading-relaxed (1.625x)
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
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
                .padding(top = 24.dp, bottom = 48.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.stackLg)
            ) {
                // Page Indicator Dots (Step 3 of 3)
                PageIndicatorDots(
                    totalPages = 3,
                    currentPage = 2
                )

                // CTA Cluster Stack
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrimaryGradientButton(
                        text = "Get Started",
                        onClick = onGetStartedClick,
                        shape = CircleShape
                    )
                    SecondaryTonalButton(
                        text = "Learn more",
                        onClick = onLearnMoreClick,
                        shape = CircleShape
                    )
                }
            }
        }
    }
}
