package com.music.pokke.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.pokke.data.model.Song
import com.music.pokke.data.model.TrendItem

@Composable
fun HomeScreen(
    recent: List<Song>,
    trends: List<TrendItem>,
    onPlay: (Song) -> Unit,
    onSettings: () -> Unit,
    onProfile: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val mix = (trends.map {
        Song(id = it.songId, title = it.title, artist = it.artist, coverUrl = it.coverUrl, audioUrl = null)
    } + recent).distinctBy { it.id }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 110.dp,
            start = 16.dp,
            end = 16.dp,
        ),
    ) {
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Pokke Home",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSettings) {
                    Icon(Icons.Rounded.Settings, "Ajustes", tint = MaterialTheme.colorScheme.onSurface)
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        if (trends.isNotEmpty()) {
            item {
                SectionTitle("Tendencias Globales")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(trends, key = { "t" + it.songId + it.title }) { t ->
                        CoverCard(
                            title = t.title,
                            subtitle = t.artist,
                            cover = t.coverUrl,
                            onClick = {
                                onPlay(Song(id = t.songId, title = t.title, artist = t.artist, coverUrl = t.coverUrl, audioUrl = null))
                            },
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        if (recent.isNotEmpty()) {
            item {
                SectionTitle("Escuchado Recientemente")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(recent.take(20), key = { "r" + it.id }) { s ->
                        CoverCard(s.title, s.artist, s.coverUrl) { onPlay(s) }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        if (mix.isNotEmpty()) {
            item {
                SectionTitle("Mix Para Ti")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    items(mix.take(15), key = { "m" + it.id }) { s ->
                        CoverCard(s.title, s.artist, s.coverUrl) { onPlay(s) }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
}

@Composable
private fun CoverCard(title: String, subtitle: String, cover: String?, onClick: () -> Unit) {
    Column(Modifier.width(148.dp).clickable(onClick = onClick)) {
        AsyncImage(
            model = cover,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(148.dp).clip(RoundedCornerShape(16.dp)),
        )
        Text(title, maxLines = 1, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp), overflow = TextOverflow.Ellipsis)
        Text(subtitle, maxLines = 1, color = MaterialTheme.colorScheme.onSurface.copy(0.5f), fontSize = 12.sp, overflow = TextOverflow.Ellipsis)
    }
}
