package com.music.pokke.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.music.pokke.data.model.Song
import com.music.pokke.data.model.TrendItem
import com.music.pokke.ui.components.SongRow

@Composable
fun HomeScreen(
    recent: List<Song>,
    trends: List<TrendItem>,
    onPlay: (Song) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 96.dp,
            start = 20.dp,
            end = 20.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text("Listen now", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Pokke catalog · Ukiyo",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (trends.isNotEmpty()) {
            item {
                Spacer(Modifier.height(16.dp))
                Text("Trending", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(trends, key = { it.songId + it.title }) { t ->
                        Column(Modifier.size(width = 130.dp, height = 180.dp)) {
                            AsyncImage(
                                model = t.coverUrl,
                                contentDescription = t.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(130.dp).clip(RoundedCornerShape(12.dp)),
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(t.title, maxLines = 1, fontWeight = FontWeight.Medium)
                            Text(
                                t.artist,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(12.dp))
            Text("From Ukiyo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
        items(recent, key = { it.id }) { song ->
            SongRow(song) { onPlay(song) }
        }
        if (recent.isEmpty()) {
            item {
                Text(
                    "No songs yet — check music API / tunnel.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                    modifier = Modifier.padding(top = 24.dp),
                )
            }
        }
    }
}
