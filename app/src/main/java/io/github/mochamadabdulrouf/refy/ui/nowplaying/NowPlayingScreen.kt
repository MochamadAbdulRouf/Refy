package io.github.mochamadabdulrouf.refy.ui.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.R
import androidx.compose.material.icons.filled.PlayArrow
import io.github.mochamadabdulrouf.refy.ui.component.ArtworkCardGlow
import io.github.mochamadabdulrouf.refy.ui.component.ArtworkPlaceholder
import io.github.mochamadabdulrouf.refy.ui.component.GlowLayer
import io.github.mochamadabdulrouf.refy.ui.component.HeadphonesGlyph
import io.github.mochamadabdulrouf.refy.ui.component.LyricsCardGlow
import io.github.mochamadabdulrouf.refy.ui.component.MicGlyph
import io.github.mochamadabdulrouf.refy.ui.component.PauseGlyph
import io.github.mochamadabdulrouf.refy.ui.component.PlayButtonGlow
import io.github.mochamadabdulrouf.refy.ui.component.PlayingBars
import io.github.mochamadabdulrouf.refy.ui.component.QueueMusicGlyph
import io.github.mochamadabdulrouf.refy.ui.component.RepeatGlyph
import io.github.mochamadabdulrouf.refy.ui.component.ScrubberThumbGlow
import io.github.mochamadabdulrouf.refy.ui.component.ShuffleGlyph
import io.github.mochamadabdulrouf.refy.ui.component.SkipNextGlyph
import io.github.mochamadabdulrouf.refy.ui.component.SkipPreviousGlyph
import io.github.mochamadabdulrouf.refy.ui.component.pillGlow
import io.github.mochamadabdulrouf.refy.ui.theme.NocturneTheme
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyShapesTokens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/** Lebar/tinggi kartu artwork — `w-[300px] h-[300px]` di HTML kanon. */
private val ArtworkSize = 300.dp

/** Tile artwork di dalam poster — PNG: 156×155 dp, 64 dp dari atas kartu. */
private val ArtworkTileSize = 156.dp
private val ArtworkTileTop = 64.dp

/**
 * Tinggi panel bawah poster. PNG: tepi panel di y≈140 dari atas kartu, jadi
 * 160 dp sampai dasar; di atasnya gradasi memudar ke artwork.
 */
private val ArtworkPanelHeight = 160.dp

/** Progres kecil di poster — PNG: 3 px, sisipan 23 dp kiri/kanan. */
private val ArtworkBarHeight = 3.dp

/** Jarak sisi poster — PNG: bilah progres 68→322 px pada kartu 45→345. */
private val ArtworkPanelInset = 23.dp

/** Tombol putar utama — `w-[72px] h-[72px]` di HTML kanon. */
private val PlayButtonSize = 72.dp

/**
 * Layar **#9 Now Playing**.
 *
 * Susunan dari PNG kanon `now-playing-screen-57a3bcf2`: header (chevron turun ·
 * menu) → pil "Playing from playlist · Violet Nights" → kartu artwork 300dp
 * `rounded-2xl` dengan glow ungu besar + badge LOSSLESS, berisi poster mini
 * (tile artwork 156dp · judul album · judul lagu + titik · progres 1:34/3:58)
 * → judul `headlineMd` 22 + badge E + tombol hati → artis → scrubber + waktu
 * → transport (shuffle dengan titik aktif, prev, putar 72dp gradien, next,
 * repeat) → bar Audio Output → kartu lirik.
 *
 * **Poster kartu:** PNG memuat isi poster (album, judul, progres) yang tidak
 * ada di HTML kanon — HTML hanya kotak kosong. Aturannya PNG menang, jadi
 * poster disusun ulang dari elemen Compose (`NebulaBackdrop`, tile
 * [ArtworkPlaceholder], teks data).
 *
 * **Lirik:** kartu lirik hanya muncul bila [NowPlayingUiState.lyrics] berisi.
 * Data contoh saat ini mengisinya supaya layar sama dengan PNG; tanpa data
 * lirik nyata (v0.2, #11) kartu itu tidak dirender (D15,
 * `docs/I18N-KEYS.md` §10).
 *
 * **Blur:** kaca di layar ini (`backdrop-blur` pada pil, badge, kartu lirik)
 * duduk di atas latar datar + glow, bukan di atas konten bergulir — jadi
 * dirender sebagai permukaan transparan + glow `pillGlow`, tanpa blur latar
 * nyata. Keputusan `haze` vs RenderNode masih terbuka untuk kaca shell
 * (nav pil + mini-player) — lihat `keputusan.md` 2026-11-22.
 */
