# PLAN — Implementasi UI dari desain Stitch

Status: **rencana, menunggu persetujuan.** Belum ada kode ditulis.
Dibuat: 2026-11-15.

Gerbang dokumen: `python scripts/check-consistency.py` → **HIJAU, exit 0** (A–L lolos,
15 dokumen diperiksa, 58 berkas akan di-stage `git add -A`, 0 dari `.rea/`).

Sumber kebenaran, dalam urutan prioritas:

1. `docs/DESIGN.md` §1 (token Kubu B) · §3 (ukuran) · §9 (risiko blur)
2. `.stitch/electric-purple/*.png` (tata letak nyata)
3. `.stitch/electric-purple/*.html` (hanya bila PNG tidak cukup jelas — dibaca **petik**, bukan penuh)
4. `docs/I18N-KEYS.md` (161 kunci, 11 layar MVP)

---

## 0. Aturan kerja selama implementasi UI

Perintah pemakai, mengikat:

- **Jangan baca semua `.stitch` sekaligus.** Berkas HTML besar.
- **Satu layar per langkah.** PNG dulu. HTML hanya bila perlu detail, dan hanya
  lewat pencarian terarah (`grep -o` kelas/ukuran), bukan `Read` penuh.
- **Token diekstrak sekali** dari **1 HTML representatif** (`home-screen-243ea0d8`,
  kanon D11) lalu dipakai ulang. Token yang sudah ada di `docs/DESIGN.md` §1
  dipakai apa adanya — tidak diekstrak ulang dari HTML, hanya diverifikasi.
- **Setelah tiap layar selesai:** centang barisnya di §5 dokumen ini + catatan singkat
  (ukuran yang dipakai, token, kunci i18n, penyimpangan).

Aturan tetap dari `CLAUDE.md`:

- Semua warna lewat token tema — **nol hex di Composable**.
- Semua string lewat resource i18n (English penuh, D6) — nol teks hardcoded.
- Palet **Kubu B** (D8): canvas `#09090B`, surface `#141418`, accent `#8B5CF6`.
  Hex Kubu A dari HTML Stitch **tidak boleh disalin** (gerbang C mengawasinya).
- Tema **dark-only** (D12). Tidak ada token light.
- Bottom nav = **pil mengambang** (D11), kanon `home-screen-243ea0d8`.
- Label tab bahasa Inggris: `Create`, bukan `Buat` (D6).

---

## 1. Peta berkas desain ↔ nomor layar ↔ backlog M3

`D7` = daftar MVP terkunci (11 layar). `M3` = baris yang sudah ada di `backlog.md`.
Nomor mengikuti `docs/PRD.md` §9.

**Keputusan pemakai (P2, 2026-11-16):** ada **17 layar desain unik** (HTML mobile
screens) di proyek ini. **Ketujuhnya belas masuk cakupan implementasi** — tidak ada
yang dibuang sebagai "sisa". Kolom Prioritas: `MVP-1` = urutan D7 (11 layar terkunci,
dikerjakan lebih dulu), `LANJUT` = 6 layar sisanya setelah #16 selesai.

