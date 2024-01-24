package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matttax.youtubedownloader.R
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed

@Composable
fun ErrorScreen(
    modifier: Modifier,
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier
                .border(
                    color = Color.LightGray,
                    width = 1.dp,
                    shape = CircleShape
                ),
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            shape = CircleShape,
            onClick = onRetry
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(55.dp)
                        .background(
                            shape = CircleShape,
                            color = Color.Transparent
                        ),
                    painter = painterResource(id = R.drawable.ic_retry),
                    contentDescription = null,
                )
                Text(
                    text = "Retry",
                    color = YouTubeRed,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}