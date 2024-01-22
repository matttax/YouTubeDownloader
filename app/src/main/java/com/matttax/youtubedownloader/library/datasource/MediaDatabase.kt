package com.matttax.youtubedownloader.library.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.matttax.youtubedownloader.library.datasource.dao.MediaItemDao
import com.matttax.youtubedownloader.library.datasource.dao.MediaToPlaylistDao
import com.matttax.youtubedownloader.library.datasource.dao.PlaylistDao
import com.matttax.youtubedownloader.library.datasource.entities.MediaItemEntity
import com.matttax.youtubedownloader.library.datasource.entities.MediaToPlaylistEntity
import com.matttax.youtubedownloader.library.datasource.entities.PlaylistEntity

@Database(
    entities = [MediaItemEntity::class, PlaylistEntity::class, MediaToPlaylistEntity::class],
    version = 1
)
abstract class MediaDatabase: RoomDatabase() {
    abstract fun getMediaItemDao(): MediaItemDao
    abstract fun getMediaToPlaylistDao(): MediaToPlaylistDao
    abstract fun getPlaylistDao(): PlaylistDao
}
