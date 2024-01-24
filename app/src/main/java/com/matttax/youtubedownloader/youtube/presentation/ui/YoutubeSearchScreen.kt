package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata
import com.matttax.youtubedownloader.core.ui.MediaItem
import com.matttax.youtubedownloader.core.ui.Player
import com.matttax.youtubedownloader.core.ui.UiMediaModel
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed
import com.matttax.youtubedownloader.youtube.presentation.Error
import com.matttax.youtubedownloader.youtube.presentation.PagingState
import com.matttax.youtubedownloader.youtube.presentation.SearchViewModel
import com.matttax.youtubedownloader.youtube.presentation.YoutubeSearchState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

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
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.errorFlow.collectLatest {
            when(it) {
                is Error.NoStreamableLinkFound -> snackbarHostState.showSnackbar(
                    "No streaming link found\n${it.reason}"
                )
                is Error.NoFormatOptionsAvailable -> snackbarHostState.showSnackbar(
                    "No format options available"
                )
            }
        }
    }

    Scaffold(
        modifier = modifier.padding(bottom = 5.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column {
            SearchBar(
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
                    .border(
                        width = (0.3).dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(7.dp)
                    ),
                searchConfig = viewModel.searchConfig,
                onDurationChange = viewModel::onSetDuration,
                onUploadDateChange = viewModel::onSetUploaded,
                onSortingChange = viewModel::onSetSorting,
                onEvery = {
                    focusManager.clearFocus()
                    selectedVideo = null
                }
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
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier
                                .navigationBarsPadding()
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
                            println(pagingState.name)
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
                                        ErrorScreen(modifier = Modifier.fillMaxWidth().height(100.dp), viewModel::onNextPage)
                                    else -> Unit
                                }
                            }
                        }
                    }
                }
                is YoutubeSearchState.NetworkError -> {
                    ErrorScreen(modifier = Modifier.fillMaxSize(), viewModel::onSearch)
                }
            }
        }
    }
    
    BackHandler {
        if (videoReady) {
            focusManager.clearFocus()
        } else {
            onBackPressedDispatcher?.onBackPressed()
        }
    }
}

@Composable
fun SearchBar(
    searchText: StateFlow<String>,
    onChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val query by searchText.collectAsState()
    TextField(
        modifier = Modifier
            .background(color = Color.LightGray.copy(alpha = 0.2f))
            .fillMaxWidth()
            .padding(5.dp),
        value = query,
        onValueChange = onChange,
        placeholder = { Text("Search") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
        shape = RoundedCornerShape(20),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = YouTubeRed,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.LightGray.copy(alpha = 0.2f),
        ),
        textStyle = TextStyle.Default.copy(fontSize = 16.sp)
    )
}

fun YoutubeVideoMetadata.toUiModel(): UiMediaModel {
    return UiMediaModel(
        id, thumbnailUri, name, author, durationSeconds
    )
}
