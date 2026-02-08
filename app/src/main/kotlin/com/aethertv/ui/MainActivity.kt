package com.aethertv.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aethertv.ui.navigation.AetherTvNavHost
import com.aethertv.ui.theme.AetherTvTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AetherTvTheme {
                AetherTvNavHost()
            }
        }
    }
}
