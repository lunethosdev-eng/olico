package com.music.pokke.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Headset
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.music.pokke.data.model.Song

@Composable
fun MiniPlayer(
    song: Song,
    playing: Boolean,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    onOpen: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xE6111114))
            .clickable(onClick = onOpen)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = song.coverUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)),
        )
        Column(Modifier.padding(horizontal = 12.dp).weight(1f)) {
            Text(song.title, maxLines = 1, fontWeight = FontWeight.SemiBold, color = Color.White)
            Text(song.artist, maxLines = 1, color = Color.White.copy(alpha = 0.55f), style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onToggle) {
            Icon(if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White)
        }
        IconButton(onClick = onNext) {
            Icon(Icons.Rounded.SkipNext, null, tint = Color.White)
        }
    }
}

@Composable
fun FullPlayerScreen(
    song: Song,
    queue: List<Song>,
    playing: Boolean,
    positionMs: Long,
    durationMs: Long,
    volume: Float,
    showQueue: Boolean,
    deviceName: String,
    lyricLine: String?,
    onToggle: () -> Unit,
    onSeek: (Long) -> Unit,
    onVolume: (Float) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onLyrics: () -> Unit,
    onQueueToggle: () -> Unit,
    onDevice: () -> Unit,
    onClose: () -> Unit,
    onPlayIndex: (Int) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        AsyncImage(
            model = song.coverUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(0.35f), Color.Black.copy(0.92f)),
                    ),
                ),
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Box(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(0.35f))
                    .clickable(onClick = onClose),
            )
            Spacer(Modifier.height(12.dp))
            if (showQueue) {
                Text("Cola", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.weight(1f)) {
                    itemsIndexed(queue) { i, s ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { onPlayIndex(i) }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = s.coverUrl,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                                Text(s.title, color = Color.White, maxLines = 1)
                                Text(s.artist, color = Color.White.copy(0.5f), maxLines = 1, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            } else {
                Spacer(Modifier.weight(0.35f))
                Text(song.title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                Text(song.artist, color = Color.White.copy(0.7f), modifier = Modifier.padding(top = 4.dp))
                Spacer(Modifier.height(16.dp))
                if (!lyricLine.isNullOrBlank()) {
                    Text(
                        lyricLine,
                        color = Color.White.copy(0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable(onClick = onLyrics),
                    )
                    Spacer(Modifier.height(12.dp))
                }
                val dur = durationMs.coerceAtLeast(1L)
                Slider(
                    value = (positionMs.toFloat() / dur).coerceIn(0f, 1f),
                    onValueChange = { onSeek((it * dur).toLong()) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(fmt(positionMs), color = Color.White.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                    Text("-" + fmt((durationMs - positionMs).coerceAtLeast(0)), color = Color.White.copy(0.6f), style = MaterialTheme.typography.labelSmall)
                }
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onPrev) {
                        Icon(Icons.Rounded.SkipPrevious, null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    IconButton(onClick = onToggle, modifier = Modifier.size(72.dp)) {
                        Icon(
                            if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                    IconButton(onClick = onNext) {
                        Icon(Icons.Rounded.SkipNext, null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.VolumeUp, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(20.dp))
                    Slider(
                        value = volume,
                        onValueChange = onVolume,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    )
                }
                Row(
                    Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    IconButton(onClick = onLyrics) {
                        Text("♪", color = Color.White, style = MaterialTheme.typography.titleLarge)
                    }
                    IconButton(onClick = onDevice) {
                        Icon(Icons.Rounded.Headset, null, tint = Color.White)
                    }
                    IconButton(onClick = onQueueToggle) {
                        Icon(Icons.Rounded.QueueMusic, null, tint = Color.White)
                    }
                }
                Text(
                    deviceName,
                    color = Color.White.copy(0.55f),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp),
                )
            }
        }
    }
}

private fun fmt(ms: Long): String {
    val s = (ms / 1000).toInt().coerceAtLeast(0)
    return "%d:%02d".format(s / 60, s % 60)
}

@Composable
fun PokkeBottomNav(
    route: String,
    onHome: () -> Unit,
    onLibrary: () -> Unit,
    onSearch: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xE6121216))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavPill("Inicio", route == "home", onHome)
        NavPill("Biblioteca", route == "library", onLibrary)
        NavPill("Buscar", route == "search", onSearch)
    }
}

@Composable
private fun NavPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) Color(0xFF2A2A32) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
    ) {
        Text(label, color = if (selected) Color.White else Color.White.copy(0.45f), fontWeight = FontWeight.Medium)
    }
}