@Composable
fun NowPlayingScreen(
    state: NowPlayingUiState,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onMore: () -> Unit = {},
    onFavoriteToggle: () -> Unit = {},
    onShuffle: () -> Unit = {},
    onPrevious: () -> Unit = {},
    onPlayPause: () -> Unit = {},
    onNext: () -> Unit = {},
    onRepeat: () -> Unit = {},
    onOutput: () -> Unit = {},
    onQueue: () -> Unit = {},
    onShare: () -> Unit = {},
    onExpandLyrics: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RefyColors.Canvas)
            .statusBarsPadding(),
    ) {
        NowPlayingHeader(onBack = onBack, onMore = onMore)

        Box(modifier = Modifier.weight(1f)) {
            PageGlow()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = RefyDimens.Margin.mobile),
                ) {
                    SourcePill(playlistName = state.playlistName)
                    ArtworkCard(state = state)
                    MetaRow(state = state, onFavoriteToggle = onFavoriteToggle)
                    Scrubber(state = state)
                    TransportControls(
                        state = state,
                        onShuffle = onShuffle,
                        onPrevious = onPrevious,
                        onPlayPause = onPlayPause,
                        onNext = onNext,
                        onRepeat = onRepeat,
                    )
                    AudioOutputBar(
                        state = state,
                        onOutput = onOutput,
                        onQueue = onQueue,
                        onShare = onShare,
                    )
                    if (state.lyrics.isNotEmpty()) {
                        LyricsCard(
                            state = state,
                            onExpand = onExpandLyrics,
                        )
                    }
                    Spacer(Modifier.height(RefyDimens.Space.xxl))
                }
            }
        }
    }
}

/** Header tetap `h-16`: chevron turun (kembali) · menu. */
@Composable
private fun NowPlayingHeader(onBack: () -> Unit, onMore: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(RefyDimens.Size.header)
            .padding(horizontal = RefyDimens.Margin.mobile),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(RefyDimens.Size.iconButton)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = stringResource(R.string.common_back),
                tint = RefyColors.TextPrimary,
                modifier = Modifier.size(26.dp),
            )
        }
        // Judul halaman `h1` — HTML: `font-headline-sm` rata tengah.
        Text(
            text = stringResource(R.string.np_title),
            style = RefyType.headlineSm,
            color = RefyColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = RefyDimens.Space.xs),
        )
        Box(
            modifier = Modifier
                .size(RefyDimens.Size.iconButton)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onMore),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.common_more_options),
                tint = RefyColors.TextPrimary,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

/**
 * Dua blob radial ungu di latar (HTML: `blur-[100px]` tengah-atas +
 * `blur-[90px]` kanan-bawah). Gradien radial sudah cukup lembut — blur GPU
 * tambahan tidak diperlukan (lihat KDoc layar).
 */
@Composable
private fun PageGlow() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-48).dp)
                .size(320.dp)
                .background(
                    Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to RefyColors.AccentBright.copy(alpha = 0.45f),
                            0.45f to RefyColors.Accent.copy(alpha = 0.25f),
                            0.75f to Color.Transparent,
                        ),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = 288.dp)
                .size(288.dp)
                .background(
                    Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to RefyColors.AccentDeep.copy(alpha = 0.2f),
                            0.55f to RefyColors.AccentDeep.copy(alpha = 0.12f),
                            1f to Color.Transparent,
                        ),
                    ),
                ),
        )
    }
}

