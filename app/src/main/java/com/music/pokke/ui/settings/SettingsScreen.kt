package com.music.pokke.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.music.pokke.data.settings.AppSettings

@Composable
fun SettingsScreen(
    isOwner: Boolean,
    onAccount: () -> Unit,
    onAdmin: () -> Unit,
    onSpotify: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
) {
    val dark by AppSettings.darkTheme.collectAsState()
    val eq by AppSettings.equalizer.collectAsState()
    val xf by AppSettings.crossfade.collectAsState()
    val skip by AppSettings.skipSilence.collectAsState()
    val glass by AppSettings.liquidGlass.collectAsState()
    val anim by AppSettings.autoAnimatedCovers.collectAsState()
    val ukiyo by AppSettings.ukiyoEnabled.collectAsState()
    val yt by AppSettings.youtubeEnabled.collectAsState()
    val sp by AppSettings.spotifyEnabled.collectAsState()
    val party by AppSettings.listenTogether.collectAsState()
    val lyrics by AppSettings.syncedLyrics.collectAsState()
    val api by AppSettings.musicApiBase.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        Section("Appearance")
        Toggle("Dark theme", "Light / dark", dark, AppSettings::setDarkTheme)
        Toggle("Liquid Glass", "Experimental — off by default", glass, AppSettings::setLiquidGlass)
        Toggle("Auto animated covers", "mp4 / webm / gif when available", anim, AppSettings::setAutoAnimatedCovers)

        Section("Playback")
        Toggle("Equalizer", "In-app EQ (wired in later build)", eq, AppSettings::setEqualizer)
        Toggle("Crossfade", "Smooth transition between tracks", xf, AppSettings::setCrossfade)
        Toggle("Skip silence", "Skip silent gaps", skip, AppSettings::setSkipSilence)
        Toggle("Synced lyrics", "Animated lyrics on by default", lyrics, AppSettings::setSyncedLyrics)
        Text("Audio quality Wi‑Fi / data — UI detail in v0.2", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f), modifier = Modifier.padding(vertical = 8.dp))

        Section("Sources")
        Toggle("Ukiyo (Pokke catalog)", "Default source", ukiyo, AppSettings::setUkiyoEnabled)
        Toggle("YouTube Music", "Off by default. Enables Google + catalog + silent harvest", yt, AppSettings::setYoutubeEnabled)
        Toggle("Spotify", "Playlists via sp_dc (BitChord-style, free account)", sp, AppSettings::setSpotifyEnabled)
        TextButton(onClick = onSpotify) { Text("Connect Spotify (sp_dc)") }

        Section("Social")
        Toggle("Listen Together", "Party mode (v0.2)", party, AppSettings::setListenTogether)

        Section("Language")
        Text("System / ES / EN — selector in v0.2", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))

        Text("API: $api", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))

        TextButton(onClick = onAccount, modifier = Modifier.padding(top = 8.dp)) { Text("Account / profile") }
        if (isOwner) {
            TextButton(onClick = onAdmin) { Text("Admin") }
        }
    }
}

@Composable
private fun Section(t: String) {
    Text(t, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 16.dp, bottom = 6.dp))
}

@Composable
private fun Toggle(title: String, sub: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Switch(checked = checked, onCheckedChange = onChange, modifier = Modifier.padding(top = 4.dp))
    }
}
