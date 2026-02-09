package com.aethertv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

/**
 * Error types for consistent error handling across the app
 */
enum class ErrorType {
    NETWORK,
    ENGINE_UNAVAILABLE,
    STREAM_FAILED,
    EPG_SYNC_FAILED,
    UNKNOWN
}

/**
 * Full-screen error state with icon, message, and retry button
 */
@Composable
fun ErrorScreen(
    errorType: ErrorType,
    message: String? = null,
    onRetry: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (icon, defaultMessage, accessibilityLabel) = when (errorType) {
        ErrorType.NETWORK -> Triple(
            "📡",
            "No internet connection. Check your network and try again.",
            "Network error"
        )
        ErrorType.ENGINE_UNAVAILABLE -> Triple(
            "⚙️",
            "AceStream engine is not responding. Make sure it's installed and running.",
            "Engine unavailable"
        )
        ErrorType.STREAM_FAILED -> Triple(
            "📺",
            "Failed to load the stream. The channel may be offline.",
            "Stream failed"
        )
        ErrorType.EPG_SYNC_FAILED -> Triple(
            "📋",
            "Failed to sync TV guide data. Check the EPG URL in settings.",
            "EPG sync failed"
        )
        ErrorType.UNKNOWN -> Triple(
            "⚠️",
            "Something went wrong. Please try again.",
            "Unknown error"
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .semantics { contentDescription = "$accessibilityLabel: ${message ?: defaultMessage}" },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(48.dp)
        ) {
            Text(
                text = icon,
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = when (errorType) {
                    ErrorType.NETWORK -> "Connection Error"
                    ErrorType.ENGINE_UNAVAILABLE -> "Engine Unavailable"
                    ErrorType.STREAM_FAILED -> "Stream Error"
                    ErrorType.EPG_SYNC_FAILED -> "Sync Failed"
                    ErrorType.UNKNOWN -> "Error"
                },
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = message ?: defaultMessage,
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                onRetry?.let {
                    ErrorButton(
                        text = "Try Again",
                        onClick = it,
                        primary = true
                    )
                }
                onDismiss?.let {
                    ErrorButton(
                        text = "Go Back",
                        onClick = it,
                        primary = false
                    )
                }
            }
        }
    }
}

/**
 * Inline error banner for non-blocking errors
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ErrorBanner(
    message: String,
    onRetry: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .background(Color(0xFF331111), RoundedCornerShape(8.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .semantics { contentDescription = "Error: $message" },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚠️",
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                color = Color(0xFFFFAAAA),
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            onRetry?.let {
                Spacer(modifier = Modifier.width(12.dp))
                ErrorButton(
                    text = "Retry",
                    onClick = it,
                    primary = true,
                    compact = true
                )
            }

            onDismiss?.let {
                Spacer(modifier = Modifier.width(8.dp))
                ErrorButton(
                    text = "✕",
                    onClick = it,
                    primary = false,
                    compact = true
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun ErrorButton(
    text: String,
    onClick: () -> Unit,
    primary: Boolean,
    compact: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }

    val backgroundColor = when {
        isFocused && primary -> Color(0xFFDD4444)
        isFocused -> Color(0xFF555555)
        primary -> Color(0xFFB33333)
        else -> Color(0xFF333333)
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .background(backgroundColor, RoundedCornerShape(8.dp)),
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = if (compact) 12.sp else 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(
                horizontal = if (compact) 12.dp else 20.dp,
                vertical = if (compact) 6.dp else 12.dp
            )
        )
    }
}
