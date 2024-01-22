package com.matttax.youtubedownloader.library.datasource.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "media_item")
data class MediaItemEntity constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    @ColumnInfo(name = "path")
    val path: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "author")
    val author: String,

    @ColumnInfo(name = "description", typeAffinity = ColumnInfo.TEXT)
    val description: String,

    @ColumnInfo(name = "duration")
    val durationSeconds: Int,

    @ColumnInfo(name = "has_video")
    val hasVideo: Boolean,

    @ColumnInfo(name = "thumbnail_path")
    val thumbnailPath: String,

    @ColumnInfo(name = "last_updated", defaultValue = "CURRENT_TIMESTAMP")
    val lastUpdatedTimestamp: Long
)
