package com.music.pokke.data.catalog

import com.music.pokke.data.settings.AppSettings
import com.music.pokke.data.ukiyo.UkiyoApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * When YouTube is ON and the user plays/searches a track, ask Ukiyo /api/search
 * in the background (no progress UI). Builds the Pokke catalog.
 */
object SilentHarvest {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val seen = LinkedHashSet<String>()

    fun enqueue(title: String, artist: String, youtubeId: String? = null) {
        if (!AppSettings.youtubeEnabled.value) return
        val q = when {
            !youtubeId.isNullOrBlank() -> "https://www.youtube.com/watch?v=$youtubeId"
            else -> listOf(title, artist).filter { it.isNotBlank() }.joinToString(" - ")
        }
        if (q.isBlank()) return
        synchronized(seen) {
            if (!seen.add(q.lowercase())) return
            if (seen.size > 200) {
                val it = seen.iterator()
                repeat(40) { if (it.hasNext()) { it.next(); it.remove() } }
            }
        }
        scope.launch { runCatching { UkiyoApi.search(q) } }
    }
}