/** Pil "Playing from playlist · Violet Nights" — `pt-space-xs pb-space-sm`. */
@Composable
private fun SourcePill(playlistName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = RefyDimens.Space.xs, bottom = RefyDimens.Space.sm),
        horizontalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .clip(RefyShapesTokens.pill)
                .background(RefyColors.SurfaceHighest.copy(alpha = 0.6f))
                .padding(horizontal = RefyDimens.Space.sm, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(RefyColors.Accent),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.np_playing_from_playlist).uppercase(),
                style = RefyType.labelXs,
                color = RefyColors.TextMuted,
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = playlistName,
                style = RefyType.labelXs.copy(fontWeight = FontWeight.Bold),
                color = RefyColors.AccentBright,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Kartu artwork 300dp: latar nebula + poster (tile artwork, judul album,
 * judul lagu, progres kecil) + glow besar + badge LOSSLESS.
 *
 * Angka dari PNG kanon, bukan HTML (HTML hanya kotak kosong):
 * kartu y 116–416, tile 117–273 × 180–335, panel bawah mulai y≈256,
 * bilah progres 68→322 (3 px), waktu 1:34 / 3:58.
 */
@Composable
private fun ArtworkCard(state: NowPlayingUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = RefyDimens.Space.sm),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(ArtworkSize)
                // Glow kartu: lapisan gradien blur-2xl + dua bayangan CSS
                // (lihat ArtworkCardGlow). Seluruh layar sudah punya blob
                // halaman; glow ini yang membuat kartu "menyala".
                .pillGlow(
                    color = RefyColors.AccentBright,
                    layers = ArtworkCardGlow,
                    maxReachDp = 130f,
                    ringWidthDp = 1.5.dp,
                    cornerRadiusDp = 16.dp,
                )
                .clip(RoundedCornerShape(16.dp))
                .background(RefyColors.Canvas),
        ) {
            NebulaBackdrop(Modifier.matchParentSize())

            // Panel bawah poster: PNG menunjukkan artwork berhenti di y≈140,
            // lalu panel gelap sampai dasar kartu.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(ArtworkPanelHeight)
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Transparent,
                                0.17f to RefyColors.Canvas.copy(alpha = 0.9f),
                                1f to RefyColors.Canvas.copy(alpha = 0.9f),
                            ),
                        ),
                    ),
            )

            // Tile artwork di tengah-atas poster.
            ArtworkPlaceholder(
                seed = state.artworkSeed,
                size = ArtworkTileSize,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = ArtworkTileTop),
            )

            // Badge LOSSLESS — `top-3.5 right-3.5` di HTML kanon.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(14.dp),
                contentAlignment = Alignment.TopEnd,
            ) {
                Row(
                    modifier = Modifier
                        .clip(RefyShapesTokens.pill)
                        .background(RefyColors.Canvas.copy(alpha = 0.7f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PlayingBars(
                        modifier = Modifier.size(14.dp),
                        color = RefyColors.Accent,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.np_badge_lossless).uppercase(),
                        style = RefyType.labelXs,
                        color = RefyColors.TextPrimary,
                    )
                }
            }

            // Poster bawah: judul album · judul lagu + titik · progres · waktu.
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = ArtworkPanelInset, end = ArtworkPanelInset, bottom = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = state.artworkCaption,
                    style = RefyType.labelXs,
                    color = RefyColors.TextFaint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.artworkTitle,
                        style = RefyType.headlineMd.copy(fontWeight = FontWeight.ExtraBold),
                        color = RefyColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(RefyColors.AccentBright),
                    )
                }
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ArtworkBarHeight)
                        .clip(RefyShapesTokens.pill)
                        .background(RefyColors.SurfaceHighest),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(state.artworkProgress)
                            .fillMaxHeight()
                            .clip(RefyShapesTokens.pill)
                            .background(RefyColors.AccentBright),
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${formatTrackTime(state.artworkPositionMs)} / " +
                        formatTrackTime(state.artworkDurationMs),
                    style = RefyType.labelXs,
                    color = RefyColors.TextFaint.copy(alpha = 0.75f),
                )
            }
        }
    }
}

