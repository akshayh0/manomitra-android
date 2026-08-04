package com.manomitra.app.feature.profile

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
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
 * ProfileScreen
 *
 * Implements the "Profile: Wellness Journey & Settings" screen matching the Stitch specifications.
 */
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHomeTabClick: () -> Unit,
    onCompanionTabClick: () -> Unit,
    onJournalTabClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()

    var isBiometricEnabled by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }
    var appearanceTheme by remember { mutableStateOf("Light") }

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

                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(40.dp)
                        .border(2.dp, Color(0xFFC3C0FF), CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_profile_large),
                        contentDescription = "Akshay Profile Photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
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
            // Profile Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier.size(104.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_profile_large),
                        contentDescription = null,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { /* Edit photo */ },
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit_note),
                            contentDescription = "Edit Profile Picture",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = "Akshay Kumar",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1E)
                    )
                )
                Text(
                    text = "akshay.wellness@email.com",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF464555)
                    )
                )

                OutlinedButton(
                    onClick = { /* Edit details */ },
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color(0xFFC7C4D8)),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Edit Profile",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Hero Card: Wellness Journey
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Wellness Journey",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF191C1E)
                                )
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Premium Member",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Stats Grid Layout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Stat 1: Wellness Score Circle
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(108.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF2F4F6)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    CircularProgressIndicator(
                                        progress = 0.82f,
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        strokeWidth = 3.dp
                                    )
                                    Text(
                                        text = "82",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 15.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "WELLNESS SCORE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = Color(0xFF777587),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        // Stat 2: Streak Fire
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(108.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF2F4F6)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = "🔥", fontSize = 20.sp)
                                    Text(
                                        text = "12",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF191C1E)
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "DAY STREAK",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = Color(0xFF777587),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Stat 3: Journal Entries
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(96.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF2F4F6)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "20",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF191C1E)
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "JOURNAL ENTRIES",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = Color(0xFF777587),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        // Stat 4: Voice Sessions
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(96.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF2F4F6)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "10",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF191C1E)
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "VOICE SESSIONS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        color = Color(0xFF777587),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = "You're making amazing progress.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.SemiBold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Achievements Row
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Achievements",
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
                            Box(
                                modifier = Modifier.size(48.dp).background(Color(0xFFFEF9C3), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎖", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "7 Day Calm", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Consistency", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
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
                            Box(
                                modifier = Modifier.size(48.dp).background(Color(0xFFDBEAFE), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "✍️", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "20 Journals", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Writer", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                        }
                    }

                    // Item 3 (Locked)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFECEEF0)),
                        modifier = Modifier
                            .size(width = 140.dp, height = 120.dp)
                            .background(Color.White.copy(alpha = 0.6f))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).background(Color(0xFFE2E8F0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎙", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Speaker", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF777587))
                            Text(text = "65% to next", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // Wellness Goals Section
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "My Wellness Goals",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF191C1E)
                        )
                    )

                    // Goal 1: Improve Sleep
                    Column(
                        modifier = Modifier
                            .background(Color(0xFFF2F4F6), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(text = "🌙", fontSize = 18.sp)
                                Text(text = "Improve Sleep", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            // Custom toggle simulator
                            Switch(
                                checked = true,
                                onCheckedChange = { }
                            )
                        }
                        LinearProgressIndicator(
                            progress = 0.8f,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = Color(0xFFC7C4D8).copy(alpha = 0.3f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                        )
                    }

                    // Goal 2: Reduce Stress
                    Column(
                        modifier = Modifier
                            .background(Color(0xFFF2F4F6), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(text = "🧘", fontSize = 18.sp)
                                Text(text = "Reduce Stress", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Switch(
                                checked = true,
                                onCheckedChange = { }
                            )
                        }
                        LinearProgressIndicator(
                            progress = 0.45f,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = Color(0xFFC7C4D8).copy(alpha = 0.3f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }

            // Quick Access list Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Quick Access",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1E)
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Item 1
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onJournalTabClick() }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "📖", fontSize = 18.sp)
                                }
                                Text(text = "Journal History", style = MaterialTheme.typography.bodyLarge)
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = Color(0xFF777587)
                            )
                        }

                        HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                        // Item 2
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Nav to Mood */ }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "😊", fontSize = 18.sp)
                                }
                                Text(text = "Mood History", style = MaterialTheme.typography.bodyLarge)
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = Color(0xFF777587)
                            )
                        }

                        HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                        // Item 3
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCompanionTabClick() }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFEEFBF7), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🎙", fontSize = 18.sp)
                                }
                                Text(text = "Voice History", style = MaterialTheme.typography.bodyLarge)
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

            // App Preferences
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "App Preferences",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1E)
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // Language selection grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Preferred Language",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF464555)),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val languages = listOf("English", "Hindi", "Kannada")
                        languages.forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            Button(
                                onClick = { selectedLanguage = lang },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color(0xFFECEEF0),
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF191C1E)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (isSelected) {
                                        Text(text = "✓", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(text = lang, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val languages = listOf("Tamil", "Telugu", "Malayalam")
                        languages.forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            Button(
                                onClick = { selectedLanguage = lang },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color(0xFFECEEF0),
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF191C1E)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (isSelected) {
                                        Text(text = "✓", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(text = lang, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Appearance tabs
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF464555)),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Surface(
                        color = Color(0xFFECEEF0),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val themes = listOf("Light", "Dark", "System")
                            themes.forEach { theme ->
                                val isSelected = appearanceTheme == theme
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color.White else Color.Transparent)
                                        .clickable { appearanceTheme = theme },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = theme,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF464555)
                                    )
                                }
                            }
                        }
                    }
                }

                // Privacy & Security options
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Privacy & Security",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF464555)),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                        tonalElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Biometric Lock
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
                                        painter = painterResource(id = R.drawable.ic_fingerprint),
                                        contentDescription = null,
                                        tint = Color(0xFF464555)
                                    )
                                    Text(text = "Biometric Lock", style = MaterialTheme.typography.bodyLarge)
                                }
                                Switch(
                                    checked = isBiometricEnabled,
                                    onCheckedChange = { isBiometricEnabled = it }
                                )
                            }

                            HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                            // Data Export
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
                                        painter = painterResource(id = R.drawable.ic_attach_file), // clip can represent data
                                        contentDescription = null,
                                        tint = Color(0xFF464555)
                                    )
                                    Text(text = "Data Export", style = MaterialTheme.typography.bodyLarge)
                                }
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chevron_right),
                                    contentDescription = null,
                                    tint = Color(0xFF777587)
                                )
                            }

                            HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                            // Delete Account
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
                                        painter = painterResource(id = R.drawable.ic_delete_forever),
                                        contentDescription = null,
                                        tint = Color(0xFFBA1A1A)
                                    )
                                    Text(text = "Delete Account", style = MaterialTheme.typography.bodyLarge, color = Color(0xFFBA1A1A))
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
            }

            // Version info & Logout Action
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Manomitra Version 2.4.1 (Stable)",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFC7C4D8))
                )

                Button(
                    onClick = onLogoutClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFFBA1A1A)
                    ),
                    border = BorderStroke(2.dp, Color(0xFFBA1A1A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .widthIn(min = 280.dp)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Log Out",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Bottom Navigation Bar
        Surface(
            color = Color.White.copy(alpha = 0.9f),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
                .border(1.dp, Color(0xFFECEEF0), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onHomeTabClick() }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color(0xFF777587)
                    )
                    Text(text = "Home", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                }

                // Companion Tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onCompanionTabClick() }
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_smart_toy),
                        contentDescription = "Companion",
                        tint = Color(0xFF777587)
                    )
                    Text(text = "Companion", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                }

                // Journal Tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onJournalTabClick() }
                        .padding(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit_note),
                        contentDescription = "Journal",
                        tint = Color(0xFF777587)
                    )
                    Text(text = "Journal", style = MaterialTheme.typography.labelSmall, color = Color(0xFF777587))
                }

                // Profile Tab (Active)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
