package io.github.mochamadabdulrouf.refy.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.R
import io.github.mochamadabdulrouf.refy.ui.component.FilterChipRow
import io.github.mochamadabdulrouf.refy.ui.component.MediaCard
import io.github.mochamadabdulrouf.refy.ui.component.MixTile
import io.github.mochamadabdulrouf.refy.ui.component.SectionHeader
import io.github.mochamadabdulrouf.refy.ui.theme.NocturneTheme
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType
import java.time.LocalTime

/**
 * Layar **#1 Home** — stateless, menerima [HomeUiState].
 *
 * Susunan mengikuti PNG kanon `home-screen-243ea0d8`:
 * sapaan besar (tanpa nama, D14) → chip filter → grid Daily Mix 2 kolom →
 * "Made For You" (karusel + "Show all") → "Trending Now" (grid 2 kolom + badge).
 *
 * **Yang sengaja tidak dirender** (D15 — fitur desain yang belum ada di v0.1
 * tidak ditampilkan sama sekali, bukan dimatikan): bel notifikasi, tombol cast,
 * tombol pengatur suara, avatar profil, chip Podcasts/Audiobooks/Live.
 * Semuanya menunggu fitur atau layarnya sendiri.
 *
 * Catatan padding: konten diberi `statusBarsPadding()` karena aplikasi memakai
 * edge-to-edge; jarak aman bawah ditangani `ScaffoldShell` (160dp).
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    onFilterSelect: (HomeFilter) -> Unit = {},
    onItemClick: (HomeItem) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = RefyDimens.Margin.mobile),
    ) {
        item(key = "greeting") {
            Text(
                text = stringResource(state.greetingRes),
                style = RefyType.headlineLgMobile,
                color = RefyColors.TextPrimary,
                modifier = Modifier.padding(top = RefyDimens.Space.lg, bottom = RefyDimens.Space.md),
            )
        }

        item(key = "filters") {
            FilterChipRow(
                labels = state.filters.map { stringResource(it.labelRes) },
                selectedIndex = state.filters.indexOf(state.selectedFilter).coerceAtLeast(0),
                onSelect = { index -> onFilterSelect(state.filters[index]) },
            )
        }

        items(
            items = state.dailyMix.chunked(2),
            key = { row -> "daily-" + row.joinToString("-") { it.id } },
        ) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = RefyDimens.Space.md),
                horizontalArrangement = Arrangement.spacedBy(RefyDimens.Space.sm),
            ) {
                row.forEach { item ->
                    MixTile(
                        title = item.title,
                        seed = item.seed,
                        onClick = { onItemClick(item) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }

        item(key = "made_for_you_header") {
            SectionHeader(
                title = stringResource(R.string.home_section_made_for_you),
                subtitle = stringResource(R.string.home_subtitle_made_for_you),
                actionLabel = stringResource(R.string.common_show_all),
                onAction = {},
            )
        }

        item(key = "made_for_you_row") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(RefyDimens.Space.sm),
                contentPadding = PaddingValues(end = RefyDimens.Space.md),
            ) {
                items(state.madeForYou, key = { it.id }) { item ->
                    MediaCard(
                        title = item.title,
                        subtitle = item.subtitle,
                        badge = item.badge,
                        artworkSize = 144.dp,
                        seed = item.seed,
                        onClick = { onItemClick(item) },
                        modifier = Modifier.width(144.dp),
                    )
                }
            }
        }

        item(key = "trending_header") {
            SectionHeader(
                title = stringResource(R.string.home_section_trending_now),
                subtitle = stringResource(R.string.home_subtitle_trending_now),
                actionLabel = stringResource(R.string.home_explore),
                onAction = {},
            )
        }

        items(
            items = state.trending.chunked(2),
            key = { row -> "trending-" + row.joinToString("-") { it.id } },
        ) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = RefyDimens.Space.md),
                horizontalArrangement = Arrangement.spacedBy(RefyDimens.Space.sm),
            ) {
                row.forEach { item ->
                    MediaCard(
                        title = item.title,
                        subtitle = item.subtitle,
                        badge = item.badge,
                        artworkSize = null,
                        seed = item.seed,
                        onClick = { onItemClick(item) },
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

/**
 * Data contoh untuk Home — **sementara**, sampai resolver (M1) dan Room (M4) ada.
 * Judul diambil dari resource `sample_*`. Tidak ada angka karangan (jumlah
 * pemutaran, durasi) yang ditampilkan sebagai fakta.
 */
@Composable
fun sampleHomeUiState(): HomeUiState = HomeUiState(
    greetingRes = greetingResFor(LocalTime.now().hour),
    filters = HomeFilter.entries,
    selectedFilter = HomeFilter.All,
    dailyMix = listOf(
        HomeItem("dm1", stringResource(R.string.sample_daily_mix_1), seed = 0),
        HomeItem("dm2", stringResource(R.string.sample_daily_mix_2), seed = 1),
        HomeItem("dm3", stringResource(R.string.sample_daily_mix_3), seed = 2),
        HomeItem("dm4", stringResource(R.string.sample_daily_mix_4), seed = 3),
    ),
    madeForYou = listOf(
        HomeItem(
            "mfy1",
            stringResource(R.string.sample_made_for_you_1),
            subtitle = stringResource(R.string.home_subtitle_made_for_you),
            badge = stringResource(R.string.sample_badge_weekly),
            seed = 1,
        ),
        HomeItem(
            "mfy2",
            stringResource(R.string.sample_made_for_you_2),
            subtitle = stringResource(R.string.home_subtitle_made_for_you),
            seed = 2,
        ),
    ),
    trending = listOf(
        HomeItem(
            "tr1",
            stringResource(R.string.sample_trending_1),
            subtitle = stringResource(R.string.sample_trending_1_artist),
            badge = stringResource(R.string.sample_badge_trending),
            seed = 3,
        ),
        HomeItem(
            "tr2",
            stringResource(R.string.sample_trending_2),
            subtitle = stringResource(R.string.sample_trending_2_artist),
            seed = 0,
        ),
    ),
)

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun HomeScreenPreview() {
    val sample = sampleHomeUiState()
    var selected by remember { mutableStateOf(HomeFilter.All) }
    NocturneTheme {
        HomeScreen(
            state = sample.copy(selectedFilter = selected),
            onFilterSelect = { selected = it },
        )
    }
}