| # | Nama layar (PRD §9) | Berkas snapshot (`.stitch/electric-purple/`) | D7 | Prioritas | Status UI |
|---|---|---|---|---|---|
| 1 | Home Screen | `home-screen-243ea0d8` | ✅ | MVP-1 | [ ] |
| 2 | Active Search Results | `active-search-results-13c3c6cd` | ✅ | MVP-4 | [ ] |
| 3 | Library & Search Screen (Discover) | `library-search-screen-3a4eb7d9` | ✅ | MVP-2 | [ ] |
| 4 | Your Library | `your-library-be4f34bd` | ✅ | MVP-3 | [ ] |
| 5 | Album Detail & Tracklist | `album-detail-tracklist-76a16cec` | ✅ | MVP-5 | [ ] |
| 6 | Album Detail - After Hours | `album-detail-after-hours-f4bb6b8c` | — | LANJUT | [ ] varian #5 — dipakai sebagai uji tema Kubu B penuh |
| 7 | Playlist Detail - Late Night Drives | `playlist-detail-late-night-drives-2f7236b9` | — | LANJUT | [ ] butuh data playlist (M4) |
| 8 | Artist Profile | `artist-profile-ebadb5ac` | ✅ | MVP-6 | [ ] |
| 9 | Now Playing Screen | `now-playing-screen-57a3bcf2` | ✅ | MVP-7 | [ ] |
| 10 | Playback Queue | `playback-queue-a12c7d72` | ✅ | MVP-8 | [ ] |
| 11 | Expanded Live Lyrics | `expanded-live-lyrics-002312fb` | — | LANJUT | [ ] data lirik belum ada (v0.2) — render shell + keadaan kosong |
| 12 | Track Options Bottom Sheet | `track-options-bottom-sheet-23b6e965` | ✅ | MVP-9 | [ ] |
| 13 | Add to Playlist | `add-to-playlist-61e27485` | — | LANJUT | [ ] **cara sendiri (P3)** — sheet sederhana: daftar playlist + "New playlist" |
| 14 | Create Playlist | `create-playlist-156d626d` | ✅ | MVP-10 | [ ] |
| 15 | User Profile & Audio Settings | `user-profile-audio-settings-58443188` | — | LANJUT | [ ] pintu masuk ke #16 |
| 16 | Settings | `settings-d60055b1` | ✅ | MVP-11 | [ ] |
| 17 | Downloads Management | `downloads-management-b26aa75b` | — | LANJUT | [ ] tombol unduh tanpa mesin unduh (M4/v0.2) — render keadaan kosong |
| 18 | Nocturne Music Streaming Prototype | `nocturne-music-streaming-prototype-a78bba57` | — | — | **artefak, jangan diimplementasikan** |

Aset (2, bukan layar): `image-png-88043631.png` (454×320, referensi gaya tab bar),
`modern-stylish-portrait-avatar-of-a-young-cool-creative-pers-5952a93b.png` (avatar).
**Bukan aset produksi** — hanya acuan; avatar final pakai placeholder, ikon final di M5.

Non-layar yang ikut M3: mini-player (56px), bottom nav pil, remap Kubu B, tema.

---

## 2. Urutan pengerjaan

Fase diberi urutan supaya tiap langkah punya prasyarat yang sudah selesai.

### Fase 0 — Blokir: scaffold M0 *(perlu izin eksplisit)*

`CLAUDE.md`: repo ini repo dokumen, **jangan scaffold Gradle tanpa instruksi eksplisit**.
UI tidak bisa dikompilasi tanpa ini. Isi Fase 0 = baris M0 di `backlog.md`:
scaffold Gradle (`applicationId io.github.mochamadabdulrouf.refy`, minSdk 26 /
target+compile 35), Hilt (D13), Compose + M3, CI, APK debug.

### Fase 1 — Token & tema (1 langkah, dipakai semua layar)

1. Verifikasi 1 HTML representatif (`home-screen-243ea0d8`) dengan `grep -o` untuk
   nilai warna/radius/spacing/blur yang nyata. Bandingkan dengan `docs/DESIGN.md` §1.
   Selisih → catat di `PLAN.md` + `keputusan.md`, jangan diam-diam.
2. Tulis `ui/theme/`: `Color.kt`, `Type.kt`, `Shape.kt`, `Dimens.kt`, `Theme.kt`
   (`NocturneTheme`) dari tabel Kubu B. Ukuran kaca dari §3.
3. Simpan hasil ekstraksi sebagai tabel di `PLAN.md` §4 supaya layar berikutnya
   tidak perlu membuka HTML lagi.

**Bukti selesai:** nol hex di luar `ui/theme/`; `analyze-snapshot.py` tidak menemukan
token Kubu A di kode.

### Fase 2 — Kerangka & komponen bersama

Urutan: komponen dulu, layar kemudian — nav pil dan mini-player muncul di hampir
semua layar, jadi salah bentuk = 11 layar salah.

