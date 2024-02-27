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

    @Query("SELECT * FROM playlist ORDER BY id DESC")
    fun getAll(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist WHERE id=:id")
    fun getPlaylistById(id: Int): Flow<PlaylistEntity>

    @Query("DELETE FROM playlist WHERE id=:id")
    fun removePlaylist(id: Int)

    @Query("UPDATE playlist SET name = :newName WHERE id = :id")
    fun renamePlaylist(id: Int, newName: String)
}
