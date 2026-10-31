package com.music.pokke.ui.player

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Headset
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.pokke.data.model.Song
import com.music.pokke.ui.glass.LiquidGlassSurface
import com.music.pokke.ui.glass.glassPill

@Composable
fun MiniPlayer(
    song: Song,
    playing: Boolean,
    liquidGlass: Boolean,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    onOpen: () -> Unit,
) {
    LiquidGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable(onClick = onOpen),
        enabled = liquidGlass,
        corner = 22.dp,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = song.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)),
            )
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(song.title, maxLines = 1, fontWeight = FontWeight.SemiBold, overflow = TextOverflow.Ellipsis)
                Text(song.artist, maxLines = 1, color = Color.White.copy(0.55f), fontSize = 12.sp, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onToggle) {
                Icon(
                    if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (playing) "Pause" else "Play",
                    tint = Color.White,
                )
            }
            IconButton(onClick = onNext) {
                Icon(Icons.Rounded.SkipNext, contentDescription = "Next", tint = Color.White)
            }
        }
    }
}

@Composable
fun PokkeBottomNav(
    route: String,
    liquidGlass: Boolean,
    profileCoverUrl: String? = null,
    onHome: () -> Unit,
    onLibrary: () -> Unit,
    onSearch: () -> Unit,
    onProfile: () -> Unit = {},
) {
    // 3 tabs only: Inicio · Biblioteca · Buscar (no Explorar)
    LiquidGlassSurface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        enabled = liquidGlass,
        corner = 32.dp,
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavTab("Inicio", Icons.Rounded.Home, route == "home", onHome)
            NavTab("Biblioteca", Icons.Rounded.LibraryMusic, route == "library", onLibrary)
            NavTab("Buscar", Icons.Rounded.Search, route == "search", onSearch)
        }
    }
}

