package com.music.pokke.data.discord

import com.music.pokke.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Discord rich presence scaffold.
 * Full RPC needs a Discord Application ID + gateway; token login UI is in Account.
 */
object DiscordPresence {
    private val _token = MutableStateFlow<String?>(null)
    val token = _token.asStateFlow()

    fun setToken(t: String?) { _token.value = t?.trim()?.ifBlank { null } }

    fun updateNowPlaying(song: Song?) {
        // When token + app id available: POST presence payload
        if (_token.value == null || song == null) return
    }
}
