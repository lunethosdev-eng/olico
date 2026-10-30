package com.music.pokke.data.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AppSettings {
    private lateinit var prefs: SharedPreferences

    private val _darkTheme = MutableStateFlow(true)
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    private val _audioQualityWifi = MutableStateFlow("high")
    val audioQualityWifi: StateFlow<String> = _audioQualityWifi.asStateFlow()

    private val _audioQualityMobile = MutableStateFlow("medium")
    val audioQualityMobile: StateFlow<String> = _audioQualityMobile.asStateFlow()

    private val _equalizer = MutableStateFlow(false)
    val equalizer: StateFlow<Boolean> = _equalizer.asStateFlow()

    private val _crossfade = MutableStateFlow(false)
    val crossfade: StateFlow<Boolean> = _crossfade.asStateFlow()

    private val _skipSilence = MutableStateFlow(false)
    val skipSilence: StateFlow<Boolean> = _skipSilence.asStateFlow()

    private val _liquidGlass = MutableStateFlow(false)
    val liquidGlass: StateFlow<Boolean> = _liquidGlass.asStateFlow()

    private val _autoAnimatedCovers = MutableStateFlow(true)
    val autoAnimatedCovers: StateFlow<Boolean> = _autoAnimatedCovers.asStateFlow()

    private val _ukiyoEnabled = MutableStateFlow(true)
    val ukiyoEnabled: StateFlow<Boolean> = _ukiyoEnabled.asStateFlow()

    private val _youtubeEnabled = MutableStateFlow(false)
    val youtubeEnabled: StateFlow<Boolean> = _youtubeEnabled.asStateFlow()

    private val _spotifyEnabled = MutableStateFlow(false)
    val spotifyEnabled: StateFlow<Boolean> = _spotifyEnabled.asStateFlow()

    private val _listenTogether = MutableStateFlow(false)
    val listenTogether: StateFlow<Boolean> = _listenTogether.asStateFlow()

    private val _language = MutableStateFlow("system")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _syncedLyrics = MutableStateFlow(true)
    val syncedLyrics: StateFlow<Boolean> = _syncedLyrics.asStateFlow()

    private val _musicApiBase = MutableStateFlow(com.music.pokke.PokkeConfig.MUSIC_API_BASE)
    val musicApiBase: StateFlow<String> = _musicApiBase.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("pokke_settings", Context.MODE_PRIVATE)
        _darkTheme.value = prefs.getBoolean("dark_theme", true)
        _audioQualityWifi.value = prefs.getString("aq_wifi", "high") ?: "high"
        _audioQualityMobile.value = prefs.getString("aq_mobile", "medium") ?: "medium"
        _equalizer.value = prefs.getBoolean("equalizer", false)
        _crossfade.value = prefs.getBoolean("crossfade", false)
        _skipSilence.value = prefs.getBoolean("skip_silence", false)
        _liquidGlass.value = prefs.getBoolean("liquid_glass", false)
        _autoAnimatedCovers.value = prefs.getBoolean("auto_anim_covers", true)
        _ukiyoEnabled.value = prefs.getBoolean("ukiyo", true)
        _youtubeEnabled.value = prefs.getBoolean("youtube", false)
        _spotifyEnabled.value = prefs.getBoolean("spotify", false)
        _listenTogether.value = prefs.getBoolean("listen_together", false)
        _language.value = prefs.getString("language", "system") ?: "system"
        _syncedLyrics.value = prefs.getBoolean("synced_lyrics", true)
        _musicApiBase.value = prefs.getString("music_api_base", null)
            ?: com.music.pokke.PokkeConfig.MUSIC_API_BASE
    }

    private fun putBool(k: String, v: Boolean) = prefs.edit().putBoolean(k, v).apply()
    private fun putStr(k: String, v: String) = prefs.edit().putString(k, v).apply()

    fun setDarkTheme(v: Boolean) { _darkTheme.value = v; putBool("dark_theme", v) }
    fun setAudioQualityWifi(v: String) { _audioQualityWifi.value = v; putStr("aq_wifi", v) }
    fun setAudioQualityMobile(v: String) { _audioQualityMobile.value = v; putStr("aq_mobile", v) }
    fun setEqualizer(v: Boolean) { _equalizer.value = v; putBool("equalizer", v) }
    fun setCrossfade(v: Boolean) { _crossfade.value = v; putBool("crossfade", v) }
    fun setSkipSilence(v: Boolean) { _skipSilence.value = v; putBool("skip_silence", v) }
    fun setLiquidGlass(v: Boolean) { _liquidGlass.value = v; putBool("liquid_glass", v) }
    fun setAutoAnimatedCovers(v: Boolean) { _autoAnimatedCovers.value = v; putBool("auto_anim_covers", v) }
    fun setUkiyoEnabled(v: Boolean) { _ukiyoEnabled.value = v; putBool("ukiyo", v) }
    fun setYoutubeEnabled(v: Boolean) { _youtubeEnabled.value = v; putBool("youtube", v) }
    fun setSpotifyEnabled(v: Boolean) { _spotifyEnabled.value = v; putBool("spotify", v) }
    fun setListenTogether(v: Boolean) { _listenTogether.value = v; putBool("listen_together", v) }
    fun setLanguage(v: String) { _language.value = v; putStr("language", v) }
    fun setSyncedLyrics(v: Boolean) { _syncedLyrics.value = v; putBool("synced_lyrics", v) }
    fun setMusicApiBase(url: String) {
        val c = url.trim().trimEnd('/')
        _musicApiBase.value = c
        putStr("music_api_base", c)
    }
}
