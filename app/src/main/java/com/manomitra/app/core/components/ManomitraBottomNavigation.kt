package com.manomitra.app.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.manomitra.app.R

enum class BottomTab {
    HOME, COMPANION, JOURNAL, PROFILE
}

@Composable
fun ManomitraBottomNavigation(
    currentTab: BottomTab?,
    onHomeClick: () -> Unit,
    onCompanionClick: () -> Unit,
    onJournalClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.9f),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .border(1.dp, Color(0xFFECEEF0), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Tab
            BottomNavItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (currentTab == BottomTab.HOME) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF777587)
                    )
                },
                label = "Home",
                isActive = currentTab == BottomTab.HOME,
                onClick = onHomeClick
            )

            // Companion Tab
            BottomNavItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_smart_toy),
                        contentDescription = "Companion",
                        tint = if (currentTab == BottomTab.COMPANION) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF777587)
                    )
                },
                label = "Companion",
                isActive = currentTab == BottomTab.COMPANION,
                onClick = onCompanionClick
            )

            // Journal Tab
            BottomNavItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit_note),
                        contentDescription = "Journal",
                        tint = if (currentTab == BottomTab.JOURNAL) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF777587)
                    )
                },
                label = "Journal",
                isActive = currentTab == BottomTab.JOURNAL,
                onClick = onJournalClick
            )

            // Profile Tab
            BottomNavItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = if (currentTab == BottomTab.PROFILE) MaterialTheme.colorScheme.onPrimaryContainer else Color(0xFF777587)
                    )
                },
                label = "Profile",
                isActive = currentTab == BottomTab.PROFILE,
                onClick = onProfileClick
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: @Composable () -> Unit,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    if (isActive) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            icon()
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .clickable { onClick() }
                .padding(8.dp)
        ) {
            icon()
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF777587)
            )
        }
    }
}
