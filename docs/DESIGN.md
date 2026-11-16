# DESIGN.md — Refy

**Sumber:** Stitch project **Electric Purple Music App** — `projects/9190295645942987043`
**Design system:** **Nocturne Audio** — `assets/9feaa5b53730417e8c1d163ffc007c63`
**Layar sumber:** **20 entri = 18 layar UI + 2 aset gambar** (dari 18 layar itu, 1 adalah artefak; jadi **17 layar nyata**), diunduh read-only ke `.stitch/electric-purple/` (HTML + PNG)
**Layar Home yang dipilih:** `home-screen-243ea0d8.html`. Varian *High Density Dark Mode* sudah dihapus desainer dari Stitch — tidak ada lagi di snapshot.
**Status:** snapshot ditarik ulang **2026-11-10** · **verifikasi ulang byte-identik 2026-11-12** (18/18 layar `SAMA`) · `updateTime` Stitch `2026-11-10T18:29:53.886928Z` · isi snapshot berasal dari tarik-ulang 2026-11-10 (9 layar berubah, 1 layar baru, 1 aset baru, **1 layar dihapus** `Downloads Management` lama `2f6ecab6`). Laporan selisih: `.stitch/DELTA-2026-11-10.md`

> **Catatan `updateTime`:** tiga dokumen pernah mencatat tiga nilai berbeda
> (`09:28:11Z` di `index.md`, `10:13:02Z` di sini, dan `09:28:11Z` di DELTA).
> Nilai yang benar-benar dibaca dari Stitch pada 2026-11-12 adalah
> **`18:29:53.886928Z`**. `updateTime` bergerak setiap kali project disentuh —
> termasuk perubahan yang tidak menambah layar — jadi ia **bukan** bukti isi
> snapshot. Buktinya adalah `verify-docs-vs-stitch.py` yang membandingkan HTML
> per layar (18/18 `SAMA`), bukan angka timestamp.

Dokumen ini adalah acuan token untuk implementasi. Semua angka di bawah **diukur dari HTML layar** (`scripts/measure-screens.py`, `scripts/analyze-snapshot.py`), bukan dikira-kira. Kalau ada pertentangan antara HTML layar dan manifest design system, aturan pemutusnya ada di §10.

> **Nomor berkas kini memuat 8 karakter id layar** (`home-screen-243ea0d8.html`). Stitch pernah memuat **dua** layar berjudul `Downloads Management`; yang lama (`2f6ecab6`) sudah dihapus desainer 2026-11-10, sisa `b26aa75b` (**D10 selesai**). Akhiran id tetap dipertahankan supaya tabrakan judul tidak terulang.

---

## 1. Palet — **DIPUTUSKAN: Kubu B**

> **KEPUTUSAN D8 (2026-11-09): palet yang dipakai adalah Kubu B — prosa brand.**
> Canvas `#09090B` · Surface `#141418` · Accent `#8B5CF6` · gradien `#A855F7 → #6366F1`.
> Nilai di bawah ini **mengikat**. Jangan pakai token manifest (`#131315` / `#d0bcff`) sebagai nilai implementasi.

### Mengapa Kubu B

Design system `Nocturne Audio` berisi dua palet yang saling bertentangan: **token** manifest (`surface #131315`, `primary #d0bcff`) dan **prosa** brand (`Base Canvas #09090B`, `Surface Elevation #141418`, `Primary Accent #8B5CF6`). Ambiguitas ini sudah diselesaikan dengan keputusan manusia (D8), bukan dengan asumsi.

Ukuran ulang 2026-11-10, dari blok `tailwind.config` tiap layar (nilai seed saat layar digenerate):

| Ukuran | Jumlah |
|---|---|
| `tailwind.config` masih Kubu A (`primary #d0bcff`, `bg #131315`) | **17 layar** |
| `tailwind.config` sudah Kubu B (`primary #8b5cf6`, `bg #09090b`) | **1 layar** (`album-detail-after-hours`) |

Di markup, 3 layar sudah menempel `#8B5CF6` inline walau config-nya Kubu A (`album-detail-tracklist`, `artist-profile`, `playlist-detail-late-night-drives`), sisanya murni Kubu A atau tidak menyebut aksen. Alat ukurnya: `python scripts/analyze-snapshot.py`.

Kubu B dipilih karena:
1. **Prosa brand menyebutnya lebih eksplisit** — dinyatakan sebagai peran, bukan hasil generate.
2. **Layar terbaru memakai Kubu B.** `album-detail-after-hours` (paling akhir disentuh) sudah pindah penuh: `primary #8b5cf6`, `background #09090b`. Empat layar lain memakai `#8B5CF6` inline walau `tailwind.config`-nya masih Kubu A. Desainer bergerak ke arah itu.
3. **Nama project "Electric Purple"** cocok dengan `#8B5CF6` (ungu listrik); `#d0bcff` adalah lavender pucat.
4. Kubu A adalah hasil auto-generate Material 3 dari seed color — konsisten secara internal, tapi bukan keputusan desain.

### Palet mengikat

