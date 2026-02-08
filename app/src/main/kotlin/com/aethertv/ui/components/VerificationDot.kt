package com.aethertv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aethertv.domain.model.Channel
import com.aethertv.ui.theme.VerifiedAmber
import com.aethertv.ui.theme.VerifiedGreen
import com.aethertv.ui.theme.VerifiedRed

@Composable
fun VerificationDot(
    channel: Channel,
    modifier: Modifier = Modifier,
) {
    val isVerified = channel.isVerified ?: return
    val peerCount = channel.verifiedPeerCount ?: 0

    val color = when {
        !isVerified -> VerifiedRed
        peerCount < 5 -> VerifiedAmber
        else -> VerifiedGreen
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color),
    )
}
