package com.matttax.youtubedownloader.navigation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.matttax.youtubedownloader.R
import com.matttax.youtubedownloader.navigation.BottomNavigationItems

@Composable
fun BottomNavigationBar(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        var selectedBottomItem by rememberSaveable { mutableStateOf(BottomNavigationItems.LIBRARY) }

        Spacer(modifier = Modifier.weight(0.4f))
        BottomNavigationItem(
            modifier = Modifier
                .weight(0.3f),
            drawableId = R.drawable.ic_library,
            selectionCondition = { selectedBottomItem == BottomNavigationItems.LIBRARY },
            onClick = {
                selectedBottomItem = BottomNavigationItems.LIBRARY
                navController.navigate(BottomNavigationItems.LIBRARY.routeName)
            }
        )
        BottomNavigationItem(
            modifier = Modifier
                .weight(0.3f)
                .offset(y = (-7).dp),
            drawableId = R.drawable.ic_youtube,
            selectionCondition = { selectedBottomItem == BottomNavigationItems.YOUTUBE },
            onClick = {
                selectedBottomItem = BottomNavigationItems.YOUTUBE
                navController.navigate(BottomNavigationItems.YOUTUBE.routeName)
            }
        )
        BottomNavigationItem(
            modifier = Modifier
                .weight(0.3f),
            drawableId = R.drawable.ic_settings,
            selectionCondition = { selectedBottomItem == BottomNavigationItems.SETTINGS },
            onClick = {
                selectedBottomItem = BottomNavigationItems.SETTINGS
                navController.navigate(BottomNavigationItems.SETTINGS.routeName)
            }
        )
        Spacer(modifier = Modifier.weight(0.4f))
    }
}