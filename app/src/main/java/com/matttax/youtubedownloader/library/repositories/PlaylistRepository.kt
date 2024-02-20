package com.matttax.youtubedownloader.library.repositories

import com.matttax.youtubedownloader.library.repositories.model.Playlist
import com.matttax.youtubedownloader.library.datasource.MediaDatabase
import com.matttax.youtubedownloader.library.datasource.entities.PlaylistEntity
import com.matttax.youtubedownloader.library.repositories.EntityMapper.toPlaylist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaylistRepository @Inject constructor(
    mediaDatabase: MediaDatabase
) {
    private val mediaToPlaylistDao = mediaDatabase.getMediaToPlaylistDao()
    private val playlistDao = mediaDatabase.getPlaylistDao()

    fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAll().map { it.map { entity -> entity.toPlaylist() } }
    }

    fun getPlaylistById(id: Int): Flow<Playlist> {
        return playlistDao.getPlaylistById(id).map { entity -> entity.toPlaylist() }
    }

    fun addPlaylist(name: String) {
        playlistDao.insertPlaylist(
            PlaylistEntity(name = name)
        )
    }

    fun removePlaylist(id: Int, withItems: Boolean = false) {
        if (withItems) {
            mediaToPlaylistDao.removeAllFromPlaylist(id)
        }
        playlistDao.removePlaylist(id)
    }
}
