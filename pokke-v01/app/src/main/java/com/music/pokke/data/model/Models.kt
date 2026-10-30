package com.music.pokke.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: String,
    val title: String,
    val artist: String = "",
    val album: String? = null,
    @SerialName("audio_url") val audioUrl: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("animated_cover_url") val animatedCoverUrl: String? = null,
    @SerialName("duration_seconds") val durationSeconds: Int? = null,
    @SerialName("youtube_id") val youtubeId: String? = null,
    val lyrics: String? = null,
    @SerialName("synced_lyrics") val syncedLyrics: String? = null,
    val source: String = "ukiyo", // ukiyo | youtube | spotify
)

data class LyricLine(val timeMs: Long, val text: String)

data class DownloadProgress(
    val id: String,
    val percent: Int,
    val stage: String = "",
    val done: Boolean = false,
    val error: String? = null,
)

@Serializable
data class TrendItem(
    val songId: String = "",
    val title: String = "",
    val artist: String = "",
    val coverUrl: String? = null,
    val plays: Long = 0,
    val source: String = "ukiyo",
)
