package com.matttax.youtubedownloader.library.repositories

import com.matttax.youtubedownloader.library.repositories.model.MediaItem
import com.matttax.youtubedownloader.library.repositories.model.Playlist
import com.matttax.youtubedownloader.library.datasource.entities.MediaItemEntity
import com.matttax.youtubedownloader.library.datasource.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

object EntityMapper {

    fun PlaylistEntity.toPlaylist() = Playlist(name, id)

    fun MediaItemEntity.toMediaItem() = MediaItem(
        title, author, description, thumbnailPath, hasVideo, durationSeconds, path, id
    )

    fun Flow<List<MediaItemEntity>>.toMediaItemFlow(): Flow<List<MediaItem>> {
        return map { it
            .filter { entity -> File(entity.path).exists() }
            .map { entity -> entity.toMediaItem() }
        }
    }
}
