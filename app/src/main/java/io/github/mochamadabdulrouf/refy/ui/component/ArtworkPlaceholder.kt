package io.github.mochamadabdulrouf.refy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyShapesTokens

/**
 * Pengganti artwork selama belum ada gambar nyata.
 *
 * Desain memakai foto album. Refy belum punya sumber gambar (resolver baru di
 * M1, gambar butuh cache di M4), jadi kartu memakai gradien accent yang
 * ditentukan [seed] — stabil per item, tidak acak tiap recomposition.
 *
 * Hanya memakai warna dari [RefyColors]; tidak ada hex di sini.
 * **Diganti** oleh pemuat gambar saat M1/M4 siap.
 */
@Composable
fun ArtworkPlaceholder(
    seed: Int,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    shape: Shape = RefyShapesTokens.small,
) {
    val palettes = listOf(
        listOf(RefyColors.AccentBright, RefyColors.AccentDeep),
        listOf(RefyColors.Accent, RefyColors.SurfaceHighest),
        listOf(RefyColors.AccentDeep, RefyColors.AccentBright),
        listOf(RefyColors.AccentBright, RefyColors.SurfaceElevated),
    )
    val colors = palettes[seed.mod(palettes.size)]
    val diagonal = if (seed.mod(2) == 0) 1f else -1f

    val sizing = if (size != null) {
        Modifier.size(size)
    } else {
        Modifier.fillMaxWidth().aspectRatio(1f)
    }

    Box(
        modifier = modifier
            .then(sizing)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 0f),
                    end = Offset(if (diagonal > 0) 600f else 0f, 600f),
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        EqualizerMark(modifier = Modifier.size(size?.times(0.35f) ?: 24.dp))
    }
}

/** Tiga batang equalizer kecil, warna putih transparan — penanda "musik". */
@Composable
private fun EqualizerMark(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier) {
        val bars = 3
        val gap = size.width / (bars * 3f)
        val barWidth = gap
        listOf(0.6f, 1f, 0.75f).forEachIndexed { index, factor ->
            val barHeight = size.height * factor
            drawRoundRect(
                color = RefyColors.OnAccent.copy(alpha = 0.85f),
                topLeft = Offset(gap + index * (barWidth + gap), (size.height - barHeight) / 2f),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f, barWidth / 2f),
            )
        }
    }
}