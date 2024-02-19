package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun Option(resId: Int, onClick: () -> Unit = {}) {
    Image(
        modifier = Modifier
            .size(50.dp, 25.dp)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                shape = CircleShape,
                color = Color.Transparent
            )
            .clickable { onClick() },
        painter = painterResource(id = resId),
        contentDescription = null,
        colorFilter = ColorFilter.tint(color = Color.Black)
    )
}