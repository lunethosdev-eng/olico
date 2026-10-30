package com.music.pokke.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text("Search") },
        )
        progress?.let {
            if (!yt) {
                Text("${it.percent}% ${it.stage}", modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.bodySmall)
                LinearProgressIndicator(
                    progress = { it.percent / 100f },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                )
            }
        }
        LazyColumn(Modifier.padding(top = 12.dp)) {
            items(results, key = { it.id }) { SongRow(it) { onPlay(it) } }
        }
    }
}
