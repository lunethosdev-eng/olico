package com.music.pokke.data.ukiyo

import com.music.pokke.PokkeConfig
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
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun gateway() = AppSettings.musicApiBase.value.trimEnd('/')

    suspend fun search(query: String): List<Song> = withContext(Dispatchers.IO) {
        val q = java.net.URLEncoder.encode(query, "UTF-8")
        val viaGw = decodeSongs(get("${gateway()}/api/search?q=$q"))
        if (viaGw.isNotEmpty()) return@withContext viaGw
        supabaseSongs(query)
    }

    suspend fun recentSongs(): List<Song> = withContext(Dispatchers.IO) {
        val viaGw = decodeSongs(get("${gateway()}/api/songs"))
        if (viaGw.isNotEmpty()) return@withContext viaGw
        supabaseSongs(null)
    }

    suspend fun progress(id: String): DownloadProgress? = withContext(Dispatchers.IO) {
        val body = get("${gateway()}/api/progress?id=${java.net.URLEncoder.encode(id, "UTF-8")}")
        if (body.isBlank()) return@withContext null
        runCatching { json.decodeFromString<ProgressDto>(body).toModel() }.getOrNull()
    }

    suspend fun trends(): List<TrendItem> = withContext(Dispatchers.IO) {
        val body = get("${gateway()}/api/trends")
        if (body.isNotBlank()) {
            runCatching { json.decodeFromString<List<TrendItem>>(body) }.getOrNull()?.let {
                if (it.isNotEmpty()) return@withContext it
            }
        }
        recentSongs().map {
            TrendItem(it.id, it.title, it.artist, it.coverUrl, 0, it.source)
        }
    }

    private fun supabaseSongs(query: String?): List<Song> {
        val url = buildString {
            append(PokkeConfig.UKIYO_SUPABASE_URL)
            append("/rest/v1/songs?select=id,title,artist,album,audio_url,cover_url,animated_cover_url,duration_seconds,youtube_id")
            append("&order=created_at.desc&limit=80")
            if (!query.isNullOrBlank()) {
                val safe = query.replace(",", " ").take(40)
                append("&or=(title.ilike.*")
                append(java.net.URLEncoder.encode(safe, "UTF-8"))
                append("*,artist.ilike.*")
                append(java.net.URLEncoder.encode(safe, "UTF-8"))
                append("*)")
            }
        }
        val req = Request.Builder()
            .url(url)
            .header("apikey", PokkeConfig.UKIYO_ANON_KEY)
            .header("Authorization", "Bearer ${PokkeConfig.UKIYO_ANON_KEY}")
            .get()
            .build()
        val body = runCatching {
            http.newCall(req).execute().use { r -> if (r.isSuccessful) r.body?.string().orEmpty() else "" }
        }.getOrDefault("")
        return decodeSongs(body).map { it.copy(source = "ukiyo") }
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
