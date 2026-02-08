package com.aethertv.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
        setContent {
            val isFirstRun by settingsDataStore.isFirstRun.collectAsState(initial = true)
            
            AetherTvTheme {
                AetherTvNavHost(isFirstRun = isFirstRun)
            }
        }
    }
}
