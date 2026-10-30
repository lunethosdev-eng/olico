package com.music.pokke.data.ukiyo

import com.music.pokke.data.model.DownloadProgress
import com.music.pokke.data.model.Song
import com.music.pokke.data.model.TrendItem
import com.music.pokke.data.settings.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object UkiyoApi {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val http = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    private fun base() = AppSettings.musicApiBase.value.trimEnd('/')

    suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        val q = java.net.URLEncoder.encode(query, "UTF-8")
        decodeSongs(get("${base()}/api/search?q=$q"))
    }

    suspend fun recentSongs(): List<Song> = withContext(Dispatchers.IO) {
        decodeSongs(get("${base()}/api/songs"))
    }

    suspend fun progress(id: String): DownloadProgress? = withContext(Dispatchers.IO) {
        val body = get("${base()}/api/progress?id=${java.net.URLEncoder.encode(id, "UTF-8")}")
        if (body.isBlank()) return@withContext null
        runCatching { json.decodeFromString<ProgressDto>(body).toModel() }.getOrNull()
    }

    /** Trends: tries /api/trends then falls back to recent songs as soft trends. */
    suspend fun trends(): List<TrendItem> = withContext(Dispatchers.IO) {
        val body = get("${base()}/api/trends")
        if (body.isNotBlank()) {
            runCatching { json.decodeFromString<List<TrendItem>>(body) }.getOrNull()?.let { return@withContext it }
        }
        recentSongs().map {
            TrendItem(
                songId = it.id,
                title = it.title,
                artist = it.artist,
                coverUrl = it.coverUrl,
                plays = 0,
                source = it.source,
            )
        }
    }

    private fun get(url: String): String {
        val req = Request.Builder().url(url).get().build()
        return runCatching {
            http.newCall(req).execute().use { r ->
                if (!r.isSuccessful) "" else r.body?.string().orEmpty()
            }
        }.getOrDefault("")
    }

    private fun decodeSongs(body: String): List<Song> {
        if (body.isBlank()) return emptyList()
        return runCatching { json.decodeFromString<List<Song>>(body) }
            .recoverCatching { json.decodeFromString<Wrap>(body).songs }
            .getOrDefault(emptyList())
            .map { it.copy(source = it.source.ifBlank { "ukiyo" }) }
    }

    @Serializable private data class Wrap(val songs: List<Song> = emptyList())
    @Serializable
    private data class ProgressDto(
        val id: String = "",
        val percent: Int = 0,
        val stage: String = "",
        val done: Boolean = false,
        val error: String? = null,
    ) {
        fun toModel() = DownloadProgress(id, percent, stage, done, error)
    }
}
