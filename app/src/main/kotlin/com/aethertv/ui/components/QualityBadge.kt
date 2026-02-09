package com.aethertv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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

    val accessibilityLabel = when (quality) {
        StreamQuality.FHD_1080P -> "Full HD 1080p quality"
        StreamQuality.HD_720P -> "HD 720p quality"
        StreamQuality.SD_480P -> "Standard definition 480p"
        StreamQuality.LOW -> "Low quality"
        StreamQuality.UNKNOWN -> ""
    }

    Text(
        text = quality.label,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(QualityBadgeBackground)
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .semantics { contentDescription = accessibilityLabel },
    )
}
