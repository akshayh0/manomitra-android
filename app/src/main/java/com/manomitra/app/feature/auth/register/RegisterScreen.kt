package com.manomitra.app.feature.auth.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manomitra.app.R
import com.manomitra.app.core.components.PrimaryGradientButton
import com.manomitra.app.core.theme.spacing

/**
 * RegisterScreen
 *
 * Implements the "Register" account creation screen matching the Manomitra design system.
 * Includes name, email, password, and confirm password fields with inline M3 error states,
 * password visibility toggles, primary sign-up gradient action, and Google OAuth option.
 */
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val backgroundColor = MaterialTheme.colorScheme.background

    fun validateForm(): Boolean {
        var isValid = true

        if (name.trim().isEmpty()) {
            nameError = "Name cannot be empty"
            isValid = false
        } else {
            nameError = null
        }

        val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        if (email.trim().isEmpty()) {
            emailError = "Email cannot be empty"
            isValid = false
        } else if (!emailPattern.matches(email.trim())) {
            emailError = "Enter a valid email address"
            isValid = false
        } else {
            emailError = null
        }

        if (password.isEmpty()) {
            passwordError = "Password cannot be empty"
            isValid = false
        } else if (password.length < 8) {
            passwordError = "Password must be at least 8 characters"
            isValid = false
        } else {
            passwordError = null
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordError = "Confirm password cannot be empty"
            isValid = false
        } else if (confirmPassword != password) {
            confirmPasswordError = "Passwords do not match"
            isValid = false
        } else {
            confirmPasswordError = null
        }

        return isValid
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                // Top-left radial gradient (rgba(79, 70, 229, 0.05))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x0D4F46E5), Color.Transparent),
                        center = Offset(size.width * 0.1f, size.height * 0.2f),
                        radius = size.width * 0.8f
                    ),
                    radius = size.width * 0.8f,
                    center = Offset(size.width * 0.1f, size.height * 0.2f)
                )
                // Bottom-right radial gradient (rgba(0, 80, 95, 0.05))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x0D00505F), Color.Transparent),
                        center = Offset(size.width * 0.9f, size.height * 0.8f),
                        radius = size.width * 0.8f
                    ),
                    radius = size.width * 0.8f,
                    center = Offset(size.width * 0.9f, size.height * 0.8f)
                )
            }
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = spacing.containerMarginMobile, vertical = spacing.gutter),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Main Form Container max-width matching web-responsive mockup
            Column(
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Brand Section
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "Manomitra Logo",
                    modifier = Modifier
                        .size(96.dp)
                        .padding(bottom = spacing.stackMd),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Manomitra",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.025).sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "Join us and start your wellness journey.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = spacing.stackLg)
                )

                // Registration Form Card
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(spacing.stackLg),
                        verticalArrangement = Arrangement.spacedBy(spacing.stackMd)
                    ) {
                        // Name Field
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Name",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            
                            var isNameFocused by remember { mutableStateOf(false) }
                            val hasNameError = nameError != null
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .border(
                                        width = if (isNameFocused || hasNameError) 2.dp else 1.dp,
                                        color = when {
                                            hasNameError -> MaterialTheme.colorScheme.error
                                            isNameFocused -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.outlineVariant
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Person Icon",
                                    tint = if (hasNameError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    if (name.isEmpty()) {
                                        Text(
                                            text = "Enter your full name",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                        )
                                    }
                                    BasicTextField(
                                        value = name,
                                        onValueChange = { name = it },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Text,
                                            imeAction = ImeAction.Next
                                        ),
                                        cursorBrush = SolidColor(if (hasNameError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { isNameFocused = it.isFocused }
                                    )
                                }
                            }
                            if (hasNameError) {
                                Text(
                                    text = nameError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        // Email Field
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Email",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            
                            var isEmailFocused by remember { mutableStateOf(false) }
                            val hasEmailError = emailError != null
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .border(
                                        width = if (isEmailFocused || hasEmailError) 2.dp else 1.dp,
                                        color = when {
                                            hasEmailError -> MaterialTheme.colorScheme.error
                                            isEmailFocused -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.outlineVariant
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email Icon",
                                    tint = if (hasEmailError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    if (email.isEmpty()) {
                                        Text(
                                            text = "Enter your email",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                        )
                                    }
                                    BasicTextField(
                                        value = email,
                                        onValueChange = { email = it },
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Email,
                                            imeAction = ImeAction.Next
                                        ),
                                        cursorBrush = SolidColor(if (hasEmailError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { isEmailFocused = it.isFocused }
                                    )
                                }
                            }
                            if (hasEmailError) {
                                Text(
                                    text = emailError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        // Password Field
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Password",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            
                            var isPasswordFocused by remember { mutableStateOf(false) }
                            val hasPasswordError = passwordError != null
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .border(
                                        width = if (isPasswordFocused || hasPasswordError) 2.dp else 1.dp,
                                        color = when {
                                            hasPasswordError -> MaterialTheme.colorScheme.error
                                            isPasswordFocused -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.outlineVariant
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password Icon",
                                    tint = if (hasPasswordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    if (password.isEmpty()) {
                                        Text(
                                            text = "Create a password",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                        )
                                    }
                                    BasicTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        singleLine = true,
                                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Password,
                                            imeAction = ImeAction.Next
                                        ),
                                        cursorBrush = SolidColor(if (hasPasswordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { isPasswordFocused = it.isFocused }
                                    )
                                }
                                IconButton(
                                    onClick = { isPasswordVisible = !isPasswordVisible },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (isPasswordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                                        ),
                                        contentDescription = "Toggle password visibility",
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                            if (hasPasswordError) {
                                Text(
                                    text = passwordError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        // Confirm Password Field
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Confirm Password",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            
                            var isConfirmPasswordFocused by remember { mutableStateOf(false) }
                            val hasConfirmPasswordError = confirmPasswordError != null
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .border(
                                        width = if (isConfirmPasswordFocused || hasConfirmPasswordError) 2.dp else 1.dp,
                                        color = when {
                                            hasConfirmPasswordError -> MaterialTheme.colorScheme.error
                                            isConfirmPasswordFocused -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.outlineVariant
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password Icon",
                                    tint = if (hasConfirmPasswordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    if (confirmPassword.isEmpty()) {
                                        Text(
                                            text = "Confirm your password",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                        )
                                    }
                                    BasicTextField(
                                        value = confirmPassword,
                                        onValueChange = { confirmPassword = it },
                                        singleLine = true,
                                        visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Password,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                if (validateForm()) {
                                                    onRegisterSuccess()
                                                }
                                            }
                                        ),
                                        cursorBrush = SolidColor(if (hasConfirmPasswordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .onFocusChanged { isConfirmPasswordFocused = it.isFocused }
                                    )
                                }
                                IconButton(
                                    onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (isConfirmPasswordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                                        ),
                                        contentDescription = "Toggle password visibility",
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                            if (hasConfirmPasswordError) {
                                Text(
                                    text = confirmPasswordError ?: "",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }

                        // Create Account Action Button
                        PrimaryGradientButton(
                            text = "Create Account",
                            onClick = {
                                if (validateForm()) {
                                    onRegisterSuccess()
                                }
                            },
                            showArrow = false,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )

                        // Divider Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "OR",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                fontWeight = FontWeight.Bold
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Google Sign In Action Button
                        Surface(
                            onClick = onRegisterSuccess,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            ),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_google),
                                    contentDescription = "Google Logo",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Continue with Google",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Footer Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.stackLg),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .clickable { onBackClick() }
                    )
                }
            }
        }
    }
}
