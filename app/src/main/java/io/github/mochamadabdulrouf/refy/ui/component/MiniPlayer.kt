package io.github.mochamadabdulrouf.refy.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.R
import io.github.mochamadabdulrouf.refy.ui.theme.NocturneTheme
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyShapesTokens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Mini-player — komponen tanda tangan (`docs/DESIGN.md` §6.3), 56dp, radius 12.
 *
 * Stateless: seluruh data masuk lewat parameter, tidak tahu ViewModel atau
 * pemutar mana pun. Saat ini **belum ada pemutar** (M2), jadi shell hanya
 * menampilkannya ketika ada track; kalau tidak ada, komponen ini tidak dipanggil
 * sama sekali — bukan ditampilkan kosong.
 *
 * Progress bar 2dp menempel di tepi bawah permukaan (bukan di bawahnya).
 * Ikon pause digambar sendiri di [EqualizerGlyph]/[PauseGlyph] agar tidak perlu
 * menambah `material-icons-extended` (±10 MB) untuk satu glyph.
 */
@Composable
fun MiniPlayer(
    title: String,
    artist: String,
    isPlaying: Boolean,
    progress: Float,
    onOpen: () -> Unit,
    onFavorite: () -> Unit,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassSurface(
        color = RefyColors.GlassMiniPlayer,
        border = RefyColors.StrokeMiniPlayer,
        shape = RefyShapesTokens.medium,
        modifier = modifier
            .fillMaxWidth()
            .height(RefyDimens.Size.miniPlayer),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RefyShapesTokens.medium)
                .clickable(onClick = onOpen),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ArtworkBadge()

                Spacer(Modifier.width(RefyDimens.Space.sm))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = RefyType.labelLg,
                        color = RefyColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = artist,
                        style = RefyType.bodySm,
                        color = RefyColors.TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                IconButton(
                    onClick = onFavorite,
                    modifier = Modifier.size(RefyDimens.Size.iconButton),
                ) {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(R.string.mini_cd_favorite),
                        tint = RefyColors.TextPrimary,
                        modifier = Modifier.size(22.dp),
                    )
                }

                PlayPauseButton(isPlaying = isPlaying, onClick = onPlayPause)
            }

            ProgressBar(
                progress = progress,
                modifier = Modifier.align(Alignment.BottomStart),
            )
        }
    }
}

/** Artwork 40dp, radius 8. Tanpa gambar nyata: gradien accent + glyph equalizer. */
@Composable
private fun ArtworkBadge() {
    Box(
        modifier = Modifier
            .size(RefyDimens.Size.artworkRow)
            .clip(RefyShapesTokens.small)
            .background(
                Brush.linearGradient(
                    listOf(RefyColors.AccentBright, RefyColors.AccentDeep),
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        EqualizerGlyph(
            color = RefyColors.OnAccent,
            modifier = Modifier.size(18.dp),
        )
    }
}

/** Tiga batang equalizer — glyph "sedang diputar" pada desain. */
@Composable
private fun EqualizerGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val bars = 3
        val gap = size.width / (bars * 3f)
        val barWidth = gap
        listOf(0.55f, 1f, 0.75f).forEachIndexed { index, factor ->
            val height = size.height * factor
            drawRoundRect(
                color = color,
                topLeft = Offset(x = gap + index * (barWidth + gap), y = (size.height - height) / 2f),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
            )
        }
    }
}

/** Dua batang vertikal — pengganti `Icons.Filled.Pause` yang tidak ada di set inti. */
@Composable
private fun PauseGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val barWidth = size.width * 0.22f
        val gap = size.width * 0.2f
        val height = size.height
        drawRoundRect(
            color = color,
            topLeft = Offset(x = (size.width - (barWidth * 2 + gap)) / 2f, y = 0f),
            size = Size(barWidth, height),
            cornerRadius = CornerRadius(barWidth / 3f, barWidth / 3f),
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(x = (size.width - (barWidth * 2 + gap)) / 2f + barWidth + gap, y = 0f),
            size = Size(barWidth, height),
            cornerRadius = CornerRadius(barWidth / 3f, barWidth / 3f),
        )
    }
}

@Composable
private fun PlayPauseButton(isPlaying: Boolean, onClick: () -> Unit) {
    val pauseLabel = stringResource(R.string.common_pause)
    Box(
        modifier = Modifier
            .size(RefyDimens.Size.playButtonMini)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = RefyColors.Accent.copy(alpha = 0.35f),
                spotColor = RefyColors.Accent.copy(alpha = 0.35f),
            )
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(RefyColors.AccentBright, RefyColors.AccentDeep),
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (isPlaying) {
            PauseGlyph(
                color = RefyColors.OnAccent,
                modifier = Modifier
                    .size(14.dp)
                    .semantics { contentDescription = pauseLabel },
            )
        } else {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.common_play),
                tint = RefyColors.OnAccent,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun ProgressBar(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(RefyDimens.Size.progressBar)
            .background(RefyColors.ProgressTrack),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .background(RefyColors.Accent),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MiniPlayerPreview() {
    NocturneTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            MiniPlayer(
                title = stringResource(R.string.app_name),
                artist = stringResource(R.string.common_playing),
                isPlaying = false,
                progress = 0.35f,
                onOpen = {},
                onFavorite = {},
                onPlayPause = {},
            )
        }
    }
}