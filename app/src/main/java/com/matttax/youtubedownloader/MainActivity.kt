package com.matttax.youtubedownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed
import com.matttax.youtubedownloader.navigation.BottomNavigationItems
import com.matttax.youtubedownloader.navigation.ui.BottomNavigationBar
import com.matttax.youtubedownloader.settings.presentation.SettingsViewModel
import com.matttax.youtubedownloader.settings.presentation.ui.SettingsScreen
import com.matttax.youtubedownloader.youtube.presentation.SearchViewModel
import com.matttax.youtubedownloader.youtube.presentation.ui.MediaData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val navController = rememberNavController()
            val searchViewModel: SearchViewModel by viewModels()
            val settingsViewModel: SettingsViewModel by viewModels()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            Column {
                AnimatedContent(
                    targetState = navBackStackEntry,
                    transitionSpec = {
                        EnterTransition.None togetherWith ExitTransition.None
                    }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier = Modifier
                                .background(
                                    color = YouTubeRed,
                                    shape = RoundedCornerShape(30.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 3.dp)
                                .animateEnterExit(
                                    enter = scaleIn(),
                                    exit = scaleOut()
                                ),
                            text = navBackStackEntry?.destination?.route.routeToScreenName(),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                NavHost(
                    navController = navController,
                    startDestination = BottomNavigationItems.YOUTUBE.routeName
                ) {
                    composable(route = BottomNavigationItems.YOUTUBE.routeName) {
                        MediaData(
                            modifier = Modifier.fillMaxHeight(0.95f),
                            viewModel = searchViewModel
                        )
                    }
                    composable(route = BottomNavigationItems.LIBRARY.routeName) {
                        searchViewModel.onQuit()
                        Text(
                            modifier = Modifier.fillMaxHeight(0.95f),
                            text = "Library"
                        )
                        BackHandler(true) {}
                    }
                    composable(route = BottomNavigationItems.SETTINGS.routeName) {
                        searchViewModel.onQuit()
                        SettingsScreen(
                            modifier = Modifier.fillMaxHeight(0.95f),
                            viewModel = settingsViewModel
                        )
                        BackHandler(true) {}
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
