package com.aethertv.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.aethertv.domain.model.Channel

@Composable
fun ChannelCard(
    channel: Channel,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        onLongClick = onLongClick ?: {},
        modifier = modifier
            .width(180.dp)
            .height(120.dp),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            ),
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
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
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = channel.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (channel.verifiedQuality != null) {
                    QualityBadge(quality = channel.verifiedQuality)
                }
            }
        }
    }
}
