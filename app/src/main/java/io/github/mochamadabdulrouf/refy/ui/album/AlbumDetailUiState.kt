package io.github.mochamadabdulrouf.refy.ui.album

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.mochamadabdulrouf.refy.R

/**
 * Keadaan layar #5 Album Detail & Tracklist.
 *
 * **Data SEMENTARA** — katalog album baru ada setelah resolver (M1) dan cache
 * Room (M4). Semua judul di sini datang dari resource `sample_*`.
 */
data class AlbumDetailUiState(
    val title: String,
    val artist: String,
    val meta: String,
    val badges: List<String>,
    val artworkSeed: Int,
    /** Dua baris kecil yang tercetak di atas artwork, seperti desain. */
    val artworkPreview: List<String>,
    val recommendations: List<AlbumRecommendation>,
    val tracks: List<AlbumTrack>,
    val nowPlayingTrackId: String? = null,
)

data class AlbumRecommendation(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val seed: Int = 0,
)

data class AlbumTrack(
    val id: String,
    val number: Int,
    val title: String,
    val artist: String,
    val duration: String,
    val explicit: Boolean = false,
)

@Composable
fun sampleAlbumDetailUiState(): AlbumDetailUiState {
    val weeknd = stringResource(R.string.sample_artist_the_weeknd)
    val hardestToLoveId = "t3"
    return AlbumDetailUiState(
        title = stringResource(R.string.sample_album_after_hours),
        artist = weeknd,
        // Angka 14 adalah data contoh album, bukan teks UI.
        meta = stringResource(
            R.string.album_meta,
            stringResource(R.string.sample_album_year),
            14,
            stringResource(R.string.sample_album_duration),
        ),
        badges = listOf(
            stringResource(R.string.album_badge_lossless),
            stringResource(R.string.album_badge_atmos),
        ),
        artworkSeed = 2,
        artworkPreview = listOf(
            "01 " + stringResource(R.string.sample_track_alone_again),
            "02 " + stringResource(R.string.sample_track_too_late),
        ),
        recommendations = listOf(
            AlbumRecommendation(
                "r1",
                stringResource(R.string.sample_song_resonance),
                stringResource(R.string.sample_artist_home),
                "3:32",
                seed = 1,
            ),
            AlbumRecommendation(
                "r2",
                stringResource(R.string.sample_song_nightcall),
                stringResource(R.string.sample_artist_kavinsky),
                "4:19",
                seed = 2,
            ),
            AlbumRecommendation(
                "r3",
                stringResource(R.string.sample_song_sunset),
                stringResource(R.string.sample_artist_the_midnight),
                "5:26",
                seed = 3,
            ),
            AlbumRecommendation(
                "r4",
                stringResource(R.string.sample_song_tech_noir),
                stringResource(R.string.sample_artist_gun),
                "4:57",
                seed = 0,
            ),
        ),
        tracks = listOf(
            AlbumTrack("t1", 1, stringResource(R.string.sample_track_alone_again), weeknd, "4:10"),
            AlbumTrack("t2", 2, stringResource(R.string.sample_track_too_late), weeknd, "3:59"),
            AlbumTrack(
                hardestToLoveId,
                3,
                stringResource(R.string.sample_track_hardest_to_love),
                weeknd,
                "3:31",
                explicit = true,
            ),
            AlbumTrack("t4", 4, stringResource(R.string.sample_track_scared_to_live), weeknd, "3:11"),
            AlbumTrack(
                "t5",
                5,
                stringResource(R.string.sample_track_snowchild),
                weeknd,
                "4:07",
                explicit = true,
            ),
            AlbumTrack(
                "t6",
                6,
                stringResource(R.string.sample_track_escape_from_la),
                weeknd,
                "5:55",
                explicit = true,
            ),
            AlbumTrack(
                "t7",
                7,
                stringResource(R.string.sample_track_heartless),
                weeknd,
                "3:20",
                explicit = true,
            ),
            AlbumTrack(
                "t8",
                8,
                stringResource(R.string.sample_track_faith),
                weeknd,
                "4:43",
                explicit = true,
            ),
            AlbumTrack("t9", 9, stringResource(R.string.sample_track_blinding_lights), weeknd, "3:20"),
            AlbumTrack("t10", 10, stringResource(R.string.sample_track_in_your_eyes), weeknd, "3:57"),
        ),
        nowPlayingTrackId = hardestToLoveId,
    )
}