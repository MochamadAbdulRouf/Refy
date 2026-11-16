### Refy App

Stack: Kotlin 2.x · Jetpack Compose + Material 3 · AndroidX Media3 (ExoPlayer +
MediaSessionService) · Hilt (KSP) · Room · DataStore · OkHttp +
kotlinx.serialization · minSdk 26 / target+compileSdk 35 · lisensi GPL-3.0-or-later

Repo kode: `D:\Coding\Refy`
Application ID: `io.github.mochamadabdulrouf.refy` (**PERMANEN**, D5)

Tahap sekarang: **M0 berjalan — Fase 0 (scaffold Gradle) selesai 2026-11-16.**
`assembleDebug` + `lintDebug` hijau, APK debug terpasang di perangkat uji. Berikutnya
Fase 1 (tema `NocturneTheme`) — urutan lengkap di `PLAN.md`. Desain Stitch tersinkron
(18 layar UI + 2 aset = 17 layar desain unik + 1 artefak), PRD v0.2, 7 flow,
16 diagram, inventaris i18n. Keputusan D1–D13 tertutup; D14–D16 terbuka dengan default.

## Aturan di folder ini

- `index.md` selalu mencerminkan status terbaru
- Keputusan teknis dicatat di `keputusan.md`: tanggal, pilihan, alasan, alternatif yang ditolak
- Tugas selesai dicentang di `backlog.md`, jangan dihapus
- **Sebelum memakai dokumen sebagai acuan implementasi, jalankan
  `python scripts/check-consistency.py` — harus exit 0.** Alat itu memeriksa
  nomor layar, ukuran tata letak, hitungan snapshot, status keputusan, kunci
  i18n, dan `.gitignore`. Kalau merah, dokumennya yang salah, bukan alatnya.
  Untuk membuktikan alat itu masih bisa gagal: `python scripts/test-gates.py`.
- **Repo ini sekarang repo kode + dokumen.** Kode ada di `app/` (Kotlin, Compose,
  Hilt); dokumen di `docs/`, `flows/`, `*.md`.
- **Rencana implementasi UI ada di `PLAN.md`** (6 fase + peta berkas snapshot per
  layar). Kerjakan **satu layar per langkah**: PNG dulu, HTML hanya lewat pencarian
  terarah (`grep -o`), jangan `Read` HTML penuh — berkasnya besar. Centang baris di
  `PLAN.md` §5 setelah tiap layar.
- **Build:** `./gradlew :app:assembleDebug` (CI: `./gradlew lintDebug testDebugUnitTest assembleDebug`).
  Mesin ini RAM-nya 7,4 GB — `gradle.properties` sudah konservatif (`-Xmx1280m`,
  `workers.max=1`). Jangan naikkan heap tanpa memeriksa RAM bebas; `-Xmx4g` sudah
  terbukti membunuh daemon JVM. Kalau perlu: `--max-workers=1`.
- **Versi dipatok, jangan "diperbarui" tanpa alasan:** AGP 9.5.1 · Gradle 9.9.1 ·
  Kotlin 2.3.20 · KSP 2.3.14 · Hilt 2.60.1 · Compose BOM 2025.06.01 · AndroidX
  versi `minCompileSdk ≤ 35`. AGP 9 memakai **built-in Kotlin** — plugin
  `org.jetbrains.kotlin.android` jangan dipasang lagi. Alasan di `keputusan.md` 2026-11-16.
- `.rea/` (806 MB, analisis APK EchoMusic) **tidak di-commit** — bukti M1,
  tersimpan lokal saja bila perlu.
- Sumber angka desain: `docs/DESIGN.md` §1 (warna, Kubu B) · §3 (ukuran) ·
  §9 (risiko blur). Jangan salin hex Kubu A dari HTML Stitch.