package com.music.pokke.data.discord

import com.music.pokke.PokkeConfig
import com.music.pokke.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URLEncoder

/**
 * Discord rich presence scaffold.
 * Full desktop RPC (IPC) no existe en Android; usamos token + estado local
 * y OAuth. Cuando hay token + enabled + canción, [statusText] refleja presencia.
 */
object DiscordPresence {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()
    private val _enabled = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()
    private val _last = MutableStateFlow<Song?>(null)
    val lastSong: StateFlow<Song?> = _last.asStateFlow()
    private val _statusText = MutableStateFlow("Sin conexión")
    val statusText: StateFlow<String> = _statusText.asStateFlow()

    const val REDIRECT_URI = "https://localhost/pokke/discord"

    fun setToken(t: String?) {
        _token.value = t?.trim()?.takeIf { it.isNotEmpty() }
        refreshStatus()
    }

    fun setEnabled(on: Boolean) {
        _enabled.value = on && PokkeConfig.DISCORD_APPLICATION_ID.isNotBlank()
        refreshStatus()
    }

    fun updateNowPlaying(song: Song?) {
        _last.value = song
        refreshStatus()
    }

    private fun refreshStatus() {
        val song = _last.value
        when {
            PokkeConfig.DISCORD_APPLICATION_ID.isBlank() ->
                _statusText.value = "Configura DISCORD_APPLICATION_ID"
            _token.value == null ->
                _statusText.value = "Sin conexión"
            !_enabled.value ->
                _statusText.value = "Presencia pausada"
            song != null ->
                _statusText.value = "Escuchando ${song.title} · ${song.artist}"
            else ->
                _statusText.value = "Conectado · idle"
        }
    }

    fun oauthUrl(): String {
        val id = PokkeConfig.DISCORD_APPLICATION_ID
        if (id.isBlank()) return "https://discord.com/login"
        val redirect = URLEncoder.encode(REDIRECT_URI, "UTF-8")
        return "https://discord.com/api/oauth2/authorize" +
            "?client_id=$id&response_type=token&scope=identify&redirect_uri=$redirect"
    }
}
