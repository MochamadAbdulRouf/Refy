// FontVariation masih eksperimental di Compose; satu-satunya alasan pemakaiannya
// adalah font variable (satu berkas untuk 5 berat). Kalau API-nya berubah, yang
// perlu diubah hanya fungsi `jakarta()` di bawah.
@file:OptIn(ExperimentalTextApi::class)

package io.github.mochamadabdulrouf.refy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.mochamadabdulrouf.refy.R

/**
 * Tipografi Refy — satu keluarga: **Plus Jakarta Sans** (`docs/DESIGN.md` §2).
 *
 * Berkas font yang dibundel adalah **variable font** (`fvar`, sumbu `wght`), jadi
 * tiap tingkat memakai berkas yang sama dengan `FontVariation.Settings`.
 * Butuh API 26+ — minSdk proyek ini 26, jadi aman.
 *
 * Lisensi font: SIL Open Font License 1.1, disalin di
 * `app/src/main/assets/licenses/PlusJakartaSans-OFL.txt`.
 *
 * Angka (ukuran / tinggi baris / tracking / berat) diambil apa adanya dari
 * `fontSize` di blok `tailwind.config` kanon `home-screen-243ea0d8.html`.
 * Tracking `em` dikonversi ke sp: `em × ukuran`.
 */
object RefyType {

    private fun jakarta(weight: Int) = Font(
        resId = R.font.plus_jakarta_sans,
        weight = FontWeight(weight),
        style = FontStyle.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
    )

    private val JakartaFamily = FontFamily(
        jakarta(400),
        jakarta(500),
        jakarta(600),
        jakarta(700),
        jakarta(800),
    )

    /** 40 / 48 · −0.03em · 800 — judul hero (hanya layar pemutar penuh). */
    val displayHero = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.W800,
        fontSize = 40.sp, lineHeight = 48.sp, letterSpacing = (-1.2).sp,
    )

    /** 32 / 40 · −0.02em · 700 — judul bagian besar (tablet). */
    val headlineLg = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.W700,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = (-0.64).sp,
    )

    /** 26 / 34 · −0.02em · 700 — judul layar di ponsel. */
    val headlineLgMobile = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.W700,
        fontSize = 26.sp, lineHeight = 34.sp, letterSpacing = (-0.52).sp,
    )

    /** 22 / 28 · −0.015em · 600 — judul bagian (home, album). */
    val headlineMd = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.W600,
        fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.33).sp,
    )

    /** 18 / 24 · −0.01em · 600 — sub-judul bagian. */
    val headlineSm = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.W600,
        fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = (-0.18).sp,
    )

    /** 16 / 24 · −0.005em · 500 — teks isi utama. */
    val bodyLg = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.W500,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = (-0.08).sp,
    )

    /** 14 / 20 · 400 — metadata, deskripsi. */
    val bodyMd = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.W400,
        fontSize = 14.sp, lineHeight = 20.sp,
    )

    /** 12 / 16 · +0.01em · 400 — keterangan tambahan. */
    val bodySm = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.W400,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.12.sp,
    )

    /** 14 / 18 · +0.01em · 600 — label tombol. */
    val labelLg = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.W600,
        fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = 0.14.sp,
    )

    /** 12 / 16 · +0.02em · 600 — label chip, tab. */
    val labelMd = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.W600,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.24.sp,
    )

    /**
     * 10 / 12 · +0.06em · 700 — label tab nav & badge. **HURUF BESAR**:
     * huruf kapitalnya diurus pemanggil (`uppercase()`), bukan di sini, supaya
     * string aslinya tetap utuh untuk i18n.
     */
    val labelXs = TextStyle(
        fontFamily = JakartaFamily, fontWeight = FontWeight.W700,
        fontSize = 10.sp, lineHeight = 12.sp, letterSpacing = 0.6.sp,
    )
}

/**
 * Skala Material 3 dipetakan ke tingkat di atas. Tingkat yang tidak punya padanan
 * langsung (`displayHero`, `labelXs`) tetap dipakai lewat [RefyType], bukan lewat
 * `MaterialTheme.typography`.
 */
val RefyTypography = Typography(
    headlineLarge = RefyType.headlineLg,
    headlineMedium = RefyType.headlineMd,
    headlineSmall = RefyType.headlineSm,
    bodyLarge = RefyType.bodyLg,
    bodyMedium = RefyType.bodyMd,
    bodySmall = RefyType.bodySm,
    labelLarge = RefyType.labelLg,
    labelMedium = RefyType.labelMd,
    titleLarge = RefyType.headlineLgMobile,
    titleMedium = RefyType.headlineSm,
    titleSmall = RefyType.labelLg,
)
