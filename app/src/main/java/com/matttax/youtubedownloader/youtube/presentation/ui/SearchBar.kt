package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SearchBar(
    searchText: StateFlow<String>,
    interactionSource: MutableInteractionSource,
    onChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val query by searchText.collectAsState()
    TextField(
        modifier = Modifier
            .background(color = Color.White)
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
            unfocusedContainerColor = Color.LightGray.copy(alpha = 0.1f),
            cursorColor = YouTubeRed
        ),
        textStyle = TextStyle.Default.copy(fontSize = 16.sp),
        interactionSource = interactionSource
    )
}