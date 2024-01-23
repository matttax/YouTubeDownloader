package com.matttax.youtubedownloader.library.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.matttax.youtubedownloader.library.datasource.entities.MediaItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMediaItem(mediaItem: MediaItemEntity)

    @Query("SELECT * FROM media_item")
    fun getAll(): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_item WHERE has_video=true")
    fun getVideos(): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_item WHERE has_video=false")
    fun getAudios(): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_item WHERE author=:authorName")
    fun getByAuthor(authorName: String): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_item WHERE id IN (:ids)")
    fun getByIds(ids: List<Long>): Flow<List<MediaItemEntity>>

    @Query("DELETE FROM media_item WHERE path=:path")
    fun deleteByPath(path: String)
}
