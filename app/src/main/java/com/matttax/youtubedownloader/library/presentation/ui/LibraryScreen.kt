package com.matttax.youtubedownloader.library.presentation.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.matttax.youtubedownloader.core.ui.Player
import com.matttax.youtubedownloader.core.ui.utils.setScreenOrientation
import com.matttax.youtubedownloader.library.presentation.LibraryViewModel

@Composable
fun LibraryScreen(
    modifier: Modifier,
    viewModel: LibraryViewModel,
    onFullscreenEnter: (Boolean) -> Unit = { },
) {
    val isMediaSelected by viewModel.isMediaItemSelected.collectAsState()
    val name by viewModel.playlistName.collectAsState()
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        fullscreen = true.also { onFullscreenEnter(it) }
    }

    Column(
        modifier = modifier
    ) {
        if (!fullscreen) {
            AnimatedVisibility(
                visible = !isMediaSelected,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = LinearOutSlowInEasing
                    )
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Playlists(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.16f)
                        .padding(vertical = 7.dp)
                        .shadow(1.dp),
                    playlistsState = viewModel.playlists,
                    onNewCreate = viewModel::onAddPlaylist,
                    onSelected = viewModel::onChoosePlaylist
                )
            }
            MediaList(
                modifier = Modifier.weight(0.7f),
                viewModel = viewModel
            )
            AnimatedVisibility(
                visible = isMediaSelected,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = LinearOutSlowInEasing
                    )
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Player(
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(bottom = 15.dp)
                        .fillMaxWidth()
                        .aspectRatio(16 / 9f),
                    exoPlayer = viewModel.getExoInstance(),
                    showNavigationButtons = true,
                    onFullscreen = {
                        fullscreen = fullscreen.not().also { onFullscreenEnter(it) }
                    }
                )
            }
        } else {
            Player(
                modifier = Modifier.fillMaxSize(),
                exoPlayer = viewModel.getExoInstance(),
                showNavigationButtons = true,
                onFullscreen = {
                    context.setScreenOrientation(Configuration.ORIENTATION_PORTRAIT)
                    fullscreen = false.also { onFullscreenEnter(it) }
                }
            )
            SideEffect {
                context.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE)
            }
        }
    }

    BackHandler(enabled = isMediaSelected || name != "All media" || fullscreen) {
        if (fullscreen) {
            context.setScreenOrientation(Configuration.ORIENTATION_PORTRAIT)
            fullscreen = false.also { onFullscreenEnter(it) }
        } else if (isMediaSelected) {
            viewModel.onStopPlayback()
        } else if (name != "All media") {
            viewModel.onChoosePlaylist(null)
        }
    }
}
