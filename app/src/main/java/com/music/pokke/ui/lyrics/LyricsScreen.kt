package com.music.pokke.ui.lyrics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.pokke.data.model.LyricLine

object LyricsParser {
    private val re = Regex("""\[(\d+):(\d+)(?:\.(\d+))?]\s*(.*)""")
    fun parse(lrc: String?): List<LyricLine> {
        if (lrc.isNullOrBlank()) return emptyList()
        return lrc.lineSequence().mapNotNull { line ->
            val m = re.find(line.trim()) ?: return@mapNotNull null
            val min = m.groupValues[1].toLong()
            val sec = m.groupValues[2].toLong()
            val frac = m.groupValues[3].padEnd(3, '0').take(3).toLongOrNull() ?: 0L
            val text = m.groupValues[4]
            if (text.isBlank()) null else LyricLine(min * 60_000 + sec * 1_000 + frac, text)
        }.sortedBy { it.timeMs }.toList()
    }
}

/** Sync + scale animation (BitChord-inspired skeleton; full port in v0.2). */
@Composable
fun LyricsScreen(lines: List<LyricLine>, positionMs: Long, modifier: Modifier = Modifier) {
    val active = remember(lines, positionMs) {
        lines.indexOfLast { it.timeMs <= positionMs }.coerceAtLeast(0)
    }
    val listState = rememberLazyListState()
    LaunchedEffect(active) {
        if (lines.isNotEmpty()) listState.animateScrollToItem(active.coerceIn(0, lines.lastIndex))
    }
    if (lines.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Sin letras", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f))
        }
        return
    }
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 72.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        itemsIndexed(lines) { i, line ->
            val on = i == active
            val scale by animateFloatAsState(if (on) 1.06f else 1f, tween(200), label = "s")
            val alpha by animateFloatAsState(if (on) 1f else 0.32f, tween(200), label = "a")
            Text(
                line.text,
                textAlign = TextAlign.Center,
                fontSize = if (on) 22.sp else 18.sp,
                fontWeight = if (on) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
                    .padding(vertical = 4.dp),
            )
        }
    }
}
