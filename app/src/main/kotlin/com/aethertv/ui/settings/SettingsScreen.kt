package com.aethertv.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import com.aethertv.data.repository.UpdateState

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val updateState by viewModel.updateState.collectAsState()
    val currentVersion by viewModel.currentVersion.collectAsState()
    
    val checkUpdateFocus = remember { FocusRequester() }
    
    LaunchedEffect(Unit) {
        checkUpdateFocus.requestFocus()
    }
    
    val scrollState = rememberScrollState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.Back) {
                    onBack()
                    true
                } else false
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(48.dp)
        ) {
            // Header
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Version info
            SettingsSection(title = "About") {
                SettingsRow(label = "Version", value = currentVersion)
                SettingsRow(label = "Build", value = "Debug")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Streaming Engine section
            SettingsSection(title = "Streaming Engine") {
                val engineState by viewModel.engineState.collectAsState()
                
                SettingsRow(
                    label = "Engine",
                    value = engineState.name
                )
                SettingsRow(
                    label = "Version",
                    value = engineState.version ?: "Not installed"
                )
                SettingsRow(
                    label = "Status",
                    value = when {
                        engineState.isRunning -> "🟢 Running"
                        engineState.isInstalled -> "🟡 Installed (not running)"
                        else -> "🔴 Not installed"
                    }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!engineState.isInstalled) {
                        FocusableButton(
                            text = "Install",
                            onClick = { viewModel.installEngine() },
                            focusRequester = null,
                            primary = true
                        )
                    } else if (!engineState.isRunning) {
                        FocusableButton(
                            text = "Launch Engine",
                            onClick = { viewModel.launchEngine() },
                            focusRequester = null,
                            primary = true
                        )
                    }
                    
                    FocusableButton(
                        text = "Refresh Status",
                        onClick = { viewModel.refreshEngineStatus() },
                        focusRequester = null
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Updates section
            SettingsSection(title = "Updates") {
                UpdateSection(
                    state = updateState,
                    onCheckUpdate = { viewModel.checkForUpdate() },
                    onDownload = { viewModel.downloadUpdate() },
                    onInstall = { viewModel.installUpdate() },
                    onDismiss = { viewModel.dismissUpdate() },
                    focusRequester = checkUpdateFocus
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Playback settings placeholder
            SettingsSection(title = "Playback") {
                SettingsRow(label = "Buffer Size", value = "Default")
                SettingsRow(label = "Hardware Decoding", value = "Auto")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Verification section
            SettingsSection(title = "Stream Verification") {
                val progress by viewModel.verificationProgress.collectAsState()
                
                Text(
                    text = "Check which channels are currently live. This may take a while.",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                if (progress.isRunning) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Verifying: ${progress.current}/${progress.total}",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "🟢 ${progress.liveCount} live channels found",
                            color = Color(0xFF4CAF50),
                            fontSize = 14.sp
                        )
                        // Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(Color(0xFF333333), RoundedCornerShape(2.dp))
                        ) {
                            val fraction = if (progress.total > 0) 
                                progress.current.toFloat() / progress.total 
                            else 0f
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .height(4.dp)
                                    .background(Color(0xFF00B4D8), RoundedCornerShape(2.dp))
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        FocusableButton(
                            text = "Stop",
                            onClick = { viewModel.stopVerification() },
                            focusRequester = null
                        )
                    }
                } else {
                    FocusableButton(
                        text = "Verify All Channels",
                        onClick = { viewModel.startVerification() },
                        focusRequester = null
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Data section
            SettingsSection(title = "Data") {
                val dataMessage by viewModel.dataMessage.collectAsState()
                
                dataMessage?.let { message ->
                    Text(
                        text = "✓ $message",
                        color = Color(0xFF4CAF50),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                FocusableButton(
                    text = "Clear Watch History",
                    onClick = { viewModel.clearWatchHistory() },
                    focusRequester = null
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Footer hint
            Text(
                text = "Press Back to return",
                color = Color.Gray,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            color = Color(0xFF00B4D8),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = SurfaceDefaults.colors(containerColor = Color(0xFF1A1A1A))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    }
}

@Composable
private fun SettingsRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp
        )
        Text(
            text = value,
            color = Color.Gray,
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun UpdateSection(
    state: UpdateState,
    onCheckUpdate: () -> Unit,
    onDownload: () -> Unit,
    onInstall: () -> Unit,
    onDismiss: () -> Unit,
    focusRequester: FocusRequester
) {
    var isFocused by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        when (state) {
            is UpdateState.Idle -> {
                FocusableButton(
                    text = "Check for Updates",
                    onClick = onCheckUpdate,
                    focusRequester = focusRequester
                )
            }
            
            is UpdateState.Checking -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Checking for updates...",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
            
            is UpdateState.UpToDate -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✓ You're up to date!",
                        color = Color(0xFF4CAF50),
                        fontSize = 16.sp
                    )
                    FocusableButton(
                        text = "Check Again",
                        onClick = onCheckUpdate,
                        focusRequester = focusRequester
                    )
                }
            }
            
            is UpdateState.Available -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "New version available: ${state.release.tag_name}",
                        color = Color(0xFFFFA726),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Current: ${state.currentVersion}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    state.release.body?.let { notes ->
                        if (notes.isNotBlank()) {
                            Text(
                                text = notes.take(200) + if (notes.length > 200) "..." else "",
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FocusableButton(
                            text = "Download & Install",
                            onClick = onDownload,
                            focusRequester = focusRequester,
                            primary = true
                        )
                        FocusableButton(
                            text = "Later",
                            onClick = onDismiss,
                            focusRequester = null
                        )
                    }
                }
            }
            
            is UpdateState.Downloading -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Downloading... ${(state.progress * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color(0xFF333333), RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(state.progress)
                                .height(8.dp)
                                .background(Color(0xFF00B4D8), RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
            
            is UpdateState.ReadyToInstall -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Download complete!",
                        color = Color(0xFF4CAF50),
                        fontSize = 16.sp
                    )
                    FocusableButton(
                        text = "Install Now",
                        onClick = onInstall,
                        focusRequester = focusRequester,
                        primary = true
                    )
                }
            }
            
            is UpdateState.Error -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Error: ${state.message}",
                        color = Color(0xFFF44336),
                        fontSize = 14.sp
                    )
                    FocusableButton(
                        text = "Try Again",
                        onClick = onCheckUpdate,
                        focusRequester = focusRequester
                    )
                }
            }
        }
    }
}

@Composable
private fun FocusableButton(
    text: String,
    onClick: () -> Unit,
    focusRequester: FocusRequester?,
    primary: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val baseColor = if (primary) Color(0xFF0077B6) else Color(0xFF2A2A2A)
    val focusedColor = if (primary) Color(0xFF00B4D8) else Color(0xFF444444)
    
    Surface(
        onClick = onClick,
        modifier = Modifier
            .then(
                if (focusRequester != null) Modifier.focusRequester(focusRequester) 
                else Modifier
            )
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
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}
