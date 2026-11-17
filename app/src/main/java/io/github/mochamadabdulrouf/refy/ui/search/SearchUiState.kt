package io.github.mochamadabdulrouf.refy.ui.search

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.mochamadabdulrouf.refy.R

/**
 * Keadaan layar #2 Active Search Results.
 *
 * **Data SEMENTARA** — pencarian sungguhan menunggu resolver (M1). Kueri di sini
 * hanya teks contoh, bukan hasil pencarian nyata.
 */
data class SearchUiState(
    val query: String,
    val filters: List<SearchFilter>,
    val selectedFilter: SearchFilter,
    val topResult: SearchTopResult,
    val songs: List<SearchSong>,
    val albums: List<SearchAlbum>,
)

/** Chip filter sesuai desain & inventaris: Top · Songs · Albums · Artists · Playlists. */
enum class SearchFilter(@StringRes val labelRes: Int) {
    Top(R.string.search_chip_top),
    Songs(R.string.search_chip_songs),
    Albums(R.string.search_chip_albums),
    Artists(R.string.search_chip_artists),
    Playlists(R.string.search_chip_playlists),
}

data class SearchTopResult(
    val title: String,
    val badge: String,
    val savesText: String,
    val byText: String,
    val seed: Int = 0,
)

data class SearchSong(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val seed: Int = 0,
)

data class SearchAlbum(
    val id: String,
    val title: String,
    val subtitle: String,
    val seed: Int = 0,
)

@Composable
fun sampleSearchUiState(): SearchUiState = SearchUiState(
    query = stringResource(R.string.sample_query),
    filters = SearchFilter.entries,
    selectedFilter = SearchFilter.Top,
    topResult = SearchTopResult(
        title = stringResource(R.string.sample_playlist_synthwave),
        badge = stringResource(R.string.sample_search_badge),
        savesText = stringResource(
            R.string.search_result_saves,
            stringResource(R.string.sample_search_saves),
            stringResource(R.string.sample_search_by),
        ),
        byText = stringResource(R.string.sample_search_by),
        seed = 0,
    ),
    songs = listOf(
        SearchSong(
            "s1",
            stringResource(R.string.sample_song_resonance),
            stringResource(R.string.sample_artist_home),
            "3:32",
            seed = 1,
        ),
        SearchSong(
            "s2",
            stringResource(R.string.sample_song_nightcall),
            stringResource(R.string.sample_artist_kavinsky),
            "4:19",
            seed = 2,
        ),
        SearchSong(
            "s3",
            stringResource(R.string.sample_song_sunset),
            stringResource(R.string.sample_artist_the_midnight),
            "5:26",
            seed = 3,
        ),
        SearchSong(
            "s4",
            stringResource(R.string.sample_song_tech_noir),
            stringResource(R.string.sample_artist_gun),
            "4:57",
            seed = 0,
        ),
    ),
    albums = listOf(
        SearchAlbum(
            "a1",
            stringResource(R.string.sample_album_endless_summer),
            stringResource(R.string.sample_album_endless_subtitle),
            seed = 2,
        ),
        SearchAlbum(
            "a2",
            stringResource(R.string.sample_album_outrun),
            stringResource(R.string.sample_album_outrun_subtitle),
            seed = 1,
        ),
    ),
)