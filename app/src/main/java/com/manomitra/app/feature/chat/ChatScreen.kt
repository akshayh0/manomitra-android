package com.manomitra.app.feature.chat

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manomitra.app.R
import com.manomitra.app.core.theme.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Message
 *
 * Data model for conversation entries.
 */
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: String
)

/**
 * ChatScreen
 *
 * Implements the "Enhanced Mind Companion Chat" screen matching the Stitch specifications.
 * Includes a WebGL-inspired animated mesh background, conversational bubbles,
 * auto-scroll behaviors, mock delay typing animations, Suggested Prompt action chips,
 * and quick exercises panel overlays.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var textInput by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    var isTyping by remember { mutableStateOf(false) }

    // Sample initial helper responses
    val mockResponses = listOf(
        "I hear you. Work pressure can really take a toll. Have you tried taking a quick breathing break today?",
        "I'm here for you. It's completely okay to feel overwhelmed. Let's explore some calming exercises together.",
        "Take a slow breath. You don't have to navigate all of this alone. Tell me more, or we can try a quick reflection.",
        "Thank you for sharing that with me. Your feelings are valid. What do you think would help you feel most supported right now?"
    )

    fun getCurrentTime(): String {
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    }

    fun sendMessage(text: String) {
        if (text.trim().isEmpty()) return
        messages.add(Message(text = text, isUser = true, timestamp = getCurrentTime()))
        textInput = ""

        // Trigger AI response with a mock delay and typing indicator
        coroutineScope.launch {
            delay(800)
            isTyping = true
            delay(1500)
            isTyping = false
            val randomReply = mockResponses.random()
            messages.add(Message(text = randomReply, isUser = false, timestamp = getCurrentTime()))
        }
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty() || isTyping) {
            listState.animateScrollToItem(
                index = if (isTyping) messages.size else messages.size - 1
            )
        }
    }

    // Infinite animation transition for the wave/mesh backdrop
    val infiniteTransition = rememberInfiniteTransition(label = "meshBackdrop")
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase1"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Emulate WebGL wave color blend (Indigo #4F46E5 & Teal #14B8A6)
                val angleRad1 = Math.toRadians(phase1.toDouble()).toFloat()
                val offsetX1 = size.width * (0.5f + 0.3f * kotlin.math.cos(angleRad1))
                val offsetY1 = size.height * (0.5f + 0.3f * kotlin.math.sin(angleRad1))

                val offsetX2 = size.width * (0.5f + 0.2f * kotlin.math.sin(angleRad1 * 1.5f))
                val offsetY2 = size.height * (0.5f + 0.2f * kotlin.math.cos(angleRad1 * 1.5f))

                // Base surface fill (#F7F9FB)
                drawRect(Color(0xFFF7F9FB))

                // Indigo radial glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF4F46E5).copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(offsetX1, offsetY1),
                        radius = size.width * 0.9f
                    ),
                    radius = size.width * 0.9f,
                    center = Offset(offsetX1, offsetY1)
                )

                // Teal radial glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF14B8A6).copy(alpha = 0.06f), Color.Transparent),
                        center = Offset(offsetX2, offsetY2),
                        radius = size.width * 0.8f
                    ),
                    radius = size.width * 0.8f,
                    center = Offset(offsetX2, offsetY2)
                )
            }
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Chat Header
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
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF191C1E)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_face),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Manomitra",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF006A7C), CircleShape) // online status dot
                                )
                                Text(
                                    text = "Online • Ready to listen",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF464555)
                                    )
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = { /* Settings actions */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Scrollable Messages Area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, bottom = 220.dp), // Leaves room for overlay sheet and input footer
            contentPadding = PaddingValues(horizontal = spacing.containerMarginMobile, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Render Hero/Empty state block if conversation is empty
            if (messages.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Hi Akshay 👋",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 36.sp
                            )
                        )
                        Text(
                            text = "I'm here to listen.\nWhat's on your mind today?",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF464555).copy(alpha = 0.8f),
                                lineHeight = 28.sp
                            )
                        )

                        // Suggested Action Prompt Chips
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val prompts = listOf(
                                "I'm feeling stressed",
                                "I'm feeling sad",
                                "I'm anxious",
                                "I can't sleep",
                                "Work stress"
                            )
                            prompts.forEach { promptText ->
                                Surface(
                                    onClick = { sendMessage(promptText) },
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, Color(0xFFC7C4D8)),
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = promptText,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF464555)
                                        ),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Privacy Note Empty State Card
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(192.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)), RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.3f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_privacy),
                                contentDescription = "Privacy Shield",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text(
                            text = "\"Every conversation is private and judgment-free.\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                fontWeight = FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.widthIn(max = 240.dp)
                        )
                    }
                }
            }

            // Chat Messages rendering
            items(messages, key = { it.id }) { message ->
                if (message.isUser) {
                    // User Message Row
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 4.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 4.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF191C1E)
                                )
                            )
                        }
                        Text(
                            text = message.timestamp,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF464555).copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.padding(top = 4.dp, end = 4.dp)
                        )
                    }
                } else {
                    // AI Message Row
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 24.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 24.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                        Text(
                            text = message.timestamp,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF464555).copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                }
            }

            // Typing Indicator inside list
            if (isTyping) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier
                                .width(72.dp)
                                .height(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 3 Bouncing dots using transition animation spec
                            val typingTransition = rememberInfiniteTransition(label = "typingDots")
                            val offset1 by typingTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = keyframes {
                                        durationMillis = 1000
                                        0f at 0
                                        0.5f at 300
                                        1f at 600
                                    },
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "dot1"
                            )
                            val offset2 by typingTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = keyframes {
                                        durationMillis = 1000
                                        0f at 200
                                        0.5f at 500
                                        1f at 800
                                    },
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "dot2"
                            )
                            val offset3 by typingTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = keyframes {
                                        durationMillis = 1000
                                        0f at 400
                                        0.5f at 700
                                        1f at 1000
                                    },
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "dot3"
                            )

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(6.dp)
                                    .graphicsLayer { scaleX = 0.6f + 0.4f * offset1; scaleY = 0.6f + 0.4f * offset1 }
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(6.dp)
                                    .graphicsLayer { scaleX = 0.6f + 0.4f * offset2; scaleY = 0.6f + 0.4f * offset2 }
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(6.dp)
                                    .graphicsLayer { scaleX = 0.6f + 0.4f * offset3; scaleY = 0.6f + 0.4f * offset3 }
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        }
                    }
                }
            }
        }

        // Talk Naturally Voice Floating button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 152.dp)
        ) {
            Button(
                onClick = { sendMessage("Talk naturally activated.") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                shape = CircleShape,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
                modifier = Modifier.height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mic),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "Talk Naturally",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Quick Exercises Sheet Panel
        Surface(
            color = Color.White.copy(alpha = 0.9f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(136.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 76.dp) // Pinned just above input bar
                .border(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.3f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag handle bar
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(40.dp)
                        .height(5.dp)
                        .background(Color(0xFFE2DFFF), CircleShape)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Breathing Item
                    Column(
                        modifier = Modifier
                            .clickable { sendMessage("Breathing Exercise started.") }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFEEFBF7), RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFACEDFF).copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_air),
                                contentDescription = "Breathing",
                                tint = Color(0xFF14B8A6),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Breathing",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF464555)
                        )
                    }

                    // Journal Item
                    Column(
                        modifier = Modifier
                            .clickable { sendMessage("Daily Journal reflection opened.") }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit_note),
                                contentDescription = "Journal",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Journal",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF464555)
                        )
                    }

                    // Meditation Item
                    Column(
                        modifier = Modifier
                            .clickable { sendMessage("Meditation started.") }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                                .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_self_care),
                                contentDescription = "Meditation",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Meditation",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF464555)
                        )
                    }

                    // Emergency Item
                    Column(
                        modifier = Modifier
                            .clickable { sendMessage("SOS Support activated.") }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFFFDAD6), RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0xFFBA1A1A).copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_emergency),
                                contentDescription = "Emergency",
                                tint = Color(0xFFBA1A1A),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            text = "Emergency",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBA1A1A)
                        )
                    }
                }
            }
        }

        // Bottom Input Bar Footer
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .align(Alignment.BottomCenter)
                .border(1.dp, Color(0xFFE2DFFF))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.containerMarginMobile),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attach file clip button
                IconButton(
                    onClick = { /* Attachment action */ },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFF7F9FB), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_attach_file),
                        contentDescription = "Attach File",
                        tint = Color(0xFF777587),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Input Box field
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .background(Color(0xFFF2F4F6).copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (textInput.isEmpty()) {
                            Text(
                                text = "Share what's on your mind...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF777587).copy(alpha = 0.7f)
                            )
                        }
                        BasicTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF191C1E)
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    sendMessage(textInput)
                                }
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    IconButton(
                        onClick = { sendMessage("Voice input recorded.") },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mic),
                            contentDescription = "Voice Input",
                            tint = Color(0xFF777587),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Send Action Button
                IconButton(
                    onClick = { sendMessage(textInput) },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
