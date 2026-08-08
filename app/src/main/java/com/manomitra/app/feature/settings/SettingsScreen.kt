package com.manomitra.app.feature.settings

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manomitra.app.R
import com.manomitra.app.core.settings.AppSettings
import com.manomitra.app.core.theme.spacing
import com.manomitra.app.core.voice.VoiceSettings
import com.manomitra.app.feature.chat.AIConfig
import com.manomitra.app.feature.chat.AIProvider

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit = {},
    authViewModel: com.manomitra.app.auth.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Load persisted settings
    var appLanguage by remember { mutableStateOf(AppSettings.getAppLanguage(context)) }
    var activeProvider by remember { mutableStateOf(AppSettings.getAIProvider(context)) }
    var voiceLanguage by remember { mutableStateOf(AppSettings.getVoiceMode(context)) }
    var notificationsEnabled by remember { mutableStateOf(AppSettings.isNotificationsEnabled(context)) }

    var isKannadaTtsAvailable by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        var ttsInstance: android.speech.tts.TextToSpeech? = null
        ttsInstance = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                val result = ttsInstance?.isLanguageAvailable(java.util.Locale("kn", "IN"))
                isKannadaTtsAvailable = (result != android.speech.tts.TextToSpeech.LANG_MISSING_DATA && 
                                          result != android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED)
                ttsInstance?.shutdown()
            }
        }
    }

    // Dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // AI Model Provider Selection
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_smart_toy),
                                    contentDescription = null,
                                    tint = Color(0xFF464555),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(text = "AI Model Provider", style = MaterialTheme.typography.bodyLarge)
                            }
                            Surface(
                                color = Color(0xFFECEEF0),
                                shape = CircleShape,
                                modifier = Modifier.fillMaxWidth().height(40.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    val providers = listOf("Groq", "Gemini")
                                    providers.forEach { providerName ->
                                        val isSelected = (providerName == "Groq" && activeProvider == AIProvider.GROQ) ||
                                                (providerName == "Gemini" && activeProvider == AIProvider.GEMINI)
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clip(CircleShape)
                                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                                .clickable {
                                                    val newProvider = if (providerName == "Groq") AIProvider.GROQ else AIProvider.GEMINI
                                                    activeProvider = newProvider
                                                    AIConfig.setProvider(context, newProvider)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = providerName,
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

            // Voice Settings Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Voice Settings",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = if (isKannadaTtsAvailable) "Kannada TTS: Available" else "Kannada TTS: Unavailable",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isKannadaTtsAvailable) Color(0xFF1E7B34) else Color(0xFFBA1A1A)
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Voice Recognition Language Selection
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_speed),
                                    contentDescription = null,
                                    tint = Color(0xFF464555),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(text = "Voice Recognition Language", style = MaterialTheme.typography.bodyLarge)
                            }
                            Surface(
                                color = Color(0xFFECEEF0),
                                shape = CircleShape,
                                modifier = Modifier.fillMaxWidth().height(40.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    val voiceModes = listOf("Auto", "English", "Kannada")
                                    voiceModes.forEach { mode ->
                                        val isSelected = voiceLanguage == mode
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clip(CircleShape)
                                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                                .clickable {
                                                    voiceLanguage = mode
                                                    AppSettings.setVoiceMode(context, mode)
                                                    VoiceSettings.setSelectedLanguage(context, mode)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = mode,
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
                                    tint = Color(0xFF464555),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(text = "App Notifications", style = MaterialTheme.typography.bodyLarge)
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    notificationsEnabled = enabled
                                    AppSettings.setNotificationsEnabled(context, enabled)
                                }
                            )
                        }
                    }
                }
            }

            // Language Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "App Language",
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
                    val languages = listOf("Auto", "English", "Kannada")
                    languages.forEach { lang ->
                        val isSelected = appLanguage == lang
                        Button(
                            onClick = {
                                appLanguage = lang
                                AppSettings.setAppLanguage(context, lang)
                                VoiceSettings.setSelectedLanguage(context, lang)
                            },
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

            // Privacy & Data Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Privacy & Data",
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
                        // Logout
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLogoutClick() }
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
                                    tint = Color(0xFF464555),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(text = "Logout", style = MaterialTheme.typography.bodyLarge)
                            }
                        }

                        HorizontalDivider(color = Color(0xFFC7C4D8).copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))

                        // Delete Account
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDeleteDialog = true }
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
                                    tint = Color(0xFFBA1A1A),
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Delete Account & Data",
                                    style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFBA1A1A))
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Delete Account Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    showDeleteDialog = false
                    deleteError = null
                }
            },
            title = { Text(text = "Delete Account") },
            text = {
                Text(
                    text = if (deleteError == "REQUIRES_RECENT_LOGIN") {
                        "For security, deleting your account requires a recent login. Please log out, sign in again, and retry."
                    } else if (deleteError != null) {
                        "An error occurred: $deleteError"
                    } else {
                        "Delete your Manomitra account and data?"
                    }
                )
            },
            confirmButton = {
                if (deleteError == "REQUIRES_RECENT_LOGIN") {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        deleteError = null
                    }) {
                        Text("OK")
                    }
                } else {
                    Button(
                        onClick = {
                            isDeleting = true
                            authViewModel.deleteAccount { result ->
                                isDeleting = false
                                result.onSuccess {
                                    showDeleteDialog = false
                                    // Clear local preferences
                                    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                                    if (uid != null) {
                                        context.getSharedPreferences("manomitra_settings_$uid", Context.MODE_PRIVATE).edit().clear().apply()
                                    }
                                    onLogoutClick()
                                }.onFailure { exception ->
                                    deleteError = exception.message ?: "Failed to delete account"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A)),
                        enabled = !isDeleting
                    ) {
                        Text(if (isDeleting) "Deleting..." else "Delete", color = Color.White)
                    }
                }
            },
            dismissButton = {
                if (deleteError == null && !isDeleting) {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}
