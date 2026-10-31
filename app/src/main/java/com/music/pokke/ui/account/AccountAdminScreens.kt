package com.music.pokke.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.music.pokke.PokkeConfig
import com.music.pokke.data.auth.KokoroAuth
import com.music.pokke.data.settings.AppSettings
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(
    onSpotify: () -> Unit = {},
    onOnboarding: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
) {
    val session by KokoroAuth.session.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text("Cuenta e integraciones", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer8()
        Card {
            if (session != null) {
                Text(session!!.email, fontWeight = FontWeight.SemiBold)
                if (KokoroAuth.isOwner) Text("Owner · Admin", color = MaterialTheme.colorScheme.primary)
                Button(onClick = { KokoroAuth.signOut() }, modifier = Modifier.padding(top = 8.dp)) { Text("Cerrar sesión") }
            } else {
                Text("Sesión no iniciada", fontWeight = FontWeight.SemiBold)
                Text("Toca para iniciar con Kokoro o Google", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true)
                OutlinedTextField(password, { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
                info?.let { Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp)) }
                Button(
                    onClick = {
                        scope.launch {
                            error = null
                            KokoroAuth.signInEmail(email.trim(), password).onFailure { error = it.message }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                ) { Text("Iniciar sesión") }
                Button(
                    onClick = {
                        scope.launch {
                            error = null; info = null
                            KokoroAuth.signUpEmail(email.trim(), password)
                                .onSuccess {
                                    info = "Cuenta creada"
                                    onOnboarding()
                                }
                                .onFailure { error = it.message }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) { Text("Crear cuenta (Sign up)") }
                Text(
                    if (KokoroAuth.googleReady()) "Sign in with Google estará activo con Client ID"
                    else "Sign in with Google · configura GOOGLE_WEB_CLIENT_ID",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.45f),
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }

        Section("SPOTIFY")
        Card {
            Row(Modifier.fillMaxWidth().clickable(onClick = onSpotify).padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Spotify", fontWeight = FontWeight.SemiBold)
                    Text("Playlists · sesión web (sp_dc)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                }
                Text(">", color = MaterialTheme.colorScheme.onSurface.copy(0.35f))
            }
            Text("La reproducción de audio usa el catálogo Pokke / YouTube según fuentes.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.45f))
        }

        Section("PRESENCIA")
        Card {
            RowItem("Discord", "Muestra lo que escuchas (token / login)")
            Text("Conector Discord: presencia enriquecida (scaffold).", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.45f))
        }

        Section("REGISTRO")
        Card {
            ToggleRow("Last.fm", "Scrobble")
            ToggleRow("ListenBrainz", "Token opcional")
        }
    }
}

@Composable
fun AdminScreen(contentPadding: PaddingValues = PaddingValues()) {
    val api by AppSettings.musicApiBase.collectAsState()
    var url by remember(api) { mutableStateOf(api) }
    Column(Modifier.padding(contentPadding).padding(16.dp)) {
        Text("Admin", style = MaterialTheme.typography.headlineMedium)
        Text("Owner: ${PokkeConfig.OWNER_EMAIL}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 8.dp))
        OutlinedTextField(url, { url = it }, label = { Text("Music API base URL") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Button(onClick = { AppSettings.setMusicApiBase(url) }, modifier = Modifier.padding(top = 8.dp)) { Text("Guardar API") }
    }
}

@Composable
fun OnboardingArtistsScreen(contentPadding: PaddingValues = PaddingValues(), onDone: () -> Unit = {}) {
    var q by remember { mutableStateOf("") }
    var artists by remember { mutableStateOf<List<com.music.pokke.data.deezer.DeezerApi.Artist>>(emptyList()) }
    val scope = rememberCoroutineScope()
    Column(Modifier.padding(contentPadding).padding(16.dp)) {
        Text("Artistas favoritos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Fotos desde Deezer (tu catálogo aún no tiene artistas).", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
        OutlinedTextField(q, {
            q = it
            scope.launch {
                artists = com.music.pokke.data.deezer.DeezerApi.searchArtists(it)
            }
        }, label = { Text("Buscar artista") }, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp))
        artists.forEach { a ->
            Text(a.name, modifier = Modifier.padding(vertical = 6.dp))
        }
        Button(onClick = onDone, modifier = Modifier.padding(top = 16.dp)) { Text("Continuar") }
    }
}

@Composable private fun Spacer8() = androidx.compose.foundation.layout.Spacer(Modifier.padding(4.dp))
@Composable private fun Section(t: String) = Text(t, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface.copy(0.45f), modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
@Composable private fun Card(c: @Composable () -> Unit) = Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(0.45f)).padding(16.dp)) { c() }
@Composable private fun RowItem(title: String, sub: String) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Text(title, fontWeight = FontWeight.SemiBold)
        Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
    }
}
@Composable private fun ToggleRow(title: String, sub: String) {
    var on by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
        }
        Switch(checked = on, onCheckedChange = { on = it })
    }
}
