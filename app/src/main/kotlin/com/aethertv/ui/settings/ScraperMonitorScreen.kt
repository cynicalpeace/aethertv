package com.aethertv.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Text
import com.aethertv.scraper.ScraperLogEntry
import com.aethertv.scraper.ScraperPhase
import com.aethertv.scraper.ScraperProgress
import com.aethertv.ui.components.TvButton

@Composable
fun ScraperMonitorScreen(
    onBack: () -> Unit,
    viewModel: ScraperMonitorViewModel = hiltViewModel(),
) {
    val progress by viewModel.progress.collectAsState()
    val warningMessage by viewModel.warningMessage.collectAsState()
    val listState = rememberLazyListState()
    
    // Auto-scroll to bottom when new logs arrive
    LaunchedEffect(progress.logs.size) {
        if (progress.logs.isNotEmpty()) {
            listState.animateScrollToItem(progress.logs.size - 1)
        }
    }
    
    // Auto-dismiss warning after 4 seconds (C13 fix)
    LaunchedEffect(warningMessage) {
        if (warningMessage != null) {
            kotlinx.coroutines.delay(4000)
            viewModel.dismissWarning()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
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
                .padding(32.dp)
        ) {
            // Header with accessibility
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "📡 Scraper Monitor",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { 
                        heading()
                        contentDescription = "Scraper Monitor Screen"
                    },
                )
                
                val buttonDescription = if (progress.isRunning) {
                    "Scraper is currently running"
                } else {
                    "Start scraping for new channels"
                }
                TvButton(
                    text = if (progress.isRunning) "⏳ Running..." else "🔄 Start Scrape",
                    onClick = { if (!progress.isRunning) viewModel.startScrape() },
                    primary = !progress.isRunning,
                    modifier = Modifier.semantics { contentDescription = buttonDescription },
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status Panel
            StatusPanel(progress = progress)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Log output
            Text(
                text = "📋 Live Log",
                color = Color(0xFF00B4D8),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF111111), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                if (progress.logs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No logs yet. Press Start Scrape to begin.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        // H40 fix: Remove timestamp key — logs in same millisecond cause key collision crash
                        items(progress.logs) { log ->
                            LogEntry(log = log)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Footer
            Text(
                text = "Press Back to return to Settings",
                color = Color.Gray,
                fontSize = 12.sp,
            )
        }
        
        // Warning message overlay (C13 fix - display warning when playback stops)
        warningMessage?.let { message ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .background(Color(0xFFFF9800), RoundedCornerShape(8.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .semantics { contentDescription = "Warning: $message" },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "⚠️",
                        fontSize = 18.sp,
                    )
                    Text(
                        text = message,
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPanel(progress: ScraperProgress) {
    // Build accessibility description for the entire status panel
    val statusDescription = buildString {
        append("Status: ")
        append(when (progress.phase) {
            ScraperPhase.IDLE -> "Idle"
            ScraperPhase.CONNECTING -> "Connecting"
            ScraperPhase.SEARCHING -> "Searching"
            ScraperPhase.FILTERING -> "Filtering"
            ScraperPhase.SAVING -> "Saving"
            ScraperPhase.COMPLETE -> "Complete"
            ScraperPhase.ERROR -> "Error"
        })
        append(". Found ${progress.channelsFound} channels")
        if (progress.channelsAfterFilter > 0) {
            append(", ${progress.channelsAfterFilter} after filtering")
        }
        if (progress.isRunning) {
            append(". Running for ${progress.elapsedSeconds} seconds")
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp))
            .padding(16.dp)
            .semantics { contentDescription = statusDescription },
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        // Phase indicator
        StatusItem(
            label = "Status",
            value = when (progress.phase) {
                ScraperPhase.IDLE -> "Idle"
                ScraperPhase.CONNECTING -> "Connecting..."
                ScraperPhase.SEARCHING -> "Searching..."
                ScraperPhase.FILTERING -> "Filtering..."
                ScraperPhase.SAVING -> "Saving..."
                ScraperPhase.COMPLETE -> "Complete ✓"
                ScraperPhase.ERROR -> "Error ✗"
            },
            valueColor = when (progress.phase) {
                ScraperPhase.COMPLETE -> Color(0xFF4CAF50)
                ScraperPhase.ERROR -> Color(0xFFF44336)
                ScraperPhase.IDLE -> Color.Gray
                else -> Color(0xFF00B4D8)
            },
        )
        
        // Channels found
        StatusItem(
            label = "Found",
            value = "${progress.channelsFound}",
            valueColor = Color.White,
        )
        
        // After filter
        StatusItem(
            label = "After Filter",
            value = "${progress.channelsAfterFilter}",
            valueColor = Color(0xFF4CAF50),
        )
        
        // Progress
        StatusItem(
            label = "Page",
            value = if (progress.totalPages != null) {
                "${progress.currentPage}/${progress.totalPages}"
            } else {
                "${progress.currentPage}"
            },
            valueColor = Color.White,
        )
        
        // Elapsed time
        StatusItem(
            label = "Elapsed",
            value = "${progress.elapsedSeconds}s",
            valueColor = Color.White,
        )
    }
    
    // Progress bar
    if (progress.isRunning && progress.progressPercent > 0) {
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color(0xFF333333), RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.progressPercent)
                    .height(4.dp)
                    .background(Color(0xFF00B4D8), RoundedCornerShape(2.dp))
            )
        }
    }
    
    // Error message
    progress.error?.let { error ->
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "❌ $error",
            color = Color(0xFFF44336),
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF331111), RoundedCornerShape(4.dp))
                .padding(8.dp),
        )
    }
}

@Composable
private fun StatusItem(
    label: String,
    value: String,
    valueColor: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            color = valueColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun LogEntry(log: ScraperLogEntry) {
    // Build accessibility description
    val levelName = when (log.level) {
        ScraperLogEntry.LogLevel.ERROR -> "Error"
        ScraperLogEntry.LogLevel.WARN -> "Warning"
        ScraperLogEntry.LogLevel.SUCCESS -> "Success"
        ScraperLogEntry.LogLevel.DEBUG -> "Debug"
        ScraperLogEntry.LogLevel.INFO -> "Info"
    }
    val accessibilityDesc = buildString {
        append("$levelName at ${log.formattedTime}: ${log.message}")
        log.details?.let { append(". Details: $it") }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .semantics { contentDescription = accessibilityDesc },
        verticalAlignment = Alignment.Top,
    ) {
        // Timestamp
        Text(
            text = log.formattedTime,
            color = Color(0xFF666666),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Emoji
        Text(
            text = log.emoji,
            fontSize = 12.sp,
        )
        
        Spacer(modifier = Modifier.width(6.dp))
        
        // Message
        Column {
            Text(
                text = log.message,
                color = when (log.level) {
                    ScraperLogEntry.LogLevel.ERROR -> Color(0xFFF44336)
                    ScraperLogEntry.LogLevel.WARN -> Color(0xFFFFC107)
                    ScraperLogEntry.LogLevel.SUCCESS -> Color(0xFF4CAF50)
                    ScraperLogEntry.LogLevel.DEBUG -> Color(0xFF888888)
                    ScraperLogEntry.LogLevel.INFO -> Color.White
                },
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
            )
            
            log.details?.let { details ->
                Text(
                    text = details,
                    color = Color(0xFF888888),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}
