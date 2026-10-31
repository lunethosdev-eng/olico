package com.music.pokke.ui.lyrics

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.music.pokke.data.model.Song

@Composable
fun ShareLyricsScreen(
    song: Song?,
    line: String?,
    contentPadding: PaddingValues = PaddingValues(),
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val text = line ?: "…"
    Column(
        Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Compartir la letra", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Tarjeta con portada y línea elegida",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
            modifier = Modifier.padding(bottom = 16.dp),
        )
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF5C2B1A), Color(0xFF1A0E0A))))
                .padding(24.dp),
        ) {
            Column {
                Text("Pokke", color = Color.White.copy(0.7f), style = MaterialTheme.typography.labelMedium)
                Text(text, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))
                Text(song?.title ?: "", color = Color.White)
                Text(song?.artist ?: "", color = Color.White.copy(0.6f))
            }
        }
        Button(
            onClick = {
                val share = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "\"$text\"\n— ${song?.title} · ${song?.artist}\nvia Pokke")
                }
                context.startActivity(Intent.createChooser(share, "Compartir letra"))
            },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
        ) { Text("Compartir") }
        TextButton(onClick = onClose) { Text("Cerrar") }
    }
}
