package io.github.mochamadabdulrouf.refy.ui.artist

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.R
import io.github.mochamadabdulrouf.refy.ui.component.ArtworkPlaceholder
import io.github.mochamadabdulrouf.refy.ui.component.FabGlow
import io.github.mochamadabdulrouf.refy.ui.component.FilterChipRow
import io.github.mochamadabdulrouf.refy.ui.component.FollowPillGlow
import io.github.mochamadabdulrouf.refy.ui.component.PlayingBars
import io.github.mochamadabdulrouf.refy.ui.component.ShuffleGlyph
import io.github.mochamadabdulrouf.refy.ui.component.pillGlow
import io.github.mochamadabdulrouf.refy.ui.theme.NocturneTheme
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyShapesTokens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Layar **#8 Artist Profile**.
 *
 * Susunan dari PNG kanon `artist-profile-ebadb5ac`: hero penuh-lebar 290dp
 * (toolbar mengambang kembali · bagikan · menu di atasnya) → badge
 * "Verified Artist" + nama + jumlah pendengar → baris aksi (Following · menu ·
 * shuffle · tombol putar 56dp) → "Popular" + 4 baris lagu (baris pertama
 * sedang diputar) → "Discography" + chip + kartu album 148dp.
 *
 * **Penyimpangan yang dicatat (PLAN §5):** chrome global (avatar, bel, tune)
 * tidak dirender (D15) sehingga hero naik ~32dp · foto artis diganti gradien
 * token (belum ada sumber gambar) · blur backdrop tombol toolbar dilewati
 * (R2 belum diputuskan) · glyph bagikan (ios_share) dan tiga titik horizontal
 * digambar sendiri karena `material-icons-core` tidak memilikinya · baris
 * lagu memakai aliran sendiri (streams, bukan durasi).
 */
@Composable
fun ArtistProfileScreen(
    state: ArtistProfileUiState,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onShare: () -> Unit = {},
    onMore: () -> Unit = {},
    onFollowToggle: () -> Unit = {},
    onShuffle: () -> Unit = {},
    onPlay: () -> Unit = {},
    onTrackClick: (ArtistTrack) -> Unit = {},
    onAlbumClick: (ArtistAlbum) -> Unit = {},
    onSeeAll: () -> Unit = {},
    onSeeDiscography: () -> Unit = {},
    onChipSelect: (Int) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.statusBarsPadding(),
    ) {
        item(key = "hero") {
            ArtistHero(
                state = state,
                onBack = onBack,
                onShare = onShare,
                onMore = onMore,
            )
        }

        item(key = "actions") {
            ArtistActionBar(
                state = state,
                onFollowToggle = onFollowToggle,
                onMore = onMore,
                onShuffle = onShuffle,
                onPlay = onPlay,
            )
        }

        item(key = "popular_header") {
            ArtistSectionHeader(
                title = stringResource(R.string.artist_popular),
                actionText = stringResource(R.string.common_see_all),
                onAction = onSeeAll,
            )
        }

        items(state.tracks.size) { index ->
            val track = state.tracks[index]
            PopularTrackRow(
                rank = index + 1,
                track = track,
                onClick = { onTrackClick(track) },
            )
        }

        item(key = "discography_header") {
            ArtistSectionHeader(
                title = stringResource(R.string.artist_discography),
                actionText = stringResource(R.string.artist_see_discography),
                onAction = onSeeDiscography,
                // Desain: section sebelumnya `pb-space-lg` (24) + header div
                // `pb-space-xs` (8) — bukan pola top-8/bottom-12 header Popular.
                topSpacing = RefyDimens.Space.lg,
                bottomSpacing = RefyDimens.Space.xs,
            )
        }

        item(key = "discography_chips") {
            // Container desain: `px-margin-mobile py-2 overflow-x-auto`
            // penuh-lebar — contentPadding (bukan padding modifier) supaya
            // LazyRow membentang ke tepi layar dan glow chip aktif masuk
            // margin 16dp, terpotong vertikal 8dp.
            FilterChipRow(
                labels = state.chips,
                selectedIndex = state.selectedChip,
                onSelect = onChipSelect,
                inactiveBackground = RefyColors.SurfaceHighest,
                contentPadding = PaddingValues(
                    horizontal = RefyDimens.Margin.mobile,
                    vertical = RefyDimens.Space.xs,
                ),
            )
        }

        item(key = "discography_cards") {
            // Container desain: `pt-2 pb-3 px-margin-mobile` — jarak chip→kartu
            // = py-2 chips (8) + pt-2 (8) = 16dp.
            LazyRow(
                contentPadding = PaddingValues(
                    start = RefyDimens.Margin.mobile,
                    top = RefyDimens.Space.xs,
                    end = RefyDimens.Margin.mobile,
                    bottom = RefyDimens.Space.sm,
                ),
                horizontalArrangement = Arrangement.spacedBy(RefyDimens.Space.sm),
            ) {
                items(state.albums.size) { index ->
                    val album = state.albums[index]
                    DiscographyCard(
                        album = album,
                        onClick = { onAlbumClick(album) },
                    )
                }
            }
        }

        item(key = "bottom_space") { Spacer(Modifier.height(RefyDimens.Space.lg)) }
    }
}