/**
 * Latar kartu artwork: "nebula" pengganti gambar bintang di PNG kanon.
 *
 * Tanpa gambar nyata (M1 belum ada resolver), artwork dirender sebagai tiga
 * blob radial dari token tema — bahasa placeholder yang sama dengan
 * [ArtworkPlaceholder] di layar lain.
 */
@Composable
private fun NebulaBackdrop(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.radialGradient(
                colorStops = arrayOf(
                    0f to RefyColors.AccentDeep.copy(alpha = 0.55f),
                    0.55f to RefyColors.Accent.copy(alpha = 0.28f),
                    1f to RefyColors.Canvas,
                ),
            ),
        ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-40).dp, y = (-36).dp)
                .size(220.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            RefyColors.AccentBright.copy(alpha = 0.55f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 52.dp, y = 4.dp)
                .size(190.dp)
                .background(
                    Brush.radialGradient(
                        listOf(
                            RefyColors.AccentBright.copy(alpha = 0.4f),
                            RefyColors.AccentDeep.copy(alpha = 0.15f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
    }
}

/** Judul + badge E + hati; nama artis. `pt-space-md pb-space-xs`. */
@Composable
private fun MetaRow(state: NowPlayingUiState, onFavoriteToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = RefyDimens.Space.md, bottom = RefyDimens.Space.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = state.title,
                    style = RefyType.headlineMd.copy(fontWeight = FontWeight.ExtraBold),
                    color = RefyColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (state.isExplicit) {
                    Spacer(Modifier.width(RefyDimens.Space.xs))
                    Text(
                        text = stringResource(R.string.album_badge_explicit),
                        style = RefyType.labelXs,
                        color = RefyColors.TextMuted,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(RefyColors.SurfaceHighest)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = state.artist,
                style = RefyType.bodyMd,
                color = RefyColors.TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(RefyDimens.Space.sm))
        val favoriteCd = stringResource(R.string.np_cd_favorite)
        Box(
            modifier = Modifier
                .size(RefyDimens.Size.iconButton)
                .pillGlow(
                    color = RefyColors.AccentBright,
                    layers = listOf(
                        GlowLayer(0.45f, 18f),
                    ),
                )
                .clip(CircleShape)
                .background(RefyColors.AccentBright.copy(alpha = 0.2f))
                .semantics { contentDescription = favoriteCd }
                .clickable(role = Role.Button, onClick = onFavoriteToggle),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = if (state.isFavorite) RefyColors.Accent else RefyColors.TextMuted,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

/**
 * Scrubber `pt-space-xs pb-space-xs`: trek 6dp + thumb + waktu di bawahnya.
 *
 * Tinggi kotak trek 30dp = `py-3` (12) + trek 6 + `py-3` (12) di HTML kanon:
 * PNG menaruh trek di y≈522 (bukan 512) dan waktu di y≈540 — kotak 30dp itu
 * yang memberi jarak atas/bawah thumb 16dp.
 */
@Composable
private fun Scrubber(state: NowPlayingUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = RefyDimens.Space.xs),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            val trackHeight = 6.dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .clip(RefyShapesTokens.pill)
                    .background(RefyColors.Border),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(state.progress)
                        .height(trackHeight)
                        .pillGlow(
                            color = RefyColors.AccentBright,
                            layers = listOf(
                                GlowLayer(1f, 16f),
                                GlowLayer(0.9f, 28f),
                            ),
                        )
                        .clip(RefyShapesTokens.pill)
                        .background(RefyColors.AccentBright),
                )
            }
            val thumbOffset = (maxWidth - 16.dp) * state.progress
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(16.dp)
                    .pillGlow(
                        color = RefyColors.AccentBright,
                        layers = ScrubberThumbGlow,
                        maxReachDp = 64f,
                    )
                    .clip(CircleShape)
                    .background(RefyColors.OnAccent),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = formatTrackTime(state.positionMs),
                style = RefyType.labelXs,
                color = RefyColors.TextFaint,
            )
            Text(
                text = stringResource(
                    R.string.np_remaining,
                    formatTrackTime((state.durationMs - state.positionMs).coerceAtLeast(0)),
                ),
                style = RefyType.labelXs,
                color = RefyColors.TextFaint,
            )
        }
    }
}

