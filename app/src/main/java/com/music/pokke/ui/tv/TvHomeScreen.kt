package com.music.pokke.ui.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.pokke.data.model.Song
import com.music.pokke.data.model.TrendItem

/**
 * TV 10-foot home: large cards, D-pad friendly focus targets.
 * Not a phone layout scaled up.
 */
@Composable
fun TvHomeScreen(
    trends: List<TrendItem>,
    recent: List<Song>,
    onPlay: (Song) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0F))
            .padding(contentPadding)
            .padding(horizontal = 48.dp, vertical = 32.dp),
    ) {
        Text("Pokke", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("Para tu tele", color = Color.White.copy(0.5f), modifier = Modifier.padding(top = 4.dp, bottom = 28.dp))

        Text("Tendencias", style = MaterialTheme.typography.titleLarge, color = Color.White, modifier = Modifier.padding(bottom = 12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            items(trends.take(20), key = { it.songId }) { t ->
                TvCard(
                    title = t.title,
                    subtitle = t.artist,
                    cover = t.coverUrl,
                    onClick = {
                        onPlay(
                            Song(
                                id = t.songId,
                                title = t.title,
                                artist = t.artist,
                                coverUrl = t.coverUrl,
                                source = t.source ?: "ukiyo",
                            ),
                        )
                    },
                )
            }
        }

        Text("Recientes", style = MaterialTheme.typography.titleLarge, color = Color.White, modifier = Modifier.padding(top = 28.dp, bottom = 12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            items(recent.take(20), key = { it.id }) { s ->
                TvCard(title = s.title, subtitle = s.artist, cover = s.coverUrl, onClick = { onPlay(s) })
            }
        }
    }
}

@Composable
private fun TvCard(
    title: String,
    subtitle: String,
    cover: String?,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .focusTarget()
            .clickable(onClick = onClick)
            .background(Color.White.copy(0.06f))
            .padding(12.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        AsyncImage(
            model = cover,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(176.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(0.08f)),
        )
        Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, modifier = Modifier.padding(top = 10.dp))
        Text(subtitle, color = Color.White.copy(0.55f), maxLines = 1, fontSize = 13.sp)
    }
}