/**
 * Hero 290dp: gradien pengganti foto + dua scrim + toolbar mengambang +
 * overlay identitas (badge, nama, pendengar).
 */
@Composable
private fun ArtistHero(
    state: ArtistProfileUiState,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onMore: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp),
    ) {
        // Pengganti foto (deviasi: belum ada sumber gambar) — warna token saja.
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(RefyColors.AccentBright, RefyColors.AccentDeep),
                        start = Offset(0f, 0f),
                        end = Offset(500f, 700f),
                    ),
                ),
        )
        // Scrim atas: dari surface/60 ke transparan (keterbacaan toolbar).
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RefyColors.Surface.copy(alpha = 0.6f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
        // Scrim bawah: transparan → surface (keterbacaan identitas), sesuai
        // HTML `from-surface via-surface/80 to-transparent` yang dibalik.
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.Transparent,
                            0.45f to RefyColors.Surface.copy(alpha = 0.8f),
                            1f to RefyColors.Surface,
                        ),
                    ),
                ),
        )

        // Toolbar mengambang di atas foto: h-14, px-16 (HTML quick-nav bar).
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = RefyDimens.Margin.mobile),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeroCircleButton(
                contentDescription = stringResource(R.string.common_back),
                onClick = onBack,
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = RefyColors.TextPrimary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.weight(1f))
            HeroCircleButton(
                contentDescription = stringResource(R.string.np_share),
                onClick = onShare,
            ) {
                IosShareGlyph(Modifier.size(20.dp))
            }
            Spacer(Modifier.width(RefyDimens.Space.xs))
            HeroCircleButton(
                contentDescription = stringResource(R.string.common_more_options),
                onClick = onMore,
            ) {
                MoreHorizGlyph(Modifier.size(20.dp))
            }
        }

        // Overlay identitas: bottom-3, px-16.
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(horizontal = RefyDimens.Margin.mobile)
                .padding(bottom = RefyDimens.Space.sm),
        ) {
            VerifiedBadge(text = stringResource(R.string.artist_verified))
            Spacer(Modifier.height(RefyDimens.Space.xs))
            Text(
                text = state.name,
                style = RefyType.headlineLgMobile,
                color = RefyColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(RefyDimens.Space.xxs))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = state.listenersText,
                    style = RefyType.labelMd,
                    color = RefyColors.TextMuted,
                )
                Spacer(Modifier.width(RefyDimens.Space.xs))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(RefyColors.Accent.copy(alpha = 0.7f)),
                )
            }
        }
    }
}

/** Pil "Verified Artist": latar primary-container/20 + garis glow + ikon. */
@Composable
private fun VerifiedBadge(text: String) {
    Row(
        modifier = Modifier
            .shadow(
                elevation = 8.dp,
                shape = RefyShapesTokens.pill,
                ambientColor = RefyColors.AccentBright,
                spotColor = RefyColors.AccentBright,
            )
            .clip(RefyShapesTokens.pill)
            .background(RefyColors.AccentBright.copy(alpha = 0.2f))
            .border(
                width = 1.dp,
                color = RefyColors.AccentBright.copy(alpha = 0.6f),
                shape = RefyShapesTokens.pill,
            )
            .padding(horizontal = RefyDimens.Space.sm, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = RefyColors.Accent,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(RefyDimens.Space.xxs))
        Text(
            text = text.uppercase(),
            style = RefyType.labelXs,
            color = RefyColors.TextPrimary,
        )
    }
}

