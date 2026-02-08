package com.aethertv.ui.setup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import kotlinx.coroutines.delay

enum class SetupStep {
    WELCOME,
    SCANNING,
    COMPLETE
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FirstRunScreen(
    onSetupComplete: () -> Unit,
    viewModel: FirstRunViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        delay(500)
        focusRequester.requestFocus()
    }
    
    LaunchedEffect(uiState.step) {
        if (uiState.step == SetupStep.COMPLETE) {
            delay(1500)
            onSetupComplete()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(48.dp)
        ) {
            when (uiState.step) {
                SetupStep.WELCOME -> WelcomeStep(
                    onGetStarted = { viewModel.startScanning() },
                    focusRequester = focusRequester
                )
                SetupStep.SCANNING -> ScanningStep(
                    channelsFound = uiState.channelsFound,
                    statusMessage = uiState.statusMessage
                )
                SetupStep.COMPLETE -> CompleteStep(
                    channelsFound = uiState.channelsFound
                )
            }
        }
    }
}

@Composable
private fun WelcomeStep(
    onGetStarted: () -> Unit,
    focusRequester: FocusRequester
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "📺",
            fontSize = 80.sp
        )
        
        Text(
            text = "Welcome to AetherTV",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Stream live TV channels via AceStream P2P",
            color = Color.Gray,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureRow("🔍", "Auto-discover channels from AceStream")
            FeatureRow("⭐", "Save your favorite channels")
            FeatureRow("📡", "Switch channels with D-pad up/down")
            FeatureRow("🔄", "In-app updates from GitHub")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        FocusableButton(
            text = "Get Started →",
            onClick = onGetStarted,
            focusRequester = focusRequester,
            primary = true
        )
        
        Text(
            text = "Requires AceStream Engine installed",
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun FeatureRow(emoji: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = emoji, fontSize = 24.sp)
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun ScanningStep(
    channelsFound: Int,
    statusMessage: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "🔍",
            fontSize = 80.sp
        )
        
        Text(
            text = "Scanning for Channels",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = statusMessage,
            color = Color.Gray,
            fontSize = 16.sp
        )
        
        AnimatedVisibility(
            visible = channelsFound > 0,
            enter = fadeIn()
        ) {
            Text(
                text = "$channelsFound channels found",
                color = Color(0xFF00B4D8),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        // Progress indicator
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(4.dp)
                .background(Color(0xFF333333), RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(4.dp)
                    .background(Color(0xFF00B4D8), RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
private fun CompleteStep(channelsFound: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "✅",
            fontSize = 80.sp
        )
        
        Text(
            text = "Setup Complete!",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = if (channelsFound > 0) {
                "Found $channelsFound channels"
            } else {
                "Using demo channels"
            },
            color = Color(0xFF00B4D8),
            fontSize = 20.sp
        )
        
        Text(
            text = "Starting AetherTV...",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FocusableButton(
    text: String,
    onClick: () -> Unit,
    focusRequester: FocusRequester,
    primary: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val baseColor = if (primary) Color(0xFF0077B6) else Color(0xFF2A2A2A)
    val focusedColor = if (primary) Color(0xFF00B4D8) else Color(0xFF444444)
    
    Surface(
        onClick = onClick,
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(),
        shape = ClickableSurfaceDefaults.shape(
            shape = RoundedCornerShape(8.dp)
        ),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = baseColor,
            focusedContainerColor = focusedColor
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
        )
    }
}
