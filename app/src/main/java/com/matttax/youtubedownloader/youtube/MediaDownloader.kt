package com.matttax.youtubedownloader.youtube

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import com.matttax.youtubedownloader.domain.model.Format

class MediaDownloader(
    context: Context,
    val downloadNotificationVisible: Boolean = true,
    val notifyDownloadCompleted: Boolean = true,
    val wifiOnly: Boolean = false,
    val destinationPath: String = Environment.DIRECTORY_MOVIES,
) {
    val downloadManager: DownloadManager = context.getSystemService(DownloadManager::class.java)

    fun download(video: Format.Video) {

    }

    fun download(audio: Format.Audio) {

    }

}