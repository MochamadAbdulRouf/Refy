# Backlog Refy

Status: `[ ]` belum · `[~]` sedang · `[x]` selesai. Tugas selesai dicentang, tidak dihapus.

---

## Perencanaan

- [x] PRD v0.1 lengkap → `docs/PRD.md`
- [x] Peta navigasi & arsitektur → `flows/00-peta-navigasi.md`
- [x] Alur putar lagu → `flows/01-alur-putar-lagu.md`
- [x] Alur antrean + skema Room → `flows/02-alur-antrean.md`
- [x] Alur background service → `flows/03-alur-background-service.md`
- [x] Alur resolver & PO token → `flows/04-alur-resolver-dan-po-token.md`
- [x] Alur cache offline → `flows/05-alur-offline-cache.md`
- [x] Alur rilis & CI → `flows/06-alur-rilis-dan-ci.md`
- [x] Catatan keputusan teknis → `keputusan.md`
- [x] Audit Stitch MCP: identifikasi project yang ada
- [x] Temukan desain asli: **Electric Purple Music App** (`projects/9190295645942987043`)
- [x] Tarik layar read-only → `.stitch/electric-purple/` (HTML + PNG + manifest)
- [x] Ukur metrik nyata dari HTML (header, nav, baris, radius, blur)
- [x] `docs/DESIGN.md` v2 dari token asli **Nocturne Audio**
- [x] Sinkronkan PRD §5 (fitur MVP) & §9 (desain) ke desain asli
- [x] Sinkronkan `flows/00` (navigasi 4 tab: Home/Discover/Library/Create)
- [x] Render 16 diagram Mermaid → SVG (0 gagal)
- [x] **Verifikasi ulang dokumen vs Stitch hidup** → temukan 4 kesalahan, perbaiki:
      - layar bertambah 14 → **16** (album-detail-after-hours, playlist-detail-late-night-drives)
      - ternyata ada **DUA palet bercampur** (11 token vs 5 prosa), bukan satu
      - klaim "tiga layar berbagi `<title>`" salah → yang dibagi `<h1>`
      - aturan pemutus "warna → manifest" salah → dibuang, butuh **D8**
