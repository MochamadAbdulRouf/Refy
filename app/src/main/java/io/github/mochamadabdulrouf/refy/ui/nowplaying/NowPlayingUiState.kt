package io.github.mochamadabdulrouf.refy.ui.nowplaying

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.mochamadabdulrouf.refy.R

/**
 * Keadaan layar #9 Now Playing.
 *
 * **Data SEMENTARA** — posisi/durasi, keluaran audio, dan lirik baru nyata
 * setelah pemutar (M2) dan resolver (M1). Teks lirik adalah *data*, bukan teks
 * UI (`docs/I18N-KEYS.md` §10).
 *
 * Kartu artwork menampilkan "poster" lagu: satu tile artwork, judul album
 * ([artworkCaption]), judul lagu ([artworkTitle]), dan progres kecil sendiri.
 * PNG kanon memuat poster itu di dalam kartu; HTML kanon hanya punya kotak
 * kosong — PNG yang diikuti. Semua nilainya data, bukan i18n.
 */
data class NowPlayingUiState(
    val title: String,
    val artist: String,
    /** "Violet Nights" — nama playlist sumber. */
    val playlistName: String,
    val isExplicit: Boolean,
    val isFavorite: Boolean,
    val isPlaying: Boolean,
    val positionMs: Long,
    val durationMs: Long,
    val isShuffleOn: Boolean,
    val isRepeatOn: Boolean,
    /** "AirPods Max • Violet Lounge" — data contoh keluaran audio. */
    val audioOutput: String,
    /** Teks lirik per baris; kosong = kartu lirik disembunyikan. */
    val lyrics: List<String>,
    /** Indeks baris lirik yang disorot (karaoke). */
    val activeLyricIndex: Int,
    val artworkSeed: Int,
    /** "SYNTHESIZED DREAMS" — baris kecil di poster kartu artwork. */
    val artworkCaption: String,
    /** "AURORA_9" — judul di poster kartu artwork. */
    val artworkTitle: String,
    /** Progres kecil di dalam poster; terpisah dari scrubber utama. */
    val artworkPositionMs: Long,
    val artworkDurationMs: Long,
) {
    /** Posisi relatif 0..1 untuk scrubber. */
    val progress: Float
        get() = if (durationMs <= 0) 0f else (positionMs.toFloat() / durationMs).coerceIn(0f, 1f)

    /** Posisi relatif 0..1 untuk progres kecil di poster artwork. */
    val artworkProgress: Float
        get() = if (artworkDurationMs <= 0) {
            0f
        } else {
            (artworkPositionMs.toFloat() / artworkDurationMs).coerceIn(0f, 1f)
        }
}

/** Format `m:ss` — cukup untuk durasi lagu; jam tidak diperlukan. */
fun formatTrackTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

@Composable
fun sampleNowPlayingUiState(): NowPlayingUiState {
    return NowPlayingUiState(
        title = stringResource(R.string.sample_trending_2),
        artist = stringResource(R.string.sample_now_playing_artist),
        playlistName = stringResource(R.string.sample_playlist_violet_nights),
        isExplicit = true,
        isFavorite = true,
        isPlaying = true,
        // 2:14 / 3:48 → sisa -1:34, sama dengan PNG kanon.
        positionMs = 134_000,
        durationMs = 228_000,
        isShuffleOn = true,
        isRepeatOn = false,
        audioOutput = stringResource(R.string.sample_audio_output_airpods_max),
        // Data contoh supaya kartu lirik (= bagian kanon #9) tampil; tanpa
        // data lirik nyata, kartu ini tidak dirender (D15 — #11 = v0.2).
        lyrics = listOf(
            stringResource(R.string.sample_lyric_1),
            stringResource(R.string.sample_lyric_2),
            stringResource(R.string.sample_lyric_3),
        ),
        // Baris kedua = baris aktif (gradien) di PNG kanon.
        activeLyricIndex = 1,
        artworkSeed = 0,
        artworkCaption = stringResource(R.string.sample_artwork_caption),
        artworkTitle = stringResource(R.string.sample_artwork_title),
        // 1:34 / 3:58 seperti poster di PNG kanon.
        artworkPositionMs = 94_000,
        artworkDurationMs = 238_000,
    )
}