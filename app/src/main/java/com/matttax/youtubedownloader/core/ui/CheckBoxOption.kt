package com.matttax.youtubedownloader.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed
import kotlinx.coroutines.flow.Flow

@Composable
fun CheckboxOption(
    text: String,
    checkedState: Flow<Boolean>,
    onCheck: (Boolean) -> Unit,
    isCheckboxTransparent: Boolean = true,
    modifier: Modifier = Modifier
) {
    val checked by checkedState.collectAsState(initial = false)
    Row(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .clickable { onCheck(!checked) },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(0.8f)
                .offset(x = 15.dp),
            text = text,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Checkbox(
            modifier = Modifier.weight(0.2f),
            colors = if (isCheckboxTransparent) {
                CheckboxDefaults.colors(
                    checkedColor = Color.Transparent,
                    uncheckedColor = Color.Transparent,
                    checkmarkColor = YouTubeRed,
                )
            } else {
                CheckboxDefaults.colors(
                    checkedColor = YouTubeRed,
                    uncheckedColor = YouTubeRed,
                    checkmarkColor = Color.White,
                )
            },
            checked = checked,
            onCheckedChange = { onCheck(it) }
        )
    }
}
