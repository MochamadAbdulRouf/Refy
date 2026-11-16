package io.github.mochamadabdulrouf.refy.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Ukuran Refy — `docs/DESIGN.md` §3 (spasi & grid) dan §5 (radius blur).
 *
 * Angka-angka ini **terukur dari HTML**, bukan tebakan, dan semuanya berulang di
 * banyak layar. Jangan ganti satu nilai di sini tanpa memeriksa `analyze-snapshot.py`
 * dan `check-consistency.py` — gerbang H menjaga ukuran yang sudah diputuskan
 * (nav 68px, mini-player 56px/88–92px) supaya tidak kembali basi.
 */
object RefyDimens {

    /** Spasi dari `spacing` di `tailwind.config`. */
    object Space {
        val xxs = 4.dp
        val xs = 8.dp
        val sm = 12.dp
        val md = 16.dp
        val lg = 24.dp
        val xl = 32.dp
        val xxl = 48.dp
    }

    /** Jarak tepi & gutter. */
    object Margin {
        /** `px-margin-mobile` = 1rem. */
        val mobile = 16.dp

        /** `margin` = 1.5rem, layar lebar. */
        val wide = 24.dp

        /** `gutter-mobile` — jarak antar kartu di karusel. */
        val gutter = 12.dp
    }

    /** Tinggi & ukuran elemen tetap. */
    object Size {
        /** Header tetap, `h-16`. */
        val header = 64.dp

        /** Bottom nav pil, `h-[68px]` — D11. */
        val navBar = 68.dp

        /** Mini-player, `h-14`. */
        val miniPlayer = 56.dp

        /** Baris lagu, `h-14`. */
        val trackRow = 56.dp

        /** Artwork di baris lagu, `w-10 h-10`. */
        val artworkRow = 40.dp

        /** Artwork hasil pencarian, `w-11 h-11`. */
        val artworkSearch = 44.dp

        /** Avatar di header, `w-8 h-8`. */
        val avatarHeader = 32.dp

        /** Tombol ikon, `h-11` — juga target sentuh minimum. */
        val iconButton = 44.dp

        /** Tombol utama, `h-12`. */
        val primaryButton = 48.dp

        /** Tombol putar di mini-player. */
        val playButtonMini = 36.dp

        /** Tombol putar besar (layar pemutar, kartu album). */
        val playButtonLarge = 48.dp

        /** Progress bar, 2px, menempel tepi bawah mini-player. */
        val progressBar = 2.dp

        /** Ikon di pil nav. */
        val navIcon = 22.dp
    }

    /** Radius blur kaca — `docs/DESIGN.md` §5 tabel "pemakaian blur terukur". */
    object Blur {
        /** `backdrop-blur-xl` — header tetap. */
        val header = 24.dp

        /** `backdrop-blur-[30px]` — pil nav (D11). */
        val navBar = 30.dp

        /** `blur(36px) saturate(180%)` — mini-player. */
        val miniPlayer = 36.dp

        /** `backdrop-blur-md` — badge di atas artwork. */
        val badge = 12.dp

        /** `backdrop-blur-sm` — sheet kecil. */
        val sheetSmall = 4.dp

        /** Overlay penuh, `blur(32px)` (DESIGN §5 tingkat 3). */
        val overlay = 32.dp

        /** Saturasi mini-player, `saturate(180%)`. */
        const val miniPlayerSaturation = 1.8f

        /** Alfa jalur mundur tanpa blur (DESIGN §9 langkah 3). */
        const val fallbackAlpha = 0.97f
    }

    /** Offset mengambang & jarak aman konten. */
    object Offset {
        /** Pil nav duduk `bottom-5` di kanon (HTML), D11 menyebut `bottom-4`. */
        val navBarBottom = 20.dp

        /** Mini-player melayang di atas pil: `bottom-[88px]` (kanon) … `92px`. */
        val miniPlayerBottom = 88.dp

        /** Jarak aman bawah konten: `pb-40` = 160px. Angka 88px dari manifest tidak cukup. */
        val contentBottom = 160.dp

        /** Jarak antara mini-player dan pil nav. Sengaja < 20px — lihat risiko R2. */
        val gapNavToMiniPlayer = 12.dp
    }
}
