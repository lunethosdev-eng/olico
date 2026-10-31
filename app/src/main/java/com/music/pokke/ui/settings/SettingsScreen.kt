package com.music.pokke.ui.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
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
        Text("Ajustes", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Personaliza tu experiencia BitChord", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 16.dp))

        CardBlock {
            Section("Apariencia & Liquid Glass")
            ToggleRow3D("Tema Oscuro", "Fondo OLED puro", dark, AppSettings::setDarkTheme)
            ToggleRow3D("Liquid Glass Real-Time", "Efecto de refracción y desenfoque avanzado", glass, AppSettings::setLiquidGlass)
            ToggleRow3D("Covers Animados", "Reproduce animaciones en portadas", anim, AppSettings::setAutoAnimatedCovers)
        }

        CardBlock {
            Section("Fuentes de Audio")
            ToggleRow3D("Catálogo Pokke", "Servidor principal Ukiyo", ukiyo, AppSettings::setUkiyoEnabled)
            ToggleRow3D("YouTube Music", "Soporte de streaming secundario", yt, AppSettings::setYoutubeEnabled)
            ToggleRow3D("Spotify Integration", "Sincronización mediante sp_dc", sp, AppSettings::setSpotifyEnabled)
            TextButton(onClick = onSpotify) { Text("Configurar Conexión Spotify") }
        }

        CardBlock {
            Section("Social & Sincronización")
            ToggleRow3D("Escuchar Juntos", "Modo Party activo", party, AppSettings::setListenTogether)
            ToggleRow3D("Letras Sincronizadas", "Efecto karaoke dinámico", lyrics, AppSettings::setSyncedLyrics)
        }

        CardBlock {
            Section("Cuenta & Admin")
            TextButton(onClick = onAccount) { Text("Gestionar Perfil y Cuenta") }
            if (isOwner) TextButton(onClick = onAdmin) { Text("Panel de Administrador") }
            Text("API Base: $api", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), modifier = Modifier.padding(top = 8.dp))
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
private fun ToggleRow3D(title: String, sub: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    // Animación 3D líquida para los toggles
    val rotY by animateFloatAsState(targetValue = if (checked) 10f else -10f, label = "toggle3d")
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            modifier = Modifier.graphicsLayer {
                rotationY = rotY
                cameraDistance = 12f * density
            }
        )
    }
}
