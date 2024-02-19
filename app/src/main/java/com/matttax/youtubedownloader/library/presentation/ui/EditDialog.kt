package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.matttax.youtubedownloader.core.ui.EditTextField
import com.matttax.youtubedownloader.core.ui.YesNoDialog
import com.matttax.youtubedownloader.library.repositories.model.MediaItem

@Composable
fun EditDialog(
    mediaItem: MediaItem,
    onEdit: (newTitle: String, newAuthor: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by mutableStateOf(mediaItem.title)
    var author by mutableStateOf(mediaItem.author)
    YesNoDialog(
        text = "Edit",
        onYes = { onEdit(title, author) },
        onDismiss = onDismiss
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.4f)
                .padding(vertical = 10.dp),
            model = mediaItem.thumbnailUri,
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        EditTextField(
            initialValue = title,
            hint = "Title"
        ) {
            title = it
        }
        EditTextField(
            initialValue = author,
            hint = "Author"
        ) {
            author = it
        }
    }
}
