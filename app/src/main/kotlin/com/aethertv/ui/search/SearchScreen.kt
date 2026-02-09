package com.aethertv.ui.search

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.aethertv.ui.components.ChannelCard

@Composable
fun SearchScreen(
    onNavigateToPlayer: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val resultsFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    
    // Request focus with error handling (H28 fix)
    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (e: IllegalStateException) {
            // FocusRequester not attached yet - this is OK, focus will be gained on interaction
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .onKeyEvent { event ->
                if (event.nativeKeyEvent.action == KeyEvent.ACTION_DOWN &&
                    event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_BACK) {
                    onBack()
                    true
                } else false
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 24.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Search Channels",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "${uiState.results.size} results",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search input with D-pad navigation support
            BasicTextField(
                value = uiState.query,
                onValueChange = { viewModel.updateQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onKeyEvent { event ->
                        // Handle D-pad down to move focus to results grid
                        if (event.type == KeyEventType.KeyDown) {
                            when (event.key) {
                                Key.DirectionDown -> {
                                    if (uiState.results.isNotEmpty()) {
                                        focusManager.moveFocus(FocusDirection.Down)
                                        true
                                    } else false
                                }
                                Key.Back, Key.Escape -> {
                                    onBack()
                                    true
                                }
                                else -> false
                            }
                        } else false
                    }
                    .background(Color(0xFF1A1A1A), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔍",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box {
                            if (uiState.query.isEmpty()) {
                                Text(
                                    text = "Type to search...",
                                    color = Color.Gray,
                                    fontSize = 18.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Search results grid
            if (uiState.results.isEmpty() && uiState.query.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "😔",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No channels found",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Try a different search term",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    contentPadding = PaddingValues(bottom = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(uiState.results, key = { it.infohash }) { channel ->
                        ChannelCard(
                            channel = channel,
                            onClick = { onNavigateToPlayer(channel.infohash) },
                        )
                    }
                }
            }
        }
    }
}
