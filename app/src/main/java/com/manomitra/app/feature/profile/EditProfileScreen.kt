package com.manomitra.app.feature.profile

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manomitra.app.R
import com.manomitra.app.core.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val spacing = MaterialTheme.spacing
    val context = LocalContext.current

    val initialName by viewModel.displayName.collectAsState()
    val initialEmail by viewModel.email.collectAsState()
    val initialPhotoPath by viewModel.profileImage.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    var nameInput by remember(initialName) { mutableStateOf(initialName) }
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var removePhoto by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            removePhoto = false
        }
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp)
                .verticalScroll(scrollState)
                .padding(horizontal = spacing.containerMarginMobile, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            // Photo Preview & Selection Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val localBitmap = remember(initialPhotoPath, selectedImageUri, removePhoto) {
                        if (removePhoto) {
                            null
                        } else if (selectedImageUri != null) {
                            try {
                                context.contentResolver.openInputStream(selectedImageUri!!).use { input ->
                                    BitmapFactory.decodeStream(input)?.asImageBitmap()
                                }
                            } catch (e: Exception) {
                                null
                            }
                        } else if (!initialPhotoPath.isNullOrEmpty()) {
                            try {
                                BitmapFactory.decodeFile(initialPhotoPath)?.asImageBitmap()
                            } catch (e: Exception) {
                                null
                            }
                        } else {
                            null
                        }
                    }

                    if (localBitmap != null) {
                        Image(
                            bitmap = localBitmap,
                            contentDescription = "Profile Photo Preview",
                            modifier = Modifier
                                .size(112.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_profile_large),
                            contentDescription = "Default Avatar Preview",
                            modifier = Modifier
                                .size(112.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = CircleShape,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Change Photo", fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = {
                            removePhoto = true
                            selectedImageUri = null
                        },
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Remove", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // Input Fields Section
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFC7C4D8).copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                    )

                    OutlinedTextField(
                        value = initialEmail,
                        onValueChange = { },
                        label = { Text("Email Address (Read-only)") },
                        singleLine = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)
                    )
                }
            }

            // Actions Block
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (nameInput.trim().isEmpty()) {
                            Toast.makeText(context, "Full Name cannot be empty.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.saveUserProfile(
                            context = context,
                            newName = nameInput,
                            selectedImageUri = selectedImageUri,
                            removePhoto = removePhoto,
                            onComplete = { result ->
                                result.fold(
                                    onSuccess = {
                                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                        onBackClick()
                                    },
                                    onFailure = { throwable ->
                                        Toast.makeText(context, "Update failed: ${throwable.message}", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onBackClick,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Header App Bar
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.containerMarginMobile),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (!isSaving) onBackClick() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Profile Details",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
