package com.music.pokke.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val lang by AppSettings.language.collectAsState()
    val aqW by AppSettings.audioQualityWifi.collectAsState()
    val aqM by AppSettings.audioQualityMobile.collectAsState()
    val api by AppSettings.musicApiBase.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text("Ajustes", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Personaliza Pokke", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 12.dp))

        CardBlock {
            Section("Apariencia")
            ToggleRow("Tema oscuro", "Fondo negro / claro", dark, AppSettings::setDarkTheme)
            ToggleRow("Liquid Glass", "Nav y paneles translúcidos", glass, AppSettings::setLiquidGlass)
            ToggleRow("Covers animados", "Cuando haya mp4/webm/gif", anim, AppSettings::setAutoAnimatedCovers)
            Text("Idioma", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
            Row(Modifier.padding(top = 6.dp)) {
                listOf("system" to "Sistema", "es" to "ES", "en" to "EN").forEach { (id, label) ->
                    FilterChip(
                        selected = lang == id,
                        onClick = { AppSettings.setLanguage(id) },
                        label = { Text(label) },
                        modifier = Modifier.padding(end = 6.dp),
                    )
                }
            }
        }

        CardBlock {
            Section("Reproducción")
            ToggleRow("Ecualizador", "Se aplica en el motor de audio (base)", eq, AppSettings::setEqualizer)
            ToggleRow("Crossfade", "Transición suave entre pistas", xf, AppSettings::setCrossfade)
            ToggleRow("Saltar silencios", "Omite huecos en silencio", skip, AppSettings::setSkipSilence)
            ToggleRow("Letras sincronizadas", "Animación de letras", lyrics, AppSettings::setSyncedLyrics)
            Text("Calidad Wi‑Fi", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
            QualityRow(aqW) { AppSettings.setAudioQualityWifi(it) }
            Text("Calidad datos", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
            QualityRow(aqM) { AppSettings.setAudioQualityMobile(it) }
        }

        CardBlock {
            Section("Fuentes")
            ToggleRow("Catálogo Pokke", "Servidor de música (Ukiyo)", ukiyo, AppSettings::setUkiyoEnabled)
            ToggleRow("YouTube Music", "Off por defecto · harvest silencioso", yt, AppSettings::setYoutubeEnabled)
            ToggleRow("Spotify", "Playlists con sp_dc (cuenta free)", sp, AppSettings::setSpotifyEnabled)
            TextButton(onClick = onSpotify) { Text("Conectar Spotify (sp_dc)") }
        }

        CardBlock {
            Section("Social")
            ToggleRow("Escuchar juntos", "Modo party", party, AppSettings::setListenTogether)
        }

        CardBlock {
            Section("Cuenta")
            TextButton(onClick = onAccount) { Text("Cuenta / perfil") }
            if (isOwner) TextButton(onClick = onAdmin) { Text("Admin") }
            Text("API: $api", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun CardBlock(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(16.dp),
    ) { content() }
}

@Composable
private fun Section(t: String) {
    Text(t, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun ToggleRow(title: String, sub: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun QualityRow(current: String, onChange: (String) -> Unit) {
    Row(Modifier.padding(top = 4.dp, bottom = 4.dp)) {
        listOf("low" to "Baja", "medium" to "Media", "high" to "Alta").forEach { (id, label) ->
            FilterChip(
                selected = current == id,
                onClick = { onChange(id) },
                label = { Text(label) },
                modifier = Modifier.padding(end = 6.dp),
            )
        }
    }
}
