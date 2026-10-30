package com.music.pokke.data.lyrics

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

object LyricsApi {
    private val http = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun fetch(title: String, artist: String, durationSec: Int? = null): LyricsResult? =
        withContext(Dispatchers.IO) {
            val q = buildString {
                append("https://lrclib.net/api/get?track_name=")
                append(java.net.URLEncoder.encode(title, "UTF-8"))
                append("&artist_name=")
                append(java.net.URLEncoder.encode(artist, "UTF-8"))
                if (durationSec != null && durationSec > 0) append("&duration=$durationSec")
            }
            val body = get(q)
            if (!body.isNullOrBlank()) {
                runCatching { json.decodeFromString<LrcDto>(body) }.getOrNull()?.toResult()?.let { return@withContext it }
            }
            search(title, artist)
        }

    private suspend fun search(title: String, artist: String): LyricsResult? {
        val q = "https://lrclib.net/api/search?q=" +
            java.net.URLEncoder.encode("$title $artist", "UTF-8")
        val body = get(q) ?: return null
        val list = runCatching { json.decodeFromString<List<LrcDto>>(body) }.getOrNull().orEmpty()
        return list.firstOrNull()?.toResult()
    }

    private fun get(url: String): String? {
        val req = Request.Builder().url(url).header("User-Agent", "Pokke/0.1").get().build()
        return runCatching {
            http.newCall(req).execute().use { r -> if (r.isSuccessful) r.body?.string() else null }
        }.getOrNull()
    }

    data class LyricsResult(val plain: String, val synced: List<Pair<Long, String>>)

    @Serializable
    private data class LrcDto(
        val plainLyrics: String? = null,
        val syncedLyrics: String? = null,
    ) {
        fun toResult(): LyricsResult {
            val synced = parseLrc(syncedLyrics.orEmpty())
            return LyricsResult(plainLyrics.orEmpty(), synced)
        }
    }

    private fun parseLrc(lrc: String): List<Pair<Long, String>> {
        val re = Regex("""\[(\d+):(\d+)(?:\.(\d+))?]\s*(.*)""")
        return lrc.lineSequence().mapNotNull { line ->
            val m = re.find(line.trim()) ?: return@mapNotNull null
            val min = m.groupValues[1].toLong()
            val sec = m.groupValues[2].toLong()
            val ms = m.groupValues[3].padEnd(3, '0').take(3).toLongOrNull() ?: 0L
            val text = m.groupValues[4]
            if (text.isBlank()) null else (min * 60_000 + sec * 1000 + ms) to text
        }.toList()
    }
}
