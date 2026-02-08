package com.aethertv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import com.aethertv.domain.model.Channel

@Composable
fun ChannelRow(
    channels: List<Channel>,
    onChannelClick: (String) -> Unit,
    onChannelLongClick: ((String) -> Unit)? = null,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(channels, key = { it.infohash }) { channel ->
            ChannelCard(
                channel = channel,
                onClick = { onChannelClick(channel.infohash) },
                onLongClick = onChannelLongClick?.let { { it(channel.infohash) } },
            )
        }
    }
}
