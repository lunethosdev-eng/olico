package com.music.pokke.ui.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.music.pokke.PokkeConfig
import com.music.pokke.data.discord.DiscordPresence

@Composable
fun DiscordConnectScreen(contentPadding: PaddingValues = PaddingValues()) {
    var token by remember { mutableStateOf("") }
    val saved by DiscordPresence.token.collectAsState()
    var showNow by remember { mutableStateOf(saved != null) }

    Column(Modifier.padding(contentPadding).padding(16.dp)) {
        Text("Discord", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "Presencia enriquecida (lo que escuchas en Pokke).",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(0.55f),
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Text(
            "1. Discord Developer Portal → New Application → copia Application ID\n" +
                "2. Pégalo en PokkeConfig.DISCORD_APPLICATION_ID\n" +
                "3. (Opcional) token de bot o login OAuth para RPC completo",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Text(
            "App ID: " + PokkeConfig.DISCORD_APPLICATION_ID.ifBlank { "(vacío — créalo en portal)" },
            style = MaterialTheme.typography.labelMedium,
        )
        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("Token (opcional / avanzado)") },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            singleLine = true,
        )
        Button(
            onClick = {
                DiscordPresence.setToken(token)
                showNow = token.isNotBlank()
            },
            modifier = Modifier.padding(top = 12.dp),
        ) { Text("Guardar") }
        Text(
            if (saved != null) "Conectado (token guardado en dispositivo)" else "Sin conexión",
            modifier = Modifier.padding(top = 12.dp),
        )
        androidx.compose.foundation.layout.Row(Modifier.padding(top = 16.dp)) {
            Column(Modifier.weight(1f)) {
                Text("Mostrar lo que escucho", fontWeight = FontWeight.SemiBold)
                Text("Requiere Application ID + gateway RPC", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = showNow && PokkeConfig.DISCORD_APPLICATION_ID.isNotBlank(), onCheckedChange = { showNow = it })
        }
    }
}
