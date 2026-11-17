package io.github.mochamadabdulrouf.refy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.R
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Kolom pencarian (pil) — `docs/DESIGN.md` §6.8. Dua keadaan:
 *
 * 1. **Kosong** (`query == null`) — dipakai Discover (#3) sebagai tombol pembuka.
 * 2. **Berisi** (`query != null`) — dipakai hasil pencarian (#2): teks kueri,
 *    tombol hapus, dan label "Cancel" di kanan.
 *
 * Ini **bukan** `TextField`: kueri belum bisa diketik karena layar hasil
 * pencarian masih memakai data contoh dan resolver baru ada di M1. Ikon mikrofon
 * dan kamera pada desain tidak dirender (D15).
 */
@Composable
fun SearchField(
    hint: String,
    modifier: Modifier = Modifier,
    query: String? = null,
    onClear: (() -> Unit)? = null,
    cancelLabel: String? = null,
    onCancel: (() -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(RefyDimens.Size.primaryButton + 8.dp)
                .clip(CircleShape)
                .background(RefyColors.Surface)
                .then(if (query == null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
                .padding(horizontal = RefyDimens.Space.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = RefyColors.TextMuted,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(RefyDimens.Space.sm))

            if (query == null) {
                Text(
                    text = hint,
                    style = RefyType.bodyMd,
                    color = RefyColors.TextFaint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            } else {
                Text(
                    text = query,
                    style = RefyType.bodyMd,
                    color = RefyColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (onClear != null) {
                    Box(
                        modifier = Modifier
                            .size(RefyDimens.Size.iconButton)
                            .clip(CircleShape)
                            .clickable(role = Role.Button, onClick = onClear),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = stringResource(R.string.common_clear),
                            tint = RefyColors.TextMuted,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }

        if (cancelLabel != null && onCancel != null) {
            Spacer(Modifier.width(RefyDimens.Space.xs))
            Text(
                text = cancelLabel,
                style = RefyType.labelLg,
                color = RefyColors.TextPrimary,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClick = onCancel)
                    .padding(horizontal = RefyDimens.Space.sm, vertical = RefyDimens.Space.xs),
            )
        }
    }
}