package com.music.pokke.ui.glass

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Liquid-glass style surface (iOS 26-ish): translucent fill + soft border + optional blur.
 * When [enabled] is false → solid Material surface.
 */
@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    corner: Dp = 28.dp,
    tint: Color = Color.White.copy(alpha = 0.08f),
    borderAlpha: Float = 0.18f,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(corner)
    if (!enabled) {
        Box(
            modifier
                .clip(shape)
                .background(Color(0xFF1C1C1E))
                .then(modifier),
            content = content,
        )
        return
    }
    Box(
        modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.14f),
                        Color.White.copy(alpha = 0.06f),
                        tint,
                    ),
                ),
            )
            .border(1.dp, Color.White.copy(alpha = borderAlpha), shape)
            .then(
                if (Build.VERSION.SDK_INT >= 31) Modifier.blur(0.5.dp) else Modifier,
            ),
        content = content,
    )
}

fun glassBg(enabled: Boolean): Color =
    if (enabled) Color(0x991C1C1E) else Color(0xFF121212)

fun glassPill(enabled: Boolean): Color =
    if (enabled) Color(0xAA2A2A2E) else Color(0xFF2A2A2E)
