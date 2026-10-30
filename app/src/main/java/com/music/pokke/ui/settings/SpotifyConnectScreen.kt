package com.music.pokke.ui.settings

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.music.pokke.data.spotify.SpotifyAuth
import com.music.pokke.data.spotify.SpotifyLibrary
import kotlinx.coroutines.launch

@Composable
fun SpotifyConnectScreen(contentPadding: PaddingValues = PaddingValues()) {
    var spdc by remember { mutableStateOf(SpotifyAuth.spDc()) }
    var status by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(Modifier.padding(contentPadding).padding(16.dp)) {
        Text("Spotify", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Cada usuario inicia sesión con SU cuenta (WebView). No se usan cookies compartidas en el APK.",
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

        Text("O pegar sp_dc manualmente", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 16.dp))
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
                    if (token == null) status = "No se pudo obtener token"
                    else {
                        val lists = runCatching { SpotifyLibrary.playlists() }.getOrElse { emptyList() }
                        status = "OK · ${lists.size} playlists"
                    }
                }
            },
            modifier = Modifier.padding(top = 12.dp),
        ) { Text("Probar token") }
        status?.let { Text(it, modifier = Modifier.padding(top = 12.dp)) }
    }
}
