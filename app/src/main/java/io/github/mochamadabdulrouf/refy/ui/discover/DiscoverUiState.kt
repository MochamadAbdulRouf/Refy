package io.github.mochamadabdulrouf.refy.ui.discover

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.mochamadabdulrouf.refy.R

/**
 * Keadaan layar #3 Discover.
 *
 * **Data SEMENTARA** — genre dan daftar "Recently played" memakai resource
 * `sample_*` sampai katalog (M1) dan riwayat (M4) ada.
 */
data class DiscoverUiState(
    val filters: List<DiscoverFilter>,
    val selectedFilter: DiscoverFilter,
    val genres: List<GenreItem>,
    val recentlyPlayed: List<DiscoverItem>,
)

enum class DiscoverFilter(@StringRes val labelRes: Int) {
    Playlists(R.string.discover_filter_playlists),
    Artists(R.string.discover_filter_artists),
    Albums(R.string.discover_filter_albums),
}

/** Judul genre adalah data katalog; [seed] hanya gradien sementara. */
data class GenreItem(val id: String, val title: String, val seed: Int)

data class DiscoverItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val seed: Int = 0,
)

@Composable
fun sampleDiscoverUiState(): DiscoverUiState = DiscoverUiState(
    filters = DiscoverFilter.entries,
    selectedFilter = DiscoverFilter.Playlists,
    genres = listOf(
        GenreItem("g1", stringResource(R.string.sample_genre_electronic), 0),
        GenreItem("g2", stringResource(R.string.sample_genre_hiphop), 1),
        GenreItem("g3", stringResource(R.string.sample_genre_lofi), 2),
        GenreItem("g4", stringResource(R.string.sample_genre_rock), 3),
        GenreItem("g5", stringResource(R.string.sample_genre_ambient), 1),
        GenreItem("g6", stringResource(R.string.sample_genre_edm), 0),
    ),
    recentlyPlayed = listOf(
        DiscoverItem(
            "rp1",
            stringResource(R.string.sample_favorite_tracks),
            stringResource(R.string.sample_playlist_songs),
            seed = 0,
        ),
        DiscoverItem(
            "rp2",
            stringResource(R.string.sample_playlist_synthwave),
            stringResource(R.string.sample_playlist_editorial),
            seed = 3,
        ),
        DiscoverItem(
            "rp3",
            stringResource(R.string.sample_made_for_you_2),
            stringResource(R.string.sample_playlist_songs),
            seed = 1,
        ),
    ),
)