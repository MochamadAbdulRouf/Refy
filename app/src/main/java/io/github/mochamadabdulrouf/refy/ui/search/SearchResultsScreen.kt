package io.github.mochamadabdulrouf.refy.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.R
import io.github.mochamadabdulrouf.refy.ui.component.FilterChipRow
import io.github.mochamadabdulrouf.refy.ui.component.MediaCard
import io.github.mochamadabdulrouf.refy.ui.component.MediaRow
import io.github.mochamadabdulrouf.refy.ui.component.ArtworkPlaceholder
import io.github.mochamadabdulrouf.refy.ui.component.SearchField
import io.github.mochamadabdulrouf.refy.ui.component.SectionHeader
import io.github.mochamadabdulrouf.refy.ui.theme.NocturneTheme
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyShapesTokens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Layar **#2 Active Search Results**.
 *
 * Susunan dari PNG kanon `active-search-results-13c3c6cd`: kolom cari + Cancel →
 * chip Top/Songs/Albums/Artists/Playlists → Top Result (kartu bergradien) →
 * Songs (baris + durasi) → Albums (grid 2 kolom).
 *
 * **Penyimpangan:** avatar di kanan atas tidak dirender (v0.1 tanpa login) ·
 * kueri belum bisa diketik (layar ini menerima kueri dari luar; input sebenarnya
 * butuh resolver M1).
 */
@Composable
fun SearchResultsScreen(
    state: SearchUiState,
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onClear: () -> Unit = {},
    onFilterSelect: (SearchFilter) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = RefyDimens.Margin.mobile),
    ) {
        // Desain tidak punya judul "Search": layar langsung dibuka kolom cari.
        item(key = "search") {
            Box(modifier = Modifier.padding(top = RefyDimens.Space.md)) {
                SearchField(
                    hint = stringResource(R.string.search_hint),
                    query = state.query,
                    onClear = onClear,
                    cancelLabel = stringResource(R.string.common_cancel),
                    onCancel = onCancel,
                )
            }
        }

        item(key = "filters") {
            Box(modifier = Modifier.padding(top = RefyDimens.Space.sm)) {
                FilterChipRow(
                    labels = state.filters.map { stringResource(it.labelRes) },
                    selectedIndex = state.filters.indexOf(state.selectedFilter).coerceAtLeast(0),
                    onSelect = { index -> onFilterSelect(state.filters[index]) },
                )
            }
        }

        item(key = "top_result_header") {
            SectionHeader(title = stringResource(R.string.search_top_result))
        }

        item(key = "top_result") {
            TopResultCard(state.topResult)
        }

        item(key = "songs_header") {
            SectionHeader(
                title = stringResource(R.string.search_section_songs),
                actionLabel = stringResource(R.string.common_see_all),
                onAction = {},
            )
        }

        items(state.songs.size) { index ->
            val song = state.songs[index]
            MediaRow(
                title = song.title,
                subtitle = song.artist,
                seed = song.seed,
                trailingText = song.duration,
                onMoreOptions = {},
            )
        }

        item(key = "albums_header") {
            SectionHeader(
                title = stringResource(R.string.search_section_albums),
                actionLabel = stringResource(R.string.common_see_all),
                onAction = {},
            )
        }

        items(state.albums.chunked(2).size) { index ->
            val row = remember(state.albums) { state.albums.chunked(2) }[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = RefyDimens.Space.md),
                horizontalArrangement = Arrangement.spacedBy(RefyDimens.Space.sm),
            ) {
                row.forEach { album ->
                    MediaCard(
                        title = album.title,
                        subtitle = album.subtitle,
                        artworkSize = null,
                        seed = album.seed,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

/** Kartu "Top Result": latar gradien accent, artwork kecil, tombol putar. */
@Composable
private fun TopResultCard(result: SearchTopResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RefyShapesTokens.medium)
            .background(Brush.linearGradient(listOf(RefyColors.Accent, RefyColors.AccentDeep)))
            .padding(RefyDimens.Space.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArtworkPlaceholder(
            seed = result.seed,
            size = 56.dp,
            shape = RefyShapesTokens.small,
        )
        Spacer(Modifier.width(RefyDimens.Space.sm))
        Column(modifier = Modifier.weight(1f)) {
            // Desain memakai badge berbentuk pil, bukan teks polos.
            Text(
                text = result.badge,
                style = RefyType.labelXs,
                color = RefyColors.OnAccent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(RefyColors.OnAccent.copy(alpha = 0.18f))
                    .padding(horizontal = RefyDimens.Space.xs, vertical = 2.dp),
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = result.title,
                style = RefyType.headlineSm,
                color = RefyColors.OnAccent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = result.savesText,
                style = RefyType.bodySm,
                color = RefyColors.OnAccent.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .size(RefyDimens.Size.primaryButton)
                .clip(CircleShape)
                .background(RefyColors.OnAccent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.common_play_playlist),
                tint = RefyColors.OnAccent,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun SearchResultsScreenPreview() {
    NocturneTheme {
        SearchResultsScreen(state = sampleSearchUiState())
    }
}