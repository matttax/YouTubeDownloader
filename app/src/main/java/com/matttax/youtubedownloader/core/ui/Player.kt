package com.matttax.youtubedownloader.core.ui

import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.matttax.youtubedownloader.R

@OptIn(UnstableApi::class)
@Composable
fun Player(
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier,
    showNavigationButtons: Boolean = false,
    onPause: () -> Unit = { },
    onFullscreen: (() -> Unit)? = null
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
        modifier = modifier,
        factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
                setShowNextButton(showNavigationButtons)
                setShowPreviousButton(showNavigationButtons)
            }.also {
                val fullscreenButton = it.findViewById<FrameLayout>(R.id.exo_fullscreen_button)
                if (onFullscreen != null) {
                    fullscreenButton.setOnClickListener {
                        onFullscreen()
                    }
                } else fullscreenButton.isVisible = false
            }
        },
        update = {
            if (lifecycle == Lifecycle.Event.ON_PAUSE) {
                onPause()
            }
        },
    )
}
