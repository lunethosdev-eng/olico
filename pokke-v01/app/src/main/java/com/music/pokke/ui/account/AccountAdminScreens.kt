package com.music.pokke.ui.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.music.pokke.PokkeConfig
import com.music.pokke.data.auth.KokoroAuth
import com.music.pokke.data.settings.AppSettings
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(contentPadding: PaddingValues = PaddingValues()) {
    val session by KokoroAuth.session.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(Modifier.padding(contentPadding).padding(16.dp)) {
        Text("Account", style = MaterialTheme.typography.headlineMedium)
        if (session != null) {
            Text("Signed in as ${session!!.email}", modifier = Modifier.padding(top = 12.dp))
            if (KokoroAuth.isOwner) Text("Owner · Admin available", color = MaterialTheme.colorScheme.primary)
            Button(onClick = { KokoroAuth.signOut() }, modifier = Modifier.padding(top = 12.dp)) { Text("Sign out") }
        } else {
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true)
            OutlinedTextField(password, { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true)
            error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
            Button(
                onClick = {
                    scope.launch {
                        error = null
                        KokoroAuth.signInEmail(email.trim(), password)
                            .onFailure { error = it.message }
                    }
                },
                modifier = Modifier.padding(top = 12.dp),
            ) { Text("Sign in (Kokoro)") }

            Text(
                if (KokoroAuth.googleReady())
                    "Google sign-in ready (links YouTube + Pokke session)"
                else
                    "Google: add GOOGLE_WEB_CLIENT_ID in PokkeConfig after Google Cloud + Supabase setup",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 16.dp),
            )
            Button(
                onClick = { /* v0.2 Credential Manager */ },
                enabled = KokoroAuth.googleReady(),
                modifier = Modifier.padding(top = 8.dp),
            ) { Text("Sign in with Google") }
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
        Button(onClick = { AppSettings.setMusicApiBase(url) }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Save API URL")
        }
    }
}