| Peran | Nilai | Dipakai untuk |
|---|---|---|
| Base canvas | `#09090B` | latar halaman, area gulir |
| Surface | `#141418` | kartu, panel sheet, kontainer dasar |
| Surface elevated | `#1C1C22` | kartu terangkat, chip inaktif |
| Surface highest | `#1F1F26` | keadaan tertekan / terpilih |
| Text primary | `#FFFFFF` | judul lagu, heading, ikon aktif |
| Text muted | `#A1A1AA` | artis, durasi, metadata |
| Text faint | `#71717A` | placeholder, label nonaktif |
| Border / divider | `#27272A` pada 40–60 % | garis 1px, tepi kaca |
| Accent primary | `#8B5CF6` | aksi utama, progress, keadaan sedang diputar |
| Accent terang | `#A855F7` | ujung gradien, glow |
| Accent gelap | `#6366F1` | ujung gradien indigo |
| Error | `#FFB4AB` / kontainer `#93000A` | hapus, gagal |
| On-accent | `#FFFFFF` | ikon di atas tombol accent |

**Gradien (hanya di tempat yang disebut):**
```
linear-gradient(135deg, #A855F7 0%, #6366F1 100%)   tombol putar utama, progress fill
```
Gradien dilarang di: latar halaman, kartu biasa, header, bottom nav, teks.

**Glow (bayangan berwarna):**
```
tombol putar aktif     0 12px 32px -4px rgba(139, 92, 246, 0.35)
ikon aktif di nav      drop-shadow 0 0 16px rgba(168,85,247,.7) + 0 0 28px rgba(168,85,247,.35)
mini-player artwork    0 0 20px rgba(168,85,247,.7) + 0 0 32px rgba(147,51,234,.5)
progress bar aktif     0 0 16px rgba(168,85,247,.85)
```

### Tabel remap untuk layar Kubu A

**17 layar** masih memakai palet Kubu A di blok `tailwind.config`-nya (diukur ulang 2026-11-10 sesudah `2f6ecab6` dihapus). Saat implementasi, nilai warna **wajib diremap** ke Kubu B. Tata letak dan ukurannya tidak berubah.

| Nilai Kubu A (di HTML) | → Nilai Kubu B (dipakai) |
|---|---|
| `#131315` (surface/background) | `#09090B` canvas · `#141418` surface |
| `#0e0e10` (surface-lowest) | `#0E0E10` — tetap (header & nav) |
| `#1c1b1d` (surface-container-low) | `#141418` |
| `#201f22` (surface-container) | `#1C1C22` |
| `#2a2a2c` (surface-container-high) | `#1F1F26` |
| `#353437` (surface-container-highest) | `#27272A` |
| `#d0bcff` (primary) | `#8B5CF6` |
| `#a078ff` (primary-container) | `#A855F7` |
| `#6d3bd7` (inverse-primary) | `#6366F1` |
| `#e5e1e4` (on-surface) | `#FFFFFF` |
| `#cbc3d7` (on-surface-variant) | `#A1A1AA` |
| `#958ea0` (outline) | `#71717A` |
| `#494454` (outline-variant) | `#27272A` |
| `#ddb7ff` (secondary) | `#A855F7` |
| `#6f00be` (secondary-container) | `#6366F1` |
| `#c0c1ff` (tertiary) | `#A1A1AA` |
| `#ffb4ab` / `#93000a` (error) | tetap |

Layar Kubu A — perlu remap (17): home-screen, active-search-results, library-search-screen, your-library, now-playing-screen, playback-queue, track-options-bottom-sheet, add-to-playlist, create-playlist, expanded-live-lyrics, user-profile-audio-settings, settings, downloads-management (`b26aa75b`), album-detail-tracklist, artist-profile, playlist-detail-late-night-drives, nocturne-music-streaming-prototype.

**Hanya 1 layar sudah Kubu B penuh:** album-detail-after-hours (`primary #8b5cf6`, `background #09090b`).

**Home Screen terpilih (`home-screen-243ea0d8.html`) ada di Kubu A** — dipakai tata letaknya, warnanya diremap sesuai tabel di atas.

---

## 2. Tipografi

Satu keluarga: **Plus Jakarta Sans**. Tidak ada font kedua.

| Token | Ukuran / berat / tinggi | Pemakaian |
|---|---|---|
| `display-hero` | 40 / 800 / 48, tracking −0.03em | judul hero (hanya layar player penuh) |
| `headline-lg` | 32 / 700 / 40, −0.02em | judul bagian besar di tablet |
| `headline-lg-mobile` | 26 / 700 / 34, −0.02em | judul layar di ponsel |
| `headline-md` | 22 / 600 / 28, −0.015em | judul bagian (home, album) |
| `headline-sm` | 18 / 600 / 24, −0.01em | sub-judul bagian |
| `body-lg` | 16 / 500 / 24, −0.005em | teks isi utama |
| `body-md` | 14 / 400 / 20 | metadata, deskripsi |
| `body-sm` | 12 / 400 / 16, +0.01em | keterangan tambahan |
| `label-lg` | 14 / 600 / 18, +0.01em | label tombol |
| `label-md` | 12 / 600 / 16, +0.02em | label chip, tab |
| `label-xs` | 10 / 700 / 12, +0.06em | label tab nav, badge (HURUF BESAR) |

Aturan:
- Judul lagu **satu baris**, elipsis di ujung. Nama artis **satu baris**, elipsis.
- Angka durasi, timestamp, jumlah stream: figur tabular.
- Teks putih murni hanya untuk judul, heading, dan keadaan sedang diputar. Sisanya `#A1A1AA`.
- Label tab nav memakai `label-xs` dengan `tracking-wider` (huruf kapital).

