package com.music.pokke.data.discord

import com.music.pokke.PokkeConfig
import com.music.pokke.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object DiscordPresence {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _enabled = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private val _last = MutableStateFlow<Song?>(null)
    val lastSong: StateFlow<Song?> = _last.asStateFlow()

    fun setToken(t: String?) {
        _token.value = t?.trim()?.takeIf { it.isNotEmpty() }
        if (_token.value != null) {
            _enabled.value = PokkeConfig.DISCORD_APPLICATION_ID.isNotBlank()
        }
    }

    fun setEnabled(on: Boolean) {
        _enabled.value = on && PokkeConfig.DISCORD_APPLICATION_ID.isNotBlank()
    }

    fun updateNowPlaying(song: Song?) {
        _last.value = song
        if (!_enabled.value || PokkeConfig.DISCORD_APPLICATION_ID.isBlank()) return
    }

    fun oauthUrl(): String {
        val id = PokkeConfig.DISCORD_APPLICATION_ID
        if (id.isBlank()) return "https://discord.com/login"
        return "https://discord.com/oauth2/authorize?client_id=$id&response_type=token&scope=identify"
    }
}