/**
 * Baris aksi: pil Following (bisa di-toggle lewat [onFollowToggle]) · tombol
 * menu · (jarak) · shuffle · tombol putar 56dp bergradien + glow.
 */
@Composable
private fun ArtistActionBar(
    state: ArtistProfileUiState,
    onFollowToggle: () -> Unit,
    onMore: () -> Unit,
    onShuffle: () -> Unit,
    onPlay: () -> Unit,
) {
    val shuffleCd = stringResource(R.string.album_cd_shuffle)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = RefyDimens.Margin.mobile)
            .padding(top = RefyDimens.Space.xs, bottom = RefyDimens.Space.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Following: bg primary-container/20 + garis + glow (HTML follow-btn:
        // `rgba(168,85,247,0.65) 0 0 16px` + `rgba(...,0.35) 0 0 28px` —
        // digambar pillGlow, Modifier.shadow terukur jauh lebih lemah).
        // Tinggi `h-9` = 36dp (HTML), teks/ikon `text-primary` → Accent.
        Row(
            modifier = Modifier
                .height(36.dp)
                .pillGlow(
                    color = RefyColors.AccentBright,
                    layers = FollowPillGlow,
                )
                .clip(RefyShapesTokens.pill)
                .background(RefyColors.AccentBright.copy(alpha = 0.2f))
                .border(
                    width = 1.dp,
                    color = RefyColors.AccentBright.copy(alpha = 0.6f),
                    shape = RefyShapesTokens.pill,
                )
                .clickable(role = Role.Button, onClick = onFollowToggle)
                .padding(horizontal = RefyDimens.Space.md, vertical = RefyDimens.Space.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (state.isFollowing) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = RefyColors.Accent,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(RefyDimens.Space.xxs))
                Text(
                    text = stringResource(R.string.artist_following),
                    style = RefyType.labelMd,
                    color = RefyColors.Accent,
                )
            } else {
                Text(
                    text = stringResource(R.string.artist_follow),
                    style = RefyType.labelMd,
                    color = RefyColors.Accent,
                )
            }
        }
        Spacer(Modifier.width(RefyDimens.Space.sm))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(RefyColors.SurfaceHighest)
                .clickable(role = Role.Button, onClick = onMore),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = null,
                tint = RefyColors.TextMuted,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(RefyDimens.Size.iconButton)
                .clip(CircleShape)
                .semantics { this.contentDescription = shuffleCd }
                .clickable(role = Role.Button, onClick = onShuffle),
            contentAlignment = Alignment.Center,
        ) {
            ShuffleGlyph(
                modifier = Modifier.size(22.dp),
                color = RefyColors.TextMuted,
            )
        }
        Spacer(Modifier.width(RefyDimens.Space.sm))
        Box(
            modifier = Modifier
                .size(56.dp)
                .pillGlow(
                    color = RefyColors.AccentBright,
                    layers = FabGlow,
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
                contentDescription = stringResource(R.string.common_play),
                tint = RefyColors.OnAccent,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

/**
 * Judul bagian + aksi teks kanan (tanpa panah — lihat PNG "See all").
 *
 * [topSpacing]/[bottomSpacing] mengikuti HTML per bagian: Popular =
 * section `pt-space-xs` + header `pb-space-sm` (8/12); Discography =
 * section sebelumnya `pb-space-lg` + header `pb-space-xs` (24/8).
 */
@Composable
private fun ArtistSectionHeader(
    title: String,
    actionText: String,
    onAction: () -> Unit,
    topSpacing: Dp = RefyDimens.Space.xs,
    bottomSpacing: Dp = RefyDimens.Space.sm,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = RefyDimens.Margin.mobile)
            .padding(top = topSpacing, bottom = bottomSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            // PNG: "Popular"/"Discography" = headline-sm 18px (ukur 2026-11-19,
            // HTML juga `font-headline-sm`) — bukan headline-md 22.
            style = RefyType.headlineSm,
            color = RefyColors.TextPrimary,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = actionText,
            style = RefyType.labelMd,
            color = RefyColors.Accent,
            modifier = Modifier
                .clip(RefyShapesTokens.pill)
                .clickable(role = Role.Button, onClick = onAction)
                .padding(horizontal = RefyDimens.Space.xxs, vertical = RefyDimens.Space.xxs),
        )
    }
}

