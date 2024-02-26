package com.matttax.youtubedownloader.youtube.download

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.google.gson.Gson
import com.matttax.youtubedownloader.core.model.Format
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaDownloader @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val downloadManager: DownloadManager =
        context.getSystemService(DownloadManager::class.java)
    private val downloadedMediaPaths = Collections.synchronizedMap(HashMap<String, String>())
    private val downloadedThumbnailPaths = Collections.synchronizedMap(HashMap<String, String>())

    private val regex = Regex("[^A-Za-z0-9 ]")
    private val gson = Gson()
    private val currentDownloading = context.getSharedPreferences("Downloading", Context.MODE_PRIVATE)

    fun download(format: Format, title: String, thumbnailUri: String? = null): Flow<Float> {
        val ext = extractFormatFromMimeType(format.mimeType) ?: ""
        val subPath = "${regex.replace(format.url.takeLast(25), "")}$ext"
        val fullPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$subPath"
        val internalPath = File(context.dataDir.absolutePath, subPath).absolutePath
        val request = buildRequest(
            uri = format.url,
            title =  title,
            mimeType = format.mimeType,
            subPath = subPath
        ).setDescription(buildParcelJson(fullPath, internalPath))
        val downloadId = downloadManager.enqueue(request)
        currentDownloading.edit().putBoolean(downloadId.toString(), false).apply()
        downloadedMediaPaths[format.url] = internalPath
        thumbnailUri?.let { downloadThumbnail(format.url, title, thumbnailUri) }
        return progressFlow(downloadId)
    }

    fun getPath(uri: String): String {
        return downloadedMediaPaths[uri] ?: ""
    }

    fun getThumbnailPath(uri: String): String? {
        return downloadedThumbnailPaths[uri]
    }

    private fun downloadThumbnail(key: String, title: String, uri: String) {
        val subPath = "${regex.replace("$title$key".takeLast(30), "")}.jpg"
        val fullPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$subPath"
        val internalPath = File(context.dataDir.absolutePath, subPath).absolutePath
        val request = buildRequest(
            uri = uri,
            title = title,
            mimeType = "image/jpg",
            subPath = subPath
        ).setDescription(buildParcelJson(fullPath, internalPath))
        val downloadId = downloadManager.enqueue(request)
        currentDownloading.edit().putBoolean(downloadId.toString(), false).apply()
        downloadedThumbnailPaths[key] = internalPath
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

    private fun buildRequest(
        uri: String, title: String, mimeType: String, subPath: String
    ): DownloadManager.Request {
        val visibility = DownloadManager.Request.VISIBILITY_HIDDEN
        return DownloadManager.Request(uri.toUri())
            .setTitle(title)
            .setMimeType(mimeType)
            .setNotificationVisibility(visibility)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, subPath
            )
    }

    private fun buildParcelJson(externalPath: String, internalPath: String): String {
        val parcel = StorageParcel(
            externalPath = externalPath,
            internalPath = internalPath
        )
        return gson.toJson(parcel)
    }
}
