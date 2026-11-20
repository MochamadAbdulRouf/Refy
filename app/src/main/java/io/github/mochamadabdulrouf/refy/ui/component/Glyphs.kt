package io.github.mochamadabdulrouf.refy.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors

/**
 * Glyph digambar sendiri yang dipakai lebih dari satu layar.
 *
 * `material-icons-core` 1.7.8 tidak memuat `Shuffle` maupun `Equalizer`
 * (keduanya hanya ada di extended, ~10 MB) — dan keputusan Fase 2 menolak
 * menambah extended hanya untuk beberapa glyph. Digambar dengan `Canvas`
 * dari proporsi PNG kanon. Lihat `keputusan.md` 2026-11-19.
 */

/** Glyph shuffle: dua garis bersilang dengan ujung panah. */
@Composable
fun ShuffleGlyph(
    modifier: Modifier = Modifier,
    color: Color = RefyColors.Accent,
) {
    Canvas(modifier = modifier) {
        val stroke = 2f
        val endTop = Offset(size.width * 0.78f, size.height * 0.26f)
        val endBottom = Offset(size.width * 0.78f, size.height * 0.74f)
        drawLine(
            color = color,
            start = Offset(size.width * 0.06f, size.height * 0.74f),
            end = endTop,
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.06f, size.height * 0.26f),
            end = endBottom,
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawPath(arrowHead(endTop, pointingUp = true), color)
        drawPath(arrowHead(endBottom, pointingUp = false), color)
    }
}

/** Segitiga ujung panah di [tip], menghadap kanan-atas atau kanan-bawah. */
private fun arrowHead(tip: Offset, pointingUp: Boolean): Path {
    val length = tip.x * 0.22f
    val half = length * 0.55f
    val verticalShift = if (pointingUp) -half else half
    return Path().apply {
        moveTo(tip.x, tip.y)
        lineTo(tip.x - length, tip.y - verticalShift)
        lineTo(tip.x - length, tip.y + verticalShift)
        close()
    }
}

/** Tiga batang equalizer kecil, penanda lagu yang sedang diputar. */
@Composable
fun PlayingBars(
    modifier: Modifier = Modifier,
    color: Color = RefyColors.Accent,
) {
    Canvas(modifier = modifier) {
        val bars = listOf(0.45f, 1f, 0.7f)
        val barWidth = size.width / 7f
        bars.forEachIndexed { index, fraction ->
            val barHeight = size.height * fraction
            drawLine(
                color = color,
                start = Offset(barWidth * (index * 2 + 1), size.height),
                end = Offset(barWidth * (index * 2 + 1), size.height - barHeight),
                strokeWidth = barWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}
