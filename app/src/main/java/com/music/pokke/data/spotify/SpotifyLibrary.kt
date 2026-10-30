package com.music.pokke.data.spotify

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

data class SpotifyPlaylist(
    val id: String,
    val name: String,
    val owner: String?,
    val imageUrl: String?,
)

data class SpotifyTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String?,
    val durationMs: Int,
    val imageUrl: String?,
)

/**
 * BitChord-style partner GraphQL (web player token via [SpotifyAuth] / sp_dc).
 * No Spotify Premium developer API.
 */
object SpotifyLibrary {
    private val http = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val mediaType = "application/json; charset=utf-8".toMediaType()
    private const val GQL = "https://api-partner.spotify.com/pathfinder/v2/query"
    private const val LIBRARY_HASH = "973e511ca44261fda7eebac8b653155e7caee3675abb4fb110cc1b8c78b091c3"

    fun interface Auth {
        suspend fun tokens(): Tokens?
    }

    class Tokens(val bearer: String, val clientToken: String?)

    @Volatile
    var auth: Auth? = null

    suspend fun playlists(): List<SpotifyPlaylist> = withContext(Dispatchers.IO) {
        val headers = authHeaders() ?: return@withContext emptyList()
        val variables = buildJsonObject {
            put("offset", 0)
            put("limit", 50)
        }
        val root = gql("libraryV3", LIBRARY_HASH, variables, headers) ?: return@withContext emptyList()
        parsePlaylists(root)
    }

    private suspend fun authHeaders(): Map<String, String>? {
        val t = auth?.tokens() ?: return null
        return buildMap {
            put("authorization", "Bearer ${t.bearer}")
            put("content-type", "application/json")
            t.clientToken?.let { put("client-token", it) }
        }
    }

    private fun gql(
        operation: String,
        hash: String,
        variables: JsonObject,
        headers: Map<String, String>,
    ): JsonObject? {
        val body = buildJsonObject {
            put("variables", variables)
            put("operationName", operation)
            putJsonObject("extensions") {
                putJsonObject("persistedQuery") {
                    put("version", 1)
                    put("sha256Hash", hash)
                }
            }
        }
        val request = Request.Builder()
            .url(GQL)
            .post(body.toString().toRequestBody(mediaType))
            .apply { headers.forEach { (n, v) -> header(n, v) } }
            .build()
        return runCatching {
            http.newCall(request).execute().use { response ->
                val text = response.body?.string().orEmpty()
                if (!response.isSuccessful) return@use null
                json.parseToJsonElement(text).jsonObject
            }
        }.getOrNull()
    }

    private fun parsePlaylists(root: JsonObject): List<SpotifyPlaylist> {
        val out = ArrayList<SpotifyPlaylist>()
        fun walk(el: kotlinx.serialization.json.JsonElement?) {
            when (el) {
                is JsonObject -> {
                    val uri = el["uri"]?.jsonPrimitive?.contentOrNull
                    val name = el["name"]?.jsonPrimitive?.contentOrNull
                    if (uri != null && name != null && uri.contains("playlist")) {
                        val id = uri.substringAfter("playlist:")
                        val owner = el["owner"]?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull
                        val images = el["images"]?.jsonArray
                        val image = images?.firstOrNull()?.jsonObject?.get("url")?.jsonPrimitive?.contentOrNull
                            ?: images?.lastOrNull()?.jsonObject?.get("url")?.jsonPrimitive?.contentOrNull
                        if (out.none { it.id == id }) {
                            out += SpotifyPlaylist(id, name, owner, image)
                        }
                    }
                    el.values.forEach { walk(it) }
                }
                is JsonArray -> el.forEach { walk(it) }
                else -> Unit
            }
        }
        walk(root)
        return out
    }
}
