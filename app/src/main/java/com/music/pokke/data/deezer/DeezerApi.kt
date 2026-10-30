package com.music.pokke.data.deezer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

object DeezerApi {
    private val http = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun searchArtists(q: String): List<Artist> = withContext(Dispatchers.IO) {
        if (q.isBlank()) return@withContext emptyList()
        val url = "https://api.deezer.com/search/artist?q=" +
            java.net.URLEncoder.encode(q, "UTF-8") + "&limit=20"
        val body = runCatching {
            http.newCall(Request.Builder().url(url).get().build()).execute()
                .use { if (it.isSuccessful) it.body?.string().orEmpty() else "" }
        }.getOrDefault("")
        if (body.isBlank()) return@withContext emptyList()
        runCatching { json.decodeFromString<Wrap>(body).data }.getOrDefault(emptyList())
    }

    @Serializable data class Artist(val id: Long = 0, val name: String = "", val picture_medium: String? = null)
    @Serializable private data class Wrap(val data: List<Artist> = emptyList())
}
