package com.music.pokke.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        Text("Tu Biblioteca", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // Las 4 cajas solicitadas: Likes, Descargas, Playlists y Recientes
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            LibraryBoxItem("❤️ Likes", Modifier.weight(1f)) {}
            LibraryBoxItem("⬇️ Descargas", Modifier.weight(1f)) {}
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            LibraryBoxItem("🎵 Playlists", Modifier.weight(1f)) {}
            LibraryBoxItem("🕒 Recientes", Modifier.weight(1f)) {}
        }

        Spacer(Modifier.height(24.dp))
        Text("Historial de canciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        LazyColumn(Modifier.weight(1f)) {
            items(songs, key = { it.id }) { SongRow(it) { onPlay(it) } }
        }
    }
}

@Composable
private fun LibraryBoxItem(title: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}
