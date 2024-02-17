package com.matttax.youtubedownloader

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.matttax.youtubedownloader.core.ui.utils.hideSystemUi
import com.matttax.youtubedownloader.core.ui.utils.setScreenOrientation
import com.matttax.youtubedownloader.core.ui.utils.showSystemUi
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setScreenOrientation(Configuration.ORIENTATION_PORTRAIT)
        installSplashScreen()
        val sharedPrefs = applicationContext.getSharedPreferences(BASIC_SETTINGS, Context.MODE_PRIVATE)
        val initialItem = sharedPrefs.getString(BASIC_SETTINGS_TAB_KEY, BottomNavigationItems.LIBRARY.routeName)
            ?: BottomNavigationItems.LIBRARY.routeName
        setContent {
            showSystemUi()
            val navController = rememberNavController()
            val searchViewModel: SearchViewModel by viewModels()
            val settingsViewModel: SettingsViewModel by viewModels()
            val libraryViewModel: LibraryViewModel by viewModels()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            var fullscreen by rememberSaveable { mutableStateOf(true) }
            Column {
                if (fullscreen) {
                    TabNameBar(
                        name = navBackStackEntry?.destination?.route.routeToScreenName()
                    )
                }
                NavHost(
                    navController = navController,
                    startDestination = initialItem
                ) {
                    composable(
                        route = BottomNavigationItems.YOUTUBE.routeName,
                        enterTransition = NavigationAnimations.enterTransition,
                        exitTransition = NavigationAnimations.exitTransition
                    ) {
                        YoutubeSearchScreen(
                            modifier = Modifier
                                .fillMaxHeight(0.95f)
                                .padding(bottom = 10.dp),
                            viewModel = searchViewModel
                        )
                    }
                    composable(
                        route = BottomNavigationItems.LIBRARY.routeName,
                        enterTransition = NavigationAnimations.enterTransition,
                        exitTransition = NavigationAnimations.exitTransition
                    ) {
                        searchViewModel.onQuit()
                        LibraryScreen(
                            modifier = Modifier
                                .fillMaxHeight(if (fullscreen) 0.95f else 1f)
                                .padding(bottom = if (fullscreen) 10.dp else 0.dp),
                            viewModel = libraryViewModel
                        ) { isFullscreen ->
                            fullscreen = !isFullscreen
                            if (isFullscreen) {
                                hideSystemUi()
                            } else showSystemUi()
                        }
                    }
                    composable(
                        route = BottomNavigationItems.SETTINGS.routeName,
                        enterTransition = NavigationAnimations.enterTransition,
                        exitTransition = NavigationAnimations.exitTransition
                    ) {
                        searchViewModel.onQuit()
                        SettingsScreen(
                            modifier = Modifier.fillMaxHeight(0.95f),
                            viewModel = settingsViewModel
                        )
                    }
                }
                if (fullscreen) {
                    BottomNavigationBar(
                        navController,
                        ROUTES_MAP[initialItem] ?: BottomNavigationItems.LIBRARY
                    )
                }
            }
            LaunchedEffect(navBackStackEntry) {
                sharedPrefs.edit()
                    .putString(
                        BASIC_SETTINGS_TAB_KEY,
                        navBackStackEntry?.destination?.route ?: BottomNavigationItems.LIBRARY.routeName
                    ).commit()
            }
        }
    }

    companion object {
        val ROUTES_MAP = BottomNavigationItems.values().associateBy { it.routeName }
        const val BASIC_SETTINGS = "basics"
        const val BASIC_SETTINGS_TAB_KEY = "route"
    }
}

fun String?.routeToScreenName(): String {
    return when(this) {
        BottomNavigationItems.LIBRARY.routeName -> "Library"
        BottomNavigationItems.SETTINGS.routeName -> "Settings"
        else -> "YouTube"
    }
}
