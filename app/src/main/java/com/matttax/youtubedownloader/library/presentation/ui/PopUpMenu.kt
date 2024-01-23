package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed

@Composable
fun PopUpMenu(
    isPopped: Boolean,
    onDeleteClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit
) {
    val defaultShape = remember { RoundedCornerShape(10.dp) }
    AnimatedVisibility(
        visible = isPopped,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            )
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(end = 8.dp)
                .shadow(
                    elevation = 15.dp,
                    shape = defaultShape
                )
                .background(
                    color = Color.White,
                    shape = defaultShape
                )
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = defaultShape
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier.height(45.dp),
                shape = defaultShape,
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                onClick = onDeleteClick
            ) {
                Text(
                    text = "Delete",
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }
            Divider(
                color = Color.LightGray,
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .width(1.dp)
            )
            Button(
                modifier = Modifier.height(45.dp),
                shape = defaultShape,
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                onClick = onAddToPlaylistClick
            ) {
                Text(
                    text = "Add to playlist",
                    color = YouTubeRed,
                    fontSize = 12.sp
                )
            }
        }
    }
}