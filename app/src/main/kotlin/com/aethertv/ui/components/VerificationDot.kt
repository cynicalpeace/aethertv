package com.aethertv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    val isVerified = channel.isVerified
    val peerCount = channel.verifiedPeerCount ?: 0

    val (color, description) = when {
        isVerified == null -> Color.Gray to "Not verified"
        !isVerified -> VerifiedRed to "Offline"
        peerCount < 5 -> VerifiedAmber to "Low peers, may be unstable"
        else -> VerifiedGreen to "Online with ${peerCount} peers"
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
            .semantics { contentDescription = description },
    )
}
