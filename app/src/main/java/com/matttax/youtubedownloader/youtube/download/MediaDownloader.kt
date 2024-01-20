package com.matttax.youtubedownloader.youtube.download

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.matttax.youtubedownloader.core.model.Format
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaDownloader @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val downloadManager: DownloadManager =
        context.getSystemService(DownloadManager::class.java)

    fun download(format: Format, title: String): Flow<Float> {
        val directory = when(format) {
            is Format.Video -> Environment.DIRECTORY_MOVIES
            is Format.Audio -> Environment.DIRECTORY_MUSIC
        }
        val ext = extractFormatFromMimeType(format.mimeType) ?: ""
        val subPath = "$title$ext"
        val request = DownloadManager.Request(format.url.toUri())
            .setTitle(title)
            .setMimeType(format.mimeType)
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED or DownloadManager.Request.VISIBILITY_VISIBLE
            ).setDestinationInExternalPublicDir(directory, subPath)
        val downloadId = downloadManager.enqueue(request)
        return progressFlow(downloadId)
    }

    private fun progressFlow(id: Long) = flow {
        val downloading = AtomicBoolean(true)
        while (downloading.get()) {
            val query = DownloadManager.Query().setFilterById(id)
            val cursor = downloadManager.query(query)
            cursor.moveToFirst()
            val downloadBytes =
                cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val totalBytes =
                cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val progress = downloadBytes.toFloat() / totalBytes
            cursor.close()
            emit(progress)
            if (downloading.get()) {
                delay(500)
            }
            if (progress == 1f) break
        }
    }.flowOn(Dispatchers.IO)

    private fun extractFormatFromMimeType(mimeType: String): String? {
        if (mimeType.startsWith("video/")) {
            return "." + mimeType.split(";", "/")[1]
        }
        if (mimeType.startsWith("audio/")) {
            return ".mp3"
        }
        return null
    }
}
