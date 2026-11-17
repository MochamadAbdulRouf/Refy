package io.github.mochamadabdulrouf.refy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Kolom pencarian (pil) di layar #3 Discover — `docs/DESIGN.md` §6.8.
 *
 * Ini **tombol**, bukan `TextField`: mengetik belum tersambung apa pun (resolver
 * M1, layar #2 menyusul). Ikon mikrofon & kamera pada desain **tidak dirender**
 * (D15 — input suara/gambar bukan fitur v0.1).
 */
@Composable
fun SearchField(
    hint: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(RefyDimens.Size.primaryButton + 8.dp)
            .clip(CircleShape)
            .background(RefyColors.Surface)
            .clickable(role = Role.Button, onClick = onClick)
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
        Text(
            text = hint,
            style = RefyType.bodyMd,
            color = RefyColors.TextFaint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}