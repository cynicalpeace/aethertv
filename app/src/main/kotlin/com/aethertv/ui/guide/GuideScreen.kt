package com.aethertv.ui.guide

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@Composable
fun GuideScreen(
    onNavigateToPlayer: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: GuideViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp),
    ) {
        Text(
            text = "TV Guide",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        // EPG grid will be implemented in Phase 1 Week 4
        Text(
            text = "EPG grid loading...",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
