package com.music.pokke.data.youtube

import com.music.pokke.data.catalog.SilentHarvest
import com.music.pokke.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Innertube search (ANDROID_MUSIC). Stream resolve via NewPipe in v0.2.1 when dep is stable.
 * For now search rows; play uses Ukiyo audio after silent harvest when possible.
 */
object YouTubeMusicClient {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val http = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()
    private val media = "application/json".toMediaType()

    private const val SEARCH_URL =
        "https://music.youtube.com/youtubei/v1/search?prettyPrint=false&key=AIzaSyC9XL3ZjWddXya6X74dJoCTL-WEYFDNX30"

    private val contextJson =
        """{"client":{"clientName":"ANDROID_MUSIC","clientVersion":"7.27.52","hl":"en","gl":"US"}}"""

    suspend fun search(query: String, limit: Int = 25): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val body = """{"context":$contextJson,"query":${query.q()},"params":"EgWKAQIIAWoKEAkQAxAEEAUQCg%3D%3D"}"""
        val req = Request.Builder()
            .url(SEARCH_URL)
            .header("Content-Type", "application/json")
            .header("User-Agent", "com.google.android.apps.youtube.music/7.27.52 (Linux; U; Android 14) gzip")
            .post(body.toRequestBody(media))
            .build()
        val text = http.newCall(req).execute().use { it.body?.string().orEmpty() }
        parse(text).take(limit)
    }

    fun noteListen(song: Song) {
        SilentHarvest.enqueue(song.title, song.artist, song.youtubeId ?: song.id)
    }

    private fun parse(raw: String): List<Song> {
        val root = runCatching { json.parseToJsonElement(raw).jsonObject }.getOrNull() ?: return emptyList()
        val out = ArrayList<Song>()
        walk(root) { obj ->
            val videoId = obj.str("videoId") ?: return@walk
            val title = obj.nestedRuns("title") ?: obj.str("title") ?: return@walk
            val artist = (obj.nestedRuns("subtitle") ?: "").substringBefore("•").trim()
            val thumb = obj.thumb()
            if (out.none { it.youtubeId == videoId }) {
                out += Song(
                    id = videoId,
                    title = title,
                    artist = artist,
                    coverUrl = thumb,
                    youtubeId = videoId,
                    source = "youtube",
                )
            }
        }
        return out
    }

    private fun walk(el: JsonElement, on: (JsonObject) -> Unit) {
        when (el) {
            is JsonObject -> {
                on(el)
                el.values.forEach { walk(it, on) }
            }
            is JsonArray -> el.forEach { walk(it, on) }
            else -> Unit
        }
    }

    private fun JsonObject.str(k: String) = this[k]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }
    private fun JsonObject.nestedRuns(k: String): String? {
        val t = this[k] ?: return null
        val runs = t.jsonObject["runs"]?.jsonArray ?: return runCatching { t.jsonPrimitive.content }.getOrNull()
        return runs.joinToString("") { it.jsonObject["text"]?.jsonPrimitive?.content.orEmpty() }.ifBlank { null }
    }
    private fun JsonObject.thumb(): String? {
        val thumbs = this["thumbnail"]?.jsonObject?.get("thumbnails")?.jsonArray
            ?: this["thumbnails"]?.jsonArray
            ?: return null
        return thumbs.lastOrNull()?.jsonObject?.get("url")?.jsonPrimitive?.content
    }

    private fun String.q() = "\"" + replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}
