package io.github.mochamadabdulrouf.refy.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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

// ---------------------------------------------------------------------------
// Glyph layar pemutar (#9) — `material-icons-core` tidak memuat pause,
// skip_next, skip_previous, repeat, headphones, queue_music, maupun mic.
// Semua digambar dari proporsi Material Symbols.
// ---------------------------------------------------------------------------

/** Glyph pause: dua batang tegak membulat. */
@Composable
fun PauseGlyph(
    modifier: Modifier = Modifier,
    color: Color = RefyColors.OnAccent,
) {
    Canvas(modifier = modifier) {
        val barWidth = size.width * 0.24f
        val radius = barWidth / 2f
        listOf(0.16f, 0.60f).forEach { startX ->
            drawRoundRect(
                color = color,
                topLeft = Offset(size.width * startX, size.height * 0.10f),
                size = Size(barWidth, size.height * 0.80f),
                cornerRadius = CornerRadius(radius, radius),
            )
        }
    }
}

/** Glyph skip-next: segitiga menghadap kanan + batang tegak di kanannya. */
@Composable
fun SkipNextGlyph(
    modifier: Modifier = Modifier,
    color: Color = RefyColors.TextPrimary,
) {
    Canvas(modifier = modifier) {
        drawPath(
            path = triangle(pointingLeft = false),
            color = color,
        )
        val barWidth = size.width * 0.13f
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.74f, size.height * 0.14f),
            size = Size(barWidth, size.height * 0.72f),
            cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
        )
    }
}

/** Glyph skip-previous: cermin dari [SkipNextGlyph]. */
@Composable
fun SkipPreviousGlyph(
    modifier: Modifier = Modifier,
    color: Color = RefyColors.TextPrimary,
) {
    Canvas(modifier = modifier) {
        drawPath(
            path = triangle(pointingLeft = true),
            color = color,
        )
        val barWidth = size.width * 0.13f
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.13f, size.height * 0.14f),
            size = Size(barWidth, size.height * 0.72f),
            cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
        )
    }
}

/** Segitiga tombol lompat, tinggi 68 % bidang. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.triangle(
    pointingLeft: Boolean,
): Path {
    val from = if (pointingLeft) 0.74f else 0.27f
    val to = if (pointingLeft) 0.27f else 0.74f
    return Path().apply {
        moveTo(size.width * from, size.height * 0.14f)
        lineTo(size.width * to, size.height * 0.5f)
        lineTo(size.width * from, size.height * 0.86f)
        close()
    }
}

/** Glyph repeat: dua jalur membulat (kiri→atas→kanan, kanan→bawah→kiri) + panah. */
@Composable
fun RepeatGlyph(
    modifier: Modifier = Modifier,
    color: Color = RefyColors.TextMuted,
) {
    Canvas(modifier = modifier) {
        val stroke = size.width * 0.11f
        val inset = stroke / 2f + size.width * 0.03f
        val left = inset
        val right = size.width - inset
        val top = inset
        val bottom = size.height - inset
        val corner = (right - left) * 0.20f
        val head = (right - left) * 0.15f
        val midY = size.height / 2f
        val gap = head * 0.25f

        // Jalur atas: tepi kiri (dari tengah) → sudut → tepi atas → pangkal panah.
        drawPath(
            Path().apply {
                moveTo(left, midY + gap)
                lineTo(left, top + corner)
                quadraticBezierTo(left, top, left + corner, top)
                lineTo(right - corner - head, top)
            },
            color,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        // Panah kanan-atas menghadap kanan.
        drawPath(
            Path().apply {
                moveTo(right - corner, top)
                lineTo(right - corner - head, top - head * 0.7f)
                lineTo(right - corner - head, top + head * 0.7f)
                close()
            },
            color,
        )

        // Jalur bawah: tepi kanan (dari tengah) → sudut → tepi bawah → pangkal panah.
        drawPath(
            Path().apply {
                moveTo(right, midY - gap)
                lineTo(right, bottom - corner)
                quadraticBezierTo(right, bottom, right - corner, bottom)
                lineTo(left + corner + head, bottom)
            },
            color,
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        // Panah kiri-bawah menghadap kiri.
        drawPath(
            Path().apply {
                moveTo(left + corner, bottom)
                lineTo(left + corner + head, bottom - head * 0.7f)
                lineTo(left + corner + head, bottom + head * 0.7f)
                close()
            },
            color,
        )
    }
}

/** Glyph headphone: busur kepala + dua rumah telinga. */
@Composable
fun HeadphonesGlyph(
    modifier: Modifier = Modifier,
    color: Color = RefyColors.Accent,
) {
    Canvas(modifier = modifier) {
        val stroke = size.width * 0.13f
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(stroke / 2f, stroke / 2f),
            size = Size(size.width - stroke, size.height - stroke * 2.2f),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        val cupWidth = size.width * 0.20f
        val cupHeight = size.height * 0.34f
        listOf(0f, 1f).forEach { side ->
            drawRoundRect(
                color = color,
                topLeft = Offset(
                    if (side == 0f) stroke / 2f else size.width - stroke / 2f - cupWidth,
                    size.height - cupHeight,
                ),
                size = Size(cupWidth, cupHeight),
                cornerRadius = CornerRadius(cupWidth / 2f, cupWidth / 2f),
            )
        }
    }
}

/** Glyph queue-music: tiga baris daftar + not dengan tangkai. */
@Composable
fun QueueMusicGlyph(
    modifier: Modifier = Modifier,
    color: Color = RefyColors.TextMuted,
) {
    Canvas(modifier = modifier) {
        val stroke = size.height * 0.10f
        listOf(0.18f to 0.52f, 0.44f to 0.52f, 0.70f to 0.34f).forEach { (y, widthFactor) ->
            drawLine(
                color = color,
                start = Offset(0f, size.height * y),
                end = Offset(size.width * widthFactor, size.height * y),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
        val noteRadius = size.width * 0.13f
        val noteCenter = Offset(size.width * 0.74f, size.height * 0.72f)
        drawCircle(color = color, radius = noteRadius, center = noteCenter)
        drawLine(
            color = color,
            start = Offset(noteCenter.x + noteRadius, noteCenter.y),
            end = Offset(noteCenter.x + noteRadius, size.height * 0.18f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(noteCenter.x + noteRadius, size.height * 0.18f),
            end = Offset(size.width, size.height * 0.32f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}

/** Glyph mikrofon: kapsul + sangkutan busur + kaki. */
@Composable
fun MicGlyph(
    modifier: Modifier = Modifier,
    color: Color = RefyColors.Accent,
) {
    Canvas(modifier = modifier) {
        val stroke = size.width * 0.12f
        val capsuleWidth = size.width * 0.30f
        drawRoundRect(
            color = color,
            topLeft = Offset((size.width - capsuleWidth) / 2f, 0f),
            size = Size(capsuleWidth, size.height * 0.58f),
            cornerRadius = CornerRadius(capsuleWidth / 2f, capsuleWidth / 2f),
        )
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(stroke / 2f + size.width * 0.06f, size.height * 0.16f),
            size = Size(size.width - stroke - size.width * 0.12f, size.height * 0.62f),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        drawLine(
            color = color,
            start = Offset(size.width / 2f, size.height * 0.78f),
            end = Offset(size.width / 2f, size.height * 0.92f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.28f, size.height * 0.92f),
            end = Offset(size.width * 0.72f, size.height * 0.92f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}
