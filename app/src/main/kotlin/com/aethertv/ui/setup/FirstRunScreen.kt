package com.aethertv.ui.setup

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Brush
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

private val accentColor = Color(0xFF00B4D8)
private val successColor = Color(0xFF2ECC71)
private val errorColor = Color(0xFFE74C3C)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun FirstRunScreen(
    onSetupComplete: () -> Unit,
    viewModel: FirstRunViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val installIntent by viewModel.installIntent.collectAsState()
    val focusRequester = remember { FocusRequester() }
    
    // Launcher for install intent
    val installLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.onInstallResult()
    }
    
    // Launch install intent when available
    LaunchedEffect(installIntent) {
        installIntent?.let { intent ->
            installLauncher.launch(intent)
        }
    }
    
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
                SetupStep.SCANNING -> {
                    if (!uiState.engineInstalled) {
                        EngineInstallStep(
                            installState = uiState.installState,
                            installProgress = uiState.installProgress,
                            installError = uiState.installError,
                            onInstallBundled = { viewModel.installBundledEngine() },
                            onInstallFromStore = { viewModel.openEngineInstall() },
                            onSkip = { viewModel.skipEngine() },
                            onRetry = { viewModel.retryInstall() },
                            focusRequester = focusRequester
                        )
                    } else {
                        ScanningStep(
                            channelsFound = uiState.channelsFound,
                            statusMessage = uiState.statusMessage,
                        )
                    }
                }
                SetupStep.COMPLETE -> CompleteStep(
                    channelsFound = uiState.channelsFound,
                    engineInstalled = uiState.engineInstalled
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
            text = "Stream live TV channels via P2P",
            color = Color.Gray,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureRow("🔍", "Auto-discover channels")
            FeatureRow("📺", "EPG with Now/Next info")
            FeatureRow("⭐", "Save your favorites")
            FeatureRow("🔄", "In-app updates")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        FocusableButton(
            text = "Get Started →",
            onClick = onGetStarted,
            focusRequester = focusRequester,
            primary = true
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
private fun EngineInstallStep(
    installState: InstallState,
    installProgress: Float,
    installError: String?,
    onInstallBundled: () -> Unit,
    onInstallFromStore: () -> Unit,
    onSkip: () -> Unit,
    onRetry: () -> Unit,
    focusRequester: FocusRequester
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = when (installState) {
                InstallState.EXTRACTING -> "📦"
                InstallState.INSTALLING, InstallState.WAITING -> "⏳"
                InstallState.SUCCESS -> "✅"
                InstallState.FAILED -> "❌"
                else -> "🔧"
            },
            fontSize = 80.sp
        )
        
        Text(
            text = when (installState) {
                InstallState.EXTRACTING -> "Preparing Installation..."
                InstallState.INSTALLING -> "Installing..."
                InstallState.WAITING -> "Please confirm installation"
                InstallState.SUCCESS -> "Installation Complete!"
                InstallState.FAILED -> "Installation Failed"
                else -> "Streaming Engine Required"
            },
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        when (installState) {
            InstallState.IDLE -> {
                Text(
                    text = "AetherTV requires AceStream Engine for P2P streaming.\nInstall it now to access live channels.",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress indicator placeholder
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FocusableButton(
                        text = "📦 Install Now (Recommended)",
                        onClick = onInstallBundled,
                        focusRequester = focusRequester,
                        primary = true
                    )
                    
                    FocusableButton(
                        text = "🏪 Get from Play Store",
                        onClick = onInstallFromStore,
                        focusRequester = remember { FocusRequester() },
                        primary = false
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FocusableButton(
                        text = "Continue with demo channels →",
                        onClick = onSkip,
                        focusRequester = remember { FocusRequester() },
                        primary = false
                    )
                }
            }
            
            InstallState.EXTRACTING, InstallState.INSTALLING, InstallState.WAITING -> {
                // Progress bar
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(8.dp)
                        .background(Color(0xFF333333), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(installProgress)
                            .height(8.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(accentColor, successColor)
                                ),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
                
                Text(
                    text = when (installState) {
                        InstallState.EXTRACTING -> "Extracting APK..."
                        InstallState.INSTALLING -> "Starting installer..."
                        InstallState.WAITING -> "Approve the installation when prompted"
                        else -> ""
                    },
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            
            InstallState.FAILED -> {
                installError?.let { error ->
                    Text(
                        text = error,
                        color = errorColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FocusableButton(
                        text = "🔄 Try Again",
                        onClick = onRetry,
                        focusRequester = focusRequester,
                        primary = true
                    )
                    
                    FocusableButton(
                        text = "Continue without engine",
                        onClick = onSkip,
                        focusRequester = remember { FocusRequester() },
                        primary = false
                    )
                }
            }
            
            InstallState.SUCCESS -> {
                Text(
                    text = "Starting channel scan...",
                    color = successColor,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ScanningStep(
    channelsFound: Int,
    statusMessage: String,
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
                color = accentColor,
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
                    .background(accentColor, RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
private fun CompleteStep(
    channelsFound: Int,
    engineInstalled: Boolean = true
) {
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
            color = accentColor,
            fontSize = 20.sp
        )
        
        if (!engineInstalled) {
            Text(
                text = "Install AceStream from Settings for live channels",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
        
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
    val focusedColor = if (primary) accentColor else Color(0xFF444444)
    
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
