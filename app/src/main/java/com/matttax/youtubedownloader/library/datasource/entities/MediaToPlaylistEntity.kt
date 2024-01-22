package com.matttax.youtubedownloader.library.datasource.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "media_to_playlist",
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("playlist_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MediaItemEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("media_id"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    primaryKeys = ["media_id", "playlist_id"]
)
data class MediaToPlaylistEntity(
    @ColumnInfo(name = "media_id")
    val mediaId: Long,

    @ColumnInfo(name = "playlist_id")
    val playlistId: Int,

    @ColumnInfo(name = "position")
    val position: Int,
)
