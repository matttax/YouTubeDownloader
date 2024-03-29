package com.matttax.youtubedownloader.settings.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.matttax.youtubedownloader.core.ui.CheckboxOption
import com.matttax.youtubedownloader.settings.model.SearchOptions
import com.matttax.youtubedownloader.settings.presentation.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Composable
fun SettingsScreen(
    modifier: Modifier,
    viewModel: SettingsViewModel
) {
    val isDark = isSystemInDarkTheme()
    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        SettingsBlock(
            text = "Appearance"
        ) {
            CheckboxOption(
                text = "Dark theme",
                checkedState = flow { emit(isDark) },
                onCheck = { },
                modifier = Modifier.fillMaxWidth()
            )
        }
        SettingsBlock(
            text = "Player"
        ) {
            CheckboxOption(
                text = "Stop playback when quit",
                checkedState = viewModel.playerSettings.map { it.stopWhenHidden },
                onCheck = { viewModel.onSetPlayWhenHidden(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        SettingsBlock(
            text = "Search"
        ) {
            CheckboxOption(
                text = "Autocorrection",
                checkedState = viewModel.searchSettings.map { it.isAutocorrectionOn },
                onCheck = { viewModel.onChangeSearchOption(SearchOptions.AUTOCORRECT, it) },
                modifier = Modifier.fillMaxWidth()
            )
            CheckboxOption(
                text = "Show movies",
                checkedState = viewModel.searchSettings.map { it.showMovies },
                onCheck = { viewModel.onChangeSearchOption(SearchOptions.SHOW_MOVIES, it) },
                modifier = Modifier.fillMaxWidth()
            )
            CheckboxOption(
                text = "Show lives",
                checkedState = viewModel.searchSettings.map { it.showLives },
                onCheck = { viewModel.onChangeSearchOption(SearchOptions.SHOW_LIVES, it) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
