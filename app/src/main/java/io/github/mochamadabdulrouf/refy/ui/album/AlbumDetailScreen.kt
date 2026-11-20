package io.github.mochamadabdulrouf.refy.ui.album

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.R
import io.github.mochamadabdulrouf.refy.ui.component.ArtworkPlaceholder
import io.github.mochamadabdulrouf.refy.ui.component.PlayingBars
import io.github.mochamadabdulrouf.refy.ui.component.SearchField
import io.github.mochamadabdulrouf.refy.ui.component.SectionHeader
import io.github.mochamadabdulrouf.refy.ui.component.ShuffleGlyph
import io.github.mochamadabdulrouf.refy.ui.theme.NocturneTheme
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyShapesTokens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Layar **#5 Album Detail & Tracklist**.
 *
 * Susunan dari PNG kanon `album-detail-tracklist-76a16cec`: tombol kembali +
 * antrean + menu di toolbar → artwork 208dp dengan glow → judul + artis + meta
 * + badge → Recommended Tracks (cari + 4 baris) → baris aksi (favorit · riwayat
 * · bagikan · shuffle · putar) → header tracklist → 10 baris lagu, baris yang
 * sedang diputar disorot.
 *
 * **Penyimpangan yang dicatat (PLAN §5):** avatar & ikon cast/mic di header
 * global tidak dirender (D15) · tombol unduh offline tidak dirender (mesin unduh
 * v0.2) · glyph antrean, shuffle, dan jam digambar sendiri karena
 * `material-icons-core` tidak punya ketiganya · bagian "Recommended Tracks"
 * kedua di bawah tracklist tidak diduplikasi (artefak desain).
 */
@Composable
fun AlbumDetailScreen(
    state: AlbumDetailUiState,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onQueue: () -> Unit = {},
    onMore: () -> Unit = {},
    onPlay: () -> Unit = {},
    onShuffle: () -> Unit = {},
    onTrackClick: (AlbumTrack) -> Unit = {},
    onRecommendationClick: (AlbumRecommendation) -> Unit = {},
    onSearchTracks: () -> Unit = {},
    onRefreshRecommendations: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = RefyDimens.Margin.mobile),
    ) {
        item(key = "toolbar") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = RefyDimens.Space.xs, bottom = RefyDimens.Space.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircleButton(
                    icon = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.common_back),
                    onClick = onBack,
                )
                Spacer(Modifier.weight(1f))
                CircleButton(
                    icon = null,
                    contentDescription = stringResource(R.string.album_cd_queue),
                    onClick = onQueue,
                    content = { QueueGlyph() },
                )
                Spacer(Modifier.width(RefyDimens.Space.xs))
                CircleButton(
                    icon = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.album_cd_more),
                    onClick = onMore,
                )
            }
        }

        item(key = "hero") {
            AlbumHero(state)
        }

        item(key = "recommended_header") {
            SectionHeader(
                title = stringResource(R.string.album_recommended_tracks),
                subtitle = stringResource(R.string.album_recommended_subtitle),
                trailing = {
                    CircleButton(
                        icon = Icons.Filled.Refresh,
                        contentDescription = stringResource(R.string.album_cd_refresh),
                        onClick = onRefreshRecommendations,
                        size = 36,
                    )
                },
            )
        }

        item(key = "recommended_search") {
            SearchField(
                hint = stringResource(R.string.album_search_hint),
                onClick = onSearchTracks,
            )
        }

        items(state.recommendations.size) { index ->
            val recommendation = state.recommendations[index]
            RecommendationRow(
                recommendation = recommendation,
                onClick = { onRecommendationClick(recommendation) },
            )
        }

        item(key = "actions") {
            AlbumActions(onPlay = onPlay, onShuffle = onShuffle)
        }

        item(key = "tracklist_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = RefyDimens.Space.md, bottom = RefyDimens.Space.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "#  " + stringResource(R.string.album_tracklist_title),
                    style = RefyType.labelMd,
                    color = RefyColors.TextMuted,
                    modifier = Modifier.weight(1f),
                )
                ClockGlyph(
                    diameter = 16.dp,
                    contentDescription = stringResource(R.string.album_tracklist_duration_cd),
                )
            }
        }

        items(state.tracks.size) { index ->
            val track = state.tracks[index]
            TrackRow(
                track = track,
                isPlaying = track.id == state.nowPlayingTrackId,
                onClick = { onTrackClick(track) },
            )
        }

        item(key = "bottom_space") { Spacer(Modifier.height(RefyDimens.Space.lg)) }
    }
}

