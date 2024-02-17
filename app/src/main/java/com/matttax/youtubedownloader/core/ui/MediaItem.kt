package com.matttax.youtubedownloader.core.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.matttax.youtubedownloader.core.ui.utils.secondsToDuration

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun MediaItem(
    videoData: UiMediaModel,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit = { },
) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .combinedClickable (
                onClick = { onClick(videoData.id) },
                onLongClick = { onLongClick(videoData.id) }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.BottomEnd
        ) {
            GlideImage(
                modifier = Modifier.fillMaxSize(),
                model = videoData.thumbnailUri,
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Text(
                modifier = Modifier
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(50.dp)
                    )
                    .padding(
                        start = 4.dp,
                        end = 3.dp,
                        top = 1.dp,
                        bottom = 3.dp
                    ),
                text = videoData.duration.secondsToDuration(),
                fontSize = 10.sp,
                color = Color.White
            )
        }
        Column(
            modifier = Modifier.padding(
                vertical = 3.dp,
                horizontal = 5.dp
            ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
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
