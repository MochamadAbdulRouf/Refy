package io.github.mochamadabdulrouf.refy.ui.artist

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.mochamadabdulrouf.refy.R

/**
 * Keadaan layar #8 Artist Profile.
 *
 * **Data SEMENTARA** — katalog artis baru ada setelah resolver (M1) dan cache
 * Room (M4). Angka pendengar/stream adalah data contoh, bukan teks UI.
 */
data class ArtistProfileUiState(
    val name: String,
    /** "112,419,802 monthly listeners" — sudah lewat [R.string.artist_monthly_listeners]. */
    val listenersText: String,
    val heroSeed: Int,
    val isFollowing: Boolean,
    val tracks: List<ArtistTrack>,
    /** Chip discography: Popular Releases · Albums · Singles & EPs. */
    val chips: List<String>,
    val selectedChip: Int,
    val albums: List<ArtistAlbum>,
)

data class ArtistTrack(
    val id: String,
    val title: String,
    /** "3,892,104,220 streams" — sudah lewat [R.string.artist_streams]. */
    val streamsText: String,
    val seed: Int,
    val isPlaying: Boolean = false,
)

data class ArtistAlbum(
    val id: String,
    val title: String,
    /** "2020 • Album" — sudah lewat [R.string.artist_release_meta]. */
    val meta: String,
    val seed: Int,
)

@Composable
fun sampleArtistProfileUiState(): ArtistProfileUiState {
    // Angka 112.419.802 dan jumlah stream adalah data contoh, bukan teks UI.
    return ArtistProfileUiState(
        name = stringResource(R.string.sample_artist_the_weeknd),
        listenersText = stringResource(R.string.artist_monthly_listeners, "112,419,802"),
        heroSeed = 3,
        isFollowing = true,
        tracks = listOf(
            ArtistTrack(
                "a1",
                stringResource(R.string.sample_track_blinding_lights),
                stringResource(R.string.artist_streams, "3,892,104,220"),
                seed = 0,
                isPlaying = true,
            ),
            ArtistTrack(
                "a2",
                stringResource(R.string.sample_track_starboy),
                stringResource(R.string.artist_streams, "2,940,118,504"),
                seed = 1,
            ),
            ArtistTrack(
                "a3",
                stringResource(R.string.sample_track_save_your_tears),
                stringResource(R.string.artist_streams, "2,180,450,119"),
                seed = 2,
            ),
            ArtistTrack(
                "a4",
                stringResource(R.string.sample_track_die_for_you),
                stringResource(R.string.artist_streams, "1,980,240,715"),
                seed = 3,
            ),
        ),
        chips = listOf(
            stringResource(R.string.artist_tab_popular_releases),
            stringResource(R.string.artist_tab_albums),
            stringResource(R.string.artist_tab_singles_eps),
        ),
        selectedChip = 0,
        albums = listOf(
            ArtistAlbum(
                "d1",
                stringResource(R.string.sample_album_after_hours_deluxe),
                stringResource(R.string.artist_release_meta, "2020"),
                seed = 2,
            ),
            ArtistAlbum(
                "d2",
                stringResource(R.string.sample_album_dawn_fm),
                stringResource(R.string.artist_release_meta, "2022"),
                seed = 0,
            ),
            ArtistAlbum(
                "d3",
                stringResource(R.string.sample_album_starboy),
                stringResource(R.string.artist_release_meta, "2016"),
                seed = 1,
            ),
            ArtistAlbum(
                "d4",
                stringResource(R.string.sample_album_beauty_behind),
                stringResource(R.string.artist_release_meta, "2015"),
                seed = 3,
            ),
        ),
    )
}
