package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.matttax.youtubedownloader.core.config.Duration
import com.matttax.youtubedownloader.core.config.SearchConfig
import com.matttax.youtubedownloader.core.config.SortedBy
import com.matttax.youtubedownloader.core.config.Uploaded
import com.matttax.youtubedownloader.youtube.presentation.ui.utils.FiltersConsts
import com.matttax.youtubedownloader.youtube.presentation.ui.utils.Options
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Composable
fun Filters(
    searchConfig: StateFlow<SearchConfig>,
    onDurationChange: (Duration) -> Unit,
    onUploadDateChange: (Uploaded) -> Unit,
    onSortingChange: (SortedBy) -> Unit,
    onEvery: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spinner(
            modifier = Modifier.weight(0.3f),
            name = FiltersConsts.DURATION.name,
            options = Options(
                argumentMap = FiltersConsts.DURATION.argumentMap,
                action = {
                    onDurationChange(it)
                    onEvery()
                }
            ),
            selectedOption = searchConfig
                .map { it.duration.text }
                .distinctUntilChanged()
                .onStart { emit(searchConfig.value.duration.text) }
        )
        Spinner(
            modifier = Modifier.weight(0.3f),
            name = FiltersConsts.UPLOADED.name,
            options = Options(
                argumentMap = FiltersConsts.UPLOADED.argumentMap,
                action = {
                    onUploadDateChange(it)
                    onEvery()
                }
            ),
            selectedOption = searchConfig
                .map { it.uploaded.text }
                .distinctUntilChanged()
                .onStart { emit(searchConfig.value.uploaded.text) }
        )
        Spinner(
            modifier = Modifier.weight(0.3f),
            name = FiltersConsts.SORTING.name,
            options = Options(
                argumentMap = FiltersConsts.SORTING.argumentMap,
                action = {
                    onSortingChange(it)
                    onEvery()
                }
            ),
            selectedOption = searchConfig
                .map { it.sortBy.text }
                .distinctUntilChanged()
                .onStart { emit(searchConfig.value.sortBy.text) }
        )
    }
}
