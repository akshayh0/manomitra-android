package com.manomitra.app.feature.mood

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manomitra.app.R
import com.manomitra.app.core.components.BottomTab
import com.manomitra.app.core.components.ManomitraBottomNavigation
import com.manomitra.app.core.theme.spacing

/**
 * MoodTrackerScreen
 *
 * Implements the "Refined Mood Tracker Dashboard" screen matching the Stitch design.
 */
@Composable
fun MoodTrackerScreen(
    onBackClick: () -> Unit,
    onHomeTabClick: () -> Unit,
    onCompanionTabClick: () -> Unit,
    onJournalTabClick: () -> Unit,
    onProfileTabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()
    var isDarkMode by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulseDot")
    val dotScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDarkMode) Color(0xFF191C1E) else Color(0xFFF7F9FB)) // Light/Dark mode simulation
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header
        Surface(
            color = if (isDarkMode) Color(0xFF191C1E).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.TopCenter)
                .border(1.dp, Color(0xFF4F46E5).copy(alpha = 0.05f))
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bubble_chart),
                            contentDescription = "Menu Chart",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "Manomitra",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Dark Mode Toggle
                    IconButton(
                        onClick = { isDarkMode = !isDarkMode },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFECEEF0), CircleShape)
                    ) {
                        Text(
                            text = if (isDarkMode) "☀️" else "🌙",
                            fontSize = 16.sp
                        )
                    }

                    // Profile Icon
                    IconButton(
                        onClick = onProfileTabClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFECEEF0), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_face),
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Scrollable content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, bottom = 80.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = spacing.containerMarginMobile, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Wellness Score Hero Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "💙", fontSize = 18.sp)
                            Text(
                                text = "WELLNESS SCORE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.9f),
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "82",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 48.sp
                                )
                            )
                            Text(
                                text = "/ 100",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Text(
                            text = "You're making steady progress.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                    }
                }
            }

            // Screen Header Text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mood Tracker",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF191C1E)
                    )
                )

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFECEEF0),
                    border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "😊", fontSize = 14.sp)
                        Text(
                            text = "Calm Today",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Weekly Graph Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isDarkMode) Color(0xFF2D3133) else Color.White,
                border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weekly Trend",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color.White else Color(0xFF191C1E)
                            )
                        )
                        Text(
                            text = "LAST 7 DAYS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF777587),
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    // Bezier sparkline canvas representation
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        val canvasSize = this.size
                        val gridLineY1 = canvasSize.height * 0.2f
                        val gridLineY2 = canvasSize.height * 0.7f

                        // Grid lines
                        drawLine(
                            color = Color(0xFFE2E8F0).copy(alpha = 0.4f),
                            start = Offset(0f, gridLineY1),
                            end = Offset(canvasSize.width, gridLineY1),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = Color(0xFFE2E8F0).copy(alpha = 0.4f),
                            start = Offset(0f, gridLineY2),
                            end = Offset(canvasSize.width, gridLineY2),
                            strokeWidth = 1.dp.toPx()
                        )

                        // Path calculation
                        val path = Path().apply {
                            moveTo(0f, canvasSize.height * 0.6f)
                            cubicTo(
                                canvasSize.width * 0.2f, canvasSize.height * 0.7f,
                                canvasSize.width * 0.35f, canvasSize.height * 0.25f,
                                canvasSize.width * 0.5f, canvasSize.height * 0.3f
                            )
                            cubicTo(
                                canvasSize.width * 0.65f, canvasSize.height * 0.35f,
                                canvasSize.width * 0.75f, canvasSize.height * 0.85f,
                                canvasSize.width * 0.85f, canvasSize.height * 0.8f
                            )
                            cubicTo(
                                canvasSize.width * 0.92f, canvasSize.height * 0.75f,
                                canvasSize.width * 0.96f, canvasSize.height * 0.25f,
                                canvasSize.width, canvasSize.height * 0.15f
                            )
                        }

                        // Drawing Bezier line
                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF3525CD), Color(0xFF6B38D4))
                            ),
                            style = Stroke(width = 4.dp.toPx())
                        )

                        // Glowing indigo dot at today index
                        val todayDotOffset = Offset(canvasSize.width, canvasSize.height * 0.15f)
                        drawCircle(
                            color = Color(0xFF3525CD).copy(alpha = 0.3f),
                            radius = 12.dp.toPx() * dotScale,
                            center = todayDotOffset
                        )
                        drawCircle(
                            color = Color(0xFF3525CD),
                            radius = 6.dp.toPx(),
                            center = todayDotOffset
                        )
                    }

                    // Weekly days row labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Today")
                        days.forEach { day ->
                            val isToday = day == "Today"
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isToday) MaterialTheme.colorScheme.primary else Color(0xFF777587)
                                )
                            )
                        }
                    }
                }
            }

            // AI Insights Swipeable Carousel Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_auto_awesome),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "AI Insights",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color(0xFF191C1E)
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card 1
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier
                            .size(width = 280.dp, height = 136.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "✨ Your sleep improved.", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            Text(text = "Consistent 8 hours this week.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
                        }
                    }

                    // Card 2
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF006A7C).copy(alpha = 0.1f) // tertiary
                        ),
                        modifier = Modifier
                            .size(width = 280.dp, height = 136.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "✨ Journaling reduced stress.", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF004E5C))
                            Text(text = "Evening sessions are working well.", fontSize = 13.sp, color = Color(0xFF004E5C).copy(alpha = 0.8f))
                        }
                    }

                    // Card 3
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier
                            .size(width = 280.dp, height = 136.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "✨ Weekend mood is higher.", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(text = "Social activities boosting scores.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            // Monthly Calendar Section
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isDarkMode) Color(0xFF2D3133) else Color.White,
                border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monthly Calendar",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkMode) Color.White else Color(0xFF191C1E)
                            )
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar_month),
                            contentDescription = null,
                            tint = Color(0xFF777587),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Weekdays headers
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        val weekdays = listOf("M", "T", "W", "T", "F", "S", "S")
                        weekdays.forEach { w ->
                            Text(
                                text = w,
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF777587)),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Calendar days rows (mocking days 1 to 11 for the current month)
                    val daysColors = listOf(
                        Color(0xFFEEFBF7), // calm
                        Color(0xFFE2DFFF), // happy
                        Color(0xFFECEEF0), // neutral
                        Color(0xFFFFDAD6), // stressed
                        Color(0xFFEEFBF7),
                        Color(0xFFFFDAD6),
                        Color(0xFFE9DDFF)
                    )
                    val textColors = listOf(
                        Color(0xFF14B8A6), Color(0xFF3525CD), Color(0xFF191C1E),
                        Color(0xFFBA1A1A), Color(0xFF14B8A6), Color(0xFFBA1A1A), Color(0xFF6B38D4)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        for (i in 1..7) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(daysColors[i - 1]),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$i",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColors[i - 1]
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        for (i in 8..11) {
                            val isToday = i == 11
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isToday) Color.White else Color(0xFFECEEF0))
                                    .border(
                                        width = if (isToday) 2.dp else 0.dp,
                                        color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$i",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) MaterialTheme.colorScheme.primary else Color(0xFF191C1E)
                                )
                            }
                        }
                        // Fillers for remaining calendar cells
                        for (i in 12..14) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    // Color Legend Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(12.dp).background(Color(0xFF14B8A6), CircleShape))
                            Text(text = "Calm", style = MaterialTheme.typography.labelSmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(12.dp).background(Color(0xFF3525CD), CircleShape))
                            Text(text = "Happy", style = MaterialTheme.typography.labelSmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(12.dp).background(Color(0xFF777587), CircleShape))
                            Text(text = "Neutral", style = MaterialTheme.typography.labelSmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(12.dp).background(Color(0xFFBA1A1A), CircleShape))
                            Text(text = "Stressed", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Achievements Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Achievements",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF191C1E)
                    )
                )

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Item 1
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFECEEF0)),
                        modifier = Modifier.size(width = 140.dp, height = 120.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🏆", fontSize = 28.sp)
                            Text(text = "7-Day Calm", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Streak", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                        }
                    }

                    // Item 2
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFECEEF0)),
                        modifier = Modifier.size(width = 140.dp, height = 120.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📖", fontSize = 28.sp)
                            Text(text = "20 Journal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Entries", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                        }
                    }

                    // Item 3
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFECEEF0)),
                        modifier = Modifier.size(width = 140.dp, height = 120.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🎙", fontSize = 28.sp)
                            Text(text = "10 Voice", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Sessions", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                        }
                    }
                }
            }

            // Refresh Recommendations Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Recommended for You",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) Color.White else Color(0xFF191C1E)
                    )
                )

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Item 1
                    Surface(
                        onClick = { /* Breathing exercise */ },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.3f)),
                        modifier = Modifier.size(width = 180.dp, height = 128.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFEEFBF7), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_air),
                                    contentDescription = null,
                                    tint = Color(0xFF14B8A6),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(text = "🌬 Try Breathing", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "4 minute session", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                            }
                        }
                    }

                    // Item 2
                    Surface(
                        onClick = { onJournalTabClick() },
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.3f)),
                        modifier = Modifier.size(width = 180.dp, height = 128.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_edit_note),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(text = "📖 Write Journal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Reflection time", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation Bar
        ManomitraBottomNavigation(
            currentTab = null,
            onHomeClick = onHomeTabClick,
            onCompanionClick = onCompanionTabClick,
            onJournalClick = onJournalTabClick,
            onProfileClick = onProfileTabClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
