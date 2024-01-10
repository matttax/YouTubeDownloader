package com.matttax.youtubedownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.matttax.youtubedownloader.domain.config.Duration
import com.matttax.youtubedownloader.domain.config.SearchConfig
import com.matttax.youtubedownloader.domain.config.SortedBy
import com.matttax.youtubedownloader.domain.config.Uploaded
import com.matttax.youtubedownloader.domain.model.YoutubeStreamable
import com.matttax.youtubedownloader.domain.model.YoutubeVideoMetadata
import com.matttax.youtubedownloader.presentation.SearchViewModel
import com.matttax.youtubedownloader.ui.utils.BottomNavigationItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val bottomItems = listOf(
        BottomNavigationItem(
            title = "Search",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search,
        ),
        BottomNavigationItem(
            title = "Saved",
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.Favorite,
        ),
        BottomNavigationItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
        ),
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val viewModel: SearchViewModel by viewModels()
        setContent {
            MediaData(viewModel)
//            var selectedBottomItemIndex by rememberSaveable { mutableStateOf(0) }
//            Scaffold(
//                bottomBar = {
//                    NavigationBar {
//                        bottomItems.forEachIndexed { index, item ->
//                            NavigationBarItem(
//                                selected = selectedBottomItemIndex == index,
//                                onClick = {
//                                    selectedBottomItemIndex = index
//                                    // navController.navigate(item.title)
//                                },
//                                label = {
//                                    Text(text = item.title)
//                                },
//                                alwaysShowLabel = false,
//                                icon = {
//                                    BadgedBox(
//                                        badge = {
//                                            if (item.badgeCount != null) {
//                                                Badge {
//                                                    Text(text = item.badgeCount.toString())
//                                                }
//                                            } else if (item.hasNews) {
//                                                Badge()
//                                            }
//                                        }
//                                    ) {
//                                        Icon(
//                                            imageVector = if (index == selectedBottomItemIndex) {
//                                                item.selectedIcon
//                                            } else item.unselectedIcon,
//                                            contentDescription = item.title
//                                        )
//                                    }
//                                }
//                            )
//                        }
//                    }
//                },
//            ) {
//                MediaData(viewModel)
//            }
        }
    }

    @Composable
    fun MediaData(viewModel: SearchViewModel) {
        val videoList by viewModel.videoList.collectAsState()
        val query by viewModel.searchText.collectAsState()
        val isSearching by viewModel.isSearching.collectAsState()
        val isLoadingPage by viewModel.isLoadingPage.collectAsState()
        var selectedVideo by rememberSaveable { mutableStateOf<Int?>(null) }
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
                onSortingChange = viewModel::onSetSorting
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
                    items(videoList.size) {
                        if (it == videoList.size - 1) {
                            viewModel.onNextPage()
                        }
                        if (it == selectedVideo) {
                            Player(viewModel.currentStreamable, viewModel.exoPlayer)
                        } else {
                            MediaItem(videoList[it]) { id ->
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

    @Composable
    fun Filters(
        searchConfig: StateFlow<SearchConfig>,
        onDurationChange: (Duration) -> Unit,
        onUploadDateChange: (Uploaded) -> Unit,
        onSortingChange: (SortedBy) -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spinner(
                modifier = Modifier.weight(0.3f),
                name = "Duration",
                options = Options(listOf("Any", "Short", "Middle", "Long")) {
                    onDurationChange(
                        when(it) {
                            "Short" -> Duration.SHORT
                            "Middle" -> Duration.MIDDLE
                            "Long" -> Duration.LONG
                            "Any" -> Duration.ANY
                            else -> throw UnknownFieldException("Field $it is unknown")
                        }
                    )
                },
                selectedOption = searchConfig
                    .map { it.duration.text }
                    .distinctUntilChanged()
                    .onStart { emit(searchConfig.value.duration.text) }
            )
            Spinner(
                modifier = Modifier.weight(0.3f),
                name = "Uploaded",
                options = Options(listOf("Any", "Today", "This week", "This month", "This year")) {
                    onUploadDateChange(
                        when(it) {
                            "Today" -> Uploaded.TODAY
                            "This week" -> Uploaded.THIS_WEEK
                            "This month" -> Uploaded.THIS_MONTH
                            "This year" -> Uploaded.THIS_YEAR
                            "Any" -> Uploaded.ANY
                            else -> throw UnknownFieldException("Field $it is unknown")
                        }
                    )
                },
                selectedOption = searchConfig
                    .map { it.uploaded.text }
                    .distinctUntilChanged()
                    .onStart { emit(searchConfig.value.uploaded.text) }
            )
            Spinner(
                modifier = Modifier.weight(0.3f),
                name = "Sort by",
                options = Options(listOf("Relevance", "View count", "Rating", "Upload date")) {
                    onSortingChange(
                        when(it) {
                            "Relevance" -> SortedBy.RELEVANCE
                            "View count" -> SortedBy.VIEW_COUNT
                            "Rating" -> SortedBy.RATING
                            "Upload date" -> SortedBy.UPLOAD_DATE
                            else -> throw UnknownFieldException("Field $it is unknown")
                        }
                    )
                },
                selectedOption = searchConfig
                    .map { it.sortBy.text }
                    .distinctUntilChanged()
                    .onStart { emit(searchConfig.value.sortBy.text) }
            )
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun MediaItem(videoData: YoutubeVideoMetadata, onClick: (String) -> Unit) {
        var clicked by rememberSaveable { mutableStateOf(false) }
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable { clicked = !clicked },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlideImage(
                    modifier = Modifier
                        .size(80.dp)
                        .weight(1f),
                    model = videoData.thumbnailUri,
                    contentDescription = null,
                )
                Spacer(
                    modifier = Modifier.weight(0.05f)
                )
                Text(
                    modifier = Modifier.weight(1.5f),
                    text = videoData.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (clicked) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onClick(videoData.id)
                }
            }
        }
    }

    @Composable
    fun Spinner(
        modifier: Modifier,
        name: String,
        options: Options,
        selectedOption: Flow<String>
    ) {
        var expanded by remember { mutableStateOf(false) }
        val selected by selectedOption.collectAsState(initial = "")
        println(selected)
        Box(
            modifier = modifier
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.White.copy(alpha = 0.4f)),
                onClick = { expanded = true }
            ) {
                Text(
                    text = name,
                    color = Color.Black
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                for (option in options.name) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .padding(start = 3.dp, end = 3.dp),
                        colors = if (option == selected)
                            ButtonDefaults.buttonColors(Color.Red.copy(alpha = 0.3f, red = 0.8f))
                        else ButtonDefaults.buttonColors(Color.White.copy(alpha = 0.6f)),
                        onClick = {
                            options.actionMapper(option)
                            expanded = false
                        }
                    ) {
                        Text(
                            text = option,
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun Player(streamable: StateFlow<YoutubeStreamable?>, exoPlayer: ExoPlayer) {
        val video by streamable.collectAsState()
        video?.videoFormats?.let {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f),
                factory = { context ->
                    PlayerView(context).also {
                        it.player = exoPlayer
                    }
                }
            )
        } ?: run {
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

data class Options(
    val name: List<String>,
    val actionMapper: ActionMapper
)

typealias ActionMapper = (String) -> Unit

class UnknownFieldException(override val message: String?) : RuntimeException(message)