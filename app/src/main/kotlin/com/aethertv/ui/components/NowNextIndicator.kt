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
import androidx.compose.runtime.remember
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val liveColor = Color(0xFFE63946)
private val accentColor = Color(0xFF00B4D8)
private val progressBg = Color(0xFF333333)

/**
 * Compact Now/Next indicator for channel cards.
 * Shows current program with progress bar and optional next program.
 */
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

    val now = System.currentTimeMillis()
    val progress = remember(currentProgram, now) {
        val elapsed = now - currentProgram.startTime
        val duration = currentProgram.endTime - currentProgram.startTime
        (elapsed.toFloat() / duration).coerceIn(0f, 1f)
    }

    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

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

        // Progress bar
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
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(liveColor, accentColor),
                        ),
                    ),
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

    val now = System.currentTimeMillis()
    val progress = remember(currentProgram, now) {
        val elapsed = now - currentProgram.startTime
        val duration = currentProgram.endTime - currentProgram.startTime
        (elapsed.toFloat() / duration).coerceIn(0f, 1f)
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