/** Transport: shuffle (titik aktif) · prev · putar 72dp · next · repeat. */
@Composable
private fun TransportControls(
    state: NowPlayingUiState,
    onShuffle: () -> Unit,
    onPrevious: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onRepeat: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = RefyDimens.Space.xs, bottom = RefyDimens.Space.sm)
            .padding(horizontal = RefyDimens.Space.xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Shuffle 40dp + titik 4dp saat aktif.
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onShuffle),
                contentAlignment = Alignment.Center,
            ) {
                ShuffleGlyph(
                    modifier = Modifier.size(22.dp),
                    color = if (state.isShuffleOn) RefyColors.Accent else RefyColors.TextMuted,
                )
            }
            if (state.isShuffleOn) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(RefyColors.Accent),
                )
            }
        }

        val previousCd = stringResource(R.string.np_cd_previous)
        Box(
            modifier = Modifier
                .size(RefyDimens.Size.iconButton)
                .clip(CircleShape)
                .semantics { contentDescription = previousCd }
                .clickable(role = Role.Button, onClick = onPrevious),
            contentAlignment = Alignment.Center,
        ) {
            SkipPreviousGlyph(modifier = Modifier.size(32.dp), color = RefyColors.TextPrimary)
        }

        // Tombol putar 72dp: gradient AccentDeep → AccentBright → Accent +
        // tiga lapis glow (PlayButtonGlow); ikon gelap `surface-container-lowest`.
        val playCd = stringResource(R.string.np_cd_play_pause)
        Box(
            modifier = Modifier
                .size(PlayButtonSize)
                .pillGlow(
                    color = RefyColors.AccentBright,
                    layers = PlayButtonGlow,
                    maxReachDp = 44f,
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(RefyColors.AccentDeep, RefyColors.AccentBright, RefyColors.Accent),
                    ),
                )
                .semantics { contentDescription = playCd }
                .clickable(role = Role.Button, onClick = onPlayPause),
            contentAlignment = Alignment.Center,
        ) {
            if (state.isPlaying) {
                PauseGlyph(modifier = Modifier.size(36.dp), color = RefyColors.Canvas)
            } else {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = RefyColors.Canvas,
                    modifier = Modifier.size(36.dp),
                )
            }
        }

        val nextCd = stringResource(R.string.np_cd_next)
        Box(
            modifier = Modifier
                .size(RefyDimens.Size.iconButton)
                .clip(CircleShape)
                .semantics { contentDescription = nextCd }
                .clickable(role = Role.Button, onClick = onNext),
            contentAlignment = Alignment.Center,
        ) {
            SkipNextGlyph(modifier = Modifier.size(32.dp), color = RefyColors.TextPrimary)
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onRepeat),
            contentAlignment = Alignment.Center,
        ) {
            RepeatGlyph(
                modifier = Modifier.size(22.dp),
                color = if (state.isRepeatOn) RefyColors.Accent else RefyColors.TextMuted,
            )
        }
    }
}

