package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matttax.youtubedownloader.R
import com.matttax.youtubedownloader.core.model.YoutubeVideoMetadata

@Composable
fun Metadata(data: YoutubeVideoMetadata) {
    var descriptionExpanded by rememberSaveable { mutableStateOf(false) }
    Text(
        text = data.author,
        color = Color.Gray,
        fontSize = 15.sp
    )
    Text(text = data.name)
    Row {
        Image(
            painter = painterResource(id = R.drawable.ic_baseline_remove_red_eye_24),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "%,d".format(data.viewCount),
            modifier = Modifier.padding(2.dp)
        )
    }
    Row(
        modifier = Modifier.clickable {
            descriptionExpanded = !descriptionExpanded
        }
    ) {
        Image(
            painter = painterResource(
                id = if (!descriptionExpanded)
                    R.drawable.ic_baseline_keyboard_arrow_right_24
                else
                    R.drawable.ic_baseline_keyboard_arrow_down_24
            ),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "Description",
            modifier = Modifier.padding(2.dp)
        )
    }
    if (descriptionExpanded) {
        Text(text = data.description)
    }
    Spacer(modifier = Modifier.width(2.dp))
}
