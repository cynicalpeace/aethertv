package com.aethertv.ui.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aethertv.domain.model.Channel

@Composable
fun MiniChannelList(
    channels: List<Channel>,
    currentInfohash: String,
    onChannelSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.width(300.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(channels, key = { it.infohash }) { channel ->
            ListItem(
                selected = channel.infohash == currentInfohash,
                onClick = { onChannelSelected(channel.infohash) },
                headlineContent = {
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
            )
        }
    }
}