@Composable
private fun NavTab(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(if (selected) 1.08f else 1f, spring(stiffness = Spring.StiffnessMedium), label = "nav")
    val tint by animateColorAsState(if (selected) Color.White else Color.White.copy(0.45f), label = "tint")
    Column(
        Modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(if (selected) Color.White.copy(0.12f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        Text(label, color = tint, fontSize = 11.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerScreen(
    song: Song,
    queue: List<Song>,
    playing: Boolean,
    positionMs: Long,
    durationMs: Long,
    volume: Float,
    showQueue: Boolean,
    showAudioSheet: Boolean,
    showMoreMenu: Boolean,
    deviceName: String,
    lyricLine: String?,
    sleepMinutes: Int?,
    onToggle: () -> Unit,
    onSeek: (Long) -> Unit,
    onVolume: (Float) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onLyrics: () -> Unit,
    onQueue: () -> Unit,
    onAudio: () -> Unit,
    onMore: () -> Unit,
    onCloseQueue: () -> Unit,
    onCloseAudio: () -> Unit,
    onCloseMore: () -> Unit,
    onSleep: (Int?) -> Unit,
    onShareLyrics: () -> Unit = {},
) {
    val dur = durationMs.coerceAtLeast(1L)
    val progress = (positionMs.toFloat() / dur).coerceIn(0f, 1f)

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AsyncImage(
            model = song.coverUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(0.25f), Color.Black.copy(0.55f), Color.Black.copy(0.92f)),
                ),
            ),
        )
        Column(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text(
                "Playing from Pokke",
                color = Color.White.copy(0.55f),
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
            )
            Spacer(Modifier.weight(0.35f))
            Text(song.title, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold, maxLines = 2)
            Text(song.artist, color = Color.White.copy(0.7f), fontSize = 16.sp)
            if (!lyricLine.isNullOrBlank()) {
                Text(
                    lyricLine,
                    color = Color.White.copy(0.85f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 10.dp).clickable(onClick = onLyrics),
                )
            }
            Spacer(Modifier.height(12.dp))
            Slider(
                value = progress,
                onValueChange = { onSeek((it * dur).toLong()) },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(0.25f),
                ),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatMs(positionMs), color = Color.White.copy(0.6f), fontSize = 12.sp)
                Text("-${formatMs((dur - positionMs).coerceAtLeast(0))}", color = Color.White.copy(0.6f), fontSize = 12.sp)
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onPrev) {
                    Icon(Icons.Rounded.SkipPrevious, null, tint = Color.White, modifier = Modifier.size(40.dp))
                }
                IconButton(
                    onClick = onToggle,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                ) {
                    Icon(
                        if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp),
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Rounded.SkipNext, null, tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }
            Slider(
                value = volume,
                onValueChange = onVolume,
                modifier = Modifier.padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(0.2f),
                ),
            )
            Row(
                Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                IconButton(onClick = onLyrics) {
                    Text("♪", color = Color.White, fontSize = 20.sp)
                }
                IconButton(onClick = onAudio) {
                    Icon(Icons.Rounded.Headset, null, tint = Color.White)
                }
                IconButton(onClick = onQueue) {
                    Icon(Icons.Rounded.QueueMusic, null, tint = Color.White)
                }
                IconButton(onClick = onMore) {
                    Icon(Icons.Rounded.MoreHoriz, null, tint = Color.White)
                }
            }
            Text(deviceName, color = Color.White.copy(0.55f), fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }

    if (showQueue) {
        ModalBottomSheet(onDismissRequest = onCloseQueue, sheetState = rememberModalBottomSheetState()) {
            Text("Cola", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            LazyColumn {
                itemsIndexed(queue) { _, s ->
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(s.coverUrl, null, Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                        Column(Modifier.padding(start = 12.dp).weight(1f)) {
                            Text(s.title, maxLines = 1, fontWeight = FontWeight.Medium)
                            Text(s.artist, maxLines = 1, color = Color.White.copy(0.5f), fontSize = 13.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showAudioSheet) {
        ModalBottomSheet(onDismissRequest = onCloseAudio, sheetState = rememberModalBottomSheetState()) {
            Text("Audio output", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            AudioRow("This phone", selected = deviceName.contains("teléfono", true) || deviceName.contains("phone", true))
            AudioRow(deviceName.ifBlank { "Bluetooth" }, selected = true)
            AudioRow("Cast", selected = false)
            Spacer(Modifier.height(16.dp))
            Text("Volumen", modifier = Modifier.padding(horizontal = 16.dp))
            Slider(value = volume, onValueChange = onVolume, modifier = Modifier.padding(horizontal = 16.dp))
            Text("Audio Pipeline · 16-bit PCM · 48 kHz", color = Color.White.copy(0.45f), fontSize = 12.sp, modifier = Modifier.padding(16.dp))
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showMoreMenu) {
        ModalBottomSheet(onDismissRequest = onCloseMore, sheetState = rememberModalBottomSheetState()) {
            Column(Modifier.padding(16.dp)) {
                Text("Más", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                MoreRow("Compartir letra") { onShareLyrics(); onCloseMore() }
                MoreRow("Agregar a la cola") { onCloseMore() }
                MoreRow("Temporizador 15 min") { onSleep(15); onCloseMore() }
                MoreRow("Temporizador 30 min") { onSleep(30); onCloseMore() }
                MoreRow("Temporizador off") { onSleep(null); onCloseMore() }
                if (sleepMinutes != null) {
                    Text("Sleep: ${sleepMinutes}m", color = Color.White.copy(0.5f), modifier = Modifier.padding(top = 8.dp))
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AudioRow(title: String, selected: Boolean) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color.White.copy(0.1f) else Color.Transparent)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Headset, null, tint = Color.White)
        Text(title, Modifier.padding(start = 12.dp).weight(1f), color = Color.White)
        if (selected) Text("✓", color = Color.White)
    }
}

@Composable
private fun MoreRow(label: String, onClick: () -> Unit) {
    Text(
        label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        color = Color.White,
        fontSize = 16.sp,
    )
}

private fun formatMs(ms: Long): String {
    val s = (ms / 1000).toInt()
    val m = s / 60
    val r = s % 60
    return "%d:%02d".format(m, r)
}
