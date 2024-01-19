package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.matttax.youtubedownloader.youtube.presentation.DownloadState
import com.matttax.youtubedownloader.youtube.presentation.SearchViewModel
import com.matttax.youtubedownloader.youtube.presentation.ui.model.toUiModel

@Composable
fun MediaData(viewModel: SearchViewModel) {
    val videoList by viewModel.videoList.collectAsState()
    val query by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val isLoadingPage by viewModel.isLoadingPage.collectAsState()
    var selectedVideo by rememberSaveable { mutableStateOf<Int?>(null) }

    val downloadState by viewModel.downloadProgressState.collectAsState()

    val videoReady by viewModel.isVideoReady.collectAsState(initial = false)

    val currentStreamable by viewModel.currentStreamable.collectAsState()

    Column {
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
                                Spacer(modifier = Modifier.height(5.dp))
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .align(Alignment.CenterHorizontally)
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colorStops = downloadState.getGradients()
                                            ),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .padding(horizontal = 10.dp),
                                    onClick = { if (!downloadState.isDownloading) viewModel.onDownload() },
                                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                                ) {
                                    Text(
                                        text = if (!downloadState.isDownloading)
                                            "Download"
                                        else "Downloading ${downloadState.progress?.times(100)?.toInt() ?: 0}%",
                                        textAlign = TextAlign.Center
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

fun DownloadState.getGradients(): Array<Pair<Float, Color>> {
    return arrayOf(
        (progress ?: 1f) to Color.Red,
        (progress ?: 1f) to Color.Blue.copy(alpha = 0.3f)
    )
}