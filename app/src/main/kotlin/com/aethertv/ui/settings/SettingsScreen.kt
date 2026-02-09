package com.aethertv.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
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
import com.aethertv.data.local.entity.FilterRuleEntity
import com.aethertv.data.repository.UpdateState

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToScraperMonitor: () -> Unit = {},
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
            
            // Scraper section
            SettingsSection(title = "Channel Scraper") {
                Text(
                    text = "Discover channels from the AceStream network",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FocusableButton(
                        text = "📡 Open Scraper Monitor",
                        onClick = onNavigateToScraperMonitor,
                        focusRequester = null,
                        primary = true
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
            
            // EPG section
            SettingsSection(title = "TV Guide (EPG)") {
                val epgState by viewModel.epgState.collectAsState()
                
                EpgSection(
                    state = epgState,
                    onUrlChange = { viewModel.updateEpgUrl(it) },
                    onSync = { viewModel.syncEpg() },
                    onCancel = { viewModel.cancelEpgSync() },
                    onClear = { viewModel.clearEpg() }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // EPG Matching section
            SettingsSection(title = "EPG Channel Matching") {
                val epgMatchState by viewModel.epgMatchState.collectAsState()
                
                LaunchedEffect(Unit) {
                    viewModel.loadEpgMatchData()
                }
                
                EpgMatchSection(
                    state = epgMatchState,
                    onAutoMatch = { viewModel.autoMatchEpg() },
                    onClearMappings = { viewModel.clearEpgMappings() }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Filter Rules section
            SettingsSection(title = "Channel Filters") {
                val rules by viewModel.filterRules.collectAsState()
                
                FilterRulesSection(
                    rules = rules,
                    onAddRule = { type, pattern -> viewModel.addFilterRule(type, pattern) },
                    onToggleRule = { rule -> viewModel.toggleFilterRule(rule) },
                    onDeleteRule = { rule -> viewModel.deleteFilterRule(rule) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Appearance section
            SettingsSection(title = "Appearance") {
                val highContrast by viewModel.highContrastEnabled.collectAsState()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "High Contrast Mode",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Improves visibility for users with visual impairments",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    FocusableButton(
                        text = if (highContrast) "ON" else "OFF",
                        onClick = { viewModel.toggleHighContrast() },
                        focusRequester = null,
                        primary = highContrast
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Playback settings
            SettingsSection(title = "Playback") {
                SettingsRow(label = "Buffer Size", value = "Default (15s)")
                SettingsRow(label = "Hardware Decoding", value = "Enabled")
                SettingsRow(label = "Preferred Quality", value = "Auto")
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Crash Logs section
            SettingsSection(title = "Diagnostics") {
                val hasLogs = viewModel.hasCrashLogs()
                val logSize = viewModel.getCrashLogSizeKb()
                
                if (hasLogs) {
                    Text(
                        text = "Crash logs: ${logSize}KB",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FocusableButton(
                            text = "View Logs",
                            onClick = { 
                                // In a real app, this would open a dialog or new screen
                                // For now we just show a message
                                viewModel.getCrashLogs()?.let { logs ->
                                    android.util.Log.d("CrashLogs", logs)
                                }
                            },
                            focusRequester = null
                        )
                        FocusableButton(
                            text = "Clear Logs",
                            onClick = { viewModel.clearCrashLogs() },
                            focusRequester = null
                        )
                    }
                } else {
                    Text(
                        text = "No crash logs recorded",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
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
private fun EpgMatchSection(
    state: EpgMatchState,
    onAutoMatch: () -> Unit,
    onClearMappings: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Match your channels to EPG data for program info in the Guide.",
            color = Color.Gray,
            fontSize = 12.sp
        )
        
        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${state.matchedChannels}/${state.totalChannels}",
                    color = Color(0xFF00B4D8),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Matched",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${state.availableEpgChannels}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "EPG Channels",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
        
        if (state.isMatching) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Matching... ${state.matchProgress}%",
                    color = Color(0xFF00B4D8),
                    fontSize = 14.sp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFF333333), RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(state.matchProgress / 100f)
                            .height(4.dp)
                            .background(Color(0xFF00B4D8), RoundedCornerShape(2.dp))
                    )
                }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.availableEpgChannels > 0) {
                    FocusableButton(
                        text = "Auto-Match",
                        onClick = onAutoMatch,
                        focusRequester = null,
                        primary = true
                    )
                }
                if (state.matchedChannels > 0) {
                    FocusableButton(
                        text = "Clear Mappings",
                        onClick = onClearMappings,
                        focusRequester = null
                    )
                }
            }
            
            if (state.availableEpgChannels == 0) {
                Text(
                    text = "Sync EPG data first to enable matching",
                    color = Color.DarkGray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun FilterRulesSection(
    rules: List<FilterRuleEntity>,
    onAddRule: (type: String, pattern: String) -> Unit,
    onToggleRule: (FilterRuleEntity) -> Unit,
    onDeleteRule: (FilterRuleEntity) -> Unit
) {
    var selectedType by remember { mutableStateOf("name_exclude") }
    var patternText by remember { mutableStateOf("") }
    
    val filterTypes = listOf(
        "name_exclude" to "Exclude by Name",
        "name_include" to "Include by Name",
        "category" to "Category Filter",
        "language" to "Language Filter",
        "country" to "Country Filter"
    )
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Filter channels by name patterns, categories, or languages.",
            color = Color.Gray,
            fontSize = 12.sp
        )
        
        // Add new rule
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Type selector
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filterTypes.take(3).forEach { (type, label) ->
                    val isSelected = selectedType == type
                    com.aethertv.ui.components.TvChip(
                        text = label.split(" ").first(),
                        onClick = { selectedType = type },
                        isSelected = isSelected
                    )
                }
            }
            
            // Pattern input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.weight(1f).focusable(),
                    shape = RoundedCornerShape(8.dp),
                    colors = SurfaceDefaults.colors(containerColor = Color(0xFF2A2A2A))
                ) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = patternText,
                        onValueChange = { patternText = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.White,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box {
                                if (patternText.isEmpty()) {
                                    Text(
                                        text = when (selectedType) {
                                            "name_exclude", "name_include" -> "e.g., Spanish|French"
                                            "category" -> "e.g., sports"
                                            "language" -> "e.g., en|es"
                                            "country" -> "e.g., US|UK"
                                            else -> "Pattern (regex)"
                                        },
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
                
                FocusableButton(
                    text = "+ Add",
                    onClick = {
                        if (patternText.isNotBlank()) {
                            onAddRule(selectedType, patternText)
                            patternText = ""
                        }
                    },
                    focusRequester = null,
                    primary = true
                )
            }
        }
        
        // Existing rules
        if (rules.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Active Rules (${rules.size})",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                rules.forEach { rule ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF222222), RoundedCornerShape(4.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = when (rule.type) {
                                    "name_exclude" -> "🚫"
                                    "name_include" -> "✓"
                                    "category" -> "📁"
                                    "language" -> "🌐"
                                    "country" -> "🏳️"
                                    else -> "📋"
                                },
                                fontSize = 14.sp
                            )
                            Text(
                                text = rule.pattern,
                                color = if (rule.isEnabled) Color.White else Color.Gray,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            com.aethertv.ui.components.TvCompactButton(
                                text = if (rule.isEnabled) "ON" else "OFF",
                                onClick = { onToggleRule(rule) },
                                textColor = if (rule.isEnabled) Color(0xFF4CAF50) else Color.Gray
                            )
                            
                            com.aethertv.ui.components.TvCompactButton(
                                text = "✕",
                                onClick = { onDeleteRule(rule) },
                                focusedColor = Color(0xFFF44336)
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "No filter rules configured",
                color = Color.DarkGray,
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun EpgSection(
    state: EpgSyncState,
    onUrlChange: (String) -> Unit,
    onSync: () -> Unit,
    onCancel: () -> Unit,
    onClear: () -> Unit
) {
    var urlText by remember(state.url) { mutableStateOf(state.url) }
    val focusRequester = remember { FocusRequester() }
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Enter an XMLTV URL to show program info in the TV Guide.",
            color = Color.Gray,
            fontSize = 12.sp
        )
        
        // URL input
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .focusable(),
                shape = RoundedCornerShape(8.dp),
                colors = SurfaceDefaults.colors(containerColor = Color(0xFF2A2A2A))
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = urlText,
                    onValueChange = { 
                        urlText = it
                        onUrlChange(it)
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box {
                            if (urlText.isEmpty()) {
                                Text(
                                    text = "https://example.com/epg.xml",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
        
        // Last sync info
        if (state.lastSync > 0) {
            val syncTime = java.text.SimpleDateFormat("MMM d, HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(state.lastSync))
            Text(
                text = "Last sync: $syncTime",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        
        // Sync status
        if (state.isSyncing) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Syncing... ${state.channelCount} channels, ${state.programCount} programs",
                    color = Color(0xFF00B4D8),
                    fontSize = 14.sp
                )
                // Progress indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFF333333), RoundedCornerShape(2.dp))
                ) {
                    // Indeterminate progress
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .height(4.dp)
                            .background(Color(0xFF00B4D8), RoundedCornerShape(2.dp))
                    )
                }
            }
        }
        
        // Error message
        state.error?.let { error ->
            Text(
                text = "Error: $error",
                color = Color(0xFFF44336),
                fontSize = 12.sp
            )
        }
        
        // Action buttons
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.isSyncing) {
                FocusableButton(
                    text = "Cancel",
                    onClick = onCancel,
                    focusRequester = null
                )
            } else {
                FocusableButton(
                    text = "Sync Now",
                    onClick = onSync,
                    focusRequester = null,
                    primary = true
                )
                if (state.lastSync > 0) {
                    FocusableButton(
                        text = "Clear EPG",
                        onClick = onClear,
                        focusRequester = null
                    )
                }
            }
        }
        
        // Popular EPG sources hint
        Text(
            text = "Popular sources: iptv-org.github.io, epg.best",
            color = Color.DarkGray,
            fontSize = 11.sp
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
    
    // Use isFocused state to update colors for proper visual feedback
    val currentColor = if (isFocused) focusedColor else baseColor
    
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
            containerColor = currentColor,
            focusedContainerColor = focusedColor
        ),
        border = if (isFocused) {
            ClickableSurfaceDefaults.border(
                focusedBorder = androidx.tv.material3.Border(
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                )
            )
        } else {
            ClickableSurfaceDefaults.border()
        }
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
