package com.matttax.youtubedownloader.library.repositories

import com.matttax.youtubedownloader.library.repositories.model.MediaItem
import com.matttax.youtubedownloader.library.datasource.MediaDatabase
import com.matttax.youtubedownloader.library.datasource.entities.MediaItemEntity
import com.matttax.youtubedownloader.library.datasource.entities.MediaToPlaylistEntity
import com.matttax.youtubedownloader.library.repositories.EntityMapper.toMediaItemFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MediaRepository @Inject constructor(
    mediaDatabase: MediaDatabase
) {

    private val mediaItemDao = mediaDatabase.getMediaItemDao()
    private val mediaToPlaylistDao = mediaDatabase.getMediaToPlaylistDao()

    fun getAllMedia(): Flow<List<MediaItem>> = mediaItemDao.getAll().toMediaItemFlow()

    fun getVideos(): Flow<List<MediaItem>> = mediaItemDao.getVideos().toMediaItemFlow()

    fun getAudios(): Flow<List<MediaItem>> = mediaItemDao.getAudios().toMediaItemFlow()

    fun getByAuthor(authorName: String) = mediaItemDao.getByAuthor(authorName).toMediaItemFlow()

    fun deleteByPath(path: String) = mediaItemDao.deleteByPath(path)

    fun getMediaItemPlaylistsById(id: Long) = mediaToPlaylistDao.getMediaPlaylistsIds(id)

    fun editMediaItemById(id: Long, newTitle: String, newAuthor: String) {
        mediaItemDao.apply {
            editTitleById(id, newTitle)
            editAuthorById(id, newAuthor)
        }
    }

    fun addMediaItemToPlaylists(mediaId: Long, playlists: List<Int>) {
        mediaToPlaylistDao.removeFromAllPlaylists(mediaId)
        playlists.forEach {
            mediaToPlaylistDao.insertMediaItemToPlaylist(
                MediaToPlaylistEntity(
                    mediaId = mediaId,
                    playlistId = it,
                    position = 0
                )
            )
        }
    }

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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllFromPlaylist(id: Int): Flow<List<MediaItem>> {
        return mediaToPlaylistDao
            .getByPlaylistId(id)
            .map {
                it.map { media -> media.mediaId }
            }.flatMapConcat { ids ->
                mediaItemDao.getByIds(ids)
            }.toMediaItemFlow()
    }
}
