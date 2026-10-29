package com.music.olico.ukiyo

import com.music.olico.OlicoConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object UkiyoClient {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val http = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    private fun apiBase(): String = OlicoConfig.MUSIC_API_BASE.trimEnd('/')

    @Serializable
    data class UkiyoSong(
        val id: String = "",
        val title: String = "",
        val artist: String = "",
        val album: String? = null,
        @SerialName("audio_url") val audioUrl: String = "",
        @SerialName("cover_url") val coverUrl: String? = null,
        @SerialName("animated_cover_url") val animatedCoverUrl: String? = null,
        @SerialName("duration_seconds") val durationSeconds: Int? = null,
        @SerialName("youtube_id") val youtubeId: String? = null,
    )

    suspend fun search(query: String, limit: Int = 25): List<UkiyoSong> = withContext(Dispatchers.IO) {
        val q = java.net.URLEncoder.encode(query, "UTF-8")
        val url = "${apiBase()}/api/search?q=$q"
        decodeSongs(get(url)).take(limit)
    }

    suspend fun recent(limit: Int = 50): List<UkiyoSong> = withContext(Dispatchers.IO) {
        decodeSongs(get("${apiBase()}/api/songs")).take(limit)
    }

    suspend fun getById(id: String): UkiyoSong? = withContext(Dispatchers.IO) {
        // Prefer search by id / youtube id; fall back to recent scan
        search(id, limit = 5).firstOrNull { it.id == id || it.youtubeId == id }
            ?: recent(100).firstOrNull { it.id == id || it.youtubeId == id }
    }

    private fun get(url: String): String {
        val req = Request.Builder().url(url).get().build()
        return http.newCall(req).execute().use { r ->
            if (!r.isSuccessful) "" else r.body?.string().orEmpty()
        }
    }

    private fun decodeSongs(body: String): List<UkiyoSong> {
        if (body.isBlank()) return emptyList()
        return runCatching { json.decodeFromString<List<UkiyoSong>>(body) }
            .recoverCatching {
                json.decodeFromString<SongsWrap>(body).songs
            }
            .getOrDefault(emptyList())
    }

    @Serializable
    private data class SongsWrap(val songs: List<UkiyoSong> = emptyList())
}
