package com.music.pokke.ui.player

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.VolumeUp
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.pokke.data.model.Song

@Composable
fun MiniPlayer(
    song: Song,
    playing: Boolean,
    liquidGlass: Boolean = false,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    onOpen: () -> Unit,
) {
    val bg = if (liquidGlass) Color(0x99101014) else Color(0xF01C1C22)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(bg)
            .clickable(onClick = onOpen)
            .padding(start = 8.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = song.coverUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)),
        )
        Column(Modifier.padding(horizontal = 12.dp).weight(1f)) {
            Text(song.title, maxLines = 1, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 15.sp)
            Text(song.artist, maxLines = 1, color = Color.White.copy(0.55f), fontSize = 13.sp)
        }
        IconButton(onClick = onToggle) {
            Icon(if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(28.dp))
        }
        IconButton(onClick = onNext) {
            Icon(Icons.Rounded.SkipNext, null, tint = Color.White, modifier = Modifier.size(28.dp))
        }
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
    showAudioSheet: Boolean = false,
    showMoreMenu: Boolean = false,
    deviceName: String,
    lyricLine: String?,
    sleepMinutes: Int = 0,
    liquidGlass: Boolean = false,
    onToggle: () -> Unit,
    onSeek: (Long) -> Unit,
    onVolume: (Float) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onLyrics: () -> Unit,
    onQueueToggle: () -> Unit,
    onDevice: () -> Unit,
    onDismissAudio: () -> Unit = {},
    onMore: () -> Unit = {},
    onDismissMore: () -> Unit = {},
    onSleep: (Int) -> Unit = {},
    onShareLyrics: () -> Unit = {},
    onClose: () -> Unit,
    onPlayIndex: (Int) -> Unit,
) {
    Box(Modifier.fillMaxSize().background(Color.Black)) {
        // Cover top with fade into dark (BitChord-like)
        AsyncImage(
            model = song.coverUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(420.dp).align(Alignment.TopCenter),
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(420.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.45f to Color.Black.copy(0.25f),
                        0.85f to Color.Black.copy(0.85f),
                        1f to Color.Black,
                    ),
                ),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to Color.Black.copy(0.4f),
                        1f to Color.Black,
                    ),
                ),
        )

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
        ) {
            Box(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(0.35f))
                    .clickable(onClick = onClose),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onMore) {
                    Icon(Icons.Rounded.MoreVert, null, tint = Color.White)
                }
            }

            if (showQueue) {
                Text("Cola", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(Modifier.height(12.dp))
                LazyColumn(Modifier.weight(1f)) {
                    itemsIndexed(queue) { i, s ->
                        Row(
                            Modifier.fillMaxWidth().clickable { onPlayIndex(i) }.padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = s.coverUrl,
                                contentDescription = null,
                                modifier = Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                                Text(s.title, color = Color.White, maxLines = 1, fontWeight = if (s.id == song.id) FontWeight.Bold else FontWeight.Normal)
                                Text(s.artist, color = Color.White.copy(0.5f), maxLines = 1, fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                Spacer(Modifier.weight(1f))
                Text(song.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 26.sp, maxLines = 2)
                Text(song.artist, color = Color.White.copy(0.65f), fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp, bottom = 14.dp))

                if (!lyricLine.isNullOrBlank()) {
                    Text(
                        lyricLine,
                        color = Color.White.copy(0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 15.sp,
                        modifier = Modifier.clickable(onClick = onLyrics).padding(bottom = 12.dp),
                    )
                }

                val dur = durationMs.coerceAtLeast(1L)
                val progress = (positionMs.toFloat() / dur).coerceIn(0f, 1f)
                Slider(
                    value = progress,
                    onValueChange = { onSeek((it * dur).toLong()) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(0.25f),
                    ),
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(fmt(positionMs), color = Color.White.copy(0.55f), fontSize = 12.sp)
                    Text("-" + fmt((durationMs - positionMs).coerceAtLeast(0)), color = Color.White.copy(0.55f), fontSize = 12.sp)
                }

                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onPrev, modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Rounded.SkipPrevious, null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    Box(
                        Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = onToggle,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(52.dp),
                        )
                    }
                    IconButton(onClick = onNext, modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Rounded.SkipNext, null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                }

                // Volume row (subtle, BitChord style)
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.VolumeUp, null, tint = Color.White.copy(0.5f), modifier = Modifier.size(18.dp))
                    Slider(
                        value = volume,
                        onValueChange = onVolume,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White.copy(0.85f),
                            inactiveTrackColor = Color.White.copy(0.2f),
                        ),
                    )
                }

                Row(
                    Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Letras",
                        color = Color.White.copy(0.85f),
                        modifier = Modifier.clickable(onClick = onLyrics).padding(12.dp),
                    )
                    Text(
                        "🎧",
                        modifier = Modifier.clickable(onClick = onDevice).padding(12.dp),
                        fontSize = 20.sp,
                    )
                    Text(
                        "≡",
                        color = Color.White,
                        fontSize = 22.sp,
                        modifier = Modifier.clickable(onClick = onQueueToggle).padding(12.dp),
                    )
                }
                Text(
                    deviceName + if (sleepMinutes > 0) " · ${sleepMinutes} min" else "",
                    color = Color.White.copy(0.45f),
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally).clickable(onClick = onDevice).padding(bottom = 12.dp),
                )
            }
        }

        if (showAudioSheet) {
            ModalBottomSheet(onDismissRequest = onDismissAudio, sheetState = rememberModalBottomSheetState()) {
                Column(Modifier.padding(20.dp).navigationBarsPadding()) {
                    Text("Audio output", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Este teléfono", modifier = Modifier.fillMaxWidth().clickable(onClick = onDismissAudio).padding(14.dp))
                    Text(deviceName, modifier = Modifier.fillMaxWidth().padding(14.dp), color = MaterialTheme.colorScheme.primary)
                    Text("Cast", modifier = Modifier.fillMaxWidth().padding(14.dp), color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                    Slider(value = volume, onValueChange = onVolume)
                }
            }
        }
        if (showMoreMenu) {
            ModalBottomSheet(onDismissRequest = onDismissMore, sheetState = rememberModalBottomSheetState()) {
                Column(Modifier.padding(20.dp).navigationBarsPadding()) {
                    Text(song.title, fontWeight = FontWeight.Bold)
                    Text(song.artist, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                    Spacer(Modifier.height(8.dp))
                    listOf(
                        "Compartir letra" to { onShareLyrics(); onDismissMore() },
                        "Agregar a la cola" to onDismissMore,
                        "Temporizador 15 min" to { onSleep(15); onDismissMore() },
                        "Temporizador 30 min" to { onSleep(30); onDismissMore() },
                        "Temporizador off" to { onSleep(0); onDismissMore() },
                    ).forEach { (label, action) ->
                        Text(label, modifier = Modifier.fillMaxWidth().clickable(onClick = action).padding(vertical = 14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PokkeBottomNav(
    route: String,
    liquidGlass: Boolean = false,
    onHome: () -> Unit,
    onLibrary: () -> Unit,
    onSearch: () -> Unit,
) {
    val bg = if (liquidGlass) Color(0x99101014) else Color(0xF0121218)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
            .navigationBarsPadding()
            .clip(RoundedCornerShape(32.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavIcon("Inicio", Icons.Rounded.Home, route == "home", onHome)
        NavIcon("Biblioteca", Icons.Rounded.LibraryMusic, route == "library", onLibrary)
        NavIcon("Buscar", Icons.Rounded.Search, route == "search", onSearch)
    }
}

@Composable
private fun NavIcon(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(if (selected) 1.05f else 1f, spring(stiffness = Spring.StiffnessMedium), label = "s")
    Column(
        Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(if (selected) Color(0xFF2A2A32) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, label, tint = if (selected) Color.White else Color.White.copy(0.4f), modifier = Modifier.size((22 * scale).dp))
        Text(label, color = if (selected) Color.White else Color.White.copy(0.4f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

private fun fmt(ms: Long): String {
    val s = (ms / 1000).toInt().coerceAtLeast(0)
    return "%d:%02d".format(s / 60, s % 60)
}
