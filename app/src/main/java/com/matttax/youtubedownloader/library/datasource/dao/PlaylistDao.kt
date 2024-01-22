package com.matttax.youtubedownloader.library.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.matttax.youtubedownloader.library.datasource.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist")
    fun getAll(): Flow<List<PlaylistEntity>>
}