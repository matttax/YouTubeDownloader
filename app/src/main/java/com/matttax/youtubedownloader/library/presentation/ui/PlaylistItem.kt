package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlaylistItem(
    drawableId: Int,
    text: String,
    onClick: () -> Unit
) {
    Button(
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        shape = CircleShape,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier
                    .size(35.dp)
                    .background(
                        shape = CircleShape,
                        color = if (!isSystemInDarkTheme())
                            MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.onBackground
                    )
                    .padding(5.dp),
                painter = painterResource(id = drawableId),
                contentDescription = null,
            )
            Text(
                text = if (text.length >= 15) text.substring(0, 12).plus("...") else text,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
