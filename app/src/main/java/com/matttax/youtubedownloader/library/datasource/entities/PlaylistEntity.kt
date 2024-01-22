package com.matttax.youtubedownloader.library.datasource.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String
)
