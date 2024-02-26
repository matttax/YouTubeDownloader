package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
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
        color = MaterialTheme.colorScheme.onSecondary,
        fontSize = 15.sp
    )
    Text(
        text = data.name,
        color = MaterialTheme.colorScheme.onPrimary,
    )
    Row {
        Image(
            painter = painterResource(id = R.drawable.ic_views),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "%,d".format(data.viewCount),
            modifier = Modifier.padding(2.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
    Row(
        modifier = Modifier.clickable {
            descriptionExpanded = !descriptionExpanded
        }.padding(bottom = 2.dp)
    ) {
        Image(
            painter = painterResource(
                id = if (!descriptionExpanded)
                    R.drawable.ic_info_hidden
                else
                    R.drawable.ic_info_unfolded
            ),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = "Description",
            modifier = Modifier.padding(2.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
    if (descriptionExpanded) {
        Text(
            text = data.description,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
    Spacer(modifier = Modifier.width(2.dp))
}
