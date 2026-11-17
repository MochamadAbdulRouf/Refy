package io.github.mochamadabdulrouf.refy.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.R
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Baris daftar bergambar: artwork 56dp + judul + satu baris metadata + tombol
 * overflow. Dipakai Discover (#3) "Recently played" dan nanti Your Library (#4).
 *
 * Tinggi tidak dipatok: mengikuti isi + padding, supaya aman untuk judul dua baris.
 * Baris lagu pemutar **berbeda** (56dp tetap, `docs/DESIGN.md` §6.4) dan akan
 * dibuat terpisah saat layar album/pencarian membutuhkannya.
 */
@Composable
fun MediaRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    seed: Int = 0,
    trailingText: String? = null,
    onClick: (() -> Unit)? = null,
    onMoreOptions: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = RefyDimens.Space.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArtworkPlaceholder(seed = seed, size = 56.dp)

        Spacer(Modifier.width(RefyDimens.Space.sm))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = RefyType.bodyLg,
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

        if (trailingText != null) {
            Text(
                text = trailingText,
                style = RefyType.bodySm,
                color = RefyColors.TextMuted,
                maxLines = 1,
            )
        }

        if (onMoreOptions != null) {
            IconButton(onClick = onMoreOptions, modifier = Modifier.size(RefyDimens.Size.iconButton)) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.common_more_options),
                    tint = RefyColors.TextMuted,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}