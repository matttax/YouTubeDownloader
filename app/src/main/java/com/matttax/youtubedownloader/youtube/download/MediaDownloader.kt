package com.matttax.youtubedownloader.youtube.download

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import com.matttax.youtubedownloader.core.model.Format

class MediaDownloader(
    context: Context,
    val wifiOnly: Boolean = false,
    val destinationPath: String = Environment.DIRECTORY_MOVIES,
) {
    val downloadManager: DownloadManager = context.getSystemService(DownloadManager::class.java)

    fun download(video: Format.Video) {

    }

    fun download(audio: Format.Audio) {

    }

}

