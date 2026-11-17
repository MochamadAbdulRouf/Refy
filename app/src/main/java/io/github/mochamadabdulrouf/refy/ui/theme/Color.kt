package io.github.mochamadabdulrouf.refy.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Token warna Refy — palet **Kubu B**, diputuskan D8.
 *
 * Sumber nilai: `docs/DESIGN.md` §1 "Palet mengikat" (§5–§6 untuk lapisan kaca).
 * Diukur ulang terhadap kanon `.stitch/electric-purple/home-screen-243ea0d8.html`
 * pada 2026-11-16 — hasil pengukuran + tiga selisih yang ditemukan dicatat di
 * `keputusan.md` 2026-11-16.
 *
 * ATURAN: **berkas ini satu-satunya tempat nilai warna boleh ditulis.** Composable
 * memakai `MaterialTheme.colorScheme.*` atau anggota objek ini; tidak ada hex di
 * tempat lain. Kalau butuh warna baru, tambahkan di sini dulu.
 *
 * Warna layar Kubu A di HTML Stitch (`#131315`, `#d0bcff`, `#e5e1e4`, …) **tidak
 * boleh disalin** — tabel remapnya ada di `docs/DESIGN.md` §1.
 */
object RefyColors {

    // ---- Permukaan dasar (elevasi 0–3, DESIGN §5) ----
    /** Elevasi 0 — latar halaman & area gulir. */
    val Canvas = Color(0xFF09090B)

    /** Elevasi 1 — kartu, panel sheet, kontainer dasar. */
    val Surface = Color(0xFF141418)

    /** Kartu terangkat, chip inaktif. */
    val SurfaceElevated = Color(0xFF1C1C22)

    /** Keadaan tertekan / terpilih. */
    val SurfaceHighest = Color(0xFF1F1F26)

    // ---- Teks ----
    /** Judul lagu, heading, ikon aktif. */
    val TextPrimary = Color(0xFFFFFFFF)

    /** Artis, durasi, metadata. */
    val TextMuted = Color(0xFFA1A1AA)

    /** Placeholder, label nonaktif. */
    val TextFaint = Color(0xFF71717A)

    // ---- Garis ----
    /** Garis 1px / tepi kaca. Dipakai 40–60 % sesuai DESIGN §1. */
    val Border = Color(0xFF27272A)

    /** Garis kartu: `rgba(255,255,255,.06)` (elevasi 1, DESIGN §5). */
    val StrokeSubtle = Color(0x0FFFFFFF)

    /** Garis atas chrome mengambang: `rgba(255,255,255,.12)`. */
    val StrokeChrome = Color(0x1FFFFFFF)

    /** Garis kaca mini-player: `rgba(255,255,255,.15)`. */
    val StrokeMiniPlayer = Color(0x26FFFFFF)

    // ---- Aksen ----
    /** Aksi utama, progress, keadaan sedang diputar. */
    val Accent = Color(0xFF8B5CF6)

    /** Ujung gradien terang + glow. */
    val AccentBright = Color(0xFFA855F7)

    /** Ujung gradien indigo. */
    val AccentDeep = Color(0xFF6366F1)

    /** Ikon di atas tombol accent. */
    val OnAccent = Color(0xFFFFFFFF)

    /** Latar baris yang sedang diputar: `rgba(139,92,246,.08)`. */
    val AccentRowTint = Color(0x148B5CF6)

    // ---- Galat ----
    val Error = Color(0xFFFFB4AB)
    val ErrorContainer = Color(0xFF93000A)

    // ---- Kaca (DESIGN §5–§6). Nilai alfa adalah bagian dari resep. ----
    /** Header tetap: `bg-surface/80` + blur 24px. */
    val GlassHeader = Color(0xCC141418)

    /** Pil bottom nav: `bg-[#121621]/85` + blur 30px (D11). */
    val GlassNav = Color(0xD9121621)

    /** Mini-player: lihat catatan selisih #2 di `keputusan.md` 2026-11-16. */
    val GlassMiniPlayer = Color(0xC7121621)

    /** Overlay penuh, mis. di belakang sheet: `rgba(9,9,11,.88)`. */
    val GlassOverlay = Color(0xE009090B)

    /** Jalur mundur tanpa blur (DESIGN §9 langkah 3): kaca jadi nyaris solid. */
    val GlassFallback = Color(0xF7141418)

    /** Latar chip filter inaktif: `rgba(20,20,24,.8)`. */
    val ChipInactive = Color(0xCC141418)

    /**
     * Garis pil nav. **Dikoreksi 2026-11-16 (Fase 2) dari 12 % → 60 %.**
     *
     * Bukti: sampel piksel PNG kanon `home-screen-243ea0d8.png` pada tepi pil
     * menghasilkan `(185,185,195)` di atas latar pil `(18,22,33)` — itu ≈ 70 %
     * putih, bukan 12 %. Nilai 12 % hanya muncul di kelas HTML
     * (`border-white/12`), sedangkan `docs/DESIGN.md` §5 dan §6.2 menulis
     * `rgba(255,255,255,.6)`, dan PNG setuju dengan dokumen. Rincian: `keputusan.md`
     * 2026-11-16 (Fase 2).
     */
    val NavBorder = Color(0x99FFFFFF)

    /** Sorotan tab aktif di pil nav: accent 6 % — hasil sampel PNG `(26,27,47)` di atas `(18,22,33)`. */
    val TabActiveTint = Color(0x0F8B5CF6)

    /** Rel progress bar yang belum terisi. */
    val ProgressTrack = Color(0x33FFFFFF)
}