---

## 3. Spasi & Grid

Token: `space-xxs` 4 · `space-xs` 8 · `space-sm` 12 · `space-md` 16 · `space-lg` 24 · `space-xl` 32 · `space-2xl` 48.
Margin tepi: **`margin-mobile` 16px**, `margin` 24px di layar lebar. Gutter karusel: 12px.

**Angka nyata dari layar (ini yang dipakai, bukan tebakan):**

| Elemen | Nilai terukur |
|---|---|
| Padding tepi konten | 16px (`px-margin-mobile`) |
| Header tetap | tinggi **64px** (`h-16`) |
| Bottom nav | tinggi **68px** (`h-[68px]`, pil mengambang) — diukur ulang 2026-11-10 |
| Mini-player | tinggi **56px** (`h-14`), melayang **88–92px** di atas dasar (dulu 64px) |
| Jarak aman bawah konten | **144px** (`pb-36`, 4 layar) atau **160px** (`pb-40`, 6 layar); ada juga `pb-44`/`pb-52` sekali. Pakai **160px** untuk aman |
| Baris lagu | **56px** (`h-14`) |
| Artwork baris lagu | **40px** (`w-10 h-10`), sudut 8px |
| Artwork hasil pencarian | **44px** (`w-11 h-11`) |
| Kartu album di home | 144px (`h-36`) dan 176px (`h-44`) |
| Tombol ikon | **44px** (`h-11`) |
| Tombol utama | **48px** (`h-12`) |
| Progress bar | **2px**, menempel di tepi bawah mini-player |

Catatan: manifest menyebut "88px safe-area spacer", tapi HTML layar memakai **144–160px** (sejak nav jadi pil, `pb-36` mulai dipakai). Yang benar untuk implementasi adalah **160px** — angka 88px tidak cukup menutup mini-player + nav.

Nav setinggi 68px (pil) ditemukan di **9 layar**; 1 layar (artefak) masih bilah penuh; 8 layar tanpa nav. Mini-player `bottom-[92px]` di 8 layar dan `bottom-[88px]` di 1 layar (`home-screen`).

**Prinsip tata letak baru (menggantikan "mini-player melayang 64px"):** nav adalah pulau mengambang; mini-player adalah pulau kedua tepat di atasnya, dengan jarak kira-kira 20px. Keduanya tidak menyentuh dasar layar maupun tepi layar (margin horizontal 16px). Konten harus menyisakan ruang bawah ≥160px.

---

## 4. Bentuk

| Radius | Nilai | Dipakai untuk |
|---|---|---|
| `rounded-lg` | **8px** | artwork di baris lagu, chip kecil, badge |
| `rounded-xl` | **12px** | kartu, mini-player, panel sheet |
| `rounded-2xl` | **16px** | artwork besar, modal, hero player |
| `rounded-full` | 9999px | tombol putar, chip filter, tab aktif, scrub thumb |

Peringatan: manifest menulis "`rounded-lg` = 16px". Itu keliru terhadap Tailwind (yang dipakai HTML-nya, di mana `rounded-xl` = 12px dan `rounded-2xl` = 16px). Gunakan tabel di atas.

---

## 5. Elevasi & Efek Kaca

Empat tingkat, tanpa bayangan hitam opaque:

| Tingkat | Resep |
|---|---|
| 0 — canvas | `#09090B`, tanpa bayangan |
| 1 — kartu | `#141418` + garis 1px `rgba(255,255,255,.06)` |
| 2 — chrome mengambang | `rgba(20,20,24,.90)` + `backdrop-blur` + garis atas `rgba(255,255,255,.12)` |
| 3 — overlay penuh | `rgba(9,9,11,.88)` + `backdrop-filter: blur(32px)` |

**Resep baru hasil tarik ulang 2026-11-10 — bottom nav pil mengambang + mini-player di atasnya:**

```
bottom nav (pill)   fixed bottom-4|bottom-5, inset-x-4, max-w-sm, h-[68px], rounded-full
                    bg-[#121621]/85 · backdrop-blur-[30px]
                    border 1–1.5px solid rgba(255,255,255,.6)
                    shadow 0 12px 32px rgba(0,0,0,.65) + 0 0 12px rgba(255,255,255,.2)

mini-player         fixed bottom-[88px] … bottom-[92px]  (di atas pil, bukan menempel dasar)
                    backdrop-filter blur(36px) saturate(180%)
                    border 1px solid rgba(255,255,255,.15)
                    shadow inset 0 1px 1px rgba(255,255,255,.14) + 0 12px 32px rgba(0,0,0,.6)
```

**Peringatan risiko (R2) — dinaikkan tingkatnya oleh perubahan ini.** Sebelumnya nav menempel dasar dan mini-player melayang 64px di atasnya. Sekarang **dua permukaan kaca rona terang** (nav pil + mini-player) berdampingan dengan jarak kurang dari 20px, dan nav punya garis putih nyata + glow luar. Ini kombinasi yang paling mudah memicu pita warna di GPU Mali. Aturan §9 berlaku penuh: `RepaintBoundary` + `ColoredBox` dasar sebelum blur, jalur mundur tanpa blur wajib benar-benar diuji.

**Pemakaian blur terukur per elemen (Tailwind):**

