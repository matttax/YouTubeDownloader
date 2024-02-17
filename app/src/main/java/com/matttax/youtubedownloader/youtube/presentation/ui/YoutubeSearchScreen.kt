package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata
import com.matttax.youtubedownloader.core.ui.MediaItem
import com.matttax.youtubedownloader.core.ui.Player
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed
import com.matttax.youtubedownloader.core.ui.UiMediaModel
import com.matttax.youtubedownloader.youtube.presentation.LoadingError
import com.matttax.youtubedownloader.youtube.presentation.SearchViewModel
import com.matttax.youtubedownloader.youtube.presentation.states.PagingState
import com.matttax.youtubedownloader.youtube.presentation.states.YoutubeSearchState
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun YoutubeSearchScreen(
    modifier: Modifier,
    viewModel: SearchViewModel
) {
    val searchState by viewModel.searchState.collectAsState()
    val pagingState by viewModel.pagingState.collectAsState()
    val videoReady by viewModel.isVideoReady.collectAsState(initial = false)
    val currentStreamable by viewModel.currentStreamable.collectAsState()

    var mediaItemKey = rememberSaveable { 0 }
    var selectedVideo by rememberSaveable { mutableStateOf<Int?>(null) }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.errorFlow.collectLatest {
            when(it) {
                is LoadingError.NoStreamableLinkFound -> snackbarHostState.showSnackbar(
                    "No streaming link found\n${it.reason}"
                )
                is LoadingError.NoFormatOptionsAvailable -> snackbarHostState.showSnackbar(
                    "No format options available"
                )
            }
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = searchState == YoutubeSearchState.Loading,
        refreshThreshold = 30.dp,
        onRefresh = viewModel::onRefresh
    )


    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column {
            SearchBar(
                interactionSource = interactionSource,
                searchText = viewModel.searchText,
                onChange = viewModel::onSearchTextChange
            ) {
                focusManager.clearFocus()
                selectedVideo = null
                viewModel.onSearch()
            }
            Filters(
                modifier = Modifier
                    .padding(horizontal = 10.dp),
                spinnerModifier = Modifier
                    .weight(0.3f)
                    .clip(RoundedCornerShape(7.dp)),
                searchConfig = viewModel.searchConfig,
                onDurationChange = viewModel::onSetDuration,
                onUploadDateChange = viewModel::onSetUploaded,
                onSortingChange = viewModel::onSetSorting,
                onEvery = {
                    focusManager.clearFocus()
                    selectedVideo = null
                }
            )
            Divider(
                modifier = Modifier.padding(
                    vertical = 3.dp,
                    horizontal = 5.dp
                )
            )
            when (searchState) {
                is YoutubeSearchState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(100.dp),
                            color = Color.Red.copy(alpha = 0.7f)
                        )
                    }
                }
                is YoutubeSearchState.Results -> {
                    (searchState as? YoutubeSearchState.Results)?.videoList?.let { list ->
                        Box(
                            modifier = Modifier
                                .pullRefresh(pullRefreshState),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(15.dp),
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .offset(
                                        y = 20.dp + (pullRefreshState.progress * 10)
                                            .roundToInt()
                                            .coerceAtMost(50).dp
                                    )
                                    .padding(5.dp),
                            ) {
                                items(
                                    count = list.size,
                                    key = {
                                        mediaItemKey += 1
                                        mediaItemKey
                                    }
                                ) {
                                    if (it == list.size - 1) {
                                        viewModel.onNextPage()
                                    }
                                    if (it == selectedVideo) {
                                        if (videoReady) {
                                            Column {
                                                currentStreamable?.metadata?.let { data ->
                                                    Metadata(data = data)
                                                }
                                                Player(
                                                    exoPlayer = viewModel.getExoInstance(),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .aspectRatio(16 / 9f),
                                                    onPause = viewModel::onPlayerHidden
                                                )
                                                StreamingOptions(
                                                    uriSelectionState = viewModel.uriSelectionState,
                                                    onMediaFormatChanged = viewModel::onMediaFormatChanged,
                                                    onQualityChanged = viewModel::onQualityChanged,
                                                    onMimeTypeChanged = viewModel::onMimeTypeChanged
                                                )
                                                Spacer(
                                                    modifier = Modifier.height(7.dp)
                                                )
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    DownloadButton(
                                                        downloading = viewModel.getCurrentDownloadState(),
                                                        action = viewModel::onDownload
                                                    )
                                                }
                                            }
                                        } else {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    color = Color.Red.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    } else {
                                        MediaItem(
                                            videoData = list[it].toUiModel(),
                                            onClick = { id ->
                                                focusManager.clearFocus()
                                                selectedVideo = it
                                                viewModel.onExtractData(id)
                                            }
                                        )
                                    }
                                }
                                item {
                                    when(pagingState) {
                                        PagingState.LOADING ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    color = Color.Red.copy(alpha = 0.7f)
                                                )
                                            }
                                        PagingState.NETWORK_ERROR ->
                                            ErrorScreen(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp),
                                                onRetry = viewModel::onNextPage
                                            )
                                        else -> Unit
                                    }
                                }
                            }
                            RefreshIndicator(
                                pullRefreshState = pullRefreshState,
                                refreshing = searchState == YoutubeSearchState.Loading
                            )
                        }
                    }
                }
                is YoutubeSearchState.NetworkError -> {
                    ErrorScreen(modifier = Modifier.fillMaxSize(), viewModel::onSearch)
                }
            }
        }
    }
    
    BackHandler(enabled = isFocused || currentStreamable != null) {
        if (isFocused) {
            focusManager.clearFocus()
        } else if (currentStreamable != null) {
            viewModel.onStopPlaying()
            selectedVideo = null
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RefreshIndicator(pullRefreshState: PullRefreshState, refreshing: Boolean) {
    PullRefreshIndicator(
        modifier = Modifier
            .fillMaxWidth(0.5f),
        state = pullRefreshState,
        refreshing = refreshing,
        contentColor = YouTubeRed
    )
}

fun YoutubeVideoMetadata.toUiModel(): UiMediaModel {
    return UiMediaModel(
        id, thumbnailUri, name, author, durationSeconds
    )
}
