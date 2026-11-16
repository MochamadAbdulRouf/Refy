# PRD — Refy

**Versi:** 0.1 (draft untuk review)
**Tanggal:** 2026-11-09
**Pemilik:** user (hobi/open source, non-komersial)
**Status:** menunggu 2 keputusan (lihat §12)

---

## 1. Executive Summary

Refy adalah aplikasi pemutar musik Android yang memutar katalog YouTube Music **tanpa iklan, tanpa login, tanpa langganan**, dan **sepenuhnya open source**.

Yang penting diklarifikasi di depan, karena brief awal (hasil diskusi dengan AI lain) mengandung satu kesalahan konsep yang mempengaruhi arsitektur:

> Refy **tidak memblokir** iklan. Refy **tidak pernah meminta** iklan.

Aplikasi ini bukan pemblokir iklan, bukan modifikasi APK YouTube Music, dan bukan injector ke proses aplikasi lain. Ia adalah klien mandiri yang mengambil aliran audio langsung dari katalog publik — sama seperti NewPipe, InnerTune, dan Metrolist. Konsekuensinya:

- Iklan tidak muncul karena tidak ada jalur iklan di arsitektur, bukan karena ada filter.
- Library adblock pihak ketiga (mis. `ghostery/adblocker`) **tidak relevan** di sini — itu untuk context browser/Electron, sedangkan Refy meminta URL stream langsung ke ExoPlayer. Tidak ada request HTML/iklan untuk difilter.
- Yang benar-benar jadi risiko teknis bukan adblock, tapi **PO token / attestation** dari YouTube (§7).

Nilai jual ke pengguna: satu aplikasi, katalog lengkap, nol iklan, nol akun, nol tracking.

---

## 2. Objectives, Metrik, dan Non-Goals

### 2.1 Objectives (kualitatif)

| # | Objective | Cara dinilai |
|---|---|---|
| O1 | Dengar lagu dari katalog YT Music tanpa iklan & tanpa login | Uji manual: 20 lagu acak, 0 iklan, 0 prompt login |
| O2 | Pemutaran latar belakang yang andal | Musik jalan 60 menit dengan layar mati, kontrol tetap responsif |
| O3 | Bangun & rilis dapat direproduksi siapa pun, gratis | Fork → build → APK identik (reproducible build) tanpa layanan berbayar |
| O4 | Perawatan berkelanjutan oleh 1 orang part-time |" Waktu perbaikan pasca-perubahan API Google < 1 hari kerja "|

### 2.2 KPI (terukur, diukur pada rilis v0.1.0)

| KPI | Target | Ambang gagal |
|---|---|---|
| Waktu mulai putar (LTE, lagu pertama) | ≤ 2,0 s |" > 4,0 s "|
| Cold start ke layar siap (mid-range) | ≤ 1,8 s |" > 3,0 s "|
| Crash-free session | ≥ 99,0 % |" < 97 % "|
| ANR rate | ≤ 0,5 % |" > 1,5 % "|
| Baterai saat putar layar mati | ≤ 4 %/jam |" > 8 %/jam "|
| Ukuran APK (arm64, rilis) | ≤ 12 MB |" > 20 MB "|
| Cakupan terjemahan ID | 100 % (bahasa utama) | — |

### 2.3 Non-Goals (tegas, untuk v1)

- N1 — Memblokir iklan di dalam aplikasi YouTube Music resmi. Tidak mungkin tanpa root; Refy tidak menempuh jalur ini.
- N2 — Memodifikasi/mendistribusikan ulang APK Google. Pelanggaran hak cipta.
- N3 — Rilis di Google Play Store. Aplikasi tipe ini akan ditolak; distribusi lewat GitHub Releases + F-Droid.
- N4 — Monetisasi, iklan, atau telemetri pihak ketiga dalam bentuk apa pun.
- N5 — Sinkronisasi library akun Google. Ditunda minimal sampai v0.3 (lihat §12, D2).

---

## 3. Persona

### P1 — "Rio, 24, mahasiswa" (persona utama)
Dengar musik 3–4 jam/hari saat kuliah & commute. Pakai akun gratis. Paling terganggu iklan audio yang memotong playlist. Sudah pernah pakai Vanced → sekarang pakai YT Music resmi dan mengeluh. Perangkat: Redmi, Android 13, RAM 6 GB.
Kebutuhan: buka app → langsung dengar. Tidak mau login. Tidak mau langganan.

### P2 — "Sari, 31, pekerja" (persona sekunder)
Butuh audio latar saat kerja, mengutamakan stabilitas & baterai. Tidak peduli fitur eksotis. Tidak mau app force-close.
Kebutuhan: background play andal, kontrol di notifikasi/lockscreen, sleep timer.

