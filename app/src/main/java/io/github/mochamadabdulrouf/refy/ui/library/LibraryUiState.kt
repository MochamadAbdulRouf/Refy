package io.github.mochamadabdulrouf.refy.ui.library

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.mochamadabdulrouf.refy.R

/**
 * Keadaan layar #4 Your Library.
 *
 * **Data SEMENTARA** — belum ada Room (M4) maupun sinkronisasi pustaka.
 */
data class LibraryUiState(
    val filters: List<LibraryFilter>,
    val selectedFilter: LibraryFilter,
    val pinned: List<LibraryItem>,
    val items: List<LibraryItem>,
)

enum class LibraryFilter(@StringRes val labelRes: Int) {
    Playlists(R.string.discover_filter_playlists),
    Albums(R.string.discover_filter_albums),
    Artists(R.string.discover_filter_artists),
}

/** [pinned] = dua kartu besar di atas daftar (Liked Songs, New Episodes). */
data class LibraryItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val seed: Int = 0,
)

@Composable
fun sampleLibraryUiState(): LibraryUiState = LibraryUiState(
    filters = LibraryFilter.entries,
    selectedFilter = LibraryFilter.Playlists,
    pinned = listOf(
        LibraryItem(
            "liked",
            stringResource(R.string.library_liked_songs),
            stringResource(R.string.sample_liked_songs_subtitle),
            seed = 0,
        ),
        LibraryItem(
            "episodes",
            stringResource(R.string.sample_new_episodes),
            stringResource(R.string.sample_new_episodes_subtitle),
            seed = 1,
        ),
    ),
    items = listOf(
        LibraryItem(
            "lib1",
            stringResource(R.string.sample_playlist_synthwave),
            stringResource(R.string.sample_playlist_editorial),
            seed = 2,
        ),
        LibraryItem(
            "lib2",
            stringResource(R.string.sample_late_night_drives),
            stringResource(R.string.sample_late_night_subtitle),
            seed = 3,
        ),
        LibraryItem(
            "lib3",
            stringResource(R.string.sample_smart_radio),
            stringResource(R.string.sample_smart_radio_subtitle),
            seed = 1,
        ),
        LibraryItem(
            "lib4",
            stringResource(R.string.sample_deep_focus),
            stringResource(R.string.sample_deep_focus_subtitle),
            seed = 2,
        ),
        LibraryItem(
            "lib5",
            stringResource(R.string.sample_artist_the_midnight),
            stringResource(R.string.sample_artist_subtitle),
            seed = 0,
        ),
        LibraryItem(
            "lib6",
            stringResource(R.string.sample_album_random_access),
            stringResource(R.string.sample_album_subtitle),
            seed = 3,
        ),
    ),
)