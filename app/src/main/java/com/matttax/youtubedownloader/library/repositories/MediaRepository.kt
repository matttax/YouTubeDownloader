package com.matttax.youtubedownloader.library.repositories

import com.matttax.youtubedownloader.library.MediaItem
import com.matttax.youtubedownloader.library.datasource.MediaDatabase
import com.matttax.youtubedownloader.library.datasource.entities.MediaItemEntity
import com.matttax.youtubedownloader.library.toMediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class MediaRepository @Inject constructor(
    mediaDatabase: MediaDatabase
) {

    private val mediaItemDao = mediaDatabase.getMediaItemDao()

    fun getAllMedia(): Flow<List<MediaItem>> = mediaItemDao.getAll().toMediaItemFlow()

    fun getVideos(): Flow<List<MediaItem>> = mediaItemDao.getVideos().toMediaItemFlow()

    fun getAudios(): Flow<List<MediaItem>> = mediaItemDao.getAudios().toMediaItemFlow()

    fun getByAuthor(authorName: String) = mediaItemDao.getByAuthor(authorName).toMediaItemFlow()

    fun addMediaItem(mediaItem: MediaItem) {
        mediaItemDao.insertMediaItem(
            MediaItemEntity(
                path = mediaItem.path,
                title = mediaItem.title,
                author = mediaItem.author,
                description = mediaItem.description,
                thumbnailPath = mediaItem.thumbnailUri,
                hasVideo = mediaItem.hasVideo,
                durationSeconds = mediaItem.durationSeconds,
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
        )
    }

    private fun Flow<List<MediaItemEntity>>.toMediaItemFlow(): Flow<List<MediaItem>> {
        return map { it
            .filter { entity -> File(entity.path).exists() }
            .map { entity -> entity.toMediaItem() }
        }
    }

}