### P3 — "Dev kontributor" (persona internal)
Orang yang fork repo. Butuh build yang jelas, lisensi bersih, arsitektur modular.
Kebutuhan: README jujur soal batasan, build satu perintah, CI tanpa kredensial berbayar.

---

## 4. Keputusan Arsitektur Sumber Musik

Tiga jalur, dengan rekomendasi:

| Opsi | Isi | Risiko | Rekomendasi |
|---|---|---|---|
| **A. YouTube Music via extractor** | NewPipeExtractor / InnerTube internal, tanpa API key | Langgar ToS; rapuh; PO token | **Jalur utama v1** |
| B. Audius (Open Audio Protocol) | REST gratis tanpa key, legal, katalog indie | Katalog bukan lagu mainstream | Provider tambahan (v0.4) |
| C. Server pribadi (Navidrome/Subsonic) | Koleksi sendiri, 100 % legal | Butuh server | Provider tambahan (v0.3) |

**Rekomendasi:** bangun **provider layer** sejak awal. v1 = provider A saja. Provider B & C masuk sebagai modul yang bisa dinyalakan — sehingga kalau resolver YouTube mati karena perubahan Google, aplikasi tetap berguna dan tidak "mati total" seperti banyak klien komunitas lain.

**Catatan komparatif (terverifikasi 2026-11-09):**

| Proyek | Lisensi | Bintang | Push terakhir | Catatan untuk Refy |
|---|---|---|---|---|
| MetrolistGroup/Metrolist | GPL-3.0 | 14.095 | 2026-11-02 | Referensi **utama**: Kotlin, aktif, arsitektur modern |
| maxrave-dev/SimpMusic | GPL-3.0 | 13.061 | 2026-11-09 | Aktif; referensi penanganan resolver |
| z-huang/InnerTune | GPL-3.0 | 6.145 | 2025-11-13 | Stale ±12 bulan — pakai sebagai bacaan, bukan basis |
| OuterTune/OuterTune | GPL-3.0 | 5.447 | 2026-10-12 | Fork InnerTune, masih jalan; referensi struktur |
| TeamNewPipe/NewPipeExtractor | GPL-3.0 | 2.046 | 2026-11-09 | Library ekstraksi; aktif |
| ghostery/adblocker | MPL-2.0 | 1.015 | 2026-11-09 | **Tidak dipakai** (lihat §1) |

---

## 5. Ruang Lingkup Fitur (MoSCoW)

### MUST — v0.1.0 (MVP)

