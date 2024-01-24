package com.matttax.youtubedownloader.youtube

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.matttax.youtubedownloader.core.model.Format
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap

@Singleton
class MediaDownloader @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val downloadManager: DownloadManager =
        context.getSystemService(DownloadManager::class.java)
    private val downloadedMediaPaths = Collections.synchronizedMap(HashMap<String, String>())
    private val downloadedThumbnailPaths = Collections.synchronizedMap(HashMap<String, String>())

    private val regex = Regex("[^A-Za-z0-9 ]")

    fun download(format: Format, title: String, thumbnailUri: String? = null): Flow<Float> {
        val directory = Environment.DIRECTORY_DOWNLOADS
        val ext = extractFormatFromMimeType(format.mimeType) ?: ""
        val subPath =
            "${regex.replace(format.url.takeLast(25), "")}$ext"
        val request = DownloadManager.Request(format.url.toUri())
            .setTitle(title)
            .setMimeType(format.mimeType)
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED or DownloadManager.Request.VISIBILITY_VISIBLE
            ).setDestinationInExternalPublicDir(directory, subPath)
        val downloadId = downloadManager.enqueue(request)
        downloadedMediaPaths[format.url] =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$subPath"
        if (thumbnailUri != null) {
            downloadThumbnail(format.url, title, thumbnailUri)
        }
        return progressFlow(downloadId)
    }

    fun getPath(uri: String): String {
        return downloadedMediaPaths[uri] ?: ""
    }

    fun getThumbnailPath(uri: String): String? {
        return downloadedThumbnailPaths[uri]
    }

    private fun downloadThumbnail(key: String, title: String, uri: String) {
        val subPath =
            "${regex.replace("$title$key".takeLast(30), "")}.jpg"
        val request = DownloadManager.Request(uri.toUri())
            .setTitle(title)
            .setMimeType("image/jpg")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN).setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS.plus("/.youtube_thumbnails"), subPath
            )
        downloadManager.enqueue(request)
        downloadedMediaPaths[key] =
            "${Environment.DIRECTORY_DOWNLOADS}/.youtube_thumbnails/$subPath"
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
