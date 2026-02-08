package com.aethertv.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Text

@Composable
fun PlayerScreen(
    infohash: String,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    DisposableEffect(infohash) {
        viewModel.loadChannel(infohash)
        onDispose { viewModel.releasePlayer() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        // ExoPlayer surface will be integrated in Phase 1 Week 3
        if (uiState.isLoading) {
            Text(text = "Loading stream...")
        }
    }
}
