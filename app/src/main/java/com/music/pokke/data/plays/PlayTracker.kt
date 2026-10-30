package com.music.pokke.data.plays

import com.music.pokke.PokkeConfig
import com.music.pokke.data.auth.KokoroAuth
import com.music.pokke.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Records plays to Kokoro for trends (Ukiyo + YouTube).
 * Table suggested: public.play_events (song_id, title, artist, source, user_id, played_at)
 * Create via SQL in Kokoro when ready; failures are ignored.
 */
object PlayTracker {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val http = OkHttpClient()
    private val media = "application/json".toMediaType()

    fun record(song: Song) {
        scope.launch {
            val session = KokoroAuth.session.value
            val body = buildString {
                append("{")
                append("\"song_id\":${song.id.json()},")
                append("\"title\":${song.title.json()},")
                append("\"artist\":${song.artist.json()},")
                append("\"source\":${song.source.json()},")
                append("\"cover_url\":${(song.coverUrl ?: "").json()}")
                if (session != null) append(",\"user_id\":${session.userId.json()}")
                append("}")
            }
            val req = Request.Builder()
                .url("${PokkeConfig.KOKORO_URL}/rest/v1/play_events")
                .header("apikey", PokkeConfig.KOKORO_ANON_KEY)
                .header("Authorization", "Bearer ${session?.accessToken ?: PokkeConfig.KOKORO_ANON_KEY}")
                .header("Content-Type", "application/json")
                .header("Prefer", "return=minimal")
                .post(body.toRequestBody(media))
                .build()
            runCatching { http.newCall(req).execute().close() }
        }
    }

    private fun String.json() = "\"" + replace("\\", "\\\\").replace("\"", "\\\"") + "\""
}