/** Artwork 208dp + glow radial, judul, artis terverifikasi, meta, dan badge. */
@Composable
private fun AlbumHero(state: AlbumDetailUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(224.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(RefyColors.Accent.copy(alpha = 0.35f), Color.Transparent),
                        ),
                    ),
            )
            Box {
                ArtworkPlaceholder(
                    seed = state.artworkSeed,
                    size = 208.dp,
                    shape = RefyShapesTokens.large,
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RefyShapesTokens.large)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, RefyColors.GlassOverlay),
                            ),
                        ),
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(RefyDimens.Space.md),
                ) {
                    state.artworkPreview.forEach { line ->
                        Text(
                            text = line,
                            style = RefyType.labelXs,
                            color = RefyColors.TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(RefyDimens.Space.md))
        Text(
            text = state.title,
            style = RefyType.headlineLgMobile,
            color = RefyColors.TextPrimary,
        )
        Spacer(Modifier.height(RefyDimens.Space.xxs))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ArtworkPlaceholder(
                seed = state.artworkSeed,
                size = 20.dp,
                shape = CircleShape,
            )
            Spacer(Modifier.width(RefyDimens.Space.xs))
            Text(
                text = state.artist,
                style = RefyType.labelMd,
                color = RefyColors.TextPrimary,
            )
            Spacer(Modifier.width(RefyDimens.Space.xxs))
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = stringResource(R.string.search_verified),
                tint = RefyColors.Accent,
                modifier = Modifier.size(14.dp),
            )
        }
        Spacer(Modifier.height(RefyDimens.Space.xxs))
        Text(
            text = state.meta,
            style = RefyType.bodySm,
            color = RefyColors.TextMuted,
        )
        Spacer(Modifier.height(RefyDimens.Space.xs))
        Row(horizontalArrangement = Arrangement.spacedBy(RefyDimens.Space.xs)) {
            state.badges.forEach { badge ->
                Text(
                    text = badge.uppercase(),
                    style = RefyType.labelXs,
                    color = RefyColors.TextMuted,
                    modifier = Modifier
                        .clip(RefyShapesTokens.pill)
                        .background(RefyColors.SurfaceHighest)
                        .padding(horizontal = RefyDimens.Space.xs, vertical = 2.dp),
                )
            }
        }
    }
}

/** Baris rekomendasi: artwork 40dp + judul/artis + durasi + tombol tambah. */
@Composable
private fun RecommendationRow(
    recommendation: AlbumRecommendation,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RefyShapesTokens.medium)
            .clickable(onClick = onClick)
            .padding(RefyDimens.Space.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArtworkPlaceholder(
            seed = recommendation.seed,
            size = 40.dp,
            shape = RefyShapesTokens.small,
        )
        Spacer(Modifier.width(RefyDimens.Space.sm))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = recommendation.title,
                style = RefyType.bodyLg,
                color = RefyColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = recommendation.artist,
                style = RefyType.bodySm,
                color = RefyColors.TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = recommendation.duration,
            style = RefyType.bodySm,
            color = RefyColors.TextMuted,
        )
        Spacer(Modifier.width(RefyDimens.Space.xs))
        CircleButton(
            icon = null,
            contentDescription = null,
            onClick = {},
            size = 32,
        ) {
            PlusGlyph()
        }
    }
}

/** Favorit · riwayat · bagikan · (jarak) · shuffle · tombol putar besar bergradien. */
@Composable
private fun AlbumActions(onPlay: () -> Unit, onShuffle: () -> Unit) {
    val shuffleCd = stringResource(R.string.album_cd_shuffle)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = RefyDimens.Space.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleButton(
            icon = Icons.Filled.Favorite,
            contentDescription = stringResource(R.string.mini_cd_favorite),
            onClick = {},
            tint = RefyColors.Accent,
            iconSize = 22,
        )
        Spacer(Modifier.width(RefyDimens.Space.xs))
        Box(contentAlignment = Alignment.Center) {
            CircleButton(
                icon = null,
                contentDescription = stringResource(R.string.album_cd_history),
                onClick = {},
                content = { ClockGlyph(diameter = 20.dp) },
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = RefyDimens.Space.xxs, bottom = RefyDimens.Space.xxs)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(RefyColors.Accent),
            )
        }
        Spacer(Modifier.width(RefyDimens.Space.xs))
        CircleButton(
            icon = Icons.Filled.Share,
            contentDescription = stringResource(R.string.np_share),
            onClick = {},
        )
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(RefyDimens.Size.iconButton)
                    .clip(CircleShape)
                    .semantics { this.contentDescription = shuffleCd }
                    .clickable(role = Role.Button, onClick = onShuffle),
                contentAlignment = Alignment.Center,
            ) {
                ShuffleGlyph(Modifier.size(22.dp))
            }
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(RefyColors.Accent),
            )
        }
        Spacer(Modifier.width(RefyDimens.Space.md))
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = RefyColors.Accent,
                    spotColor = RefyColors.Accent,
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(RefyColors.AccentBright, RefyColors.AccentDeep),
                    ),
                )
                .clickable(role = Role.Button, onClick = onPlay),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.album_cd_play),
                tint = RefyColors.OnAccent,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

