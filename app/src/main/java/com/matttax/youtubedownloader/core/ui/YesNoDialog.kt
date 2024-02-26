package com.matttax.youtubedownloader.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed
import com.matttax.youtubedownloader.library.presentation.ui.Title

@Composable
fun YesNoDialog(
    text: String,
    onYes: () -> Unit,
    onDismiss: () -> Unit,
    yesText: String = "Yes",
    content: @Composable () -> Unit = { }
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Title(text = text)
                content()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(Color.Transparent),
                    ) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp
                        )
                    }
                    Button(
                        onClick = {
                            onYes()
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(Color.Transparent),
                    ) {
                        Text(
                            text = yesText,
                            color = YouTubeRed,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
