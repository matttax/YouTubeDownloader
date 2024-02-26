package com.matttax.youtubedownloader.youtube.download

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class InternalStorageSaver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        println("start receive")
        if (context == null) return
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent?.action) {
            println("start copy")
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val currentDownloading = context.getSharedPreferences("Downloading", Context.MODE_PRIVATE)
            val cursor = downloadManager.query(DownloadManager.Query())
            if (cursor.moveToFirst()) {
                do {
                    val column = cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION)
                    if (column == -1) break
                    val downloadDescription = cursor.getString(column)
                    readPath(downloadDescription)?.tryCopy()
                    currentDownloading.edit().remove(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1).toString()).apply()
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
    }

    private fun readPath(parcel: String): StorageParcel? {
        val typeToken = object : TypeToken<StorageParcel>(){}.type
        return try {
            Gson().fromJson(parcel, typeToken)
        } catch (wrongJsonEx: Exception) {
            null
        }
    }
}
