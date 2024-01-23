package com.matttax.youtubedownloader.library.repositories

import com.matttax.youtubedownloader.library.repositories.model.MediaItem
import com.matttax.youtubedownloader.library.repositories.model.Playlist
import com.matttax.youtubedownloader.library.datasource.entities.MediaItemEntity
import com.matttax.youtubedownloader.library.datasource.entities.PlaylistEntity

object EntityMapper {

    fun PlaylistEntity.toPlaylist() = Playlist(name)

    fun MediaItemEntity.toMediaItem() = MediaItem(
        title, author, description, thumbnailPath, hasVideo, durationSeconds, path
    )

}