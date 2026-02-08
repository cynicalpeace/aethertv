package com.aethertv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aethertv.domain.model.StreamQuality
import com.aethertv.ui.theme.QualityBadgeBackground

@Composable
fun QualityBadge(
    quality: StreamQuality,
    modifier: Modifier = Modifier,
) {
    if (quality == StreamQuality.UNKNOWN) return

    Text(
        text = quality.label,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(QualityBadgeBackground)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}
