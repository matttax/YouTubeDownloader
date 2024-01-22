package com.matttax.youtubedownloader.core.ui

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun Player(
    exoPlayer: ExoPlayer,
    onPause: () -> Unit = { },
    modifier: Modifier = Modifier,
    showNavigationButtons: Boolean = false
) {
    var lifecycle by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f),
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                setShowNextButton(showNavigationButtons)
                setShowPreviousButton(showNavigationButtons)
            }
        },
        update = {
            if (lifecycle == Lifecycle.Event.ON_PAUSE) {
                onPause()
            }
        },
    )
}
