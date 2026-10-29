package com.music.bitchord.data.sources

import com.music.olico.OlicoConfig
import com.music.olico.ukiyo.UkiyoClient
import com.music.bitchord.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Olico primary catalogue: Ukiyo music API (+ catalog fills from /api/search).
 * Stored as ADDON with [OlicoConfig.MUSIC_API_BASE] so it appears in Sources UI.
 */
class UkiyoSource(
    override val config: SourceConfig,
) : MusicSource, SourceRegistry.ConfigBacked {

    override val configId: String get() = config.id
    override val kind: SourceKind get() = config.kind
    override val displayName: String get() = config.label.ifBlank { "Ukiyo" }

    override suspend fun health(): SourceHealth = withContext(Dispatchers.IO) {
        runCatching {
            UkiyoClient.recent(1)
            SourceHealth.Ok("Ukiyo")
        }.getOrElse { SourceHealth.Unreachable(it.message ?: "unreachable") }
    }

    override suspend fun search(
        query: String,
        limit: Int,
        waitForAll: Boolean,
        request: StreamRequest?,
    ): List<Song> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        UkiyoClient.search(query, limit).map { it.toSong(config.id) }
    }

    override suspend fun stream(trackId: String, request: StreamRequest): SourceStream? =
        withContext(Dispatchers.IO) {
            val row = UkiyoClient.getById(trackId) ?: return@withContext null
            if (row.audioUrl.isBlank()) return@withContext null
            SourceStream(url = row.audioUrl, format = StreamFormat())
        }

    private fun UkiyoClient.UkiyoSong.toSong(configId: String): Song {
        val dur = durationSeconds?.let { s -> "%d:%02d".format(s / 60, s % 60) }
        return Song(
            videoId = SourceRegistry.trackKey(configId, id.ifBlank { youtubeId ?: title }),
            title = title,
            artist = artist,
            thumbnailUrl = coverUrl ?: animatedCoverUrl,
            durationText = dur,
            albumName = album,
        )
    }

    companion object {
        fun isUkiyo(config: SourceConfig): Boolean {
            if (config.label.equals("Ukiyo", true)) return true
            val u = config.baseUrl.lowercase()
            return u.contains("rotary-align-arrive-lift") ||
                u.contains("esjoifsjljvymttinyhj") ||
                u.contains(OlicoConfig.MUSIC_API_BASE.lowercase().removePrefix("https://"))
        }

        fun defaultConfig() = SourceConfig(
            kind = SourceKind.ADDON,
            label = "Ukiyo",
            baseUrl = OlicoConfig.MUSIC_API_BASE,
            enabled = true,
        )
    }
}
