package com.matttax.youtubedownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.matttax.youtubedownloader.navigation.BottomNavigationItems
import com.matttax.youtubedownloader.navigation.ui.BottomNavigationBar
import com.matttax.youtubedownloader.youtube.presentation.SearchViewModel
import com.matttax.youtubedownloader.youtube.presentation.ui.MediaData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val navController = rememberNavController()
            val searchViewModel: SearchViewModel by viewModels()
            Column {
                NavHost(
                    navController = navController,
                    startDestination = BottomNavigationItems.YOUTUBE.routeName,
                ) {
                    composable(route = BottomNavigationItems.YOUTUBE.routeName) {
                        MediaData(
                            modifier = Modifier.fillMaxHeight(0.95f),
                            viewModel = searchViewModel
                        )
                        BackHandler(true) {
                            // Hide player
                        }
                    }
                    composable(route = BottomNavigationItems.LIBRARY.routeName) {
                        Text(
                            modifier = Modifier.fillMaxHeight(0.95f),
                            text = "Library"
                        )
                        BackHandler(true) {}
                    }
                    composable(route = BottomNavigationItems.SETTINGS.routeName) {
                        Text(
                            modifier = Modifier.fillMaxHeight(0.95f),
                            text = "Settings"
                        )
                        BackHandler(true) {}
                    }
                }
                BottomNavigationBar(navController)
            }
        }
    }
}
