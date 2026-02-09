package com.aethertv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.aethertv.domain.model.EpgProgram
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val liveColor = Color(0xFFE63946)
private val accentColor = Color(0xFF00B4D8)
private val progressBg = Color(0xFF333333)

// Pre-created gradient brush to avoid allocation on every recomposition
private val progressGradientBrush = Brush.horizontalGradient(
    colors = listOf(liveColor, accentColor),
)

// C19 fix: ThreadLocal time formatter for thread safety.
// SimpleDateFormat is NOT thread-safe; concurrent access from multiple composables
// can corrupt output or throw ArrayIndexOutOfBoundsException.
// Pattern matches GuideScreen.kt's correct ThreadLocal approach.
private val NowNextTimeFormatter = ThreadLocal.withInitial {
    SimpleDateFormat("HH:mm", Locale.ROOT)
}

/**
 * Compact Now/Next indicator for channel cards.
 * Shows current program with progress bar and optional next program.
 */
/**
 * Progress update interval for live EPG indicators (30 seconds).
 */
private const val PROGRESS_UPDATE_INTERVAL_MS = 30_000L

@Composable
fun NowNextIndicator(
    currentProgram: EpgProgram?,
    nextProgram: EpgProgram?,
    modifier: Modifier = Modifier,
) {
    if (currentProgram == null) {
        // No EPG data
        return
    }

    // Use mutable state that updates periodically for live progress
    var progress by remember { mutableFloatStateOf(0f) }
    
    // Calculate initial progress and update periodically
    LaunchedEffect(currentProgram) {
        while (true) {
            val now = System.currentTimeMillis()
            val elapsed = now - currentProgram.startTime
            val duration = currentProgram.endTime - currentProgram.startTime
            progress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
            
            // Stop updating if program has ended
            if (now >= currentProgram.endTime) break
            
            delay(PROGRESS_UPDATE_INTERVAL_MS)
        }
    }

    // C19 fix: Use ThreadLocal getter for thread-safe formatter access
    val timeFormatter = NowNextTimeFormatter.get()!!

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        // Current program
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Live badge
            Box(
                modifier = Modifier
                    .background(liveColor, RoundedCornerShape(2.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp),
            ) {
                Text(
                    text = "LIVE",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            Text(
                text = currentProgram.title,
                fontSize = 11.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }

        // Progress bar using pre-created gradient brush
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(progressBg),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(3.dp)
                    .background(progressGradientBrush),
            )
        }

        // Next program (if available)
        nextProgram?.let { next ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Next: ",
                    fontSize = 9.sp,
                    color = Color.Gray,
                )
                Text(
                    text = next.title,
                    fontSize = 9.sp,
                    color = Color.LightGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = timeFormatter.format(Date(next.startTime)),
                    fontSize = 9.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}

/**
 * Compact version showing just the current program name with progress.
 * For use in smaller card layouts.
 */
@Composable
fun NowIndicatorCompact(
    currentProgram: EpgProgram?,
    modifier: Modifier = Modifier,
) {
    if (currentProgram == null) return

    // Use mutable state that updates periodically for live progress
    var progress by remember { mutableFloatStateOf(0f) }
    
    // Calculate initial progress and update periodically
    LaunchedEffect(currentProgram) {
        while (true) {
            val now = System.currentTimeMillis()
            val elapsed = now - currentProgram.startTime
            val duration = currentProgram.endTime - currentProgram.startTime
            progress = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
            
            // Stop updating if program has ended
            if (now >= currentProgram.endTime) break
            
            delay(PROGRESS_UPDATE_INTERVAL_MS)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = currentProgram.title,
            fontSize = 10.sp,
            color = Color.LightGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(progressBg),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(2.dp)
                    .background(accentColor),
            )
        }
    }
}
