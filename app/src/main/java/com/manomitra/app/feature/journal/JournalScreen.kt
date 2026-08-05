package com.manomitra.app.feature.journal

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manomitra.app.R
import com.manomitra.app.core.components.BottomTab
import com.manomitra.app.core.components.ManomitraBottomNavigation
import com.manomitra.app.core.components.PrimaryGradientButton
import com.manomitra.app.core.theme.spacing

/**
 * JournalScreen
 *
 * Implements the "Refined Daily Journaling Reflection" screen matching the Stitch design.
 */
@Composable
fun JournalScreen(
    onBackClick: () -> Unit,
    onHomeTabClick: () -> Unit,
    onCompanionTabClick: () -> Unit,
    onProfileTabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    var reflectionText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FB)) // f7f9fb base background
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top App Bar
        Surface(
            color = Color.White.copy(alpha = 0.8f),
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
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "Journal",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                IconButton(
                    onClick = { /* Calendar action */ },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF1F5F9), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar_month),
                        contentDescription = "Calendar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Scrollable content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, bottom = 80.dp) // Offset for header & bottom navigation
                .verticalScroll(scrollState)
                .padding(horizontal = spacing.containerMarginMobile, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Date Section
            Column {
                Text(
                    text = "Monday, Oct 24",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp
                    )
                )
                Text(
                    text = "Peaceful Evening",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1E)
                    )
                )
            }

            // AI Insight Card (Detected Mood)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.3f)),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "😊", fontSize = 32.sp)
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_auto_awesome),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Calm",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF191C1E)
                                    )
                                )
                            }
                            Text(
                                text = "Detected by Manomitra AI",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF777587)
                                )
                            )
                        }
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Today's Reflection Input Hero
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Reflection",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1E)
                            )
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit_note),
                            contentDescription = "Edit",
                            tint = Color(0xFF777587),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    TextField(
                        value = reflectionText,
                        onValueChange = { reflectionText = it },
                        placeholder = {
                            Text(
                                text = "How was your day today?",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF777587).copy(alpha = 0.8f)
                                )
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 208.dp)
                    )
                }
            }

            // Reflection Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "WORDS",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF777587))
                    )
                    Text(
                        text = "248",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TIME",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF777587))
                    )
                    Text(
                        text = "4 min",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "STREAK",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF777587))
                    )
                    Text(
                        text = "12 days",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            // AI Insight Feedback Banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_auto_awesome),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "AI REFLECTION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                    Text(
                        text = "\"You've had a peaceful day. Focusing on gratitude will help maintain this balance.\"",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF191C1E),
                            fontStyle = FontStyle.Italic
                        )
                    )
                }
            }

            // Voice & Illustration grid-like row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Voice Journal Button
                Button(
                    onClick = { /* Voice journal trigger */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(144.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_mic),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Voice Journal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Serene Illustration Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(144.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFECEEF0))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_serene_journal),
                        contentDescription = "Serene Meditation Illustration",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // AI Prompts Carousel
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Need inspiration?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1E)
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

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
                            .size(width = 240.dp, height = 140.dp)
                            .border(1.dp, Color(0xFFD0BCFF).copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "😊", fontSize = 24.sp)
                            Text(
                                text = "What made you smile today?",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }

                    // Card 2
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF006A7C).copy(alpha = 0.1f) // tertiary
                        ),
                        modifier = Modifier
                            .size(width = 240.dp, height = 140.dp)
                            .border(1.dp, Color(0xFFACEDFF).copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "🧠", fontSize = 24.sp)
                            Text(
                                text = "What challenged you today?",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF004E5C)
                                )
                            )
                        }
                    }

                    // Card 3
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier
                            .size(width = 240.dp, height = 140.dp)
                            .border(1.dp, Color(0xFFC3C0FF).copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "❤️", fontSize = 24.sp)
                            Text(
                                text = "What are you grateful for?",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }

            PrimaryGradientButton(
                text = "Save Journal Entry",
                onClick = onBackClick,
                showArrow = false,
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Bottom Navigation Bar
        ManomitraBottomNavigation(
            currentTab = BottomTab.JOURNAL,
            onHomeClick = onHomeTabClick,
            onCompanionClick = onCompanionTabClick,
            onJournalClick = { },
            onProfileClick = onProfileTabClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