1. `BottomNavPill` (D11: `bottom-4`, `inset-x-4`, `max-w-sm`, `h-[68px]`,
   `rounded-full`, `bg-[#121621]/85`, blur 30px, border putih 1–1.5px, glow).
2. `MiniPlayer` (56px, melayang **88–92px** di atas dasar).
3. `GlassSurface` — **prioritas naik**: nav pil dan mini-player berjarak <20px,
   dua rona terang berdampingan = pemicu pita blur Mali paling mudah.
   Wajib: penyangga opaque sebelum blur + jalur mundur tanpa blur.
4. `RefyNavHost` + `Destinations` (4 tab: Home / Discover / Library / Create).
5. Komponen daftar: `TrackRow`, `ChipRow`, `SectionHeader`, `AlbumCard`, `ArtistCard`.

**Bukti selesai:** satu `ScaffoldShell` yang membungkus nav + mini-player, 4 tab
berpindah, tanpa layar isi.

### Fase 3 — Layar tab (inti MVP)

| Urut | Layar | Alasan urutan |
|---|---|---|
| 1 | #1 Home Screen | kanon D11, acuan token & nav; paling banyak komponen dipakai ulang |
| 2 | #3 Library & Search (Discover) | satu berkas dua tab — memaksa `ChipRow` + grid genre benar |
| 3 | #4 Your Library | tab Library, daftar 5 kategori |
| 4 | #2 Active Search Results | butuh `ChipRow` dari #3 dan `TrackRow` dari #1 |

### Fase 4 — Detail & pemutar

| Urut | Layar | Catatan |
|---|---|---|
| 5 | #5 Album Detail & Tracklist | dasar untuk #8 |
| 6 | #8 Artist Profile | memakai ulang header dari #5 |
| 7 | #9 Now Playing Screen | kaca terbesar; risiko blur tertinggi setelah nav |
| 8 | #10 Playback Queue | multi-pilih checkbox + drag handle |
| 9 | #12 Track Options Bottom Sheet | `ModalBottomSheet` M3 + kunci `options.*` |

### Fase 5 — Sisa MVP + penutup

| Urut | Layar | Catatan |
|---|---|---|
| 10 | #14 Create Playlist | menulis ke Room `playlists` (M4); D16: tolak duplikat |
| 11 | #16 Settings | tanpa bottom nav; kunci `settings.*` sudah ada di inventaris |

### Fase 6 — Layar lanjutan (6 layar, setelah MVP 11 selesai)

Urutan dipilih supaya yang butuh data datang belakangan:

| Urut | Layar | Prasyarat |
|---|---|---|
| 12 | #6 Album Detail - After Hours | cukup #5 — varian; sekaligus uji layar Kubu B penuh |
| 13 | #15 User Profile & Audio Settings | pintu masuk ke #16; butuh #16 sudah ada |
| 14 | #13 Add to Playlist | cara sendiri (P3); idealnya setelah Room `playlists` (M4) |
| 15 | #7 Playlist Detail | Room `playlists` + `playlist_items` (M4) |
| 16 | #17 Downloads Management | render keadaan kosong — mesin unduh belum ada |
| 17 | #11 Expanded Live Lyrics | render shell + keadaan kosong — data lirik v0.2 |