- [x] **Resync kedua (12:34 UTC)** → **17 layar UI** + 1 aset avatar:
      - +**Settings** (#16) — pengaturan aplikasi, layar sendiri
      - +**Downloads Management** (#17) — kelola unduhan
      - −*Home Screen - High Density Dark Mode* — dihapus desainer dari Stitch
      - palet bergeser: 13 kubu token, 4 kubu prosa
      - D7 diperbarui: Settings masuk MVP, Downloads tidak dipakai di v0.1
- [x] **Tarik ulang bersih (13:20 UTC)** — folder `.stitch/electric-purple/` dihapus,
      fetch ulang dari Stitch. Hasil: 18 HTML + 17 PNG.
      - `add-to-playlist.html` **berubah** (avatar profil di header dihapus)
      - entri "Nocturne Music Streaming Prototype" = **duplikat byte-identik** Home Screen, bukan layar
      - tidak ada layar dihapus; 17 layar unik tetap
- [x] **Resync ketiga (2026-11-10, `updateTime` 09:28 UTC)** — snapshot `.stitch/` disegarkan dari Stitch hidup:
      - 9 layar **berubah isi** (Your Library, Album Detail & Tracklist, Downloads Management ×2, Home Screen,
        Album Detail - After Hours, Library & Search Screen, Active Search Results, Playlist Detail, Artist Profile)
      - +1 layar UI baru: **Downloads Management** `b26aa75b` (780×2110) — **judul sama** dengan yang lama `2f6ecab6`
      - +1 aset gambar baru: `image.png` 454×320
      - 0 layar dihapus saat tarik ulang · bottom nav **didesain ulang jadi pil mengambang** (68px, blur 30px, `#121621`/85)
      - **susulan**: desainer menghapus `Downloads Management` lama (`2f6ecab6`) → snapshot jadi 20 entri = 18 layar + 2 aset
      - mini-player naik `bottom-16` → `bottom-[88–92px]`
      - palet: **17 layar** masih Kubu A di `tailwind.config`, hanya 1 (`album-detail-after-hours`) Kubu B penuh
      - label tab: 9 layar masih "Buat", 1 layar (`your-library`) sudah "Create"
      - klaim "duplikat byte-identik Home Screen" **gugur** — Home Screen berubah, artefak tidak
      - laporan selisih: `.stitch/DELTA-2026-11-10.md`
- [x] **D10** — `Downloads Management` dipilih `b26aa75b` (780×2110); versi lama `2f6ecab6` dihapus desainer dari Stitch — 2026-11-10
- [x] **D11** — bottom nav final: **PIL MENGAMBANG** (9 layar); bilah penuh hanya tersisa di artefak `nocturne-music-streaming-prototype` — 2026-11-10
- [x] Tinjau aset `image.png` (454×320) — gambar referensi tab bar glassmorphism; acuan gaya, bukan aset produksi — 2026-11-10
- [x] Perbaiki skrip snapshot — 2026-11-10: `SNAP` absolut (dulu bikin folder sampah `scripts/.stitch`), `slug()` + 8 char id layar (dua layar berjudul sama saling menimpa), PNG ikut ditarik saat layar berubah, deteksi label tab tidak lagi bergantung kelas CSS
- [x] Tambah `scripts/analyze-snapshot.py` — audit palet/nav/label tab per layar — 2026-11-10
- [x] **D5 — `applicationId` final: `io.github.mochamadabdulrouf.refy`** — akun GitHub `MochamadAbdulRouf` terverifikasi (76 repo publik, terdaftar 2024-03-16). **PERMANEN**: tidak bisa diubah setelah rilis pertama tanpa memutus jalur update — 2026-11-10
- [x] **Riset REA EchoMusic** — baca `[[Project App/refy-app/REA EchoMusic]]`, pola bebas-iklannya diturunkan jadi tugas konkret di M1.1–M1.6 — 2026-11-10
- [x] Jawab keputusan terbuka D1–D4 (§12 PRD)
- [x] Tetapkan bahasa UI: **English penuh** (D6) — kunci resource i18n
- [x] **Kunci palet: Kubu B** (D8) — canvas `#09090B`, surface `#141418`, accent `#8B5CF6`; tabel remap siap di `docs/DESIGN.md` §1. Jumlah layar Kubu A diperbarui 2026-11-10: **17 layar** (dulu disebut 13)
- [x] **Kunci daftar layar MVP: 11 layar** (D7) — #1, #2, #3, #4, #5, #8, #9, #10, #12, #14, #16

## Audit kelayakan sebelum M0 (2026-11-12)

> Ditemukan 8 penghambat + 7 cacat dokumen. Semua ditutup; rincian berbukti di
> `keputusan.md` 2026-11-12. Tanpa audit ini, Claude Code akan mulai dari nomor
> layar yang salah dan repo membengkak 806 MB.

- [x] `.rea/` (806 MB) masuk `.gitignore` — bukti M1 tetap lokal, tidak di-commit
- [x] Perbaiki negasi `.gitignore` yang mati: pola `.stitch` → `.stitch/*`, sehingga `!.stitch/...` berlaku. Kini `DESIGN.md` (manifest asli) + `DELTA-2026-11-10.md` benar-benar ikut commit
- [x] `docs/diagrams/*.mmd` dikeluarkan dari ignore (sumber diagram, ~1 KB); hanya `.svg`/`.png` turunan yang di-ignore
- [x] Perbaiki nomor layar di PRD §5 F-11..F-15 (#4→#3, #6→#5, #7→#8, #11→#12, #12/#13→#14) + `flows/01` + `flows/02`
- [x] Perbaiki ukuran basi: PRD §7.1 "nav 64px / mini-player 64px" → **68px pil / 88–92px**; `index.md` juga
- [x] **D13 — DI: Hilt** (PRD §7.1 "pilih satu" → Hilt; scaffold M0 dapat kepastian)
- [x] **D12 — tema v0.1 dark-only** (F-09 dikeluarkan dari MUST; desain tidak punya token light)
- [x] Tambah tabel Room `playlists` + `playlist_items` di `flows/02` — F-15 (MUST) sebelumnya tidak punya tempat menyimpan
- [x] Perbaiki F-01: chip filter = **Top / Songs / Albums / Artists / Playlists / Podcasts** (desain nyata, tanpa "Video")
- [x] **D1–D4 ditutup** (publik · tanpa login · tulis dari nol · Kotlin+Compose) — bukan lagi "menunggu jawaban"
- [x] **D14–D16 dibuka dengan default tertulis** (sapaan tanpa nama · fitur non-v0.1 tidak dirender · tolak duplikat playlist)
- [x] Perbaiki cacat dokumen: "10 layar Buat"→9 · "18 layar token"→17 · "19/21 entri screens.json"→18 html + 20 entri · h1 di #3,#4,#5→#2,#3,#4
- [x] Buat **`docs/I18N-KEYS.md`** — inventaris kunci string (161 awal; 168 per 2026-11-19 setelah #5 #8) untuk 11 layar MVP; jadi daftar periksa audit M5
- [x] Perluas **`scripts/check-consistency.py`** dari 6 bagian jadi **11 gerbang (A–K)** + exit code, termasuk nomor layar, ukuran, hitungan snapshot, status keputusan, kunci i18n, dan `.gitignore` lewat `git check-ignore`
- [x] Buat **`scripts/test-gates.py`** — uji negatif yang membuktikan ke-12 gerbang **bisa gagal** (gate yang tidak bisa gagal tidak memverifikasi apa pun)
- [x] Rapikan `CLAUDE.md` (masih `<isi stack Anda>`), `index.md`, dan tambah workflow dokumen di `flows/06`
- [x] Catat keputusan diagram: `.mmd` (sumber) ikut commit, `.svg`/`.png` (turunan) tidak — `keputusan.md`
- [x] Perbaiki baris `docs/DESIGN.md` §12 "11 layar perlu diremap" yang masih menulis **18 layar** (nyata: 17) + tambah 4 baris klaim-salah baru (mini-player 64px, `.mmd` di-ignore, "gate hijau = siap", nomor layar bisa dipercaya)
- [x] **Verifikasi snapshot `.stitch/` vs Stitch hidup (2026-11-12)** — `scripts/verify-docs-vs-stitch.py`: 20 entri live = 20 entri snapshot, **18/18 layar HTML byte-identik (`SAMA`)**, 0 slug hanya-di-live, 0 hanya-di-snapshot, 0 html/png hilang, 0 berkas yatim. Snapshot **SESUAI** dengan desain saat ini
- [x] Perbaiki **bug `stitch_client.py`**: error tingkat-tool MCP datang di `result.isError`, bukan `error` tingkat-atas. Tanpa cabang itu, kegagalan sesaat dikembalikan sebagai dict kosong dan pembaca menyimpulkan "proyek kosong" — persis yang terjadi (`live: 0 entri` padahal Stitch memuat 20). Sekarang memicu retry dan akhirnya `RuntimeError`
- [x] Perbaiki `verify-docs-vs-stitch.py`: identitas layar dinilai dari **slug + resource name**, bukan judul (3 layar berbagi `<h1>`, dan Stitch pernah memuat 2 layar berjudul sama — judul bukan identitas) + ringkasan + exit code
- [x] Temukan & perbaiki **`updateTime` tidak seragam**: `index.md` `09:28:11Z` · `docs/DESIGN.md` `10:13:02Z` · DELTA `09:28:11Z` · live `18:29:53Z`. Diseragamkan ke nilai live + catatan bahwa timestamp bukan bukti isi snapshot
- [x] Tambah **gerbang L** (updateTime seragam di dokumen hidup) + `scripts/test-gates.py` jadi **14 gate terbukti bisa gagal**
- [x] **Perbaiki gerbang K** — 2026-11-16: gerbang itu membandingkan daftar dokumen dengan keluaran `git add -An .`, yang hanya benar **sebelum commit pertama**. Setelah commit `9f7a36d` ia merah permanen (`K: 13 dokumen tidak ter-stage`) padahal tidak ada dokumen yang hilang. Diganti `git check-ignore` per dokumen — itu yang sebenarnya dimaksud "dokumen akan ikut commit atau tidak". Uji negatif ke-15 ditambahkan dan ia **butuh berkas baru** (`docs/Z-dokumen-uji.md`), karena git tidak menerapkan `.gitignore` pada berkas yang sudah tercatat. Hasil: **15 gate terbukti bisa gagal**- [x] Persempit pola basi `byte-identik` di gerbang A — pola lebar menangkap klaim **sah** ("HTML snapshot byte-identik dengan live") dan membuat gerbang merah palsu
- [x] Hapus skrip diagnosa sementara (`diag-flaky.py`) setelah membuktikan `list_screens` stabil 6/6 = 20 entri
- [ ] **Commit pertama** — repo ini masih **0 commit** (`git rev-list --count HEAD` → fatal). Semua ignore-rule baru belum pernah diuji oleh commit nyata, dan tidak ada riwayat versi. Jalankan `git add -A && git commit` sebelum M0. *(belum dikerjakan — menunggu izin; user belum meminta commit)*

## M0 — Fondasi (± 2–3 hari)

> **Tidak ada lagi penghambat.** `applicationId` sudah dikunci (D5); D1–D13
> tertutup; skema DB lengkap; tema diputuskan dark-only (D12); DI = Hilt (D13).
> **Sebelum menulis kode, `python scripts/check-consistency.py` wajib exit 0.**

- [x] Scaffold Gradle: `applicationId = "io.github.mochamadabdulrouf.refy"`, `namespace` sama, minSdk 26 / target+compile 35 — 2026-11-16. Toolchain nyata: AGP 9.5.1 · Gradle 9.9.1 · Kotlin 2.3.20 · KSP 2.3.14 · Hilt 2.60.1 · Compose BOM 2025.06.01. **AGP 9 wajib** karena Hilt 2.59 dan 2.60 sama-sama menolak AGP 8.x; AGP 9 memakai built-in Kotlin sehingga plugin `kotlin-android` dihapus. AndroidX dipatok ke versi `minCompileSdk ≤ 35`. Alasan + bukti: `keputusan.md` 2026-11-16
- [x] `git init` tersimpan, struktur folder Gradle dibuat — 2026-11-16 (`app/`, `gradle/`, `settings.gradle.kts`, `build.gradle.kts`, wrapper 9.9.1 ikut commit)
- [x] `LICENSE` GPL-3.0-or-later + `NOTICE` kredit NewPipeExtractor — 2026-11-16 (teks GPL diambil dari gnu.org, bukan diketik dari ingatan)
- [x] `README.md`: cara build, batasan hukum, disclaimer merek — 2026-11-16 (termasuk catatan RAM mesin 7,4 GB + `local.properties`)
- [x] Setup Compose + Material 3 + theme (token dari `docs/DESIGN.md`) — 2026-11-16: Compose + Material 3 sudah ada sejak Fase 0; **tema selesai di Fase 1** (`ui/theme/`: Color, Type, Shape, Dimens, Theme). Font Plus Jakarta Sans dibundel sebagai variable font + lisensi OFL. Tiga selisih dokumen-vs-HTML diputuskan & dicatat di `keputusan.md` 2026-11-16
- [x] Setup CI: lint + test + assembleDebug — 2026-11-16, `.github/workflows/ci.yml`. **Terbukti jalan di GitHub**: run #3 (`afd548d8`) **HIJAU**. Dua run sebelumnya gagal karena lingkungan, bukan kode: #1 `gradlew` tercatat mode 100644 → `Permission denied` exit 126; #2 metaspace 384 MiB → lint `InferredThreadDetector` gagal init. Rincian + perbaikan: `keputusan.md` 2026-11-16
- [x] APK debug terpasang di perangkat uji — 2026-11-16, `adb install -r` sukses di perangkat wireless, `MainActivity` jalan (`pidof` mengembalikan PID)

## M1 — Resolver hidup (± 1 minggu)

- [ ] Definisikan `MusicProvider` + `StreamInfo` + `ProviderRegistry`
- [ ] Implementasi `YtMusicProvider` (search + getStreamInfo)
- [ ] Provider cache dengan `expiresAt` (Room)
- [ ] Skrip health-check 10 trackId (dapat dijalankan manual)
- [ ] Uji: 20 kueri → ≥ 18 URL dapat diputar

### M1 — Penjaga "tanpa iklan" (kriteria rilis, bukan fitur opsional)

> **Ini BUKAN adblock, dan bukan "buang field iklan setelah parse".** Refy harus **tidak mampu merepresentasikan iklan** — bukan meminta lalu membuang.
> Acuan konkret: **EchoMusic 1.4.6** (fork Metrolist), sudah dibuktikan lewat REA di `[[Project App/refy-app/REA EchoMusic]]`. Ia bebas iklan bukan karena memfilter, tapi karena **model datanya tidak punya tempat untuk iklan**. Tiru pola itu; **jangan** tiru Pear Desktop (parse dulu → hapus belakangan).
>
> **Pola 4 lapis:** (1) model tipe tertutup → (2) permintaan tanpa slot iklan → (3) endpoint bersih → (4) uji penjaga yang GAGAL kalau ada yang menambah field iklan.

#### M1.1 — Model tipe tertutup (inti)

- [ ] **`PlayerResponse` Refy hanya berisi field yang dipakai.** EchoMusic memakai tepat **5**: `PlayabilityStatus`, `StreamingData`, `VideoDetails`, `PlaybackTracking`, `PlayerConfig`. Ukur dengan uji, bukan perasaan: DTO Refy tidak boleh punya field yang tidak dipakai.
- [ ] **Tidak ada tipe model untuk iklan di seluruh proyek.** Hasil pencarian harus **0** untuk: `playerAds`, `adPlacements`, `adSlots`, `adBreakHeartbeatParams`, `adParams`, `adTrackingUrl`, `googleRemarketingUrl`, `youtubeRemarketingUrl`, `ptrackingUrl`. (EchoMusic: 0 dari semuanya, di ±19.000 file smali.)
- [ ] **Serialisasi wajib "drop unknown key"** — `ignoreUnknownKeys = true` di kotlinx.serialization. **Ini lapisan yang membuat iklan tidak pernah terbentuk di memori**: server boleh mengirim blok `playerAds`, objeknya tidak akan pernah ada.
- [ ] **Jangan pakai `JsonElement`/`Map<String, Any>` untuk respons player** — itu mengembalikan seluruh respons mentah ke memori dan membatalkan penjagaan di atas.
- [ ] **Tidak ada renderer iklan di model feed.** Dari `next`/`search`/`playlist`, Refy hanya memodelkan renderer yang dipakai (track, album, playlist, artist, section). Hasil pencarian nama renderer iklan harus **0**: `adSlotRenderer`, `displayAdRenderer`, `promotedSparklesTextSearchRenderer`, `promotedSparklesWebRenderer`, `searchPyvRenderer`, `compactPromotedVideoRenderer`, `inFeedAd`, `adSlotLoggingData`.
- [ ] **Tidak ada SDK iklan.** Tidak ada AdMob / `com.google.android.gms.ads.*`, tidak ada `ACCESS_ADSERVICES_*`, tidak ada permission `AD_ID`. (EchoMusic: 0 kelas `com/google/android/gms/ads/`.)
- [ ] **`StreamInfo` tetap bersih** — hanya `url`, `mimeType`, `bitrate`, `codec`, `expiresAt`, `headers`. Field lain yang berhubungan dengan iklan = desain salah.

#### M1.2 — Permintaan tanpa slot iklan

- [ ] **Body `/youtubei/v1/player` tidak punya parameter iklan.** EchoMusic memakai tepat **9 field**: `context`, `videoId`, `playlistId`, `playbackContext`, `thirdParty`, `serviceIntegrityDimensions`, `contentCheckOk`, `racyCheckOk`, `videoCheckOk` — tanpa `params`, tanpa `adParams`. Tiru komposisi ini (nama field Refy boleh beda, tapi **tidak ada field iklan**).
- [ ] **Pilih client context yang tidak menyalakan jalur iklan.** Prioritaskan client music (`WEB_REMIX`, `ANDROID_MUSIC`) seperti `PlayerClientDirector` EchoMusic. Catat pilihan + alasan di `keputusan.md`. Hindari `WEB`/`MWEB` sebagai klien utama — itu yang membawa jalur `playerAds`.
- [ ] **Manfaatkan proyeksi field sisi server (`&$fields=...`).** EchoMusic memakai ini untuk meminta server mengirim hanya field tertentu. Refy memakai `&$fields` pada request yang tidak butuh respons penuh — server tidak mengirim blok yang tidak diminta, jadi iklan tertutup dari sisi server, bukan difilter di klien.
- [ ] **Endpoint iklan tidak pernah dipanggil.** Hasil pencarian `player/ad_break` harus **0** di seluruh proyek (EchoMusic: 0).

#### M1.3 — Hanya audio

- [ ] Ambil hanya dari `adaptiveFormats`/`formats` dengan `mimeType` `audio/*`. Jangan ambil track video.
- [ ] **Eksklusi DRM eksplisit** seperti EchoMusic: saring kandidat ber-`drm`, `drmFamilies`, `contentprotection`, `widevine`, `playready`, `fairplay`, `licenseurl`. Stream ber-DRM tidak bisa diputar ExoPlayer dan bukan jalur yang benar.

#### M1.4 — Uji penjaga (harus GAGAL kalau ada yang menambah iklan)

> Fungsinya **regresi**, bukan cek sekali jalan. Kalau seseorang kelak menambahkan field iklan ke DTO, uji ini harus merah.

- [ ] **Uji refleksi atas DTO** — enumerate semua field `PlayerResponse` + model feed; assert tidak satupun cocok dengan pola nama iklan: `playerAds|adPlacements|adSlots|adBreakHeartbeatParams|adParams|adTrackingUrl|.*[Rr]emarketing.*|ptracking.*|adSlotRenderer|.*[Pp]romoted.*|inFeedAd`. Paling murah, paling tahan lama.
- [ ] **Uji parser wajib mengabaikan field iklan** — fixture respons `player` **yang sengaja disisipi** `playerAds`/`adPlacements`; assert objek hasil parse tidak memiliki data itu dan pemutaran tetap jalan. Kalau gagal → `ignoreUnknownKeys` mati.
- [ ] **Uji permintaan bersih** — intercept body yang dikirim ke `/youtubei/v1/player` (20 trackId) → assert nol field iklan; assert `/player/ad_break` tidak pernah muncul di log request.
- [ ] **Fixture offline** — simpan ≥3 respons mentah (player, next/search, playlist) di `app/src/test/resources/` supaya uji jalan tanpa jaringan.
- [ ] **Uji manual 20 lagu acak** — 0 audio iklan, 0 visual iklan, 0 prompt login (memenuhi O1 & F-03 PRD §2.1/§2.2).
- [ ] **Uji 10 lagu berurutan tanpa menekan next** — tidak ada jeda tak dikenal, tidak ada audio asing di antara lagu.

#### M1.5 — Yang TIDAK boleh ditiru

- [ ] **Jangan tiru pola Pear Desktop** — memuat halaman YouTube Music asli lalu **menghapus** `playerAds` setelah `JSON.parse` (plus trap `defineProperty`). Itu bebas-iklan *reaktif* di atas permukaan milik Google: rawan berubah, butuh perawatan DOM. Refy menempuh jalur EchoMusic: **tidak pernah meminta**.
- [ ] **Jangan simpan respons player sebagai `JsonElement`/`Map<String,Any>`** — membatalkan seluruh penjagaan M1.1.
- [ ] **Jangan pasang library adblock** (`ghostery/adblocker` dsb.) — tidak ada request iklan berbasis halaman di Refy.
- [ ] **Jangan pakai filter domain/VPN** — iklan YouTube datang dari domain yang sama dengan stream; memfilternya mematikan pemutaran.

#### M1.6 — Catat & pantau

- [ ] Tulis di `flows/04` strategi bebas-iklan Refy = **4 lapis di atas**, dengan acuan bukti `REA EchoMusic` §2a–§2d.
- [ ] Tulis di `keputusan.md` keputusan "model tipe tertutup, bukan pembersihan reaktif", plus alternatif yang ditolak (Pear post-parse, adblock library, filter domain).
- [ ] **Risiko yang dipantau** — kalau YouTube menyisipkan iklan audio **server-side ke dalam segmen stream** (bukan track terpisah), tidak satupun dari 4 lapis ini menolong. Deteksi: uji 10 lagu berurutan. Kalau terdeteksi → keputusan produk baru (mis. pemotongan segmen), bukan perbaikan bug.

## M2 — Pemutar (± 1 minggu)

- [ ] MediaSessionService + Media3 ExoPlayer
- [ ] Notifikasi MediaStyle + kontrol lockscreen
- [ ] Audio focus + noisy receiver + penanganan Bluetooth
- [ ] Manifest: `foregroundServiceType="mediaPlayback"`, izin API 34+
- [ ] Uji: 60 menit layar mati, kontrol tetap responsif

## M3 — UI MVP (± 1,5–2 minggu)

Mengikuti **18 layar UI + 2 aset** desain (17 layar nyata + 1 artefak); MVP dikunci **11 layar** (D7). Nama di bawah memakai nomor layar dari PRD §9.

> Urutan pengerjaan, peta berkas snapshot per layar, dan struktur komponen ada di
> **`PLAN.md`**. Keputusan pemakai 2026-11-16: **17 layar desain unik semuanya masuk
> cakupan** — 11 MVP lebih dulu (Fase 3–5), 6 sisanya di Fase 6. #13 Add to Playlist
> dibuat dengan rancangan sendiri (sheet: daftar playlist + "New playlist").

- [x] #1 Home Screen (sapaan, chip All/Music/Podcasts, Daily Mix, Made For You, Trending Now) — **UI selesai 2026-11-18** (`ui/home/`), data contoh `sample_*` sampai M1/M4. Chip hanya All/Music (inventaris i18n tak punya kunci podcast); bel/cast/equalizer/avatar tidak dirender (D15). Rincian `PLAN.md` §5
- [x] #2 Active Search Results (Top Result, chip Top/Songs/Albums/Artists/Playlists, Songs, Albums) — **UI selesai 2026-11-18** (`ui/search/`, route `search`), kueri diterima dari luar; avatar & chip Podcasts tidak dirender (D15). Rincian `PLAN.md` §5
- [x] #3 Library & Search Screen (tab Discover: Browse all, chip genre, Recently played) — **UI selesai 2026-11-18** (`ui/discover/`), data contoh `sample_*`. Ikon mic/kamera & "Explore tags" tidak dirender (D15); judul memakai `discover.title`, bukan `<h1>` desain
- [x] #4 Your Library (tab Library: Playlists, Albums, Artists; Podcasts & Downloaded ditunda D15) — **UI selesai 2026-11-18** (`ui/library/`), data contoh `sample_*`; tombol "+" menyambung ke #14 di Fase 5. Rincian `PLAN.md` §5
- [x] #5 Album Detail & Tracklist (`album-detail-tracklist-76a16cec`) — **UI selesai 2026-11-19** (`ui/album/`, route `album` dari kartu Home). Toolbar kembali/antrean/menu, hero 208dp + glow, badge, rekomendasi + refresh, baris aksi (favorit/riwayat/bagikan/shuffle/putar), tracklist 10 baris dengan baris aktif. Glyph antrean/shuffle/jam digambar `Canvas` (set inti tak punya). Unduh offline & chrome global tidak dirender. Rincian `PLAN.md` §5
- [x] #8 Artist Profile (`artist-profile-ebadb5ac`) — **selesai 2026-11-20** (`ui/artist/`, route `artist` dari baris artis Library, `Glyphs.kt` baru: shuffle/equalizer bersama #5). Banding PNG: hero/aksi/baris/chip/kartu cocok (pitch baris 66,3dp vs 66px). Tiga koreksi pasca-band (judul bagian 22→18, glow chip aktif, tinggi chip 28dp) terverifikasi di perangkat 2026-11-20; glow ditulis ulang jadi `ui/component/Glow.kt` (`pillGlow`) karena `Modifier.shadow` terukur 4× lebih lemah dari box-shadow CSS; pil Following dikoreksi ke 36dp (HTML `h-9`) + teks `Accent`; FAB glow `pillGlow`. Rincian `PLAN.md` §5
- [ ] #9 Now Playing Screen
- [ ] #10 Playback Queue (checkbox multi-pilih + drag handle)
- [ ] #12 Track Options Bottom Sheet
- [ ] #14 Create Playlist
- [ ] #15 User Profile & Audio Settings (profil + ringkasan audio)
- [ ] #16 Settings (`settings-d60055b1`) — kualitas streaming, kualitas unduhan, normalisasi volume, crossfade, gapless, equalizer, audio output
- [ ] Mini-player (komponen persisten, 56px, melayang **88–92px** di atas dasar — nav pil naik, ukuran ulang 2026-11-10)
- [x] Bottom nav 4 tab: Home / Discover / Library / Create — bentuk **pil mengambang** (**D11**, mengikat) — **komponen selesai 2026-11-18** (`ui/component/BottomNavPill.kt`): `bottom-4/5` (20dp), `inset-x-4` (16dp), `max-w-sm` (384dp), `h-[68px]`, `rounded-full`, `bg-[#121621]/85`, garis putih **60%** (dikoreksi dari 12% lewat sampel piksel PNG), sorotan tab aktif accent 6%. Kanon `home-screen-243ea0d8`. Ikon masih set inti, bukan Material Symbols — lihat `keputusan.md` 2026-11-18
- [ ] **Remap 17 layar ke palet Kubu B** — tabel remap `docs/DESIGN.md` §1, ukur dengan `python scripts/analyze-snapshot.py`
- [ ] Ganti tab "Buat" → **"Create"** di bottom nav semua layar (D6) — 9 layar masih "Buat" per 2026-11-10
- [x] Tema `NocturneTheme` dari token `docs/DESIGN.md` §1 — 2026-11-16 (Fase 1). Palet **Kubu B** (canvas `#09090B`, surface `#141418`, accent `#8B5CF6`), seluruh warna lewat token tema — nol hex di Composable, dibuktikan `grep` (`Color(0x` di luar `ui/theme/` = 0)
- [~] Komponen kaca: `RepaintBoundary` + `ColoredBox` dasar sebelum blur + jalur mundur tanpa blur — **prioritas naik 2026-11-10**: nav pil (blur 30px, garis putih nyata, glow luar) + mini-player (blur 36px) kini berdampingan dengan jarak <20px, dua kaca rona terang yang paling mudah memicu pita Mali. **2026-11-18:** `GlassSurface` ada dengan struktur §9 (penyangga opaque + garis), tetapi **blur latar belum nyata** — Compose tidak punya backdrop blur bawaan. **R2 belum tersentuh.** Perlu keputusan pustaka (`haze`) atau RenderNode sebelum Fase 3 selesai — lihat `keputusan.md` 2026-11-18
- [ ] Semua string lewat resource i18n (tidak ada teks hardcoded di Composable) — daftar periksa: `docs/I18N-KEYS.md`
- [ ] Semua skenario Gherkin US-01...US-05 lolos

## M4 — Pustaka & antrean (± 3–4 hari)

- [ ] Room: `queue_items`, `favorites`, `history`, `provider_cache`, **`playlists`, `playlist_items`** (skema lengkap di `flows/02`)
- [ ] Persistensi antrean lintas-restart (reindexing transaksional)
- [ ] Shuffle menyimpan urutan asli; repeat off/all/one
- [ ] Sinkron antrean → MediaSession setiap mutasi

## M5 — Poles & rilis (± 3–4 hari)

- [ ] **Gerbang rilis: uji penjaga anti-iklan lulus** (M1.4) — khususnya **uji refleksi DTO** dan **uji parser dengan fixture berisi `playerAds`**. Tanpa ini, klaim "tanpa iklan" di README/PRD tidak boleh ditulis
- [ ] Audit i18n: English 100 % (semua string di `values/`, nol teks hardcoded); `values-in/` opsional menyusul
- [ ] Ikon aplikasi + nama tanpa merek Google
- [ ] Signing key + GitHub Secrets
- [ ] Workflow rilis tag `v*` + checksum SHA-256
- [ ] Uji pemasangan bersih di 2 perangkat (Android 8–10 dan 13+)
- [ ] Rilis v0.1.0 di GitHub Releases
- [ ] Ajukan ke IzzyOnDroid (opsional)

## v0.2.0 (Should)

- [ ] SponsorBlock + pengaturan per kategori
- [ ] Lirik tersinkron
- [ ] Cache offline otomatis (lihat `flows/05`)
- [ ] Playlist lokal buatan pengguna
- [ ] Sleep timer
- [ ] Autoplay dari `getRelated` (nyalakan setelah stabil)

## v0.3.0+ (Could)

- [ ] Provider subsonic/Navidrome
- [ ] Provider Audius
- [ ] Login Google untuk sinkronisasi pustaka
- [ ] Android Auto
- [ ] Ekualiser + normalisasi audio
- [ ] Import daftar putar dari YouTube Music

## Tidak dikerjakan (Won't)

- [x] Blokir iklan di dalam aplikasi YouTube Music resmi — tidak mungkin tanpa root, keputusan sadar
- [x] Memasang library adblock (`ghostery/adblocker` dsb.) di Refy — tidak ada sasaran; Refy tidak memuat HTML/JS YouTube (lihat `keputusan.md` 2026-11-09 dan `docs/PRD.md` §1)
- [x] Menjanjikan "bebas iklan" lewat filter domain/VPN lokal — iklan YouTube datang dari domain yang sama dengan stream; memfilternya mematikan pemutaran
- [x] Pola Pear Desktop: muat halaman YouTube Music lalu hapus `playerAds` setelah `JSON.parse` — reaktif dan rawan; Refy menempuh jalur EchoMusic (tidak pernah meminta)
- [x] Menyimpan respons player sebagai `JsonElement`/`Map<String,Any>` — membatalkan penjagaan "drop unknown key" (M1.1)
- [x] Modifikasi & distribusi ulang APK Google — pelanggaran hak cipta
- [x] Rilis Play Store — akan ditolak
- [x] Monetisasi / iklan internal / telemetri — bertentangan dengan tujuan proyek