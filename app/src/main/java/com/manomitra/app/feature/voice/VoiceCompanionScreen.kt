package com.manomitra.app.feature.voice

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manomitra.app.R
import com.manomitra.app.core.theme.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

/**
 * VoiceState
 *
 * Defines the state machine for the voice conversation session.
 */
enum class VoiceState(val label: String, val emoji: String) {
    IDLE("Idle", "💤"),
    LISTENING("Listening...", "🎙"),
    TRANSCRIBING("Transcribing...", "⚡"),
    THINKING("Thinking...", "🧠"),
    SPEAKING("Speaking...", "💬"),
    ERROR("Error", "⚠️")
}

/**
 * VoiceCompanionScreen
 *
 * Implements the "Refined Voice Companion Flagship" screen.
 * Features a local demo state machine (Idle -> Listening -> Thinking -> Speaking),
 * an animated canvas microphone orb matching WebGL shader specifications,
 * atmospheric expanding sound rings, dialogue transcripts, and call control footers.
 */
@Composable
fun VoiceCompanionScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VoiceCompanionViewModel = viewModel()
) {
    val spacing = MaterialTheme.spacing
    val context = LocalContext.current

    val permission = android.Manifest.permission.RECORD_AUDIO
    var hasPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                permission
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    var showPermissionDeniedCard by remember { mutableStateOf(false) }

    val recordAudioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasPermission = isGranted
            showPermissionDeniedCard = !isGranted
        }
    )

    val currentState = viewModel.currentState
    val isMuted = viewModel.isMuted
    val isSpeakerOn = viewModel.isSpeakerOn
    val userTranscript = viewModel.userTranscript.ifEmpty { "I'm listening. Speak your thoughts..." }
    val aiTranscript = viewModel.aiTranscript.ifEmpty { "I am your warm and empathetic companion. Ask me anything." }

    val isSessionActive = currentState != VoiceState.IDLE && currentState != VoiceState.ERROR
    var showExitDialog by remember { mutableStateOf(false) }

    androidx.activity.compose.BackHandler(enabled = isSessionActive) {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "End voice session?") },
            text = { Text(text = "Leaving this screen will terminate your active call session with your companion.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        viewModel.shutdown()
                        onBackClick()
                    }
                ) {
                    Text("End session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Continue")
                }
            }
        )
    }

    var elapsedSeconds by remember { mutableStateOf(0) }

    // Running session timer
    LaunchedEffect(true) {
        while (true) {
            delay(1000)
            elapsedSeconds++
        }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            viewModel.startListening(context)
        } else {
            recordAudioPermissionLauncher.launch(permission)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.shutdown()
        }
    }

    // Infinite transition for orb pulsing
    val infiniteTransition = rememberInfiniteTransition(label = "voiceOrb")
    val pulseSize by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (currentState) {
                    VoiceState.IDLE -> 3000
                    VoiceState.LISTENING -> 1500
                    VoiceState.TRANSCRIBING -> 400
                    VoiceState.THINKING -> 600
                    VoiceState.SPEAKING -> 1000
                    VoiceState.ERROR -> 2000
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseSize"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    // Format timer
    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeLabel = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Navigation Bar
        Surface(
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
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
                    IconButton(
                        onClick = {
                            if (isSessionActive) {
                                showExitDialog = true
                            } else {
                                onBackClick()
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF3525CD)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_voice_avatar),
                            contentDescription = "AI Companion Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column {
                            Text(
                                text = "Voice Companion",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = currentState.emoji,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = currentState.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF464555)
                                    )
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = { /* Settings action */ },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF1F5F9), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "Settings",
                        tint = Color(0xFF3525CD),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Main content column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, bottom = 220.dp) // Offsets for headers & controls footer
                .padding(horizontal = spacing.containerMarginMobile),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Privacy Note Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF2F4F6),
                border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = null,
                        tint = Color(0xFF464555),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Your conversations are private and processed securely.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF464555),
                            fontSize = 13.sp
                        )
                    )
                }
            }

            if (showPermissionDeniedCard) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFDAD6),
                    border = BorderStroke(1.dp, Color(0xFFBA1A1A).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mic_off),
                            contentDescription = null,
                            tint = Color(0xFFBA1A1A),
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Microphone permission is required to speak with the Voice Companion. Please allow access to continue.",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF410002),
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "TAP HERE TO RETRY",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFBA1A1A),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.clickable {
                                    recordAudioPermissionLauncher.launch(permission)
                                }
                            )
                        }
                    }
                }
            }

            // Orb Animation Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Expanding Ring 1
                Box(
                    modifier = Modifier
                        .size(360.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = when (currentState) {
                                    VoiceState.IDLE -> 0.02f
                                    VoiceState.LISTENING -> 0.05f * pulseSize
                                    else -> 0.03f
                                }
                            ),
                            shape = CircleShape
                        )
                )

                // Expanding Ring 2
                Box(
                    modifier = Modifier
                        .size(420.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = when (currentState) {
                                    VoiceState.IDLE -> 0.01f
                                    VoiceState.LISTENING -> 0.03f * (2f - pulseSize)
                                    else -> 0.02f
                                }
                            ),
                            shape = CircleShape
                        )
                )

                // Main Shader Orb
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clickable {
                            if (!hasPermission) {
                                recordAudioPermissionLauncher.launch(permission)
                            } else if (currentState == VoiceState.LISTENING) {
                                viewModel.stopListening()
                            } else {
                                viewModel.startListening(context)
                            }
                        }
                        .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                        .drawBehind {
                            val canvasSize = this.size
                            // Base color definitions
                            val indigo = Color(0xFF4F46E5)
                            val teal = Color(0xFF14B8A6)
                            val lightIndigo = Color(0xFFE0E3E5)

                            // 1. Draw dynamic state radial gradients
                            val accentColor = when (currentState) {
                                VoiceState.IDLE -> indigo.copy(alpha = 0.5f)
                                VoiceState.LISTENING -> teal.copy(alpha = 0.7f)
                                VoiceState.TRANSCRIBING -> Color(0xFFF59E0B).copy(alpha = 0.7f)
                                VoiceState.THINKING -> teal
                                VoiceState.SPEAKING -> indigo
                                VoiceState.ERROR -> Color(0xFFBA1A1A)
                            }

                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFF7F9FB), lightIndigo)
                                )
                            )

                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(accentColor.copy(alpha = 0.25f), Color.Transparent),
                                    center = Offset(canvasSize.width * 0.5f, canvasSize.height * 0.5f),
                                    radius = canvasSize.width * 0.5f * pulseSize
                                ),
                                radius = canvasSize.width * 0.5f * pulseSize,
                                center = Offset(canvasSize.width * 0.5f, canvasSize.height * 0.5f)
                            )

                            // 2. Draw speaking sine waves inside orb
                            if (currentState == VoiceState.SPEAKING) {
                                val wavePath = Path().apply {
                                    val waveMid = canvasSize.height * 0.5f
                                    moveTo(0f, waveMid)
                                    for (x in 0..canvasSize.width.toInt() step 5) {
                                        val angleRad = Math.toRadians((x * 2.0 + wavePhase).toDouble()).toFloat()
                                        val y = waveMid + (canvasSize.height * 0.15f) * pulseSize * kotlin.math.sin(angleRad)
                                        lineTo(x.toFloat(), y)
                                    }
                                }
                                drawPath(
                                    path = wavePath,
                                    color = Color.White,
                                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            // 3. Draw thinking expansion waves inside orb
                            if (currentState == VoiceState.THINKING) {
                                drawCircle(
                                    color = teal.copy(alpha = 0.15f),
                                    radius = canvasSize.width * 0.35f * (2f - pulseSize),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                        .background(Color.White)
                ) {
                    // Small visual indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(72.dp)
                            .background(Color.White.copy(alpha = 0.3f), CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (currentState) {
                                VoiceState.IDLE -> "💤"
                                VoiceState.LISTENING -> "🎙"
                                VoiceState.TRANSCRIBING -> "⚡"
                                VoiceState.THINKING -> "🧠"
                                VoiceState.SPEAKING -> "🔊"
                                VoiceState.ERROR -> "⚠️"
                            },
                            fontSize = 32.sp
                        )
                    }
                }
            }

            // Dialogue transcript section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // User statement card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.3f)),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(Color(0xFF777587).copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "👤", fontSize = 10.sp)
                                }
                                Text(
                                    text = "You",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF464555)
                                    )
                                )
                            }
                            Text(
                                text = timeLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFF777587),
                                    fontSize = 10.sp
                                )
                            )
                        }
                        Text(
                            text = "\"$userTranscript\"",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF191C1E),
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // AI feedback card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "✨", fontSize = 12.sp)
                                Text(
                                    text = "Mind Companion",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                            Text(
                                text = timeLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                            )
                        }
                        Text(
                            text = "\"$aiTranscript\"",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF191C1E),
                                fontStyle = FontStyle.Italic
                            )
                        )
                    }
                }
            }
        }

        // Bottom Controls interface
        Surface(
            color = Color.White.copy(alpha = 0.9f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .border(1.dp, Color(0xFFE2E8F0).copy(alpha = 0.3f), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                // Drag handle bar
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(6.dp)
                        .background(Color(0xFFECEEF0), CircleShape)
                )

                // Call Controls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Control: Mute
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.clickable {
                            if (hasPermission) {
                                viewModel.toggleMute(context)
                            } else {
                                recordAudioPermissionLauncher.launch(permission)
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    if (isMuted) Color(0xFFFFDAD6) else Color(0xFFECEEF0),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isMuted) R.drawable.ic_mic_off else R.drawable.ic_mic
                                ),
                                contentDescription = "Mute",
                                tint = if (isMuted) Color(0xFFBA1A1A) else Color(0xFF191C1E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = if (isMuted) "Unmute" else "Mute",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1E)
                            )
                        )
                    }

                    // Center: Mic orb toggle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            // Pulsing glow background
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .graphicsLayer { scaleX = pulseSize; scaleY = pulseSize }
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), CircleShape)
                            )
                            IconButton(
                                onClick = {
                                    if (!hasPermission) {
                                        recordAudioPermissionLauncher.launch(permission)
                                    } else if (currentState == VoiceState.LISTENING) {
                                        viewModel.stopListening()
                                    } else {
                                        viewModel.startListening(context)
                                    }
                                },
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_mic),
                                    contentDescription = "Voice Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Text(
                            text = currentState.label.uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.5.sp
                            ),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Right Control: Speaker
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.clickable { viewModel.toggleSpeaker() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFECEEF0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isSpeakerOn) R.drawable.ic_volume_up else R.drawable.ic_volume_off
                                ),
                                contentDescription = "Speaker",
                                tint = Color(0xFF191C1E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Speaker",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191C1E)
                            )
                        )
                    }
                }

                // End call CTA button
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFDAD6).copy(alpha = 0.3f),
                        contentColor = Color(0xFFBA1A1A)
                    ),
                    border = BorderStroke(2.dp, Color(0xFFBA1A1A).copy(alpha = 0.2f)),
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_call_end),
                            contentDescription = null,
                            tint = Color(0xFFBA1A1A),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "END CONVERSATION",
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
