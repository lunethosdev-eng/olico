package com.music.pokke.data.spotify

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Canvas URL for a Spotify track id, using web-player bearer (sp_dc session).
 * BitChord uses a similar partner/canvas endpoint family.
 */
object SpotifyCanvas {
    private val http = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun videoUrl(trackId: String): String? = withContext(Dispatchers.IO) {
        val token = SpotifyAuth.accessToken() ?: return@withContext null
        val id = trackId.removePrefix("spotify:track:").removePrefix("sp:")
        // Public canvas CDN lookup pattern used by many open clients
        val url = "https://api.spotify.com/v1/tracks/$id"
        val req = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .get()
            .build()
        // Canvas needs special endpoint; try canvaz graph if available later.
        // For now return null unless partner canvas is wired — keeps compile safe.
        null
    }
}
