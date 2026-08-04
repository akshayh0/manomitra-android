package com.manomitra.app.feature.settings

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manomitra.app.R
import com.manomitra.app.core.theme.spacing

/**
 * SettingsScreen
 *
 * Implements the "Settings: Preferences & Security" screen matching the Stitch design.
 */
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()

    // Interactive preferences states
    var dailyReminder by remember { mutableStateOf(true) }
    var journalReminder by remember { mutableStateOf(false) }
    var moodCheckReminder by remember { mutableStateOf(true) }
    var voiceSpeed by remember { mutableStateOf(1.0f) }
    var voiceGender by remember { mutableStateOf("Female") }
    var conversationMemory by remember { mutableStateOf(true) }
    var wellnessSuggestions by remember { mutableStateOf(true) }
    var appLanguage by remember { mutableStateOf("English") }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 24.sp
                        )
                    )
                }

                IconButton(
                    onClick = { /* More actions */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings), // fallback or we can use more_vert if it exists
                        contentDescription = "More Settings Options",
                        tint = Color(0xFF777587),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Scrollable settings items
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = spacing.containerMarginMobile, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Account Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Edit Profile item
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_person_edit),
                                    contentDescription = null,
                                    tint = Color(0xFF464555)
                                )
                                Text(text = "Edit Profile", style = MaterialTheme.typography.bodyLarge)
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = Color(0xFF777587)
                            )
                        }

                        HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                        // Change Password item
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_lock_reset),
                                    contentDescription = null,
                                    tint = Color(0xFF464555)
                                )
                                Text(text = "Change Password", style = MaterialTheme.typography.bodyLarge)
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = Color(0xFF777587)
                            )
                        }

                        HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                        // Connected accounts item
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_link),
                                    contentDescription = null,
                                    tint = Color(0xFF464555)
                                )
                                Text(text = "Connected Accounts", style = MaterialTheme.typography.bodyLarge)
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = Color(0xFF777587)
                            )
                        }
                    }
                }
            }

            // Notifications Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Daily Reminder
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_alarm),
                                    contentDescription = null,
                                    tint = Color(0xFF464555)
                                )
                                Text(text = "Daily Reminder", style = MaterialTheme.typography.bodyLarge)
                            }
                            Switch(
                                checked = dailyReminder,
                                onCheckedChange = { dailyReminder = it }
                            )
                        }

                        HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                        // Journal Reminder
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_edit_note),
                                    contentDescription = null,
                                    tint = Color(0xFF464555)
                                )
                                Text(text = "Journal Reminder", style = MaterialTheme.typography.bodyLarge)
                            }
                            Switch(
                                checked = journalReminder,
                                onCheckedChange = { journalReminder = it }
                            )
                        }

                        HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                        // Mood Check Reminder
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_face),
                                    contentDescription = null,
                                    tint = Color(0xFF464555)
                                )
                                Text(text = "Mood Check Reminder", style = MaterialTheme.typography.bodyLarge)
                            }
                            Switch(
                                checked = moodCheckReminder,
                                onCheckedChange = { moodCheckReminder = it }
                            )
                        }
                    }
                }
            }

            // Voice Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Voice",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Voice Language dropdown simulator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_smart_toy), // language flag or speed representing voice
                                    contentDescription = null,
                                    tint = Color(0xFF464555)
                                )
                                Column {
                                    Text(text = "Voice Language", style = MaterialTheme.typography.bodyLarge)
                                    Text(text = "English (US)", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                                }
                            }
                            Text(text = "▼", fontSize = 10.sp, color = Color(0xFF777587))
                        }

                        HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f))

                        // Voice Speed range slider
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_speed),
                                    contentDescription = null,
                                    tint = Color(0xFF464555)
                                )
                                Text(text = "Voice Speed", style = MaterialTheme.typography.bodyLarge)
                            }
                            Slider(
                                value = voiceSpeed,
                                onValueChange = { voiceSpeed = it },
                                valueRange = 0.5f..2.0f,
                                steps = 14,
                                colors = SliderDefaults.colors(
                                    activeTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                    thumbColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Slow", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                                Text(text = "Normal", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                                Text(text = "Fast", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                            }
                        }

                        HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f))

                        // Voice Gender options tabs
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_person_4),
                                    contentDescription = null,
                                    tint = Color(0xFF464555)
                                )
                                Text(text = "Voice Gender", style = MaterialTheme.typography.bodyLarge)
                            }

                            Surface(
                                color = Color(0xFFECEEF0),
                                shape = CircleShape,
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    val genders = listOf("Female", "Male", "Neutral")
                                    genders.forEach { gender ->
                                        val isSelected = voiceGender == gender
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clip(CircleShape)
                                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                                .clickable { voiceGender = gender },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = gender,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF464555)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // AI Preferences Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "AI Preferences",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Response Style item
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_smart_toy),
                                    contentDescription = null,
                                    tint = Color(0xFF464555)
                                )
                                Column {
                                    Text(text = "Response Style", style = MaterialTheme.typography.bodyLarge)
                                    Text(text = "Empathetic", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                                }
                            }
                            Text(text = "▼", fontSize = 10.sp, color = Color(0xFF777587))
                        }

                        HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                        // Conversation Memory item
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_memory),
                                    contentDescription = null,
                                    tint = Color(0xFF464555)
                                )
                                Column(modifier = Modifier.padding(end = 8.dp)) {
                                    Text(text = "Conversation Memory", style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        text = "Allows AI to remember past sessions for better context.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF777587)
                                    )
                                }
                            }
                            Switch(
                                checked = conversationMemory,
                                onCheckedChange = { conversationMemory = it }
                            )
                        }

                        HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                        // Wellness Suggestions item
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_lightbulb),
                                    contentDescription = null,
                                    tint = Color(0xFF464555)
                                )
                                Text(text = "Wellness Suggestions", style = MaterialTheme.typography.bodyLarge)
                            }
                            Switch(
                                checked = wellnessSuggestions,
                                onCheckedChange = { wellnessSuggestions = it }
                            )
                        }
                    }
                }
            }

            // Language Section horizontal scroll
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Language",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val languages = listOf("English", "Hindi", "Kannada", "Tamil", "Telugu", "Malayalam")
                    languages.forEach { lang ->
                        val isSelected = appLanguage == lang
                        Button(
                            onClick = { appLanguage = lang },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.White,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF464555)
                            ),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.5f)),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = lang, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (isSelected) {
                                    Text(text = "✓", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
