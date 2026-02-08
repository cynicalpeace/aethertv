package com.aethertv.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.aethertv.domain.model.Channel
import com.aethertv.domain.model.EpgProgram

@Composable
fun ChannelCard(
    channel: Channel,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    currentProgram: EpgProgram? = null,
    nextProgram: EpgProgram? = null,
    modifier: Modifier = Modifier,
) {
    val hasEpg = currentProgram != null
    val cardHeight = if (hasEpg) 140.dp else 120.dp

    Card(
        onClick = onClick,
        onLongClick = onLongClick ?: {},
        modifier = modifier
            .width(180.dp)
            .height(cardHeight),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            ),
        ),
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            Column {
                // Header row: icon, verification dot, favorite
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (channel.iconUrl != null) {
                            AsyncImage(
                                model = channel.iconUrl,
                                contentDescription = channel.name,
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        VerificationDot(channel = channel)
                    }
                    // Favorite indicator
                    if (channel.isFavorite) {
                        Text(
                            text = "⭐",
                            fontSize = 16.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Channel name
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                
                // Quality badge and category
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (channel.verifiedQuality != null) {
                        QualityBadge(quality = channel.verifiedQuality)
                    }
                    channel.categories.firstOrNull()?.let { category ->
                        Text(
                            text = category.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }

                // Now/Next EPG info
                if (hasEpg) {
                    Spacer(modifier = Modifier.height(6.dp))
                    NowIndicatorCompact(
                        currentProgram = currentProgram,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