| Elemen | Class | Radius blur |
|---|---|---|
| Header tetap | `backdrop-blur-xl` | 24px |
| Bottom nav | `backdrop-blur-xl` | 24px |
| Mini-player | `backdrop-blur-2xl` | 40px |
| Badge di atas artwork | `backdrop-blur-md` | 12px |
| Sheet kecil | `backdrop-blur-sm` | 4px |

**Peringatan implementasi — ini risiko nyata, bukan teori.** Tiga elemen kaca di atas **persisten** di hampir semua layar (header 24px, nav 24px, mini-player 40px). Di perangkat GPU Mali, `BackdropFilter` berlapis di area yang terus berubah memicu artefak pita merah — persis bug yang sudah pernah muncul di proyek Recify. Rencana penanganan ada di §9.

`filter: blur(36px/48px/75px)` yang juga muncul di beberapa layar adalah **glow dekoratif** (div radial-gradient kosong), bukan kaca. Ini boleh tetap, tapi sebaiknya di-prerender jadi aset PNG karena tidak berubah.

---

## 6. Komponen

### 6.1 Header tetap
Tinggi 64px. Latar `surface/80` + `backdrop-blur-xl` + garis bawah halus (`0 1px 8px rgba(0,0,0,.04)`). Kiri: judul layar atau nama sapaan. Kanan: tombol notifikasi 44px + avatar 32px bulat. Padding tepi 16px.

### 6.2 Bottom navigation — **PIL MENGAMBANG** (didesain ulang 2026-11-10)

Bentuk final menurut snapshot 2026-11-10 (**9 layar**, keputusan **D11** — pil mengambang): pil mengambang, **bukan** bilah menempel dasar.

| Properti | Nilai |
|---|---|
| Posisi | `bottom-4` (16px) atau `bottom-5` (20px), `inset-x-4`, `max-w-sm`, `mx-auto` |
| Tinggi | **68px** (bukan 64px) |
| Radius | `rounded-full` |
| Latar | `#121621` pada 85 % |
| Blur | `backdrop-blur-[30px]` |
| Garis | `1–1.5px solid rgba(255,255,255,.6)` |
| Bayangan | `0 12px 32px rgba(0,0,0,.65)` + `0 0 12px rgba(255,255,255,.2)` |

**Empat tab, dan ini berbeda dari PRD lama:**
```
Home  /  Discover  /  Library  /  Buat   →  implementasi memakai "Create" (D6)
```
Tab aktif: ikon + label putih dengan `drop-shadow` putih/ungu. Tab nonaktif: `#A1A1AA` … `#94A3B8`. Ikon 20–22px, label 10–11px.

**Belum seragam antar layar — jangan disalin buta:** nav lama masih ada di `nocturne-music-streaming-prototype` (`fixed bottom-0 w-full` + `backdrop-blur-xl`); `album-detail-tracklist` belum punya `bg-[#121621]`; `downloads-management-b26aa75b` navnya `px-2.5` sedangkan `your-library` `px-3`. Ambil satu varian sebagai kanon: **`home-screen-243ea0d8`**.

Status label tab per 2026-11-10 (diukur `scripts/analyze-snapshot.py`, lihat `.stitch/electric-purple/ANALYSIS.md`): **9 layar masih "Buat"**, **1 layar (`your-library`) sudah "Create"**, **8 layar tidak punya nav** (sheet/layar turunan). Dari 10 layar bernav, 9 perlu diganti, 1 sudah benar.

### 6.3 Mini-player (komponen tanda tangan)
Melayang **di atas** bottom nav — offset berubah 2026-11-10: **`bottom: 88px` … `92px`** (dulu 64px), mengikuti nav pil yang naik 16–20px dari dasar. Tinggi 56px, radius 12px, latar `#121621`/`surface-container-high` + `blur(36px) saturate(180%)`, garis `rgba(255,255,255,.15)`, bayangan `0 12px 32px rgba(0,0,0,.6)`.
Isi: artwork 40px (radius 8px, dengan glow ungu saat aktif) · judul satu baris · artis satu baris · tombol favorit 44px · tombol putar bulat 36px bergradien dengan glow · **progress bar 2px di tepi bawah** mini-player itu sendiri.
Ketuk badan → buka Now Playing. Geser ke bawah → tutup antrean.

### 6.4 Baris lagu
Tinggi 56px. Artwork 40px (radius 8px) · judul `body-md`-ish putih satu baris · artis `body-sm` muted satu baris · kanan: durasi tabular atau tombol overflow 44px.
Keadaan sedang diputar: judul berwarna `#8B5CF6`, latar baris `rgba(139,92,246,.08)`, durasi diganti glyph equalizer 3 batang beranimasi.

### 6.5 Kartu album / playlist
Artwork persegi radius 8px di dalam kontainer `#141418` radius 12px dengan padding dalam 12px. Tombol putar melayang muncul saat interaksi. Badge kecil di sudut kiri atas (mis. "WEEKLY", "#1 TRENDING") memakai `label-xs` + `backdrop-blur-md`.

### 6.6 Chip filter
Pil, padding vertikal 6px horizontal 14px. Inaktif: `rgba(20,20,24,.8)` + teks `#A1A1AA`. Aktif: ungu solid `#8B5CF6` **atau** gradien + teks putih.

