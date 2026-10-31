package com.music.pokke.data.spotify

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Spotify Canvas (loop video) via partner canvaz API when sp_dc session works.
 */
object SpotifyCanvas {
    private val http = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val media = "application/json".toMediaType()

    suspend fun videoUrl(trackId: String): String? = withContext(Dispatchers.IO) {
        val token = SpotifyAuth.accessToken() ?: return@withContext null
        val id = trackId
            .removePrefix("spotify:track:")
            .removePrefix("sp:")
            .trim()
        if (id.isBlank()) return@withContext null

        // Partner canvaz endpoint used by open clients
        val body = """{"track_uris":["spotify:track:$id"]}"""
        val req = Request.Builder()
            .url("https://spclient.wg.spotify.com/canvaz-cache/v0/canvases")
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .post(body.toRequestBody(media))
            .build()
        runCatching {
            http.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) return@use null
                val root = json.parseToJsonElement(resp.body?.string().orEmpty()).jsonObject
                val canvases = root["canvases"]?.jsonArray ?: return@use null
                canvases.firstOrNull()?.jsonObject?.get("canvas_url")?.jsonPrimitive?.content
                    ?: canvases.firstOrNull()?.jsonObject?.get("url")?.jsonPrimitive?.content
            }
        }.getOrNull()
    }
}