/** Bar Audio Output: headphone + label + nama perangkat; antrean & bagikan. */
@Composable
private fun AudioOutputBar(
    state: NowPlayingUiState,
    onOutput: () -> Unit,
    onQueue: () -> Unit,
    onShare: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = RefyDimens.Space.sm)
            .clip(RefyShapesTokens.medium)
            .background(RefyColors.Surface.copy(alpha = 0.7f))
            .padding(horizontal = RefyDimens.Space.xs, vertical = RefyDimens.Space.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val outputCd = stringResource(R.string.np_cd_output)
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RefyShapesTokens.small)
                .semantics { contentDescription = outputCd }
                .clickable(role = Role.Button, onClick = onOutput)
                .padding(horizontal = RefyDimens.Space.sm, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeadphonesGlyph(modifier = Modifier.size(20.dp), color = RefyColors.Accent)
            Spacer(Modifier.width(RefyDimens.Space.xs))
            Column {
                Text(
                    text = stringResource(R.string.np_audio_output).uppercase(),
                    style = RefyType.labelXs,
                    color = RefyColors.Accent,
                )
                Text(
                    text = state.audioOutput,
                    style = RefyType.bodySm.copy(fontWeight = FontWeight.SemiBold),
                    color = RefyColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        val queueCd = stringResource(R.string.np_cd_queue)
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .semantics { contentDescription = queueCd }
                .clickable(role = Role.Button, onClick = onQueue),
            contentAlignment = Alignment.Center,
        ) {
            QueueMusicGlyph(modifier = Modifier.size(20.dp), color = RefyColors.TextMuted)
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onShare),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = stringResource(R.string.np_share),
                tint = RefyColors.TextMuted,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Kartu lirik `backdrop-blur-xl` — hanya dirender bila lagu punya lirik.
 *
 * Data contoh (`sample_lyric_*`) mengisi tiga baris supaya layar sama dengan
 * PNG kanon; tanpa data lirik nyata (#11 = v0.2) kartu ini tidak dirender —
 * pemanggil yang menjaga syarat itu (lihat [NowPlayingScreen]).
 */
@Composable
private fun LyricsCard(state: NowPlayingUiState, onExpand: () -> Unit) {
    val expandCd = stringResource(R.string.np_cd_lyrics)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // HTML: bar audio `mb-space-sm` — itu satu-satunya jarak ke kartu.
            .pillGlow(
                color = RefyColors.AccentBright,
                layers = LyricsCardGlow,
                cornerRadiusDp = 16.dp,
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        // HTML: `from-secondary-container/30` — ungu, bukan
                        // AccentDeep (biru): PNG kartu atas (48,17,76).
                        RefyColors.Accent.copy(alpha = 0.3f),
                        RefyColors.SurfaceHighest.copy(alpha = 0.5f),
                    ),
                ),
            )
            .semantics { contentDescription = expandCd }
            .clickable(role = Role.Button, onClick = onExpand)
            .padding(RefyDimens.Space.md),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MicGlyph(modifier = Modifier.size(18.dp), color = RefyColors.Accent)
            Spacer(Modifier.width(RefyDimens.Space.xs))
            Text(
                text = stringResource(R.string.np_live_lyrics).uppercase(),
                style = RefyType.labelMd.copy(fontWeight = FontWeight.Bold),
                color = RefyColors.TextPrimary,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.np_expand),
                style = RefyType.labelXs,
                color = RefyColors.TextMuted,
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowUp,
                contentDescription = null,
                tint = RefyColors.TextMuted,
                modifier = Modifier.size(16.dp),
            )
        }
        // PNG: baris pertama di 777 — 27px di bawah header; HTML hanya
        // `pt-1` (4), jadi jarak diambil dari PNG.
        Spacer(Modifier.height(RefyDimens.Space.md))
        state.lyrics.forEachIndexed { index, line ->
            val active = index == state.activeLyricIndex
            Text(
                text = line,
                style = if (active) {
                    // Baris aktif: gradien putih → accent + drop-shadow.
                    RefyType.headlineSm.copy(
                        fontWeight = FontWeight.Black,
                        brush = Brush.horizontalGradient(
                            listOf(
                                RefyColors.TextPrimary,
                                RefyColors.AccentBright,
                                RefyColors.Accent,
                            ),
                        ),
                        shadow = Shadow(
                            color = RefyColors.AccentBright.copy(alpha = 0.4f),
                            offset = Offset(0f, 2f),
                            blurRadius = 8f,
                        ),
                    )
                } else {
                    RefyType.headlineSm.copy(fontWeight = FontWeight.Bold)
                },
                color = if (active) Color.Unspecified else RefyColors.TextMuted.copy(alpha = 0.4f),
                modifier = Modifier.padding(vertical = 2.dp),
            )
        }
        Spacer(Modifier.height(RefyDimens.Space.xs))
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(width = 40.dp, height = 3.dp)
                .clip(RefyShapesTokens.pill)
                .background(RefyColors.TextMuted.copy(alpha = 0.4f)),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF09090B)
@Composable
private fun NowPlayingScreenPreview() {
    NocturneTheme {
        Box {
            NowPlayingScreen(state = sampleNowPlayingUiState())
        }
    }
}
