package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MediaItem(videoData: YoutubeVideoMetadata, onClick: (String) -> Unit) {
    var clicked by rememberSaveable { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clickable { clicked = !clicked },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                modifier = Modifier
                    .size(80.dp)
                    .border(
                        width = 3.dp,
                        color = Color.Black.copy(),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .weight(1f),
                model = videoData.thumbnailUri,
                contentDescription = null,
            )
            Spacer(
                modifier = Modifier.weight(0.05f)
            )
            Column(
                modifier = Modifier.weight(1.5f)
            ) {
                Text(
                    text = videoData.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = videoData.author,
                    maxLines = 1,
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
        if (clicked) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                onClick(videoData.id)
            }
        }
    }
}
