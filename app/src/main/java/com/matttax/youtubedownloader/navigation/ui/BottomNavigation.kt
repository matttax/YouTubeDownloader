package com.matttax.youtubedownloader.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.matttax.youtubedownloader.R
import com.matttax.youtubedownloader.navigation.BottomNavigationItems

@Composable
fun BottomNavigationBar(
    modifier: Modifier,
    navController: NavController,
    initialItem: BottomNavigationItems
) {
    Row(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.Top
    ) {
        var selectedBottomItem by rememberSaveable { mutableStateOf(initialItem) }

        Spacer(modifier = Modifier.weight(0.4f))
        BottomNavigationItem(
            modifier = Modifier.weight(0.3f),
            drawableId = R.drawable.ic_library,
            selectionCondition = { selectedBottomItem == BottomNavigationItems.LIBRARY },
            onClick = {
                selectedBottomItem = BottomNavigationItems.LIBRARY
                navController.navigate(BottomNavigationItems.LIBRARY.routeName) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        BottomNavigationItem(
            modifier = Modifier.weight(0.3f),
            drawableId = R.drawable.ic_youtube,
            selectionCondition = { selectedBottomItem == BottomNavigationItems.YOUTUBE },
            onClick = {
                selectedBottomItem = BottomNavigationItems.YOUTUBE
                navController.navigate(BottomNavigationItems.YOUTUBE.routeName) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        BottomNavigationItem(
            modifier = Modifier.weight(0.3f),
            drawableId = R.drawable.ic_settings,
            selectionCondition = { selectedBottomItem == BottomNavigationItems.SETTINGS },
            onClick = {
                selectedBottomItem = BottomNavigationItems.SETTINGS
                navController.navigate(BottomNavigationItems.SETTINGS.routeName) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
        Spacer(modifier = Modifier.weight(0.4f))
    }
}
