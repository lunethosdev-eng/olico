package com.music.pokke

import android.app.Application
import com.music.pokke.data.settings.AppSettings
import com.music.pokke.data.spotify.SpotifyAuth

class PokkeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppSettings.init(this)
        SpotifyAuth.init(this)
    }
}
