package com.music.pokke.ui.lyrics

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.pokke.data.model.Song

@Composable
fun ShareLyricsScreen(
    song: Song?,
    line: String?,
    contentPadding: PaddingValues = PaddingValues(),
    onClose: () -> Unit = {},
) {
    val ctx = LocalContext.current
    val title = song?.title ?: "Canción"
    val artist = song?.artist ?: ""
    val textLine = line?.takeIf { it.isNotBlank() } ?: "…"

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(contentPadding)
            .padding(20.dp),
    ) {
        Text("Compartir la letra", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Tarjeta con la portada y la línea elegida.",
            color = Color.White.copy(0.55f),
            modifier = Modifier.padding(top = 6.dp, bottom = 16.dp),
        )
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF5C2E1A), Color(0xFF2A150C))))
                .padding(20.dp),
        ) {
            Column {
                Text("Pokke", color = Color.White.copy(0.7f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.End))
                Spacer(Modifier.height(12.dp))
                Text(textLine, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = song?.coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)),
                    )
                    Column(Modifier.padding(start = 10.dp)) {
                        Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text(artist, color = Color.White.copy(0.65f), fontSize = 13.sp)
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onClose, modifier = Modifier.weight(1f)) { Text("Cancelar") }
            Button(
                onClick = {
                    val text = "\"$textLine\"\n— $title · $artist\nPokke"
                    ctx.startActivity(
                        Intent.createChooser(
                            Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, text)
                            },
                            "Compartir",
                        ),
                    )
                },
                modifier = Modifier.weight(1f),
            ) { Text("Compartir") }
        }
    }
}
