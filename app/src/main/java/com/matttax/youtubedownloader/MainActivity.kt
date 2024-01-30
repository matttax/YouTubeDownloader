package com.matttax.youtubedownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed
import com.matttax.youtubedownloader.library.presentation.ui.LibraryScreen
import com.matttax.youtubedownloader.library.presentation.LibraryViewModel
import com.matttax.youtubedownloader.navigation.BottomNavigationItems
import com.matttax.youtubedownloader.navigation.ui.BottomNavigationBar
import com.matttax.youtubedownloader.navigation.ui.NavigationAnimations
import com.matttax.youtubedownloader.navigation.ui.TabNameBar
import com.matttax.youtubedownloader.settings.presentation.SettingsViewModel
import com.matttax.youtubedownloader.settings.presentation.ui.SettingsScreen
import com.matttax.youtubedownloader.youtube.presentation.SearchViewModel
import com.matttax.youtubedownloader.youtube.presentation.ui.YoutubeSearchScreen
import com.matttax.youtubedownloader.youtube.search.CacheManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val navController = rememberNavController()
            val searchViewModel: SearchViewModel by viewModels()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            Column {
                navBackStackEntry?.let { TabNameBar(it) }
                NavHost(
                    navController = navController,
                    startDestination = BottomNavigationItems.LIBRARY.routeName
                ) {
                    composable(
                        route = BottomNavigationItems.YOUTUBE.routeName,
                        enterTransition = NavigationAnimations.enterTransition,
                        exitTransition = NavigationAnimations.exitTransition
                    ) {
                        YoutubeSearchScreen(
                            modifier = Modifier.fillMaxHeight(0.95f),
                            viewModel = searchViewModel,
                            onBack = { finishAffinity() }
                        )
                    }
                    composable(
                        route = BottomNavigationItems.LIBRARY.routeName,
                        enterTransition = NavigationAnimations.enterTransition,
                        exitTransition = NavigationAnimations.exitTransition
                    ) {
                        val libraryViewModel: LibraryViewModel by viewModels()
                        searchViewModel.onQuit()
                        LibraryScreen(
                            modifier = Modifier.fillMaxHeight(0.95f),
                            viewModel = libraryViewModel,
                            onBack = { finishAffinity() }
                        )
                    }
                    composable(
                        route = BottomNavigationItems.SETTINGS.routeName,
                        enterTransition = NavigationAnimations.enterTransition,
                        exitTransition = NavigationAnimations.exitTransition
                    ) {
                        val settingsViewModel: SettingsViewModel by viewModels()
                        searchViewModel.onQuit()
                        SettingsScreen(
                            modifier = Modifier.fillMaxHeight(0.95f),
                            viewModel = settingsViewModel
                        )
                    }
                }
                BottomNavigationBar(navController)
            }
        }
    }
}

fun String?.routeToScreenName(): String {
    return when(this) {
        BottomNavigationItems.LIBRARY.routeName -> "Library"
        BottomNavigationItems.SETTINGS.routeName -> "Settings"
        else -> "YouTube"
    }
}
