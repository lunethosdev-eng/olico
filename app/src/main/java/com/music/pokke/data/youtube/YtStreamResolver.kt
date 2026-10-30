package com.music.pokke.data.youtube

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfo

/**
 * Direct YouTube audio URL via NewPipeExtractor (BitChord-class approach).
 */
object YtStreamResolver {
    @Volatile private var inited = false

    private fun ensureInit() {
        if (inited) return
        synchronized(this) {
            if (inited) return
            runCatching {
                // NewPipeExtractor self-inits services on first use in many versions
                ServiceList.YouTube
            }
            inited = true
        }
    }

    suspend fun audioUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        if (videoId.isBlank()) return@withContext null
        ensureInit()
        runCatching {
            val url = "https://www.youtube.com/watch?v=$videoId"
            val info = StreamInfo.getInfo(ServiceList.YouTube, url)
            val audio = info.audioStreams
                .sortedByDescending { it.bitrate }
                .firstOrNull()
                ?: info.videoStreams.firstOrNull()
            audio?.content
        }.getOrNull()
    }
}
