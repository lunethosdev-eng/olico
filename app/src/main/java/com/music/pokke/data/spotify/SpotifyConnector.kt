package com.music.pokke.data.spotify

import com.music.pokke.data.model.Song

/**
 * Spotify: playlists, import, canvas (BitChord-style role).
 * OAuth token required — wire in settings when user connects Spotify.
 */
object SpotifyConnector {
    @Volatile
    var accessToken: String? = null

    fun isConnected(): Boolean = !accessToken.isNullOrBlank()

    /** Canvas URL lookup placeholder — port BitChord SpotifyCanvas in full next. */
    suspend fun canvasUrl(trackId: String): String? = null

    fun asSong(id: String, title: String, artist: String, cover: String?) =
        Song(id = "sp:$id", title = title, artist = artist, coverUrl = cover, source = "spotify")
}
