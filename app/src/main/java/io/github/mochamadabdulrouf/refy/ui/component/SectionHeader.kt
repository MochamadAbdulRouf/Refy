package io.github.mochamadabdulrouf.refy.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyShapesTokens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Judul bagian + subjudul + aksi teks — `docs/DESIGN.md` §6, dipakai Home (#1)
 * dan Discover (#3).
 *
 * Ukuran: judul [RefyType.headlineSm] (18/600), subjudul [RefyType.bodySm]
 * (12/400 muted). Aksi memakai [RefyType.labelMd] dengan tanda panah, seperti
 * "Show all >" pada desain.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = RefyDimens.Space.lg, bottom = RefyDimens.Space.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = RefyType.headlineSm, color = RefyColors.TextPrimary)
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(text = subtitle, style = RefyType.bodyMd, color = RefyColors.TextMuted)
            }
        }
        if (actionLabel != null) {
            Row(
                modifier = Modifier
                    .clip(RefyShapesTokens.pill)
                    .clickable(
                        enabled = onAction != null,
                        role = Role.Button,
                        onClick = { onAction?.invoke() },
                    )
                    .padding(horizontal = RefyDimens.Space.xs, vertical = RefyDimens.Space.xxs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = actionLabel, style = RefyType.labelMd, color = RefyColors.TextMuted)
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = RefyColors.TextMuted,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        trailing?.invoke()
    }
}

/**
 * Baris chip filter — pil, dapat digulir mendatar.
 *
 * Pilihan aktif: ungu solid `#8B5CF6` + teks putih (DESIGN §6.6) + glow ungu
 * [pillGlow] dengan profil box-shadow HTML (bukti PNG 2026-11-19:
 * α0,63 di tepi, potong pada clip container). Inaktif: [inactiveBackground]
 * + teks `#A1A1AA`. Tinggi pil 28dp (line-height 16 + padding 6/14).
 *
 * [contentPadding] dipakai layar #8 untuk meniru container desain
 * `px-margin-mobile py-2` penuh-lebar: LazyRow membentang ke tepi layar
 * sehingga glow horizontal masuk margin 16dp dan vertikal terpotong 8dp
 * (clip LazyRow pada batas contentPadding). Layar lain memakai default
 * nol — margin horizontal mereka berasal dari contentPadding LazyColumn.
 *
 * [inactiveBackground] default `ChipInactive` (`rgba(20,20,24,.8)`, §6.6).
 * Layar #8 memakai `SurfaceHighest` karena HTML-nya memakai
 * `bg-surface-container-high` (`#2a2a2c` → remap §1) — dibuktikan sampel
 * piksel PNG `artist-profile-ebadb5ac` (2026-11-19).
 *
 * Catatan: desain Home menampilkan 5 chip (All · Music · Podcasts · Audiobooks ·
 * Live). Inventaris `docs/I18N-KEYS.md` hanya punya `home.filter_all` dan
 * `home.filter_music`, dan D15 melarang merender fitur yang belum ada di v0.1
 * (podcast/audiobook/live). Jadi hanya dua chip — dicatat di `PLAN.md` §5.
 */
@Composable
fun FilterChipRow(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    inactiveBackground: Color = RefyColors.ChipInactive,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(RefyDimens.Space.xs),
    ) {
        items(labels.size) { index ->
            FilterChip(
                label = labels[index],
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                inactiveBackground = inactiveBackground,
                glowClip = with(contentPadding) {
                    maxOf(calculateTopPadding(), calculateBottomPadding())
                },
            )
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    inactiveBackground: Color = RefyColors.ChipInactive,
    glowClip: Dp = 0.dp,
) {
    val background = if (selected) RefyColors.Accent else inactiveBackground
    val contentColor = if (selected) RefyColors.OnAccent else RefyColors.TextMuted
    // Glow: HTML memberi box-shadow ungu kuat pada chip aktif
    // (`rgb(168,85,247) 0 0 20px` + `rgba(...,0.7) 0 0 35px`) — dibuktikan
    // di PNG 2026-11-19 (α0,63 di tepi; Modifier.shadow hanya 0,16, jadi
    // digambar manual lewat pillGlow). Chip inaktif tanpa glow.
    val glow = if (selected) {
        Modifier.pillGlow(
            color = RefyColors.AccentBright,
            layers = ActiveChipGlow,
            clipVerticalDp = glowClip,
        )
    } else {
        Modifier
    }

    Text(
        text = label,
        style = RefyType.labelMd,
        color = contentColor,
        modifier = glow
            .clip(CircleShape)
            .background(background)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    )
}