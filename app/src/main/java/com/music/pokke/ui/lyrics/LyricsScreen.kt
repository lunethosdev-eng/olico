package com.music.pokke.ui.lyrics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.music.pokke.data.lyrics.LyricsApi
import com.music.pokke.data.model.Song

@Composable
fun LyricsScreen(
    song: Song?,
    positionMs: Long = 0L,
    contentPadding: PaddingValues = PaddingValues(),
) {
    var lines by remember { mutableStateOf<List<Pair<Long, String>>>(emptyList()) }
    var plain by remember { mutableStateOf("") }
    LaunchedEffect(song?.id) {
        lines = emptyList()
        plain = ""
        if (song == null) return@LaunchedEffect
        val r = runCatching {
            LyricsApi.fetch(song.title, song.artist, song.durationSeconds)
        }.getOrNull()
        lines = r?.synced.orEmpty()
        plain = r?.plain.orEmpty()
    }
    val active = lines.indexOfLast { it.first <= positionMs }.coerceAtLeast(0)
    val listState = rememberLazyListState()
    LaunchedEffect(active) {
        if (lines.isNotEmpty()) runCatching { listState.animateScrollToItem(active) }
    }
    Column(Modifier.fillMaxSize().padding(contentPadding).padding(16.dp)) {
        if (song != null) {
            Text(song.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(song.artist, color = MaterialTheme.colorScheme.onSurface.copy(0.55f))
        }
        if (lines.isNotEmpty()) {
            LazyColumn(state = listState, modifier = Modifier.padding(top = 20.dp).weight(1f)) {
                itemsIndexed(lines) { i, (_, text) ->
                    Text(
                        text,
                        style = if (i == active) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                        fontWeight = if (i == active) FontWeight.Bold else FontWeight.Normal,
                        color = if (i == active) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurface.copy(0.35f),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    )
                }
            }
            Text("Letra · lrclib.net", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(0.4f))
        } else if (plain.isNotBlank()) {
            LazyColumn(Modifier.padding(top = 20.dp)) {
                item {
                    Text(plain, style = MaterialTheme.typography.titleMedium, lineHeight = MaterialTheme.typography.titleMedium.lineHeight * 1.4)
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Sin letras", color = MaterialTheme.colorScheme.onSurface.copy(0.45f))
            }
        }
    }
}
