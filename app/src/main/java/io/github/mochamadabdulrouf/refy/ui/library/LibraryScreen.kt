package io.github.mochamadabdulrouf.refy.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.R
import io.github.mochamadabdulrouf.refy.ui.component.ArtworkPlaceholder
import io.github.mochamadabdulrouf.refy.ui.component.FilterChipRow
import io.github.mochamadabdulrouf.refy.ui.component.MediaRow
import io.github.mochamadabdulrouf.refy.ui.component.SectionHeader
import io.github.mochamadabdulrouf.refy.ui.theme.NocturneTheme
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyShapesTokens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Layar **#4 Your Library** (tab Library).
 *
 * Susunan dari PNG kanon `your-library-be4f34bd`: judul + tombol cari & tambah →
 * chip filter → "Recently Played" → dua kartu besar (pinned) → daftar baris.
 *
 * **Penyimpangan (D15 / D14):**
 * - Avatar pengguna di kanan atas **tidak** dirender (v0.1 tanpa login).
 * - Chip `Podcasts` dan `Downloaded` tidak dirender.
 * - Ikon urutkan & tampilan-grid di header tidak dirender (belum ada perilaku).
 * - Tombol "+" dirender karena membuat playlist adalah F-15 (MUST); aksinya
 *   menyambung ke layar #14 di Fase 5.
 */
@Composable
fun LibraryScreen(
    state: LibraryUiState,
    modifier: Modifier = Modifier,
    onFilterSelect: (LibraryFilter) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onCreatePlaylist: () -> Unit = {},
    onItemClick: (LibraryItem) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = RefyDimens.Margin.mobile),
    ) {
        item(key = "title") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = RefyDimens.Space.lg, bottom = RefyDimens.Space.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.library_title),
                    style = RefyType.headlineLgMobile,
                    color = RefyColors.TextPrimary,
                    modifier = Modifier.weight(1f),
                )
                CircleIconButton(
                    icon = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.common_search),
                    onClick = onSearchClick,
                )
                Spacer(Modifier.width(RefyDimens.Space.xs))
                CircleIconButton(
                    icon = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.library_cd_add),
                    onClick = onCreatePlaylist,
                    accent = true,
                )
            }
        }

        item(key = "filters") {
            FilterChipRow(
                labels = state.filters.map { stringResource(it.labelRes) },
                selectedIndex = state.filters.indexOf(state.selectedFilter).coerceAtLeast(0),
                onSelect = { index -> onFilterSelect(state.filters[index]) },
            )
        }

        item(key = "recently_played_header") {
            SectionHeader(title = stringResource(R.string.library_recently_played))
        }

        items(state.pinned.size) { index ->
            val item = state.pinned[index]
            PinnedCard(item = item, onClick = { onItemClick(item) })
        }

        if (state.items.isEmpty()) {
            item(key = "empty") {
                Text(
                    text = stringResource(R.string.library_empty),
                    style = RefyType.bodyMd,
                    color = RefyColors.TextMuted,
                    modifier = Modifier.padding(vertical = RefyDimens.Space.lg),
                )
            }
        } else {
            items(state.items.size) { index ->
                val item = state.items[index]
                MediaRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    seed = item.seed,
                    onClick = { onItemClick(item) },
                    onMoreOptions = {},
                )
            }
        }
    }
}

/** Kartu besar bergambar 72dp dengan titik status di kanan (gaya "pinned"). */
@Composable
private fun PinnedCard(item: LibraryItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = RefyDimens.Space.sm)
            .clip(RefyShapesTokens.medium)
            .background(RefyColors.Surface)
            .clickable(onClick = onClick)
            .padding(RefyDimens.Space.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArtworkPlaceholder(seed = item.seed, size = 72.dp, shape = RefyShapesTokens.medium)
        Spacer(Modifier.width(RefyDimens.Space.sm))
        Box(modifier = Modifier.weight(1f)) {
            androidx.compose.foundation.layout.Column {
                Text(
                    text = item.title,
                    style = RefyType.headlineSm,
                    color = RefyColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.subtitle,
                    style = RefyType.bodySm,
                    color = RefyColors.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(RefyColors.Accent),
        )
    }
}

@Composable
private fun CircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    accent: Boolean = false,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(RefyDimens.Size.iconButton)
            .clip(CircleShape)
            .background(if (accent) RefyColors.Accent else RefyColors.Surface),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (accent) RefyColors.OnAccent else RefyColors.TextPrimary,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun LibraryScreenPreview() {
    val sample = sampleLibraryUiState()
    var selected by remember { mutableStateOf(LibraryFilter.Playlists) }
    NocturneTheme {
        LibraryScreen(
            state = sample.copy(selectedFilter = selected),
            onFilterSelect = { selected = it },
        )
    }
}