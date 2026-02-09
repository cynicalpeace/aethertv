package com.aethertv.ui

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aethertv.data.preferences.SettingsDataStore
import com.aethertv.ui.navigation.AetherTvNavHost
import com.aethertv.ui.theme.AetherTvTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val initialSearchQuery = handleSearchIntent(intent)
        
        setContent {
            val isFirstRun by settingsDataStore.isFirstRun.collectAsState(initial = true)
            val highContrast by settingsDataStore.highContrastEnabled.collectAsState(initial = false)
            var searchQuery by remember { mutableStateOf(initialSearchQuery) }
            
            AetherTvTheme(highContrast = highContrast) {
                AetherTvNavHost(
                    isFirstRun = isFirstRun,
                    initialSearchQuery = searchQuery,
                    onSearchHandled = { searchQuery = null }
                )
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Handle new search intents
        handleSearchIntent(intent)?.let { query ->
            // Trigger recomposition with new search
            recreate()
        }
    }
    
    private fun handleSearchIntent(intent: Intent): String? {
        return when (intent.action) {
            Intent.ACTION_SEARCH -> {
                intent.getStringExtra(SearchManager.QUERY)
            }
            Intent.ACTION_VIEW -> {
                // Handle deep link from search suggestion
                intent.dataString?.let { uri ->
                    if (uri.startsWith("aethertv://channel/")) {
                        // Extract infohash and navigate to player
                        // For now, we'll handle this via the navigation
                        null
                    } else null
                }
            }
            else -> null
        }
    }
}
