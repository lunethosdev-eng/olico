package com.music.pokke.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.music.pokke.data.model.DownloadProgress
import com.music.pokke.data.model.Song
import com.music.pokke.data.settings.AppSettings
import com.music.pokke.ui.components.SongRow

@Composable
fun SearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    results: List<Song>,
    progress: DownloadProgress?,
    onPlay: (Song) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val yt by AppSettings.youtubeEnabled.collectAsState()
    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(if (yt) "Search Ukiyo + YouTube" else "Search Pokke / Ukiyo") },
        )
        if (!yt) {
            Text(
                "YouTube is off. Missing tracks go to Ukiyo /api/search (progress below).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp),
            )
        } else {
            Text(
                "YouTube on — listens harvest silently into Ukiyo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        progress?.let {
            Text("Downloading ${it.percent}% ${it.stage}", modifier = Modifier.padding(top = 12.dp))
            LinearProgressIndicator(progress = { it.percent / 100f }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
        }
        LazyColumn(Modifier.padding(top = 12.dp)) {
            items(results, key = { it.id }) { SongRow(it) { onPlay(it) } }
        }
    }
}
