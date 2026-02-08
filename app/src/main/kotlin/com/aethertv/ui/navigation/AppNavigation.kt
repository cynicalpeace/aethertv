package com.aethertv.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aethertv.data.preferences.SettingsDataStore
import com.aethertv.ui.guide.GuideScreen
import com.aethertv.ui.home.HomeScreen
import com.aethertv.ui.player.PlayerScreen
import com.aethertv.ui.search.SearchScreen
import com.aethertv.ui.settings.SettingsScreen
import com.aethertv.ui.setup.FirstRunScreen
import kotlinx.serialization.Serializable

// Type-safe navigation routes
@Serializable
object FirstRunRoute

@Serializable
object HomeRoute

@Serializable
object GuideRoute

@Serializable
data class PlayerRoute(val infohash: String)

@Serializable
object SearchRoute

@Serializable
object SettingsRoute

@Composable
fun AetherTvNavHost(
    navController: NavHostController = rememberNavController(),
    isFirstRun: Boolean = true,
    initialSearchQuery: String? = null,
    onSearchHandled: () -> Unit = {},
) {
    // Handle initial search query
    LaunchedEffect(initialSearchQuery) {
        if (initialSearchQuery != null && !isFirstRun) {
            navController.navigate(SearchRoute)
            onSearchHandled()
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = if (isFirstRun) FirstRunRoute else HomeRoute,
    ) {
        composable<FirstRunRoute> {
            FirstRunScreen(
                onSetupComplete = {
                    navController.navigate(HomeRoute) {
                        popUpTo(FirstRunRoute) { inclusive = true }
                    }
                }
            )
        }
        composable<HomeRoute> {
            HomeScreen(
                onNavigateToPlayer = { infohash ->
                    navController.navigate(PlayerRoute(infohash))
                },
                onNavigateToGuide = {
                    navController.navigate(GuideRoute)
                },
                onNavigateToSearch = {
                    navController.navigate(SearchRoute)
                },
                onNavigateToSettings = {
                    navController.navigate(SettingsRoute)
                },
            )
        }
        composable<GuideRoute> {
            GuideScreen(
                onNavigateToPlayer = { infohash ->
                    navController.navigate(PlayerRoute(infohash))
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable<PlayerRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<PlayerRoute>()
            PlayerScreen(
                infohash = route.infohash,
                onBack = { navController.popBackStack() },
            )
        }
        composable<SearchRoute> {
            SearchScreen(
                onNavigateToPlayer = { infohash ->
                    navController.navigate(PlayerRoute(infohash))
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable<SettingsRoute> {
            SettingsScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
