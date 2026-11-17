package io.github.mochamadabdulrouf.refy.ui.home

import androidx.annotation.StringRes
import io.github.mochamadabdulrouf.refy.R

/**
 * Keadaan layar Home (#1) — stateless, tanpa ViewModel dulu.
 *
 * **Data di sini SEMENTARA.** Resolver baru ada di M1 dan Room di M4, jadi
 * [sample] hanya menyusun struktur yang bisa dilihat. Tidak ada satu pun nilai
 * runtime (lagu asli, jumlah pemutaran) yang dikarang di UI; judul memakai
 * resource `sample_*` yang akan dibuang.
 */
data class HomeUiState(
    @StringRes val greetingRes: Int,
    val filters: List<HomeFilter>,
    val selectedFilter: HomeFilter,
    val dailyMix: List<HomeItem>,
    val madeForYou: List<HomeItem>,
    val trending: List<HomeItem>,
)

enum class HomeFilter(@StringRes val labelRes: Int) {
    All(R.string.home_filter_all),
    Music(R.string.home_filter_music),
}

/** Satu kartu di Home. [seed] hanya menentukan gradien artwork sementara. */
data class HomeItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val badge: String? = null,
    val seed: Int = 0,
)

/**
 * Sapaan menurut jam perangkat — tanpa nama pengguna (D14: v0.1 tanpa login).
 * Dipisah dari Composable supaya bisa diuji tanpa UI.
 */
fun greetingResFor(hourOfDay: Int): Int = when (hourOfDay) {
    in 5..11 -> R.string.home_greeting_morning
    in 12..17 -> R.string.home_greeting_afternoon
    else -> R.string.home_greeting_evening
}