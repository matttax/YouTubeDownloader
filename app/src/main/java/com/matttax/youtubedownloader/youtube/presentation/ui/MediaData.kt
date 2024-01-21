package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata
import com.matttax.youtubedownloader.youtube.presentation.SearchViewModel

@Composable
fun MediaData(
    modifier: Modifier,
    viewModel: SearchViewModel
) {
    val videoList by viewModel.videoList.collectAsState()
    val query by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val isLoadingPage by viewModel.isLoadingPage.collectAsState()
    var selectedVideo by rememberSaveable { mutableStateOf<Int?>(null) }

    val videoReady by viewModel.isVideoReady.collectAsState(initial = false)

    val currentStreamable by viewModel.currentStreamable.collectAsState()

    Column(
        modifier = modifier
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = viewModel::onSearchTextChange,
            placeholder = { Text("Search") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    selectedVideo = null
                    viewModel.onSearch(query)
                }
            )
        )
        Spacer(modifier = Modifier.height(7.dp))
        Filters(
            searchConfig = viewModel.searchConfig,
            onDurationChange = viewModel::onSetDuration,
            onUploadDateChange = viewModel::onSetUploaded,
            onSortingChange = viewModel::onSetSorting,
            onEvery = { selectedVideo = null }
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isSearching) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp),
                    color = Color.Red.copy(alpha = 0.7f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(5.dp),
            ) {
                items(
                    count = videoList.size,
                    key = { videoList[it].id + videoList.size }
                ) {
                    if (it == videoList.size - 1) {
                        viewModel.onNextPage()
                    }
                    if (it == selectedVideo) {
                        if (videoReady) {
                            Column {
                                currentStreamable?.metadata?.let { data ->
                                    Metadata(data = data)
                                }
                                Player(
                                    exoPlayer = viewModel.getExoInstance()
                                )
                                StreamingOptions(
                                    uriSelectionState = viewModel.uriSelectionState,
                                    onMediaFormatChanged = viewModel::onMediaFormatChanged,
                                    onQualityChanged = viewModel::onQualityChanged,
                                    onMimeTypeChanged = viewModel::onMimeTypeChanged
                                )
                                Spacer(
                                    modifier = Modifier.height(5.dp)
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
                                CircularProgressIndicator()
                            }
                        }
                    } else {
                        MediaItem(videoList[it].toUiModel()) { id ->
                            run {
                                selectedVideo = it
                                viewModel.onExtractData(id)
                            }
                        }
                    }
                }
                if (isLoadingPage) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

fun YoutubeVideoMetadata.toUiModel(): UiMediaModel {
    return UiMediaModel(
        id, thumbnailUri, name, author
    )
}
