package com.matttax.youtubedownloader.settings.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow

@Composable
fun CheckboxOption(
    text: String,
    checkedState: Flow<Boolean>,
    onCheck: (Boolean) -> Unit,
) {
    val checked by checkedState.collectAsState(initial = false)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(0.8f)
                .offset(x = 15.dp),
            text = text,
        )
        Checkbox(
            modifier = Modifier.weight(0.2f),
            checked = checked,
            onCheckedChange = { onCheck(it) }
        )
    }
}
