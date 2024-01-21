package com.matttax.youtubedownloader.navigation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed

@Composable
fun BottomNavigationItem(
    modifier: Modifier,
    drawableId: Int,
    selectionCondition: () -> Boolean,
    onClick: () -> Unit
) {
    Image(
        modifier = modifier
            .clickable(
                onClick = onClick,
                indication = rememberRipple(
                    bounded = false,
                    radius = 30.dp,
                    color = Color.Unspecified
                ),
                interactionSource = remember { MutableInteractionSource() }
            ),
        painter = painterResource(drawableId),
        contentDescription = null,
        colorFilter =
        if (selectionCondition())
            ColorFilter.tint(YouTubeRed)
        else ColorFilter.tint(Color.Gray)
    )
}
