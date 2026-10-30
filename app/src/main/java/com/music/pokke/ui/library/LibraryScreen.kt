package com.music.pokke.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.music.pokke.data.model.Song
import com.music.pokke.ui.components.SongRow

@Composable
fun LibraryScreen(
    songs: List<Song>,
    onPlay: (Song) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
    ) {
        Text("Biblioteca", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text(
            "Tus canciones y descargas",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )
        LazyColumn {
            items(songs, key = { it.id }) { SongRow(it) { onPlay(it) } }
        }
    }
}
