package io.github.mochamadabdulrouf.refy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyShapesTokens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Kartu media vertikal (artwork + judul + subjudul) — dipakai "Made For You"
 * dan "Trending Now" di Home (#1).
 *
 * Ukuran: artwork [artworkSize] persegi, radius 12 (`docs/DESIGN.md` §6.5),
 * badge kecil di sudut kiri atas (label-xs). Judul `body-md` putih, subjudul
 * `body-sm` muted, keduanya satu baris dengan elipsis.
 */
@Composable
fun MediaCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    badge: String? = null,
    artworkSize: Dp? = 144.dp,
    seed: Int = 0,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Box {
            ArtworkPlaceholder(
                seed = seed,
                size = artworkSize,
                shape = RefyShapesTokens.medium,
            )
            if (badge != null) {
                Text(
                    text = badge,
                    style = RefyType.labelXs,
                    color = RefyColors.TextPrimary,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(RefyDimens.Space.xs)
                        .clip(RefyShapesTokens.small)
                        .background(RefyColors.GlassOverlay)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
        Spacer(Modifier.height(RefyDimens.Space.xs))
        Text(
            text = title,
            style = RefyType.bodyMd,
            color = RefyColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = RefyType.bodySm,
                color = RefyColors.TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Tile "Daily Mix": kartu mendatar (artwork kecil + judul) di dalam kontainer
 * `#141418` radius 12 — `docs/DESIGN.md` §6.5.
 */
@Composable
fun MixTile(
    title: String,
    modifier: Modifier = Modifier,
    seed: Int = 0,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RefyShapesTokens.medium)
            .background(RefyColors.Surface)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(RefyDimens.Space.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArtworkPlaceholder(
            seed = seed,
            size = 56.dp,
            shape = RefyShapesTokens.small,
        )
        Spacer(Modifier.width(RefyDimens.Space.sm))
        Text(
            text = title,
            style = RefyType.labelLg,
            color = RefyColors.TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(end = RefyDimens.Space.xs),
        )
    }
}