### 6.7 Tombol
- **Primer (putar):** lingkaran, isi gradien `#A855F7 → #6366F1`, ikon putih, glow ungu. 48px untuk aksi besar, 36px di mini-player.
- **Sekunder:** `#141418` + garis `rgba(255,255,255,.12)` + teks putih, tinggi 48px.
- **Ghost / kontrol media:** transparan, ikon `#A1A1AA` → putih saat disentuh, target 44px.

### 6.8 Search field
Kontainer pil atau radius 12px, isi `#141418`, glyph cari inset `#A1A1AA`, placeholder `#71717A`. Fokus: garis luar ungu `#8B5CF6`.

### 6.9 Sheet
Radius atas 16–24px, latar `surface` dengan garis atas, tinggi penuh untuk antrean. Header sheet: judul + aksi teks ("Clear all" / "Hapus semua").

---

## 7. Inventaris layar

**20 entri = 18 layar UI + 2 aset gambar.** Dari 18 layar itu **17 layar nyata** dan **1 artefak** (§ bawah). Transkrip lengkap ada di `.stitch/electric-purple/screens.json`. Nomor berkas memuat 8 karakter id layar.

| # | Layar sumber | Berkas | Palet cfg | Peran dalam aplikasi |
|---|---|---|---|---|
| 1 | Home Screen | `home-screen-243ea0d8.html` | A | **dipakai** — beranda (tata letak + kanon nav pil) |
| 2 | Active Search Results | `active-search-results-13c3c6cd.html` | A | hasil pencarian aktif |
| 3 | Library & Search Screen | `library-search-screen-3a4eb7d9.html` | A | tab Discover (jelajah + cari) |
| 4 | Your Library | `your-library-be4f34bd.html` | A | tab Library · **satu-satunya yang sudah pakai "Create"** |
| 5 | Album Detail & Tracklist | `album-detail-tracklist-76a16cec.html` | A | detail album |
| 6 | Album Detail - After Hours | `album-detail-after-hours-f4bb6b8c.html` | **B** | varian detail album ("After Hours (Deluxe)") — **satu-satunya Kubu B penuh** |
| 7 | Playlist Detail - Late Night Drives | `playlist-detail-late-night-drives-2f7236b9.html` | A | detail playlist |
| 8 | Artist Profile | `artist-profile-ebadb5ac.html` | A | profil artis |
| 9 | Now Playing Screen | `now-playing-screen-57a3bcf2.html` | A | pemutar penuh |
| 10 | Playback Queue | `playback-queue-a12c7d72.html` | A | antrean |
| 11 | Expanded Live Lyrics | `expanded-live-lyrics-002312fb.html` | A | lirik penuh |
| 12 | Track Options Bottom Sheet | `track-options-bottom-sheet-23b6e965.html` | A | menu per lagu |
| 13 | Add to Playlist | `add-to-playlist-61e27485.html` | A | tambah ke playlist |
| 14 | Create Playlist | `create-playlist-156d626d.html` | A | buat playlist |
| 15 | User Profile & Audio Settings | `user-profile-audio-settings-58443188.html` | A | profil + ringkasan audio |
| 16 | Settings | `settings-d60055b1.html` | A | pengaturan aplikasi (tanpa nav) |
| 17 | Downloads Management | `downloads-management-b26aa75b.html` | A | 780×2110 · dibuat 2026-11-10 · **D10 selesai**: layar lama `2f6ecab6` (780×2178) sudah dihapus desainer |
| 18 | Nocturne Music Streaming Prototype | `nocturne-music-streaming-prototype-a78bba57.html` | A | **artefak**, bukan layar (§ di bawah) |

**Bukan layar:**

| Entri | Berkas | Sebab |
|---|---|---|
| Nocturne Music Streaming Prototype | `nocturne-music-streaming-prototype-a78bba57.html` | Artefak salin/uji. Dimensi 390×884, tanpa screenshot, tanpa createTime. **Sejak 2026-11-10 TIDAK lagi byte-identik dengan Home Screen** (md5 `444ea47…` vs `14f52cf…`) — Home Screen ikut berubah, artefak tidak. Jangan diimplementasikan. |
| Modern stylish portrait avatar of a young cool creative person named Alex… | — (0 byte HTML) | **Aset gambar avatar** (1024×1024), bukan layar. |
| `image.png` | — (0 byte HTML) | **Aset gambar baru** 454×320 (id `8804363142684983345`), muncul 2026-11-10. Isi belum ditinjau. |

**Riwayat jumlah:** 14 → 16 → 17 unik (+1 duplikat) → 19 entri / 18 judul → **20 entri = 18 layar + 2 aset** (2026-11-10, sesudah `Downloads Management` lama `2f6ecab6` dihapus desainer).

**Perubahan pada tarik ulang 2026-11-10:** 9 layar berubah isi (Your Library, Album Detail & Tracklist, Home Screen, Album Detail - After Hours, Library & Search Screen, Active Search Results, Playlist Detail, Artist Profile, + satu `Downloads Management`), 1 layar baru (`b26aa75b`), 1 aset baru (`image.png`), 1 layar dihapus (`2f6ecab6`). Rincian: `.stitch/DELTA-2026-11-10.md`.

**Cacat aset yang harus diketahui saat implementasi:**

