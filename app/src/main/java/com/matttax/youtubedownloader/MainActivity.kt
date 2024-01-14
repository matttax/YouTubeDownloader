package com.matttax.youtubedownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.matttax.youtubedownloader.youtube.presentation.SearchViewModel
import com.matttax.youtubedownloader.core.ui.utils.BottomNavigationItem
import com.matttax.youtubedownloader.youtube.presentation.ui.MediaData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val bottomItems = listOf(
        BottomNavigationItem(
            title = "Search",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search,
        ),
        BottomNavigationItem(
            title = "Saved",
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.Favorite,
        ),
        BottomNavigationItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
        ),
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val viewModel: SearchViewModel by viewModels()
        setContent {
            MediaData(viewModel)
//            var selectedBottomItemIndex by rememberSaveable { mutableStateOf(0) }
//            Scaffold(
//                bottomBar = {
//                    NavigationBar {
//                        bottomItems.forEachIndexed { index, item ->
//                            NavigationBarItem(
//                                selected = selectedBottomItemIndex == index,
//                                onClick = {
//                                    selectedBottomItemIndex = index
//                                    // navController.navigate(item.title)
//                                },
//                                label = {
//                                    Text(text = item.title)
//                                },
//                                alwaysShowLabel = false,
//                                icon = {
//                                    BadgedBox(
//                                        badge = {
//                                            if (item.badgeCount != null) {
//                                                Badge {
//                                                    Text(text = item.badgeCount.toString())
//                                                }
//                                            } else if (item.hasNews) {
//                                                Badge()
//                                            }
//                                        }
//                                    ) {
//                                        Icon(
//                                            imageVector = if (index == selectedBottomItemIndex) {
//                                                item.selectedIcon
//                                            } else item.unselectedIcon,
//                                            contentDescription = item.title
//                                        )
//                                    }
//                                }
//                            )
//                        }
//                    }
//                },
//            ) {
//                MediaData(viewModel)
//            }
        }
    }
}
