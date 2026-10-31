package com.music.pokke.ui.lyrics

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.pokke.data.lyrics.LyricsApi
import com.music.pokke.data.model.Song

@Composable
fun LyricsScreen(
    song: Song?,
    positionMs: Long = 0L,
    contentPadding: PaddingValues = PaddingValues(),
    onShare: () -> Unit = {},
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
    val active = if (lines.isEmpty()) -1 else lines.indexOfLast { it.first <= positionMs }.coerceAtLeast(0)
    val listState = rememberLazyListState()
    LaunchedEffect(active) {
        if (active >= 0 && lines.isNotEmpty()) {
            runCatching { listState.animateScrollToItem(index = active, scrollOffset = -120) }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(contentPadding),
    ) {
        // soft cover wash
        song?.coverUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.25f,
            )
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color.Black.copy(0.55f), Color.Black.copy(0.92f))),
                ),
            )
        }

        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            if (song != null) {
                Row(
                    Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = song.coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                    )
                    Column(Modifier.padding(start = 12.dp).weight(1f)) {
                        Text(song.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1)
                        Text(song.artist, color = Color.White.copy(0.55f), fontSize = 13.sp, maxLines = 1)
                    }
                    IconButton(onClick = onShare) {
                        Icon(Icons.Rounded.Share, null, tint = Color.White)
                    }
                }
            }

            if (lines.isNotEmpty()) {
                LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
                    itemsIndexed(lines) { i, (_, text) ->
                        val isActive = i == active
                        val color by animateColorAsState(
                            if (isActive) Color.White else Color.White.copy(0.28f),
                            tween(280),
                            label = "ly",
                        )
                        Text(
                            text,
                            color = color,
                            fontSize = if (isActive) 28.sp else 22.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            lineHeight = if (isActive) 36.sp else 30.sp,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        )
                    }
                }
                Text("Letra · lrclib.net", color = Color.White.copy(0.35f), fontSize = 11.sp, modifier = Modifier.padding(bottom = 12.dp))
            } else if (plain.isNotBlank()) {
                LazyColumn(Modifier.weight(1f)) {
                    item {
                        Text(plain, color = Color.White.copy(0.85f), fontSize = 20.sp, lineHeight = 32.sp)
                    }
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sin letras", color = Color.White.copy(0.4f))
                }
            }
        }
    }
}
