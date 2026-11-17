package io.github.mochamadabdulrouf.refy.ui.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.draw.clip
import io.github.mochamadabdulrouf.refy.R
import io.github.mochamadabdulrouf.refy.ui.component.FilterChipRow
import io.github.mochamadabdulrouf.refy.ui.component.MediaRow
import io.github.mochamadabdulrouf.refy.ui.component.SectionHeader
import io.github.mochamadabdulrouf.refy.ui.component.SearchField
import io.github.mochamadabdulrouf.refy.ui.theme.NocturneTheme
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyShapesTokens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Layar **#3 Library & Search Screen — tab Discover**.
 *
 * Susunan dari PNG kanon `library-search-screen-3a4eb7d9`: judul → kolom cari →
 * "Browse all" (grid genre 2 kolom) → chip filter → "Recently played" (daftar).
 *
 * **Penyimpangan yang dicatat:**
 * - Judul memakai `discover.title` ("Discover"), bukan `<h1>` desain
 *   "Library And Search Screen" — inventaris i18n menyebut `<h1>` itu cacat
 *   desain yang dipakai tiga layar sekaligus.
 * - Ikon mikrofon & kamera di kolom cari **tidak dirender** (D15: bukan fitur
 *   v0.1). Aksi "Explore tags" juga tidak dirender.
 * - Chip `Downloaded` dan `Podcasts` tidak dirender (D15).
 * - Judul genre adalah **data katalog**, bukan string UI — untuk sekarang
 *   memakai resource `sample_*`.
 */
@Composable
fun DiscoverScreen(
    state: DiscoverUiState,
    modifier: Modifier = Modifier,
    onFilterSelect: (DiscoverFilter) -> Unit = {},
    onGenreClick: (GenreItem) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onItemClick: (DiscoverItem) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = RefyDimens.Margin.mobile),
    ) {
        item(key = "title") {
            Text(
                text = stringResource(R.string.discover_title),
                style = RefyType.headlineSm,
                color = RefyColors.TextPrimary,
                modifier = Modifier.padding(top = RefyDimens.Space.md, bottom = RefyDimens.Space.md),
            )
        }

        item(key = "search") {
            SearchField(
                hint = stringResource(R.string.search_hint),
                onClick = onSearchClick,
            )
        }

        item(key = "browse_header") {
            SectionHeader(title = stringResource(R.string.discover_browse_all))
        }

        items(state.genres.chunked(2).size) { index ->
            val row = remember(state.genres) { state.genres.chunked(2) }[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = RefyDimens.Space.sm),
                horizontalArrangement = Arrangement.spacedBy(RefyDimens.Space.sm),
            ) {
                row.forEach { genre ->
                    GenreCard(
                        title = genre.title,
                        seed = genre.seed,
                        onClick = { onGenreClick(genre) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
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
            // Ikon urutkan & ikon tampilan-grid pada desain belum punya perilaku di
            // v0.1 → tidak dirender (D15). Dulu sempat memakai `Icons.Filled.SwapVert`
            // yang ternyata tidak ada di set ikon inti.
            SectionHeader(title = stringResource(R.string.discover_recently_played))
        }

        items(state.recentlyPlayed.size) { index ->
            val item = state.recentlyPlayed[index]
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

/** Kartu genre: gradien penuh, judul di kiri atas (desain tanpa gambar nyata). */
@Composable
private fun GenreCard(
    title: String,
    seed: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tones = listOf(
        RefyColors.AccentBright to RefyColors.AccentDeep,
        RefyColors.Accent to RefyColors.SurfaceHighest,
        RefyColors.AccentDeep to RefyColors.Accent,
    )
    val (start, end) = tones[seed.mod(tones.size)]

    Box(
        modifier = modifier
            .aspectRatio(1.45f)
            .clip(RefyShapesTokens.medium)
            .background(Brush.linearGradient(listOf(start, end)))
            .clickable(onClick = onClick)
            .padding(RefyDimens.Space.sm),
        contentAlignment = Alignment.TopStart,
    ) {
        Text(
            text = title,
            style = RefyType.labelLg,
            color = RefyColors.OnAccent,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun DiscoverScreenPreview() {
    val sample = sampleDiscoverUiState()
    var selected by remember { mutableStateOf(DiscoverFilter.Playlists) }
    NocturneTheme {
        DiscoverScreen(
            state = sample.copy(selectedFilter = selected),
            onFilterSelect = { selected = it },
        )
    }
}