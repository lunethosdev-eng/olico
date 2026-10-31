package com.music.pokke.ui.account

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.music.pokke.PokkeConfig
import com.music.pokke.data.discord.DiscordPresence

@Composable
fun DiscordConnectScreen(contentPadding: PaddingValues = PaddingValues()) {
    var tokenInput by remember { mutableStateOf("") }
    val savedToken by DiscordPresence.token.collectAsState()
    val isEnabled by DiscordPresence.enabled.collectAsState()
    val context = LocalContext.current

    Column(Modifier.padding(contentPadding).padding(16.dp)) {
        Text("Discord", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "App ID: ${PokkeConfig.DISCORD_APPLICATION_ID.ifBlank { "(vacío)" }}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            "IMPORTANTE — en Discord Developer Portal → OAuth2 → Redirects agrega exactamente:\n${DiscordPresence.REDIRECT_URI}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 10.dp),
        )
        Button(
            onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DiscordPresence.oauthUrl())))
            },
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Abrir Discord / autorizar") }

        Text("O pegar token (avanzado)", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 16.dp))
        OutlinedTextField(
            value = tokenInput,
            onValueChange = { tokenInput = it },
            label = { Text("Token") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Button(onClick = { DiscordPresence.setToken(tokenInput) }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Guardar token")
        }
        Text(
            if (savedToken != null) "Token guardado" else "Sin token",
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(0.55f),
        )
        Row(Modifier.padding(top = 16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Mostrar lo que escucho", fontWeight = FontWeight.SemiBold)
                Text("Presencia (base)", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = isEnabled, onCheckedChange = { DiscordPresence.setEnabled(it) })
        }
    }
}
