package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.matttax.youtubedownloader.core.model.MediaFormat
import com.matttax.youtubedownloader.youtube.mappers.SelectionMapper
import com.matttax.youtubedownloader.youtube.presentation.states.UriSelectionState
import com.matttax.youtubedownloader.youtube.presentation.ui.utils.Options
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

@Composable
fun StreamingOptions(
    uriSelectionState: StateFlow<UriSelectionState?>,
    onMediaFormatChanged: (MediaFormat) -> Unit,
    onQualityChanged: (Enum<*>) -> Unit,
    onMimeTypeChanged: (String) -> Unit
) {
    val uriSelection by uriSelectionState.collectAsState()
    Column {
        Spinner(
            modifier = Modifier,
            name = "Format",
            options = Options(
                argumentMap = SelectionMapper.FORMAT_MAP,
                action = { onMediaFormatChanged(it) }
            ),
            selectedOption = uriSelectionState.map { it?.selectedFormat ?: "" }
        )
        Spinner(
            modifier = Modifier,
            name = "Quality",
            options = Options(
                argumentMap = when (uriSelection?.selectedFormat) {
                    MediaFormat.AUDIO.text -> SelectionMapper.AUDIO_QUALITY_MAP
                        .filterKeys {
                            uriSelection?.qualityOptions?.contains(
                                it
                            ) ?: false
                        }
                    MediaFormat.VIDEO.text -> SelectionMapper.VIDEO_QUALITY_MAP
                        .filterKeys {
                            uriSelection?.qualityOptions?.contains(
                                it
                            ) ?: false
                        }
                    else -> emptyMap()
                },
                action = { onQualityChanged(it) }
            ),
            selectedOption = uriSelectionState.map { it?.selectedQuality ?: "" }
        )
        Spinner(
            modifier = Modifier,
            name = "Mime type",
            options = Options(
                argumentMap = uriSelection?.mimeOptions?.associateBy { it } ?: emptyMap(),
                action = { onMimeTypeChanged(it) }
            ),
            selectedOption = uriSelectionState.map { it?.selectedMime ?: "" },
            optionSize = 1f
        )
    }
}