/** Baris lagu: nomor/equalizer + judul (+ badge E) + artis + durasi + menu. */
@Composable
private fun TrackRow(
    track: AlbumTrack,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RefyShapesTokens.medium)
            // Desain: latar baris aktif #2A2A2C → remap Kubu B = SurfaceHighest
            // (bukan SurfaceElevated yang datang dari #201f22). Judul aktif
            // memakai accent — DESIGN §6.4. Banding sampel piksel 2026-11-19.
            .background(if (isPlaying) RefyColors.SurfaceHighest else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(RefyDimens.Space.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.width(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isPlaying) {
                PlayingBars(Modifier.size(14.dp))
            } else {
                Text(
                    text = track.number.toString(),
                    style = RefyType.labelMd,
                    color = RefyColors.TextMuted,
                )
            }
        }
        Spacer(Modifier.width(RefyDimens.Space.sm))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.title,
                    style = RefyType.bodyLg,
                    color = if (isPlaying) RefyColors.Accent else RefyColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (track.explicit) {
                    Spacer(Modifier.width(RefyDimens.Space.xxs))
                    Text(
                        text = stringResource(R.string.album_badge_explicit),
                        style = RefyType.labelXs,
                        color = RefyColors.TextMuted,
                        modifier = Modifier
                            .clip(RefyShapesTokens.small)
                            .background(RefyColors.SurfaceHighest)
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                    )
                }
            }
            Text(
                text = track.artist,
                style = RefyType.bodySm,
                color = RefyColors.TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = track.duration,
            style = RefyType.bodySm,
            color = RefyColors.TextMuted,
        )
        Spacer(Modifier.width(RefyDimens.Space.xs))
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.common_more_options),
            tint = RefyColors.TextMuted,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable(onClick = {}),
        )
    }
}

/** Tombol bulat 40dp (bisa diperkecil) dengan ikon atau isi khusus. */
@Composable
private fun CircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    contentDescription: String?,
    onClick: () -> Unit,
    size: Int = 40,
    tint: Color = RefyColors.TextPrimary,
    iconSize: Int = 20,
    content: @Composable (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(RefyColors.Surface)
            .then(
                if (icon == null && contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                },
            )
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(iconSize.dp),
            )
        } else {
            content?.invoke()
        }
    }
}

/** Glyph antrean: tiga garis + not balok — set inti tidak punya `QueueMusic`. */
@Composable
private fun QueueGlyph() {
    Canvas(modifier = Modifier.size(20.dp)) {
        val color = RefyColors.TextPrimary
        val stroke = 1.8f
        val lineStart = size.width * 0.06f
        val lines = listOf(
            0.24f to 0.58f,
            0.46f to 0.58f,
            0.68f to 0.40f,
        )
        lines.forEach { (fractionY, endFractionX) ->
            drawLine(
                color = color,
                start = Offset(lineStart, size.height * fractionY),
                end = Offset(size.width * endFractionX, size.height * fractionY),
                strokeWidth = stroke,
                cap = StrokeCap.Round,
            )
        }
        // Not: tangkai dari kepala not ke atas, lalu bendera pendek ke kanan.
        val stemX = size.width * 0.76f
        drawLine(
            color = color,
            start = Offset(stemX, size.height * 0.20f),
            end = Offset(stemX, size.height * 0.72f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(stemX, size.height * 0.20f),
            end = Offset(size.width * 0.94f, size.height * 0.28f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = color,
            radius = size.width * 0.11f,
            center = Offset(stemX - size.width * 0.03f, size.height * 0.74f),
        )
    }
}

/** Glyph jam: lingkaran + dua jarum — set inti tidak punya `Schedule`. */
@Composable
private fun ClockGlyph(diameter: androidx.compose.ui.unit.Dp, contentDescription: String? = null) {
    Canvas(
        modifier = Modifier
            .size(diameter)
            .then(
                if (contentDescription == null) {
                    Modifier
                } else {
                    Modifier.semantics { this.contentDescription = contentDescription }
                },
            ),
    ) {
        val stroke = diameter.toPx() * 0.09f
        val color = RefyColors.TextMuted
        val width = this.size.width
        val height = this.size.height
        drawCircle(
            color = color,
            radius = this.size.minDimension / 2f - stroke / 2f,
            style = Stroke(width = stroke),
        )
        val center = Offset(width / 2f, height / 2f)
        drawLine(
            color = color,
            start = center,
            end = Offset(center.x, height * 0.28f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = center,
            end = Offset(width * 0.72f, center.y),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}

/** Glyph plus kecil untuk tombol tambah pada baris rekomendasi. */
@Composable
private fun PlusGlyph() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val color = RefyColors.TextPrimary
        drawLine(
            color = color,
            start = Offset(size.width / 2f, size.height * 0.15f),
            end = Offset(size.width / 2f, size.height * 0.85f),
            strokeWidth = 2f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.15f, size.height / 2f),
            end = Offset(size.width * 0.85f, size.height / 2f),
            strokeWidth = 2f,
            cap = StrokeCap.Round,
        )
    }
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun AlbumDetailScreenPreview() {
    NocturneTheme {
        AlbumDetailScreen(state = sampleAlbumDetailUiState())
    }
}