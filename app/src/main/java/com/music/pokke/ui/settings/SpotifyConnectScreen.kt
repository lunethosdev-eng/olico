package com.music.pokke.ui.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.music.pokke.data.settings.AppSettings
import com.music.pokke.data.spotify.SpotifyAuth
import com.music.pokke.data.spotify.SpotifyLibrary
import com.music.pokke.data.spotify.SpotifyPlaylist
import kotlinx.coroutines.launch

@Composable
fun SpotifyConnectScreen(contentPadding: PaddingValues = PaddingValues()) {
    var spdc by remember { mutableStateOf(SpotifyAuth.spDc()) }
    var status by remember { mutableStateOf<String?>(null) }
    var playlists by remember { mutableStateOf<List<SpotifyPlaylist>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun refreshPlaylists() {
        scope.launch {
            loading = true
            status = "Cargando playlists…"
            playlists = runCatching { SpotifyLibrary.playlists() }.getOrDefault(emptyList())
            status = if (playlists.isEmpty()) {
                "Sin playlists (revisa sp_dc / sesión)"
            } else {
                "${playlists.size} playlists"
            }
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        if (SpotifyAuth.spDc().isNotBlank()) refreshPlaylists()
    }

    Column(Modifier.padding(contentPadding).padding(16.dp)) {
        Text("Spotify", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Sesión web (sp_dc). Importa playlists sin API Premium oficial.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Button(
            onClick = {
                context.startActivity(Intent(context, SpotifyWebLoginActivity::class.java))
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Iniciar sesión en Spotify") }

        Text("O pegar sp_dc", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 16.dp))
        OutlinedTextField(
            value = spdc,
            onValueChange = { spdc = it },
            label = { Text("sp_dc") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    SpotifyAuth.setSpDc(spdc.trim())
                    AppSettings.setSpotifyEnabled(true)
                    status = "sp_dc guardado"
                    refreshPlaylists()
                },
            ) { Text("Guardar y cargar") }
            TextButton(onClick = { refreshPlaylists() }, enabled = !loading) {
                Text(if (loading) "…" else "Recargar")
            }
        }
        status?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
        }

        if (playlists.isNotEmpty()) {
            Text(
                "Playlists",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )
            LazyColumn {
                items(playlists, key = { it.id }) { pl ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = pl.imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                        )
                        Column(Modifier.padding(start = 12.dp)) {
                            Text(pl.name, style = MaterialTheme.typography.bodyLarge)
                            pl.owner?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}
