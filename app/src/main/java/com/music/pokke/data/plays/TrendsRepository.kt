package com.music.pokke.data.plays

import com.music.pokke.PokkeConfig
import com.music.pokke.data.model.TrendItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

object TrendsRepository {
    private val http = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun load(): List<TrendItem> = withContext(Dispatchers.IO) {
        val url = "${PokkeConfig.KOKORO_URL}/rest/v1/trending_songs?select=*&limit=30"
        val req = Request.Builder()
            .url(url)
            .header("apikey", PokkeConfig.KOKORO_ANON_KEY)
            .header("Authorization", "Bearer ${PokkeConfig.KOKORO_ANON_KEY}")
            .get()
            .build()
        val body = runCatching {
            http.newCall(req).execute().use { r -> if (r.isSuccessful) r.body?.string().orEmpty() else "" }
        }.getOrDefault("")
        if (body.isBlank()) return@withContext emptyList()
        runCatching {
            json.parseToJsonElement(body).jsonArray.mapNotNull { el ->
                val o = el.jsonObject
                TrendItem(
                    songId = o["song_id"]?.jsonPrimitive?.content.orEmpty(),
                    title = o["title"]?.jsonPrimitive?.content.orEmpty(),
                    artist = o["artist"]?.jsonPrimitive?.content.orEmpty(),
                    coverUrl = o["cover_url"]?.jsonPrimitive?.content,
                    plays = o["plays"]?.jsonPrimitive?.longOrNull ?: 0L,
                    source = o["source"]?.jsonPrimitive?.content ?: "ukiyo",
                )
            }
        }.getOrDefault(emptyList())
    }
}