| ID | Fitur | Kriteria penerimaan ringkas |
|---|---|---|
| F-01 | Cari lagu/album/artis dari katalog | Hasil muncul ≤ 2 s; chip filter **Top, Songs, Albums, Artists, Playlists** (mengikuti desain `active-search-results`; **tanpa chip Video** — Refy hanya audio, lihat M1.3 dan WON'T) |
| F-02 | Putar audio | Stream diputar ExoPlayer; seek, next/prev, shuffle, repeat |
| F-03 | Putar tanpa iklan | Tidak ada audio/visual iklan sama sekali (by construction) |
| F-04 | Tanpa login | Aplikasi berfungsi penuh tanpa akun Google |
| F-05 | Background playback | MediaSessionService; lanjut saat app di-kill dari recents |
| F-06 | Kontrol notifikasi + lockscreen | Play/pause/next/prev, artwork, judul; MediaStyle |
| F-07 | Antrean (queue) | Tambah ke antrean, lihat, urutkan, hapus |
| F-08 | Pustaka dasar | Riwayat putar + favorit, persistensi lokal |
| F-09 | Tema gelap/terang | **v0.1 DARK-ONLY.** Ganti tema + override manual = **D12**; token light belum ada di desain. Lihat §7.1 dan `keputusan.md` 2026-11-12. Opsi "Ikuti sistem" hanya menerima hasil gelap |
| F-10 | Unduh APK sendiri | Signing key milik proyek, rilis di GitHub Releases |
| F-11 | Tab Discover (jelajah) | Kategori/genre + pencarian dari satu layar (desain layar **#3**) |
| F-12 | Halaman detail album + daftar lagu | Header album, tombol putar, daftar lagu bernomor (desain layar **#5**; varian **#6** ditunda — duplikat fungsional) |
| F-13 | Profil artis | Header artis, lagu populer, diskografi (desain layar **#8**) |
| F-14 | Menu opsi per lagu | Sheet: putar berikutnya, tambah ke antrean, ke album, ke artis, bagikan (desain layar **#12**) |
| F-15 | Putar playlist (buat + kelola) | Buat playlist, tambah/hapus lagu, ubah nama, hapus playlist (desain layar **#14**; layar "Add to Playlist" **#13** ditunda ke v0.2 per D7 — dilayani lewat sheet #12) |

### SHOULD — v0.2.0

F-20 SponsorBlock (skip segmen non-musik) · F-21 Lirik tersinkron · F-22 Cache offline otomatis · F-23 Playlist lokal buatan pengguna · F-24 Sleep timer · F-25 Terjemahan Indonesia (`values-in/`) — English jadi default, bukan tambahan

### COULD — v0.3.0+

F-30 Login Google (khusus sync) · F-31 Android Auto · F-32 Provider Audius · F-33 Provider subsonic/self-host · F-34 Ekualiser & normalisasi · F-35 Import daftar putar dari YT Music

### WON'T — tidak dikerjakan di jalur ini

F-90 Blokir iklan in-app di aplikasi YT Music resmi · F-91 APK mod Google · F-92 Rilis Play Store · F-93 Monetisasi/iklan internal · F-94 Telemetri apa pun

---

## 6. User Stories + Kriteria Penerimaan (Gherkin)

### US-01 — Cari lagu
> Sebagai pendengar, saya ingin mencari lagu agar bisa memutar yang saya mau.

```gherkin
Scenario: Pencarian berhasil
  Given pengguna berada di tab Cari
  And koneksi internet aktif
  When pengguna mengetik "tulus monokrom" dan menekan enter
  Then daftar hasil muncul dalam ≤ 2 detik
  And setiap item menampilkan judul, artis, dan durasi
  And tab Lagu/Album/Playlist/Artis dapat dipilih untuk memfilter

Scenario: Pencarian tanpa koneksi
  Given pengguna tidak memiliki koneksi internet
  When pengguna mengetik kueri dan menekan enter
  Then muncul pesan "Tidak ada koneksi"
  And tersedia tombol "Coba lagi"
  And aplikasi tidak crash
```

### US-02 — Putar & kontrol
```gherkin
Scenario: Mulai memutar dari hasil pencarian
  Given daftar hasil pencarian tampil
  When pengguna menekan satu lagu
  Then lagu mulai berbunyi dalam ≤ 2 detik
  And mini-player muncul di bagian bawah dengan artwork dan judul
  And lagu berikutnya di antrean disiapkan

Scenario: Kontrol latar belakang
  Given lagu sedang diputar
  When pengguna menekan tombol home dan mematikan layar
  Then audio tetap berjalan
  And notifikasi MediaStyle menampilkan artwork, judul, artis
  And tombol play/pause/next/prev di notifikasi berfungsi
  And kontrol di lockscreen berfungsi
```

### US-03 — Tanpa login
```gherkin
Scenario: Pemakaian pertama tanpa akun
  Given aplikasi baru dipasang
  When pengguna membuka aplikasi
  Then tidak ada dialog login yang wajib ditutup
  And pengguna dapat mencari dan memutar lagu tanpa memasukkan kredensial apa pun
  And tidak ada data dikirim ke server pihak ketiga milik pengembang
```

### US-04 — Nol iklan
```gherkin
Scenario: Pemutaran berurutan tanpa iklan
  Given antrean berisi 10 lagu
  When kesepuluh lagu diputar sampai selesai
  Then tidak ada jeda audio iklan di antara lagu
  And tidak ada banner/video iklan di antarmuka
  And tidak ada permintaan upgrade/langganan
```

### US-05 — Antrean
```gherkin
Scenario: Menambah ke antrean
  Given lagu A sedang diputar
  When pengguna long-press lagu B dan memilih "Tambah ke antrean"
  Then lagu B muncul di akhir daftar antrean
  And lagu A tidak terhenti
  And antrean bertahan setelah aplikasi ditutup dan dibuka kembali
```

### US-06 — Offline (Should, v0.2)
```gherkin
Scenario: Putar dari cache
  Given lagu X pernah diputar dan sudah ter-cache
  When perangkat masuk mode pesawat
  And pengguna memutar lagu X dari Riwayat
  Then lagu X diputar dari cache lokal
  And muncul indikator "Offline"
```

### US-07 — SponsorBlock (Should, v0.2)
```gherkin
Scenario: Lewati segmen sponsor
  Given SponsorBlock aktif di Pengaturan
  And lagu Y memiliki segmen "sponsor" yang terdaftar
  When pemutaran melewati awal segmen tersebut
  Then pemutaran melompat ke akhir segmen
  And muncul toast "Melewati sponsor"
  And pengguna dapat menonaktifkan perilaku ini per kategori segmen
```

---

## 7. Kebutuhan Non-Fungsional & Risiko Teknis

### 7.1 Platform

| Item | Nilai |
|---|---|
| minSdk | 26 (Android 8.0) |
| targetSdk / compileSdk | 35 |
| Bahasa | Kotlin (2.x) |
| UI | Jetpack Compose + Material 3 |
| Pemutar | AndroidX Media3 (ExoPlayer) |
| Latar belakang | MediaSessionService + MediaStyle notification |
| Persistensi | Room (pustaka/antrean) + DataStore (preferensi) |
| DI | **Hilt** — diputuskan 2026-11-12 (lihat `keputusan.md` + D13 §12) |
| Jaringan | OkHttp + kotlinx.serialization |
| Peta bebas GMS | Tidak ada Firebase, tidak ada Play Services wajib (syarat F-Droid) |
| Tema | **v0.1: dark-only** (Kubu B). Token light belum ada; F-09 diturunkan (D12 §12) |

**Batasan ukuran yang diwarisi dari desain (terukur, bukan asumsi):**

| Variabel | Nilai |
|---|---|
| Layar rujukan | 390 × 844 dp |
| Margin tepi konten | 16px |
| Header tetap | 64px |
| Bottom nav | **68px — pil mengambang, D11** (`bottom-4`/`5`, jadi 16–20px di atas dasar) |
| Mini-player | 56px, melayang **88–92px** di atas dasar — di atas pil nav, bukan menempel dasar |
| Baris lagu / artwork baris | 56px / 40px |
| Target sentuh ikon / tombol utama | 44px / 48px |
| Konten bawah berhenti | 160px dari dasar |
| Radius baris / kartu / artwork besar | 8 / 12 / 16 px |

> Sumber angka: `docs/DESIGN.md` §3. Tabel ini dulu menulis "bottom nav 64px" dan "mini-player 64px" — nilai sebelum D11 (nav masih menempel dasar). Sudah dikoreksi 2026-11-12.

### 7.2 Risiko teknis (peringkat)

| # | Risiko | Dampak | Mitigasi |
|---|---|---|---|
| R1 | **PO token / attestation YouTube** — tanpa token valid, stream dibatasi (laporan komunitas: kualitas turun sampai 360p) | Tinggi | Abstraksi resolver; uji otomatis mingguan; siapkan mekanisme token berbasis WebView/solver; dokumentasikan cara update cepat |
| R2 | Perubahan endpoint/InnerTube internal Google | Tinggi | Semua pemanggilan lewat satu modul; tidak ada pemanggilan tersebar |
| R3 | NewPipeExtractor (GPL-3.0) memaksa Refy GPL-3.0 | Sedang | Diterima — proyek memang open source GPL-3.0 |
| R4 | Takedown DMCA / keluhan merek | Sedang | Tidak pakai kata "YouTube"/"YT" di nama, ikon, atau judul store; disclaimer di README |
| R5 | Beban perawatan berkelanjutan (1–3 jam/bulan saat API berubah, bisa lebih saat insiden) | Sedang | Rilis cepat; dokumentasi proses rilis 1 halaman |
| R6 | Baterai boros karena dekode berkualitas tinggi terus-menerus | Rendah | Default kualitas adaptif; uji baterai di CI manual |

### 7.3 Keamanan & privasi

- Tidak ada kredensial yang disimpan pada v1 (tidak ada login).
- Tidak ada SDK analitik, crash reporting, atau iklan pihak ketiga.
- Izin diminta seminimal mungkin: `INTERNET`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, `POST_NOTIFICATIONS`, `WAKE_LOCK`. Tanpa `READ_EXTERNAL_STORAGE` di v1 (belum ada file lokal).
- Kalau login Google ditambahkan nanti (F-30): token masuk Android Keystore; cookie tidak dalam bentuk plaintext; peringatan risiko pembatasan akun ditampilkan ke pengguna sebelum login.

---

## 8. Alur Aplikasi

Lihat berkas terpisah di `flows/`:

- `flows/00-peta-navigasi.md` — semua layar & jalur
- `flows/01-alur-putar-lagu.md` — dari pencarian sampai audio berbunyi
- `flows/02-alur-antrean.md` — manajemen antrean & persistensi
- `flows/03-alur-background-service.md` — lifecycle MediaSessionService
- `flows/04-alur-resolver-dan-po-token.md` — pemilihan stream + penanganan kegagalan
- `flows/05-alur-offline-cache.md` — (v0.2)
- `flows/06-alur-rilis-dan-ci.md` — build → tanda tangan → GitHub Releases → F-Droid

---

## 9. Desain (Stitch — Electric Purple Music App)

Desain **sudah ada** dan menjadi acuan implementasi Compose. Bukan direncanakan, bukan digenerate ulang.

| Item | Nilai |
|---|---|
| Project Stitch | **Electric Purple Music App** — `projects/9190295645942987043` |
| Design system | **Nocturne Audio** — `assets/9feaa5b53730417e8c1d163ffc007c63` |
| Jumlah layar | **20 entri = 18 layar UI + 2 aset gambar** (17 layar nyata + 1 artefak) |
| Layar Home terpilih | `home-screen-243ea0d8.html` — dipakai **tata letaknya** saja (warnanya Kubu A, lihat di bawah) |
| Salinan read-only | `.stitch/electric-purple/` (HTML + PNG + manifest + indeks) |
| Dokumen token | **`docs/DESIGN.md`** — palet, tipografi, spasi, radius, blur, komponen |
| Inventaris string | **`docs/I18N-KEYS.md`** — daftar kunci i18n untuk 11 layar MVP; daftar periksa audit M5 |
| Terakhir ditarik ulang | **2026-11-10** — 9 layar berubah, 1 baru, 1 aset baru, **1 dihapus** (`Downloads Management` lama). Selisih: `.stitch/DELTA-2026-11-10.md` |
| Verifikasi terakhir vs Stitch hidup | **2026-11-12** — `scripts/verify-docs-vs-stitch.py`: 20 entri live = 20 entri snapshot, **18/18 layar HTML `SAMA`** (byte-identik), 0 slug hanya-di-live, 0 hanya-di-snapshot. `updateTime` Stitch `2026-11-10T18:29:53.886928Z` |

> **Palet SUDAH DIPUTUSKAN (D8, 2026-11-09): Kubu B.** Canvas `#09090B` · surface `#141418` · accent `#8B5CF6` · gradien `#A855F7 → #6366F1` · text `#FFFFFF`/`#A1A1AA`. Per ukuran ulang 2026-11-10, **17 layar** masih memakai palet lama di blok `tailwind.config`-nya dan **wajib diremap** memakai tabel remap di `docs/DESIGN.md` §1 (`python scripts/analyze-snapshot.py`).

**Palet inti (mengikat):** canvas `#09090B` · surface `#141418` · accent `#8B5CF6` · gradien `#A855F7 → #6366F1` · text `#FFFFFF`/`#A1A1AA`. Font tunggal **Plus Jakarta Sans** (tidak diperdebatkan).

**Navigasi menurut desain (4 tab):** `Home` · `Discover` · `Library` · `Create`. Pengaturan **tidak** punya tab sendiri, tapi kini punya **layar sendiri** (#16 Settings), dibuka dari layar #15 (User Profile & Audio Settings) lewat kartu "App Settings". Ini menggantikan rencana "S-06 Pengaturan" pada draft sebelumnya — sekarang desainnya sudah menyediakannya.

**20 entri = 18 layar UI + 2 aset gambar** (17 layar nyata + 1 artefak):

| # | Layar | Peran |
|---|---|---|
| 1 | Home Screen | beranda **(dipakai — tata letak)** |
| 2 | Active Search Results | hasil pencarian |
| 3 | Library & Search Screen | tab Discover |
| 4 | Your Library | tab Library |
| 5 | Album Detail & Tracklist | detail album |
| 6 | Album Detail - After Hours | varian detail album ("After Hours (Deluxe)") |
| 7 | Playlist Detail - Late Night Drives | detail playlist |
| 8 | Artist Profile | profil artis |
| 9 | Now Playing Screen | pemutar penuh |
| 10 | Playback Queue | antrean |
| 11 | Expanded Live Lyrics | lirik penuh |
| 12 | Track Options Bottom Sheet | menu per lagu |
| 13 | Add to Playlist | tambah ke playlist |
| 14 | Create Playlist | buat playlist |
| 15 | User Profile & Audio Settings | profil + ringkasan audio |
| 16 | **Settings** | **baru** — pengaturan aplikasi (streaming, unduhan, audio, output) · tanpa bottom nav |
| 17 | **Downloads Management** | `b26aa75b` (780×2110), dibuat 2026-11-10 · **D10 selesai**: versi lama `2f6ecab6` sudah dihapus desainer · **tidak dipakai di v0.1 (D7)** |
| 18 | Nocturne Music Streaming Prototype | **artefak**, bukan layar — jangan diimplementasikan |

**Perubahan terakhir (2026-11-10):** 9 layar berubah isi, 1 layar UI baru (`Downloads Management` `b26aa75b`), 1 aset gambar baru (`image.png` 454×320 = gambar referensi nav glassmorphism), 1 layar **dihapus** (`Downloads Management` lama `2f6ecab6`). Perubahan terbesar: **bottom nav didesain ulang menjadi pil mengambang** (`bottom-4`, `rounded-full`, `h-[68px]`, `bg-[#121621]/85`, `blur(30px)`) dan mini-player naik ke `bottom-[88–92px]`. Entri **"Nocturne Music Streaming Prototype"** adalah artefak — **sejak 2026-11-10 tidak lagi byte-identik** dengan Home Screen (Home Screen ikut berubah, artefak tidak). Jangan diimplementasikan. Dua entri lain adalah **aset gambar**, juga bukan layar.

**Alur pengaturan:** avatar di header → `#15` (profil + ringkasan audio) → kartu "App Settings" → `#16 Settings` → `#17 Downloads Management`. Keduanya layar turunan, bukan tab bottom-nav.

**Metrik terukur dari HTML** (bukan dari deskripsi): header 64px · bottom nav **68px, pil mengambang** (`bottom-4`/`5`, jadi 16–20px di atas dasar; keputusan **D11**) · mini-player 56px melayang **88–92px** di atas dasar · baris lagu 56px · artwork baris 40px · tombol ikon 44px · tombol utama 48px · radius baris 8px / kartu 12px / artwork besar 16px · progress bar 2px · jarak aman bawah konten **160px**.

**Risiko render yang sudah diidentifikasi:** permukaan kaca persisten (header, bottom nav, mini-player) pada GPU Mali memicu artefak pita warna seperti yang pernah terjadi di proyek Recify. **Naik tingkat sejak nav jadi pil:** nav (blur 30px, garis putih nyata, glow luar) dan mini-player (blur 36px) kini berdampingan dengan jarak <20px, sehingga dua lapisan kaca rona terang saling menutupi. Wajib: `RepaintBoundary` + `ColoredBox` dasar sebelum blur, plus jalur mundur tanpa blur yang benar-benar diuji di perangkat Mali. Detail di `docs/DESIGN.md` §9.

**Cacat aset desain yang harus ditangani saat implementasi:**
1. `library-search-screen.html` menandai **dua** tab aktif sekaligus (Discover dan Library). Tidak valid; pilih satu per layar.
2. Hampir semua layar **tidak punya `<title>`**; yang dibagi bersama adalah `<h1>` "Library And Search Screen" di tiga layar (**#2, #3, #4**). Jangan pakai sebagai identitas layar.
3. Bahasa campur: Inggris hampir di semua string, Indonesia pada tab "Buat". **Sudah diputuskan (D6): English penuh** — tab "Buat" diganti "Create". Semua string wajib masuk resource i18n, tidak boleh hardcoded.
4. **Palet SUDAH DIPUTUSKAN (D8): Kubu B** — canvas `#09090B`, surface `#141418`, accent `#8B5CF6`. **17 layar** masih memakai palet lama di blok `tailwind.config`-nya dan wajib diremap (`docs/DESIGN.md` §1, tabel remap). Jangan salin hex dari HTML apa adanya.
5. `album-detail-tracklist.html` dan `album-detail-after-hours.html` sama-sama menggambarkan album "After Hours" (yang kedua "After Hours (Deluxe)") — satu fitur digambar dua kali. Pilih satu saat implementasi.
6. Layar #16 Settings **tidak ada di bottom nav**. Layar #17 Downloads Management yang tersisa (`b26aa75b`) **punya** nav pil penuh — klaim lama "nav-nya kosong" tidak berlaku lagi. Keduanya layar turunan, bukan tab.
7. *Home Screen - High Density Dark Mode* sudah **dihapus desainer dari Stitch**. Berkas lokalnya artefak usang; jangan dipakai.

**Kontrak ke Claude Code:** implementasi Compose mengikuti `docs/DESIGN.md` sebagai acuan token dan `.stitch/electric-purple/*.html` sebagai acuan tata letak (nama berkas memuat 8 karakter id layar — jangan dihapus). Aturan pemutus konflik ada di `docs/DESIGN.md` §10 — **warna → Kubu B (`#09090B`/`#141418`/`#8B5CF6`), jangan salin hex Kubu A dari HTML**; ukuran/radius/struktur → HTML; tipografi → manifest. Tidak ada nilai hex langsung di Composable; semua lewat token tema.

---

## 10. Milestone

| Milestone | Isi | Kriteria selesai | Estimasi part-time |
|---|---|---|---|
| M0 — Fondasi | Repo, lisensi, CI, skema, design token | Build kosong lolos CI; APK dapat dipasang | 2–3 hari |
| M1 — Resolver hidup | Modul sumber bisa mencari & mengambil URL audio | Uji: 20 kueri, ≥ 18 menghasilkan URL yang dapat diputar | 1 minggu |
| M2 — Pemutar | ExoPlayer + MediaSessionService + notifikasi | Audio jalan 60 menit layar mati; kontrol lengkap | 1 minggu |
| M3 — UI MVP | Layar MVP sesuai keputusan **D7** (usulan: 11 layar + mini-player) mengikuti desain Stitch | Semua US-01…US-05 lolos | 1,5–2 minggu |
| M4 — Pustaka & antrean | Room, favorit, riwayat, persistensi antrean | Data bertahan restart; US-05 lolos | 3–4 hari |
| M5 — Poles & rilis | Audit i18n (English 100 %, tidak ada string hardcoded), ikon, signing, README, GitHub Release | APK rilis terpasang bersih di 2 perangkat | 3–4 hari |
| **Total MVP (v0.1.0)** | | | **± 5–6 minggu part-time** |

Setelah M5: pemeliharaan rutin (R5) — perkirakan **1–3 jam/bulan**, dan **0,5–2 hari** setiap kali Google mengubah jalur stream.

---

## 11. Definisi Selesai (v0.1.0)

1. Semua fitur MUST (§5) berfungsi pada perangkat uji nyata (minimal 1 Android 8–10, 1 Android 13+).
2. Semua skenario Gherkin §6 lolos.
3. `./gradlew lint test assembleRelease` hijau di CI bersih.
4. README memuat: cara build, batasan hukum, disclaimer merek, cara melaporkan bug.
5. `LICENSE` = GPL-3.0-or-later; semua dependensi kompatibel.
6. APK rilis ditandatangani dan dapat diunduh publik.
7. Tidak ada kata "YouTube"/"YT" pada nama aplikasi, nama paket, ikon, atau judul rilis.

---

## 12. Keputusan yang Masih Terbuka

| ID | Pertanyaan | Default kalau tidak dijawab |
|---|---|---|
| D1 | ~~Rilis publik atau privat dulu?~~ **DIPUTUSKAN 2026-11-12: publik.** Rilis v0.1.0 di GitHub Releases; IzzyOnDroid menyusul sebagai opsi. | Publik. Ini juga yang membuat F-10 & lisensi GPL-3.0 masuk akal; proyek privat tidak butuh CI rilis bertanda tangan |
| D2 | ~~v0.1 tanpa login atau harus login?~~ **DIPUTUSKAN 2026-11-12: tanpa login.** Berfungsi penuh tanpa akun Google; login ditunda ke v0.3 (F-30). | Mengikat. Lihat juga §7.3 — v1 tidak menyimpan kredensial apa pun |
| D3 | ~~Tulis dari nol atau fork Metrolist?~~ **DIPUTUSKAN 2026-11-12: tulis dari nol**, pola diambil dari Metrolist/EchoMusic sebagai referensi, bukan kode yang di-fork. | Mengikat. Fork akan mewarisi permukaan iklan + riwayat upstream yang tidak kita kendalikan (lihat `keputusan.md` 2026-11-10) |
| D4 | ~~Native Android (Kotlin) atau Flutter?~~ **DIPUTUSKAN 2026-11-12: Kotlin + Jetpack Compose.** | Mengikat |
| D5 | ~~Nama paket final~~ **DIPUTUSKAN 2026-11-10: `io.github.mochamadabdulrouf.refy`** — akun GitHub terverifikasi (`MochamadAbdulRouf`, 76 repo publik, terdaftar 2024-03-16). | **Mengikat dan PERMANEN.** `applicationId` tidak bisa diubah setelah rilis pertama tanpa memutus jalur update |
| D6 | ~~Bahasa antarmuka~~ **DIPUTUSKAN 2026-11-09: English penuh.** | English (`values/`); Indonesia menyusul sebagai `values-in/` tanpa ubah kode |
| D7 | ~~Layar mana yang masuk MVP?~~ **DIPUTUSKAN: 11 layar** — #1, #2, #3, #4, #5, #8, #9, #10, #12, #14, #16. Ditunda: #6, #7, #11, #13. Tidak dipakai: #17. Dibuang: Home High Density. | 11 layar mengikat untuk M3 |
| D8 | ~~Palet mana yang dipakai?~~ **DIPUTUSKAN 2026-11-09: Kubu B** (prosa brand — canvas `#09090B`, surface `#141418`, accent `#8B5CF6`, gradien `#A855F7 → #6366F1`). Layar Kubu A wajib diremap pakai tabel remap `docs/DESIGN.md` §1. | Kubu B mengikat. Token manifest ~~`#131315`~~/~~`#d0bcff`~~ **DILARANG** dipakai sebagai nilai implementasi |
| D9 | ~~Snapshot vs dokumen, mana yang menang?~~ **DIPUTUSKAN 2026-11-10: snapshot menang.** Tarik ulang `.stitch/` lalu perbarui dokumen mengikuti temuan. | Snapshot Stitch adalah sumber tata letak; `docs/DESIGN.md` sumber token. Lihat `keputusan.md` 2026-11-10 |
| D10 | ~~Dua layar berjudul `Downloads Management` — pakai yang mana?~~ **DIPUTUSKAN 2026-11-10: `b26aa75b`.** Desainer menghapus versi lama `2f6ecab6`; snapshot kini memuat satu layar Downloads. | `downloads-management-b26aa75b.html` sebagai acuan; layar ini tetap **tidak dipakai di v0.1** (D7) |
| D11 | ~~Bentuk bottom nav final: pil mengambang atau bilah menempel penuh?~~ **DIPUTUSKAN 2026-11-10: PIL MENGAMBANG** (9 layar). Bilah penuh hanya tersisa di artefak `nocturne-music-streaming-prototype`. | Mengikat. Nilai: `bottom-4`, `inset-x-4`, `max-w-sm`, `h-[68px]`, `rounded-full`, `bg-[#121621]/85`, `blur(30px)`, border `1–1.5px rgba(255,255,255,.6)`. Kanon: `home-screen-243ea0d8` |
| D12 | ~~Tema gelap/terang (F-09) — desain hanya punya token gelap. Apa yang dipakai v0.1?~~ **DIPUTUSKAN 2026-11-12: v0.1 DARK-ONLY.** F-09 dikeluarkan dari MUST; diganti tema + override manual ditunda ke v0.2 (butuh palet light yang belum digambar desainer). | Mengikat. Opsi "Ikuti sistem" hanya menerima hasil gelap. Jangan mengarang token light — itu akan melanggar D8. Lihat `keputusan.md` 2026-11-12 |
| D13 | ~~Dependency injection: Hilt atau Koin?~~ **DIPUTUSKAN 2026-11-12: Hilt.** | Mengikat. Sebab: Metrolist/EchoMusic memakai Hilt → pola yang ditiru M1 langsung sejajar; dukungan `hiltViewModel()` di Compose sudah jadi jalur utama. Koin ditolak karena menambah DSL sendiri tanpa keuntungan di proyek satu-modul |
| D14 | **Sapaan di Home + nama di profil.** Desain menulis "Good evening, **Alex**" dan punya tombol Edit. Tapi v0.1 tanpa login (D2) — tidak ada nama dari akun. Pakai sapaan tanpa nama, atau minta nama sekali lalu simpan di DataStore? | Default: **sapaan tanpa nama** (`Good evening`). Nama opsional + tombol Edit ditunda ke v0.2. Kunci i18n sudah siap dua-duanya (`home.greeting_evening`, `profile.edit`) |
| D15 | **Fitur yang ada di desain tapi tidak ada di v0.1** — chip `Podcasts`/`Downloaded`, baris `Normalize volume`, `Equalizer`, `Crossfade`, `Downloads on mobile data`, `New releases`, `Sleep Timer`, `Download for Offline`, kartu `Offline Storage`. Dirender sebagai baris "segera hadir" (disabled), atau tidak dibuat sama sekali? | Default: **tidak dibuat sama sekali** — UI mati yang tidak bisa ditekan lebih membingungkan daripada UI yang belum ada. Kalau dipilih "tampil disabled", catat di `keputusan.md` dan tambahkan ke `docs/I18N-KEYS.md` §15 |
| D16 | **Lagu yang sama dua kali di satu playlist.** Skema `flows/02` memakai `PRIMARY KEY (playlist_id, track_id)` → duplikat ditolak. | Default: **tolak duplikat** (perilaku Spotify/Apple Music). Kalau diubah ke `(playlist_id, position)`, `ON CONFLICT` harus ditangani dan `idx_playlist_position` tetap unik |

---

## Lampiran A — Ringkasan verifikasi klaim 2026-11-09

| Klaim | Status | Bukti |
|---|---|---|
| Biaya Rp 0 | Benar | Semua komponen: Android Studio, Kotlin, Compose, Media3, NewPipeExtractor, SponsorBlock API, GitHub Actions — gratis |
| Play Store bukan opsi | Benar | Aplikasi tipe ini ditolak karena melanggar ToS |
| NewPipeExtractor GPL-3 | Benar | `TeamNewPipe/NewPipeExtractor` — GPL-3.0, aktif (push 2026-11-09) |
| Ghostery adblocker bisa dipakai | **Salah/kabur** | MPL-2.0 benar, tapi library ini untuk konteks browser/Electron. Di pemutar native tidak ada request iklan untuk difilter. Tidak dipakai |
| "GPL hilang sebagai penghalang" | Benar | Karena proyek memang GPL-3, kompatibilitas aman |
| Risiko utama = PO token | Benar | Isu terbuka NewPipeExtractor (Agu 2026): kualitas dibatasi 360p meski bg-helper terkonfigurasi |
| Est. 3–6 minggu part-time | Wajar | Rincian per milestone di §10 |
| "Pemeliharaan berlangganan, bukan sekali jadi" | Benar | Lihat R5 + §10 |