/**
 * Baris "Popular": peringkat/equalizer + artwork 44dp + judul + streams +
 * menu. Baris yang sedang diputar memakai judul accent dan latar
 * surface/60 (HTML `bg-surface-container-low/60`, diremap §1).
 */
@Composable
private fun PopularTrackRow(
    rank: Int,
    track: ArtistTrack,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = RefyDimens.Margin.mobile, vertical = 3.dp)
            .clip(RefyShapesTokens.medium)
            .background(if (track.isPlaying) RefyColors.Surface else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(RefyDimens.Space.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.width(20.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (track.isPlaying) {
                PlayingBars(Modifier.size(14.dp))
            } else {
                Text(
                    text = rank.toString(),
                    style = RefyType.labelMd,
                    color = RefyColors.TextMuted,
                )
            }
        }
        Spacer(Modifier.width(RefyDimens.Space.sm))
        ArtworkPlaceholder(
            seed = track.seed,
            size = 44.dp,
            shape = RefyShapesTokens.small,
        )
        Spacer(Modifier.width(RefyDimens.Space.sm))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = RefyType.labelLg,
                color = if (track.isPlaying) RefyColors.Accent else RefyColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = track.streamsText,
                style = RefyType.bodySm,
                color = RefyColors.TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(RefyDimens.Space.xs))
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.common_more_options),
            tint = RefyColors.TextMuted,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable(onClick = {}),
        )
    }
}

/** Kartu discography: 148dp, padding 10, artwork persegi + judul + meta. */
@Composable
private fun DiscographyCard(
    album: ArtistAlbum,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(148.dp)
            .clip(RefyShapesTokens.large)
            .background(RefyColors.Surface)
            .clickable(onClick = onClick)
            .padding(10.dp),
    ) {
        ArtworkPlaceholder(
            seed = album.seed,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            shape = RefyShapesTokens.medium,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = album.title,
            style = RefyType.labelLg,
            color = RefyColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = album.meta,
            style = RefyType.bodySm,
            color = RefyColors.TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Tombol bulat 40dp di toolbar hero: latar surface/70 (blur dilewati, R2). */
@Composable
private fun HeroCircleButton(
    contentDescription: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(RefyColors.Surface.copy(alpha = 0.7f))
            .semantics { this.contentDescription = contentDescription }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/** Glyph ios_share (kotak terbuka + panah naik) — set inti tidak memilikinya. */
@Composable
private fun IosShareGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val color = RefyColors.TextPrimary
        val stroke = size.width * 0.08f
        drawLine(
            color = color,
            start = Offset(size.width * 0.2f, size.height * 0.42f),
            end = Offset(size.width * 0.2f, size.height * 0.86f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.2f, size.height * 0.86f),
            end = Offset(size.width * 0.8f, size.height * 0.86f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.8f, size.height * 0.86f),
            end = Offset(size.width * 0.8f, size.height * 0.42f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * 0.14f),
            end = Offset(size.width * 0.5f, size.height * 0.62f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.32f, size.height * 0.32f),
            end = Offset(size.width * 0.5f, size.height * 0.14f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.5f, size.height * 0.14f),
            end = Offset(size.width * 0.68f, size.height * 0.32f),
            strokeWidth = stroke,
            cap = StrokeCap.Round,
        )
    }
}

/** Glyph tiga titik horizontal (`more_horiz`) — set inti hanya punya MoreVert. */
@Composable
private fun MoreHorizGlyph(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val color = RefyColors.TextPrimary
        val radius = size.width * 0.075f
        listOf(0.26f, 0.5f, 0.74f).forEach { fraction ->
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(size.width * fraction, size.height / 2f),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun ArtistProfileScreenPreview() {
    NocturneTheme {
        ArtistProfileScreen(state = sampleArtistProfileUiState())
    }
}
