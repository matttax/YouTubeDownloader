package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.activity.compose.BackHandler
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
import com.matttax.youtubedownloader.youtube.presentation.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaData(
    modifier: Modifier,
    viewModel: SearchViewModel
) {
    val videoList by viewModel.videoList.collectAsState()
    val query by viewModel.searchText.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val isLoadingPage by viewModel.isLoadingPage.collectAsState()
    var selectedVideo by rememberSaveable { mutableStateOf<Int?>(null) } //TODO()

    val videoReady by viewModel.isVideoReady.collectAsState(initial = false)

    val currentStreamable by viewModel.currentStreamable.collectAsState()

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            value = query,
            onValueChange = viewModel::onSearchTextChange,
            placeholder = { Text("Search") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    selectedVideo = null
                    viewModel.onSearch(query)
                }
            ),
            shape = RoundedCornerShape(20),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = YouTubeRed,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.LightGray.copy(alpha = 0.2f),
            ),
            textStyle = TextStyle.Default.copy(fontSize = 16.sp)
        )
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
                                CircularProgressIndicator()
                            }
                        }
                    } else {
                        MediaItem(
                            videoData = videoList[it].toUiModel(),
                            onClick = { id ->
                                focusManager.clearFocus()
                                selectedVideo = it
                                viewModel.onExtractData(id)
                            }
                        )
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
    BackHandler {
        focusManager.clearFocus()
    }
}

fun YoutubeVideoMetadata.toUiModel(): UiMediaModel {
    return UiMediaModel(
        id, thumbnailUri, name, author, durationSeconds
    )
}
