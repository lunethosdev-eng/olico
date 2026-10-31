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
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
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
            Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
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
                Icon(if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White)
            }
            IconButton(onClick = onNext) {
                Icon(Icons.Rounded.SkipNext, null, tint = Color.White)
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

/** Signature aligned with MainActivity call site. */
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
    onQueueToggle: () -> Unit,
    onDevice: () -> Unit,
    onDismissAudio: () -> Unit,
    onMore: () -> Unit,
    onDismissMore: () -> Unit,
    onSleep: (Int?) -> Unit,
    onShareLyrics: () -> Unit = {},
    liquidGlass: Boolean = false,
    onClose: () -> Unit = {},
    onPlayIndex: (Int) -> Unit = {},
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
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp).clickable(onClick = onClose),
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
                    modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.White),
                ) {
                    Icon(
                        if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        null,
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
                IconButton(onClick = onLyrics) { Text("♪", color = Color.White, fontSize = 20.sp) }
                IconButton(onClick = onDevice) { Icon(Icons.Rounded.Headset, null, tint = Color.White) }
                IconButton(onClick = onQueueToggle) { Icon(Icons.Rounded.QueueMusic, null, tint = Color.White) }
                IconButton(onClick = onMore) { Icon(Icons.Rounded.MoreHoriz, null, tint = Color.White) }
            }
            Text(deviceName, color = Color.White.copy(0.55f), fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }

    if (showQueue) {
        ModalBottomSheet(onDismissRequest = onQueueToggle, sheetState = rememberModalBottomSheetState()) {
            Text("Cola", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            LazyColumn {
                itemsIndexed(queue) { index, s ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onPlayIndex(index) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
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
        ModalBottomSheet(onDismissRequest = onDismissAudio, sheetState = rememberModalBottomSheetState()) {
            Text("Audio output", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            Text(deviceName, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = Color.White)
            Slider(value = volume, onValueChange = onVolume, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showMoreMenu) {
        ModalBottomSheet(onDismissRequest = onDismissMore, sheetState = rememberModalBottomSheetState()) {
            Column(Modifier.padding(16.dp)) {
                Text("Más", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text("Compartir letra", Modifier.fillMaxWidth().clickable { onShareLyrics(); onDismissMore() }.padding(vertical = 14.dp), color = Color.White, fontSize = 16.sp)
                Text("Temporizador 15 min", Modifier.fillMaxWidth().clickable { onSleep(15); onDismissMore() }.padding(vertical = 14.dp), color = Color.White, fontSize = 16.sp)
                Text("Temporizador 30 min", Modifier.fillMaxWidth().clickable { onSleep(30); onDismissMore() }.padding(vertical = 14.dp), color = Color.White, fontSize = 16.sp)
                Text("Temporizador off", Modifier.fillMaxWidth().clickable { onSleep(null); onDismissMore() }.padding(vertical = 14.dp), color = Color.White, fontSize = 16.sp)
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val s = (ms / 1000).toInt()
    return "%d:%02d".format(s / 60, s % 60)
}
