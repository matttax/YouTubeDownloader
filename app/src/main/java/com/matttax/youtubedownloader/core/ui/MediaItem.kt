package com.matttax.youtubedownloader.core.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun MediaItem(
    videoData: UiMediaModel,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit = { },
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .combinedClickable (
                    onClick = { onClick(videoData.id) },
                    onLongClick = { onLongClick(videoData.id) }
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(height = 100.dp, width = 120.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .border(width = 2.dp, color = Color.Black)
                    .weight(0.9f),
                contentAlignment = Alignment.BottomEnd
            ) {
                GlideImage(
                    modifier = Modifier.fillMaxSize(),
                    model = videoData.thumbnailUri,
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight
                )
                Text(
                    modifier = Modifier
                        .background(
                            color = Color.Black,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(
                            start = 3.dp,
                            end = 3.dp,
                            top = 1.dp,
                            bottom = 3.dp
                        ),
                    text = videoData.duration.secondsToDuration(),
                    fontSize = 10.sp,
                    color = Color.White
                )
            }
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
    }
}

@Immutable
data class UiMediaModel(
    val id: String,
    val thumbnailUri: String,
    val name: String,
    val author: String,
    val duration: Int
)

fun Int.secondsToDuration(): String {
    val hours = div(3600)
    val minutes = mod(3600).div(60)
    val seconds = mod(60)
    return if (hours > 0)
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    else
        String.format("%02d:%02d", minutes, seconds)
}