Layar 16 & 17 dirender **tanpa** data, dengan keadaan kosong yang jujur ("Belum ada
unduhan" / "Lirik belum tersedia"). Jangan bikin data palsu supaya tampak penuh.

Penutup:

- Audit i18n: 161 kunci di `docs/I18N-KEYS.md` terpakai 100 %, nol hardcoded.
- Audit kaca: jalur mundur tanpa blur teruji di perangkat Mali.
- Audit token: nol hex di luar `ui/theme/`.
- `python scripts/analyze-snapshot.py` + `check-consistency.py` hijau.
- Centang baris M3 di `backlog.md` (jangan hapus).

---

## 3. Struktur folder/komponen yang diusulkan

Ditaruh di bawah `app/src/main/java/io/github/mochamadabdulrouf/refy/` (Fase 0):

```
ui/
  theme/        Color.kt  Type.kt  Shape.kt  Dimens.kt  Theme.kt      ← Fase 1
  component/    GlassSurface.kt  BottomNavPill.kt  MiniPlayer.kt
                TrackRow.kt  ChipRow.kt  SectionHeader.kt
                AlbumCard.kt  ArtistCard.kt  ArtworkImage.kt         ← Fase 2
  shell/        ScaffoldShell.kt  RefyNavHost.kt  Destinations.kt    ← Fase 2
  home/         HomeScreen.kt  HomeViewModel.kt  HomeUiState.kt
  discover/     DiscoverScreen.kt  ...
  library/      LibraryScreen.kt  ...
  search/       SearchResultsScreen.kt  ...
  album/        AlbumDetailScreen.kt  ...
  artist/       ArtistProfileScreen.kt  ...
  player/       NowPlayingScreen.kt  ...
  queue/        QueueScreen.kt  ...
  options/      TrackOptionsSheet.kt  ...
  create/       CreatePlaylistScreen.kt  ...
  settings/     SettingsScreen.kt  ...
```

Aturan tiap fitur:

- `*Screen.kt` **stateless** — terima `UiState` + lambda, tidak menyentuh ViewModel
  langsung. Wajib punya `@Preview` (tema gelap) supaya bisa diperiksa tanpa emulator.
- `*ViewModel.kt` Hilt `@HiltViewModel`, satu `UiState` immutable per layar.
- Preview memakai `@Preview` + `NocturneTheme`; pratinjau = jalur tercepat membandingkan
  hasil dengan PNG tanpa membaca HTML lagi.
- Berkas data/domain (Room, resolver) **tidak** masuk fase ini — M1/M4.

---

## 4. Token (diisi di Fase 1)

*Kosong sampai Fase 1 dijalankan.* Sumber awal: `docs/DESIGN.md` §1 (Kubu B),
§3 (ukuran: nav pil 68px, mini-player 56px, offset 88–92px, blur nav 30px,
blur mini-player 36px).

Format yang akan diisi: nama token · nilai · asal (DESIGN.md §x / HTML grep) · dipakai di.

---

## 5. Catatan per layar (diisi setelah tiap layar selesai)

Satu baris per layar: tanggal · ukuran yang dipakai · token khusus · kunci i18n ·
penyimpangan dari desain + alasan.

*Belum ada.*

---

## 6. Keputusan pemakai (dijawab 2026-11-16)

| # | Pertanyaan | Jawaban | Akibat ke rencana |
|---|---|---|---|
| **P1** | Boleh scaffold Gradle M0? | **Boleh** | Fase 0 dibuka; `CLAUDE.md` diperbarui: repo ini kini repo kode + dokumen |
| **P2** | #15 masuk MVP atau sisa baris? | **17 layar desain unik** ada di proyek, semuanya bagian cakupan | §1 kolom Prioritas; Fase 6 baru (6 layar lanjutan); D7 tetap urutan MVP |
| **P3** | #13 Add to Playlist? | **Bikin sendiri sesuai cara saya** | masuk Fase 6 urut 14: daftar playlist + "New playlist" |
| **P4** | Irama commit? | **Tiap fase**, dan beri tahu dulu sebelum commit | commit di akhir tiap fase; `PLAN.md` + `backlog.md` ikut di commit itu |

Catatan tambahan pemakai: kalau layar tidak ada di snapshot, cek MCP Stitch langsung
(server `stitch` terhubung). Dipakai hanya bila ada layar baru/hilang, bukan untuk
membaca ulang 17 layar yang sudah ada.

### Status Fase

| Fase | Isi | Status |
|---|---|---|
| 0 | Scaffold Gradle M0 | **[x] selesai 2026-11-16** |
| 1 | Token & tema | [ ] berikutnya |
| 2 | Kerangka & komponen | [ ] |
| 3 | Tab: #1 #3 #4 #2 | [ ] |
| 4 | Detail & pemutar: #5 #8 #9 #10 #12 | [ ] |
| 5 | #14 #16 + penutup MVP | [ ] |
| 6 | Lanjutan: #6 #15 #13 #7 #17 #11 | [ ] |

### Fase 0 — catatan hasil (2026-11-16)

Yang jadi, semua diverifikasi dengan perintah bukan tafsiran:

| Bukti | Hasil |
|---|---|
| `./gradlew :app:assembleDebug` | HIJAU 3m28s → `app/build/outputs/apk/debug/app-debug.apk` **11,2 MB** |
| `./gradlew lintDebug` | HIJAU; `testDebugUnitTest` NO-SOURCE (belum ada uji) |
| `adb install -r` + `am start` | terpasang & jalan (`pidof` → PID) |
| `LICENSE` | teks GPL resmi dari gnu.org (674 baris), bukan diketik dari ingatan |
| `NOTICE` · `README.md` · `.github/workflows/ci.yml` | ditulis |
| `scripts/check-consistency.py` | HIJAU exit 0 setelah semua berkas baru |

Empat build gagal sebelum hijau — tiap kegagalan mengubah keputusan, dan semuanya
dicatat di `keputusan.md` 2026-11-16:

1. AGP 8.13.1 + Hilt 2.60.1 → _"Hilt Android Gradle plugin is only compatible with
   AGP 9.0.0 or higher"_. Turun ke Hilt 2.59.2 → **pesan identik** ⇒ AGP 9 wajib.
2. AGP 9 + `org.jetbrains.kotlin.android` → plugin itu **tidak boleh ada lagi**
   (AGP 9 punya built-in Kotlin). Dihapus dari root + modul; `jvmTarget` tidak perlu.
3. compileSdk 35 vs AndroidX terbaru → 23 kegagalan `minCompileSdk 36/37`.
   Versi dipilih dengan membaca `aar-metadata.properties` per AAR, bukan dari ingatan.
4. Daemon JVM mati: RAM mesin **7,4 GB total, ~1,1 GB bebas** saat build →
   _"Native memory allocation (mmap) failed to map 214958080 bytes … G1 virtual space"_.
   `gradle.properties` dipatok konservatif (`-Xmx1280m`, tanpa parallel, 1 worker).

Belum: tema `NocturneTheme` (Fase 1). CI belum pernah jalan di GitHub — baru ditulis.
Install ke perangkat memakai adb wireless perangkat uji yang sudah terpasang.


### Lingkungan mesin (diperiksa 2026-11-16, diperbarui setelah Fase 0)

- JDK: Temurin **17.0.20.1** (AGP 9 minimum JDK 17) ✅
- Android SDK: `%LOCALAPPDATA%\Android\Sdk` — platform `android-34/35/36/36.1`,
  build-tools `35.0.0/36.0.0/36.1.0/37.0.0`. **Tidak ada `cmdline-tools`** ⇒
  `sdkmanager` tidak tersedia; jangan berencana memasang platform baru dari skrip.
  `ANDROID_HOME` kosong ⇒ wajib `local.properties` berisi `sdk.dir` (tidak di-commit).
- Gradle: **wrapper 9.9.1 ikut repo** (`gradle/wrapper/gradle-wrapper.jar` di-commit).
  Gradle CLI tidak ada di PATH. `gradle-9.1-*` di `~/.gradle/wrapper/dists` = unduhan
  gagal; versi itu tidak pernah dirilis.
- Versi pakai: AGP **9.5.1** · Kotlin **2.3.20** · KSP **2.3.14** · Hilt **2.60.1** ·
  Compose BOM **2025.06.01**. **AGP 9 wajib** (Hilt 2.59+ menolak AGP 8); AndroidX
  dipatok ke versi `minCompileSdk ≤ 35` — jangan naikkan satu per satu.
- RAM: **7,4 GB total**. Build dengan `-Xmx4g` + parallel membunuh daemon JVM.
  `gradle.properties` disetel `-Xmx1280m`, `workers.max=1`, `parallel=false`.
  Bangun saat RAM bebas > 2 GB; pakai `--max-workers=1`.
- Perangkat uji: tersambung adb wireless; `adb install -r` + `am start` berhasil.

