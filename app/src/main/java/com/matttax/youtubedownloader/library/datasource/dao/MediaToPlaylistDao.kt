package com.matttax.youtubedownloader.library.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.matttax.youtubedownloader.library.datasource.entities.MediaToPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaToPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMediaItemToPlaylist(mediaToPlaylist: MediaToPlaylistEntity)

    @Query("SELECT * FROM media_to_playlist WHERE playlist_id=:id")
    fun getByPlaylistId(id: Int): Flow<List<MediaToPlaylistEntity>>

    @Query(
        "SELECT * FROM media_to_playlist WHERE playlist_id IN (SELECT name FROM playlist WHERE name=:name)"
    )
    fun getByPlaylistName(name: String): Flow<List<MediaToPlaylistEntity>>

    @Query("SELECT playlist_id FROM media_to_playlist WHERE media_id=:mediaId")
    fun getMediaPlaylistsIds(mediaId: Long): Flow<List<Int>>

    @Query("DELETE FROM media_to_playlist WHERE media_id=:mediaId")
    fun removeFromAllPlaylists(mediaId: Long)
}
