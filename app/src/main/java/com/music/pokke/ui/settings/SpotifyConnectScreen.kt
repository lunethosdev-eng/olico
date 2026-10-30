package com.music.pokke.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.music.pokke.data.spotify.SpotifyAuth
import com.music.pokke.data.spotify.SpotifyLibrary
import kotlinx.coroutines.launch

/**
 * Connect free Spotify account the BitChord way: paste sp_dc cookie.
 * Then [SpotifyLibrary.playlists] works without Premium API keys.
 */
@Composable
fun SpotifyConnectScreen(contentPadding: PaddingValues = PaddingValues()) {
    var spdc by remember { mutableStateOf(SpotifyAuth.spDc()) }
    var status by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(Modifier.padding(contentPadding).padding(16.dp)) {
        Text("Spotify (como BitChord)", style = MaterialTheme.typography.headlineSmall)
        Text(
            "No usa API Premium. Usa la sesión web (cookie sp_dc) igual que BitChord " +
                "para leer playlists y liked songs de una cuenta free.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Text(
            "En el navegador: open.spotify.com → F12 → Application → Cookies → sp_dc → copiar valor.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        OutlinedTextField(
            value = spdc,
            onValueChange = { spdc = it },
            label = { Text("sp_dc") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Button(
            onClick = {
                SpotifyAuth.setSpDc(spdc)
                scope.launch {
                    val token = SpotifyAuth.accessToken()
                    if (token == null) {
                        status = "No se pudo obtener token. Revisa sp_dc."
                    } else {
                        val lists = runCatching { SpotifyLibrary.playlists() }.getOrElse { emptyList() }
                        status = "OK · ${lists.size} playlists"
                    }
                }
            },
            modifier = Modifier.padding(top = 12.dp),
        ) { Text("Guardar y probar") }
        status?.let { Text(it, modifier = Modifier.padding(top = 12.dp)) }
    }
}