1. **Dua palet bercampur.** **17 layar** masih memakai token manifest di `tailwind.config`-nya (diukur `scripts/analyze-snapshot.py`), 1 layar (`album-detail-after-hours`) sudah prosa brand penuh. **Sudah diputuskan (D8): Kubu B.** Layar kubu token wajib diremap — tabel remap di §1. *(Dokumen ini pernah menulis "18 layar"; angka yang benar adalah 17 — lihat §12.)*
2. **`<title>` kosong di hampir semua layar** — hanya `track-options-bottom-sheet.html` yang punya. Yang dibagi bersama adalah **`<h1>`**: teks `"Library And Search Screen"` muncul sebagai `<h1>` di **tiga** layar (#2 Active Search, #3 Library & Search, #4 Your Library).
3. **Dua tab aktif sekaligus** — `library-search-screen.html` menandai `aria-current="page"` pada **Discover dan Library** sekaligus. Tidak valid; pilih satu per layar.
4. **Tab "Buat" berbahasa Indonesia**, tiga tab lain berbahasa Inggris. **Sudah diputuskan (D6): English penuh** — implementasi memakai **"Create"**. Lihat §8.
5. `album-detail-tracklist.html` dan `album-detail-after-hours.html` sama-sama menggambarkan album "After Hours" (yang kedua "After Hours (Deluxe)") — satu fitur digambar dua kali. Pilih satu saat implementasi.
6. Layar **#16 Settings tidak ada di bottom nav** (tanpa nav sama sekali) — layar turunan, bukan tab. Sejak 2026-11-10 **kedua #17 Downloads Management justru punya nav pil penuh**, jadi klaim lama "nav-nya kosong" tidak berlaku lagi.
7. Empat layar kubu prosa (#5, #6, #7, #8) adalah yang paling baru dibuat dan **tidak perlu diremap**.
8. Permukaan kaca per layar bertambah sejak 2026-11-10. `settings-d60055b1` 1× blur (header). `downloads-management-2f6ecab6` header + nav pil + mini-player = **3 permukaan**, dan nav+mini-player hanya terpisah ~20px. Ini kasus uji terberat untuk R2 — lihat §9.

9. **Dua bottom nav berbeda hidup bersama.** 10 layar memakai pil mengambang, `nocturne-music-streaming-prototype` masih memakai bilah `fixed bottom-0 w-full`. Ambil `home-screen-243ea0d8` sebagai kanon; jangan menyalin dari artefak.
10. ~~Dua layar berjudul `Downloads Management`.~~ **Selesai:** desainer menghapus versi lama `2f6ecab6`; sisa `b26aa75b` (780×2110) sebagai acuan. **D10 tertutup 2026-11-10.**

**Alur pengaturan yang terbaca dari aset:** `#4 Your Library` / header avatar → `#15 User Profile & Audio Settings` (profil + ringkasan audio) → kartu "App Settings" → `#16 Settings`. Untuk unduhan: `#16 Settings` → `#17 Downloads Management`, atau dari kartu "Offline Storage" di `#15`.

---

## 8. Bahasa

Desain mencampur dua bahasa:
- **Inggris:** "Good evening, Alex", "Made For You", "Trending Now", "Now Playing", "Recently Played", "See all", "Continue Listening", "Your Library", "Add to Playlist", "Create Playlist", "Clear all", "Save as Playlist", "Lossless Audio", "Following".
- **Indonesia:** tab "Buat".

**KEPUTUSAN (D6, 2026-11-09): English penuh.** Antarmuka memakai bahasa Inggris di semua tempat. Tab "Buat" pada desain diganti **"Create"**.

Aturan implementasi:
1. Semua string masuk resource i18n sejak hari pertama. Tidak ada teks literal di Composable.
2. Kunci tidak berbau bahasa: `home.greeting`, `nav.create`, `queue.clear_all`.
3. Default di `values/` (English). Terjemahan Indonesia menyusul di `values-in/` tanpa mengubah kode.
4. Nama tab final: `Home` · `Discover` · `Library` · `Create`.
5. Semua label lain mengikuti teks Inggris yang sudah ada di desain — jangan diterjemahkan.

**Daftar kunci lengkap: `docs/I18N-KEYS.md`.** Berkas itu inventaris terkunci untuk 11 layar MVP (aturan penamaan, kunci wajib per layar, kunci bentuk jamak/format angka). Desain §8 hanya memberi 3 contoh — tanpa inventaris, Composable akan menumpuk teks hardcoded lalu "diperbaiki nanti", dan itu tidak pernah terjadi. Audit M5 memakai berkas itu sebagai daftar periksa.

**Catatan tema:** desain hanya punya palet gelap (Kubu B). **v0.1 dark-only** (D12) — tidak ada `values-night/` untuk warna, hanya `values-in/` untuk bahasa. String tetap satu set; tema tidak memengaruhi teks.

---

## 9. Penanganan risiko render (wajib)

Tiga lapisan kaca persisten (header 24px, nav 24px, mini-player 40px) adalah titik paling rawan di perangkat kelas bawah.

| Langkah | Aturan |
|---|---|
| 1 | Bungkus tiap permukaan kaca dengan `RepaintBoundary` di sisi luar, lalu `ClipRRect`, lalu `ColoredBox` dasar (canvas) **sebelum** efek blur. Tanpa lapisan dasar, sampling tile memori bisa menghasilkan pita warna (bug Mali). |
| 2 | Jangan menumpuk dua `BackdropFilter` yang saling menutupi. Header, nav, dan mini-player tidak boleh beririsan. |
| 3 | Sediakan jalur mundur **tanpa blur**: kaca diganti warna solid `rgba(20,20,24,.97)` + garis 1px saat (a) pengaturan "kurangi transparansi" aktif, (b) perangkat terdeteksi GPU kelas rendah, atau (c) pengguna memilih hemat baterai. Wajib diuji, bukan sekadar ada. |
| 4 | Glow dekoratif (`filter: blur(36–75px)`) di-prerender sebagai PNG. Statis, jadi tidak perlu dihitung ulang tiap frame. |
| 5 | Uji di perangkat Mali nyata sebelum rilis. Emulator tidak mereproduksi artefak ini. |

---

## 10. Aturan pemutus konflik

Design system `Nocturne Audio` mengandung dua palet yang bertentangan (token manifest vs prosa brand). Ambiguitas itu **sudah diselesaikan** oleh keputusan D8: **Kubu B menang** (§1).

| Jenis konflik | Yang menang | Alasan |
|---|---|---|
| **Warna** | **Kubu B** — nilai mengikat di §1 | Diputuskan pengguna (D8, 2026-11-09). Layar Kubu A wajib diremap memakai tabel remap §1; jangan salin hex dari HTML apa adanya. |
| Ukuran, radius, spasi, struktur | **HTML layar** | Itu yang benar-benar tergambar. Contoh: radius kartu 12px (`rounded-xl`), bukan 16px seperti klaim prosa. |
| Tipografi (keluarga font, skala) | **manifest** | Keluarga font tunggal (Plus Jakarta Sans) dan skala 11 tingkat konsisten, tidak diperdebatkan. |
| Perilaku/animasi | **HTML layar** | Deskripsi prosa tidak memuat detail interaksi. |
| Kalau masih ragu | **tulis di `keputusan.md`** | Jangan diam-diam memilih; keputusan tak tercatat akan hilang. |

Ringkasnya: **warna dari §1 (Kubu B), ukuran dari HTML, font dari manifest.** Jangan menanam nilai hex langsung di Composable — pakai token tema.

---

## 11. Berkas pendukung

```
.stitch/electric-purple/home-screen-243ea0d8.html    layar terpilih (tata letak + kanon nav pil)
.stitch/electric-purple/*.html, *.png                18 layar + 2 aset · nama = slug + 8 char id
.stitch/electric-purple/nocturne-music-streaming-prototype-a78bba57.html
                                                      artefak — jangan dipakai
.stitch/electric-purple/DESIGN.md                     manifest asli Nocturne Audio
.stitch/electric-purple/DESIGN-manifest.json          token lengkap (colors/type/radius/spacing/surface)
.stitch/electric-purple/design-systems.json           id asset: assets/9feaa5b53730417e8c1d163ffc007c63
.stitch/electric-purple/screens.json                  indeks 20 entri (18 layar + 2 aset)
.stitch/electric-purple/SCREEN-TEXT.txt               teks terlihat per layar
.stitch/electric-purple/STRUCTURE.txt                 tab nav, heading, mini-player per layar
.stitch/electric-purple/ANALYSIS.md                   tabel audit palet/nav/label tab per layar
.stitch/DELTA-2026-11-10.md                           laporan selisih Stitch hidup vs snapshot
scripts/stitch_client.py                              klien MCP + pembaca STITCH_API_KEY + SNAP absolut
scripts/fetch-electric-purple.py                      tarik ulang seluruh aset (read-only)
scripts/resync-screens.py                             deteksi layar baru/berubah/dihapus
scripts/analyze-snapshot.py                           audit per layar: palet, nav, label tab
scripts/census-palette.py                             sensus palet per layar
scripts/measure-screens.py                            pengukur metrik
scripts/extract-structure.py                          pengambil struktur & tab
scripts/verify-docs-vs-stitch.py                      pembanding dokumen vs Stitch (live)
scripts/check-consistency.py                          pemeriksa konsistensi dokumen — 12 gerbang (A–L), exit 1 kalau merah
scripts/test-gates.py                                 uji negatif: buktikan tiap gerbang BISA gagal
scripts/render-diagrams.py                            render Mermaid -> SVG
scripts/quarantine-stale.py                           karantina artefak usang
scripts/summarize-screens.py                          ekstrak teks per layar
scripts/stitch-query.py                               query cepat: projects/screens/ds
scripts/fetch-missing-png.py                          tarik PNG yang belum ada
```

**Yang di-commit vs tidak (lihat `.gitignore`, dikoreksi 2026-11-12):**

| Jalur | Ikut commit? | Sebab |
|---|---|---|
| `docs/`, `flows/`, `keputusan.md`, `backlog.md`, `index.md` | ya | dokumen inti |
| `.stitch/electric-purple/DESIGN.md` (manifest asli) | ya | kecil, sumber token §1, dikutip dokumen |
| `.stitch/DELTA-*.md` | ya | laporan selisih, dikutip §11 + PRD §9 |
| `.stitch/electric-purple/*.html`, `*.png`, `screens.json`, `*.txt` | tidak | beberapa MB, ditarik ulang dengan `scripts/resync-screens.py` |
| `.rea/` (analisis APK EchoMusic) | tidak | 806 MB; bukti M1, disimpan lokal |
| `docs/diagrams/*.mmd` | **ya** | sumber diagram, ~1 KB/berkas, tidak bisa dibuat tanpa tangan manusia |
| `docs/diagrams/*.svg`, `*.png` | tidak | turunan ±3,4 MB, dibuat ulang `scripts/render-diagrams.py` |

> Pola `.gitignore` **wajib** memakai `.stitch/*` (bukan `.stitch` folder polos). Git tidak masuk ke direktori yang diabaikan, jadi negasi `!.stitch/...` diam-diam tidak berlaku. Ditemukan 2026-11-12: `git check-ignore -v .stitch/electric-purple/DESIGN.md` masih menghasilkan hit padahal sudah dinegasi.

**Catatan operasional skrip (2026-11-10):** semua skrip bisa dijalankan dari direktori mana pun (path snapshot absolut, dihitung dari akar repo). Dulu menjalankannya dari dalam `scripts/` membuat folder sampah `scripts/.stitch`. Nama berkas **wajib** memakai `slug(title, screen)` karena ada dua layar berjudul sama.

Desain Stitch **tidak diubah** oleh skrip mana pun. Semua operasi hanya baca; `download_file` dan `list_*` tidak menulis apa pun ke Stitch.

**Kredensial:** semua skrip membaca `STITCH_API_KEY` dari environment atau `.env` (lihat `.env.example`). Tidak ada kunci tertanam di kode. `.env` sudah masuk `.gitignore`.

---

## 12. Catatan keandalan dokumen ini

Dokumen ini pernah salah lebih dari sekali dan sudah dikoreksi setelah verifikasi ke Stitch hidup. Yang pernah salah, supaya tidak terulang:

| Klaim lama | Kenyataan |
|---|---|
| "Palet kanonik: 13 dari 14 layar sepakat dengan manifest" | Salah. Dua palet bercampur; hitungannya berubah tiap kali layar ditambah. |
| "Tiga layar berbagi `<title>`" | Salah. Hampir semua layar tidak punya `<title>`; yang dibagi adalah `<h1>`. |
| "14 layar" | Usang → 16 → **17**. Desainer masih aktif menambah dan menghapus. |
| "16 layar" | Usang. Sekarang **17 layar UI** + 1 aset gambar. |
| "Pengaturan ada di dalam User Profile" | Usang. Sejak 2026-11-09 ada layar **Settings** sendiri (#16). |
| "Home High Density adalah varian alternatif" | Usang. Desainer **menghapus** layar itu dari Stitch. |
| "Warna → manifest menang" | Salah dan berbahaya. Diganti: warna → Kubu B (D8). |
| "11 layar perlu diremap" | Usang. Sekarang **17 layar** perlu diremap menurut blok `tailwind.config` (diukur 2026-11-12; 1 layar sudah Kubu B penuh). |
| "17 layar unik + 1 duplikat" | Usang. **20 entri = 18 layar + 2 aset**; dari 18 layar itu 1 artefak → **17 layar nyata**. |
| "19 entri layar / 18 judul, ada dua `Downloads Management`" | Usang. Versi lama `2f6ecab6` **dihapus desainer** 2026-11-10; sisa satu. |
| "`nocturne-music-streaming-prototype` byte-identik dengan Home Screen" | Salah sejak 2026-11-10. Home Screen berubah, artefak tidak. |
| "bottom nav 64px menempel dasar, blur 24px" | Usang. Sekarang **pil mengambang 68px, blur 30px**, nav tidak menempel dasar. |
| "`downloads-management.html` nav-nya kosong" | Usang. Layar `Downloads Management` yang tersisa (`b26aa75b`) punya nav pil penuh. |
| "18 layar perlu diremap" | Usang. Sesudah `2f6ecab6` dihapus: **17 layar**. |
| "bottom nav pil 68px, **mini-player 64px**" (PRD §7.1) | Salah. Mini-player ikut naik ke **88–92px** waktu nav jadi pil — kalau tetap 64px, ia menutupi pil. Dikoreksi 2026-11-12. |
| "`.mmd` diagram cukup di-ignore, `.svg`-nya sudah ada" | Salah. `.svg` adalah **turunan**; meng-ignore `.mmd` berarti sumber diagram hilang dari repo. Dikoreksi 2026-11-12 (`keputusan.md`). |
| "Gerbang konsistensi hijau = dokumen siap" | Salah. Alat versi lama hanya mencocokkan kata wajib, jadi nomor layar, ukuran piksel, skema DB, dan `.gitignore` tidak pernah diperiksa. Hijau ≠ siap; 11 gerbang A–K sekarang, dibuktikan bisa gagal oleh `scripts/test-gates.py`. |
| "Nilai `#N` di dokumen bisa dipercaya karena sudah pernah dikoreksi" | Salah. Nomor layar salah **dua kali** di proyek ini. Karena itu diperiksa otomatis (gerbang G), bukan dengan disiplin manual. |

**Pelajaran:** dokumen desain bukan artefak sekali jadi. Selama desainer masih bekerja, jumlah layar dan isinya berubah. Setiap kali dokumen ini dipakai sebagai acuan implementasi, jalankan `scripts/check-consistency.py` (11 gerbang) — jangan percaya snapshot lama, dan jangan percaya hasil pemeriksa lama. Untuk membuktikan pemeriksanya masih tajam: `scripts/test-gates.py`.