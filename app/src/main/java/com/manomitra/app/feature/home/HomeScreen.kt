package com.manomitra.app.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manomitra.app.R
import com.manomitra.app.core.theme.spacing

/**
 * HomeScreen
 *
 * Implements the "Polished Home Dashboard" screen matching the Stitch design specifications.
 * Features a glassmorphic top/bottom bar, personalized greetings, mood selectors with active highlight pulsing,
 * an AI Companion hero card with radial background mesh glows, quick calm actions, reflection/journal prompts,
 * a dynamic sparkline mood chart, and Material 3 design tokens.
 */
@Composable
fun HomeScreen(
    onChatClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onJournalClick: () -> Unit,
    onMoodClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()

    var selectedMood by remember { mutableStateOf("Calm") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)) // F8FAFC background
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = spacing.containerMarginMobile)
                .padding(top = 88.dp, bottom = 100.dp), // Offsets for fixed header & footer
            verticalArrangement = Arrangement.spacedBy(spacing.stackLg)
        ) {
            // Greeting Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Good Morning 👋",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                )
                Text(
                    text = "Akshay",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 32.sp
                    )
                )
                Text(
                    text = "\"The secret of getting ahead is getting started.\"",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF64748B)
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Mood Section
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "How are you feeling today?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val moods = listOf(
                            "Happy" to "😊",
                            "Calm" to "😌",
                            "Neutral" to "😐",
                            "Sad" to "😔",
                            "Stressed" to "😣"
                        )
                        moods.forEach { (moodName, emoji) ->
                            val isSelected = selectedMood == moodName
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        selectedMood = moodName
                                        onMoodClick()
                                    }
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        else Color.Transparent
                                    )
                                    .padding(vertical = 8.dp, horizontal = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = emoji,
                                    fontSize = if (isSelected) 36.sp else 30.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                                Text(
                                    text = moodName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF64748B)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Mind Companion Card (Hero)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .drawBehind {
                        // Ambient top-right glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent),
                                center = Offset(size.width * 0.9f, 0f),
                                radius = size.width * 0.6f
                            ),
                            radius = size.width * 0.6f,
                            center = Offset(size.width * 0.9f, 0f)
                        )
                        // Ambient bottom-left glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF6B38D4).copy(alpha = 0.3f), Color.Transparent),
                                center = Offset(size.width * 0.1f, size.height),
                                radius = size.width * 0.5f
                            ),
                            radius = size.width * 0.5f,
                            center = Offset(size.width * 0.1f, size.height)
                        )
                    }
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF4F46E5), Color(0xFF6B38D4))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_smart_toy),
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "AI COMPANION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Mind Companion",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.ic_auto_awesome),
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Always here to listen and guide you through your day.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier.widthIn(max = 240.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onChatClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF4F46E5)
                            ),
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "💬 Start Chat",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = onVoiceClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.2f),
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "🎤 Voice Companion",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Journal & Mood Trend Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Today's Journal Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_edit_note),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "DAILY REFLECTION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF94A3B8)
                                    )
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Today's Journal",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "Write your thoughts for today.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF64748B)
                                )
                            )
                        }

                        Button(
                            onClick = onJournalClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF8FAFC),
                                contentColor = Color(0xFF1E293B)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            Text(
                                text = "Write Now",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Mood Trend Section
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mood Trend",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_trending_up),
                                    contentDescription = null,
                                    tint = Color(0xFF14B8A6),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "THIS WEEK",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8)
                                )
                            )
                            Text(
                                text = "Improved 12% ↗",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF14B8A6)
                                )
                            )
                        }

                        // Line chart preview drawn on Canvas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val canvasSize = this.size
                                val chartPath = Path().apply {
                                    moveTo(0f, canvasSize.height * 0.8f)
                                    cubicTo(
                                        canvasSize.width * 0.2f, canvasSize.height * 0.75f,
                                        canvasSize.width * 0.35f, canvasSize.height * 0.8f,
                                        canvasSize.width * 0.5f, canvasSize.height * 0.45f
                                    )
                                    cubicTo(
                                        canvasSize.width * 0.65f, canvasSize.height * 0.15f,
                                        canvasSize.width * 0.8f, canvasSize.height * 0.25f,
                                        canvasSize.width * 1.0f, canvasSize.height * 0.1f
                                    )
                                }
                                drawPath(
                                    path = chartPath,
                                    color = Color(0xFF14B8A6),
                                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                                )
                                
                                // Gradient fill underneath path
                                val fillPath = Path().apply {
                                    addPath(chartPath)
                                    lineTo(canvasSize.width, canvasSize.height)
                                    lineTo(0f, canvasSize.height)
                                    close()
                                }
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF14B8A6).copy(alpha = 0.25f),
                                            Color(0xFF14B8A6).copy(alpha = 0f)
                                        )
                                    )
                                )
                                
                                // Last point dot indicator
                                drawCircle(
                                    color = Color(0xFF14B8A6),
                                    radius = 3.dp.toPx(),
                                    center = Offset(canvasSize.width, canvasSize.height * 0.1f)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val days = listOf("M", "T", "W", "T", "F", "S", "S")
                            days.forEach { day ->
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Quick Calm Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Quick Calm",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val calmItems = listOf(
                        Triple("Breathing", "🌬", Color(0xFF14B8A6)),
                        Triple("Meditation", "🧘", Color(0xFF4F46E5)),
                        Triple("Sleep Sounds", "🎵", Color(0xFF6B38D4))
                    )
                    calmItems.forEach { (title, emoji, color) ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onMoodClick() },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .border(1.dp, color.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                                    .background(color.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = emoji,
                                    fontSize = 32.sp
                                )
                            }
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF475569)
                                )
                            )
                        }
                    }
                }
            }

            // Today's Insight Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .drawBehind {
                        // Ambient glow top right
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF14B8A6).copy(alpha = 0.15f), Color.Transparent),
                                center = Offset(size.width * 0.9f, 0f),
                                radius = size.width * 0.4f
                            ),
                            radius = size.width * 0.4f,
                            center = Offset(size.width * 0.9f, 0f)
                        )
                    }
                    .background(Color(0xFF0F172A))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_auto_awesome),
                                    contentDescription = null,
                                    tint = Color(0xFF14B8A6),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "✨ TODAY'S INSIGHT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp,
                                    color = Color.White
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFACC15),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "\"Taking five deep breaths can lower stress in under a minute.\"",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                                lineHeight = 24.sp
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable { onSettingsClick() }
                        ) {
                            Text(
                                text = "Read More",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF14B8A6)
                                )
                            )
                            Text(
                                text = "→",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF14B8A6)
                                )
                            )
                        }
                    }

                    Text(
                        text = "POWERED BY MANOMITRA AI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.3f),
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }

        // Top App Bar Header
        Surface(
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.TopCenter)
                .border(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.containerMarginMobile),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_avatar),
                        contentDescription = "User profile picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                            .clickable { onProfileClick() },
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                            .padding(vertical = 4.dp, horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔥 7 Day Streak",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF1F5F9), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notifications),
                        contentDescription = "Notifications",
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Bottom Navigation Bar
        Surface(
            color = Color.White.copy(alpha = 0.92f),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .border(1.dp, Color(0xFFF1F5F9))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Nav Item (Active)
                Column(
                    modifier = Modifier.clickable { },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Companion Nav Item
                Column(
                    modifier = Modifier.clickable { onChatClick() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_smart_toy),
                        contentDescription = "Companion",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Companion",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Journal Nav Item
                Column(
                    modifier = Modifier.clickable { onJournalClick() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit_note),
                        contentDescription = "Journal",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Journal",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Profile Nav Item
                Column(
                    modifier = Modifier.clickable { onProfileClick() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
