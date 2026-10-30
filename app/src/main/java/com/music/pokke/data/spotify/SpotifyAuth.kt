package com.music.pokke.data.spotify

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Same approach as BitChord: free Spotify account session via `sp_dc` cookie
 * (web player), NOT Spotify Premium Web API client credentials.
 *
 * User pastes sp_dc from open.spotify.com cookies (or WebView login later).
 */
object SpotifyAuth {
    private val http = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val media = "application/json".toMediaType()
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("pokke_spotify", Context.MODE_PRIVATE)
        SpotifyLibrary.auth = SpotifyLibrary.Auth {
            val bearer = accessToken() ?: return@Auth null
            SpotifyLibrary.Tokens(bearer, clientToken = null)
        }
    }

    fun spDc(): String = prefs.getString("sp_dc", "") ?: ""

    fun setSpDc(value: String) {
        prefs.edit().putString("sp_dc", value.trim()).apply()
        cachedBearer = null
    }

    @Volatile private var cachedBearer: String? = null
    @Volatile private var expiresAt = 0L

    suspend fun accessToken(): String? = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        cachedBearer?.takeIf { now < expiresAt - 60_000 }?.let { return@withContext it }
        val spdc = spDc()
        if (spdc.isBlank()) return@withContext null
        // Same family of endpoints BitChord uses: get_access_token with sp_dc cookie
        val url = "https://open.spotify.com/get_access_token?reason=transport&productType=web_player"
        val req = Request.Builder()
            .url(url)
            .header("Cookie", "sp_dc=$spdc")
            .header("User-Agent", "Mozilla/5.0")
            .get()
            .build()
        runCatching {
            http.newCall(req).execute().use { resp ->
                val body = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) return@use null
                val obj = json.parseToJsonElement(body).jsonObject
                val token = obj["accessToken"]?.jsonPrimitive?.content
                val exp = obj["accessTokenExpirationTimestampMs"]?.jsonPrimitive?.content?.toLongOrNull()
                if (token != null) {
                    cachedBearer = token
                    expiresAt = exp ?: (now + 3_600_000)
                }
                token
            }
        }.getOrNull()
    }
}
