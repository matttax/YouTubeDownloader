package com.matttax.youtubedownloader.core.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.matttax.youtubedownloader.R
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed
import com.matttax.youtubedownloader.core.ui.utils.UiMediaModel
import com.matttax.youtubedownloader.core.ui.utils.secondsToDuration
import kotlin.math.roundToInt

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlayableMediaItem(
    videoData: UiMediaModel,
    playingState: PlayingState,
    onClick: (String) -> Unit,
    onSwipe: (String) -> Unit = { },
    swipeEnabled: Boolean = true,
    swipeContent: @Composable (Boolean) -> Unit = { }
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedPlayIconSize by infiniteTransition.animateFloat(
        initialValue = 35f,
        targetValue = 45f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
    )
    var menuShown by rememberSaveable { mutableStateOf(false) }

    val pxToMove = with(LocalDensity.current) {
        (-100).dp.toPx().roundToInt()
    }
    val offsetAnimation by animateIntOffsetAsState(
        targetValue = if (menuShown) {
            IntOffset(pxToMove, 0)
        } else {
            IntOffset.Zero
        }
    )
    if (!swipeEnabled) {
        menuShown = false
    }

    Box(
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clickable {
                    menuShown = false
                    onClick(videoData.id)
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (!swipeEnabled) {
                            return@detectHorizontalDragGestures
                        }
                        if (dragAmount.dp.value > 10 && menuShown) {
                            menuShown = false
                        } else if (dragAmount.dp.value < -30) {
                            menuShown = true
                            onSwipe(videoData.id)
                        }
                    }
                }
                .offset { offsetAnimation },
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                GlideImage(
                    modifier = Modifier
                        .size(80.dp)
                        .aspectRatio(15 / 8f)
                        .clip(RoundedCornerShape(20.dp))
                        .blur(if (playingState != PlayingState.NONE) 5.dp else 0.dp),
                    model = videoData.thumbnailUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                when(playingState) {
                    PlayingState.PLAYING -> Image(
                        modifier = Modifier.size(animatedPlayIconSize.dp),
                        painter = painterResource(R.drawable.ic_play_arrow),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(YouTubeRed)
                    )
                    PlayingState.PAUSED -> Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(R.drawable.ic_pause),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(YouTubeRed)
                    )
                    else -> {}
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                Text(text = videoData.name, overflow = TextOverflow.Ellipsis, maxLines = 1)
                Text(text = videoData.author, color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.padding(end = 5.dp),
                text = videoData.duration.secondsToDuration()
            )
        }
        swipeContent(menuShown)
    }
}

enum class PlayingState {
    PLAYING, PAUSED, NONE
}
