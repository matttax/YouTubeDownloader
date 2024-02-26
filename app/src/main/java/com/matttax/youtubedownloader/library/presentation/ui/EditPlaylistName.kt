package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed

@Composable
fun EditPlaylistName(
    modifier: Modifier,
    initial: String,
    onChange: (String) -> Unit
) {
    var playlistName by remember { mutableStateOf(initial) }
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = TextFieldValue(
            text = playlistName,
            selection = TextRange(playlistName.length)
        ),
        onValueChange = { playlistName = it.text },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { onChange(playlistName) }
        ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = YouTubeRed
        ),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    )
}
