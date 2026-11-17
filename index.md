# Refy — Index

**Nama kerja:** Refy
**Bentuk:** Klien pemutar musik Android, open source, tanpa iklan, tanpa login
**Sumber musik v0.1:** katalog YouTube Music via extractor komunitas
**Lisensi rencana:** GPL-3.0-or-later
**Repositori:** `D:\Coding\Refy`
**Status:** **M0 berjalan** — audit kelayakan 2026-11-12 menutup 8 penghambat; PRD v0.2, desain tersinkron dari Stitch (**dicek ulang 2026-11-12**, `updateTime` `2026-11-10T18:29:53Z`). **Fase 0–2 selesai**: scaffold Gradle, tema `NocturneTheme`, dan kerangka UI (pil nav 4 tab + `MiniPlayer` + `ScaffoldShell`) terpasang di perangkat; CI run #3 hijau. Berikutnya Fase 3 (#1 Home → #3 Discover → #4 Library → #2 Search) — urutan di `PLAN.md`
**Desain:** Stitch project **Electric Purple Music App** (`projects/9190295645942987043`), design system **Nocturne Audio** (`assets/9feaa5b53730417e8c1d163ffc007c63`), **18 layar UI + 2 aset** (17 layar nyata + 1 artefak)
**Perhatian:** palet **sudah dikunci** — Kubu B (D8). Bottom nav **pil mengambang** (D11). Lihat `docs/DESIGN.md` §1 (remap) dan §6.2 (nav).

---

## Dokumen

| Berkas | Isi |
|---|---|
| `docs/PRD.md` | PRD lengkap: tujuan, KPI, persona, MoSCoW, user story Gherkin, NFR, risiko, milestone |
| `docs/DESIGN.md` | **Token desain nyata** dari Stitch: palet, tipografi, spasi, radius, blur, komponen, aturan pemutus konflik |
| `docs/I18N-KEYS.md` | **Inventaris kunci string** 11 layar MVP — daftar periksa audit M5 |
| `flows/00-peta-navigasi.md` | Peta layar + arsitektur lapisan + kontrak provider |
| `flows/01-alur-putar-lagu.md` | Urutan cari → putar, state machine pemutar |
| `flows/02-alur-antrean.md` | Manajemen antrean + **skema Room lengkap** (termasuk `playlists`, `playlist_items`) |
| `flows/03-alur-background-service.md` | Lifecycle MediaSessionService, syarat API 34+ |
| `flows/04-alur-resolver-dan-po-token.md` | Strategi resolver + penanganan PO token + kontinjensi |
| `flows/05-alur-offline-cache.md` | Cache offline (v0.2) |
| `flows/06-alur-rilis-dan-ci.md` | CI GitHub Actions + aturan rilis |
| `docs/diagrams/` | 16 diagram Mermaid terekam sebagai SVG (semua lolos parse); sumber `.mmd` ikut commit |
| `keputusan.md` | Catatan keputusan teknis |
| `backlog.md` | Daftar tugas dengan status |
| `PLAN.md` | **Rencana implementasi UI**: peta 17 layar desain → nomor layar, urutan 6 fase, struktur komponen, status per fase |
| `app/`, `gradle/`, `build.gradle.kts`, `settings.gradle.kts` | **Kode**: scaffold Gradle + Compose + Hilt (M0 Fase 0) |
| `CLAUDE.md` | Aturan kerja di folder ini |

---

## Navigasi (mengikuti desain, bukan draft awal)

```
Bottom nav 4 tab:  Home  ·  Discover  ·  Library  ·  Create
Pengaturan         : di dalam "User Profile & Audio Settings", lewat avatar di header
Mini-player        : melayang 56px, 88–92px di atas dasar (di atas pil nav), progress bar 2px
```

---

## Status tahap

| Tahap | Status |
|---|---|
| Perencanaan produk (PRD) | Selesai — v0.2, tersinkron ke desain |
| Alur & diagram (Mermaid) | Selesai — 7 berkas, 16 diagram terverifikasi |
| Desain (Stitch) | **Ada & terpakai** — 18 layar UI + 2 aset, disalin read-only ke `.stitch/electric-purple/`. Tarik ulang terakhir 2026-11-10: 9 layar berubah, 1 baru, 1 aset baru, 1 dihapus |
| Token desain | `docs/DESIGN.md` — diukur dari HTML; palet **terkunci Kubu B** (D8) |
| Inventaris string | `docs/I18N-KEYS.md` — dibekukan 2026-11-12 |
| Keputusan terbuka | **D1–D13 tertutup**; D14–D16 dibuka dengan default tertulis (tidak menghambat M0) |
| Fondasi kode (M0) | **Berjalan** — Fase 0 (2026-11-16): scaffold Gradle AGP 9.5.1 / Gradle 9.9.1 / Kotlin 2.3.20 / Hilt 2.60.1, Compose + M3, CI hijau di GitHub, `LICENSE`/`NOTICE`/`README.md`, APK terpasang. Fase 1 (2026-11-16): tema `NocturneTheme` + token Kubu B + font Plus Jakarta Sans. Fase 2 (2026-11-18): `ScaffoldShell` + pil nav 4 tab + `MiniPlayer` + `NavHost`. **Blur latar belum nyata** (R2 belum tersentuh — lihat `keputusan.md` 2026-11-18). Berikutnya Fase 3 — rincian `PLAN.md` |

---

## Keputusan terbuka

**Tidak ada keputusan yang menghambat M0.** D1–D13 sudah diputuskan; D14–D16 punya default dan bisa dijawab kapan saja sebelum M3.

| ID | Pertanyaan | Default |
|---|---|---|
| D1 | ~~Rilis publik atau privat dulu?~~ **DIPUTUSKAN 2026-11-12: publik** | Publik di GitHub Releases |
| D2 | ~~v0.1 tanpa login?~~ **DIPUTUSKAN 2026-11-12: tanpa login** | Tanpa login |
| D3 | ~~Tulis dari nol atau fork Metrolist?~~ **DIPUTUSKAN 2026-11-12: tulis dari nol** | Tulis dari nol, pola dari Metrolist/EchoMusic |
| D4 | ~~Kotlin native atau Flutter?~~ **DIPUTUSKAN 2026-11-12: Kotlin + Compose** | Kotlin + Compose |
| D5 | ~~`applicationId` final~~ **DIPUTUSKAN 2026-11-10: `io.github.mochamadabdulrouf.refy`** | akun `MochamadAbdulRouf` terverifikasi; permanen setelah rilis pertama |
| D6 | ~~Bahasa UI~~ **DIPUTUSKAN: English penuh** | tab "Buat" → "Create"; Indonesia menyusul di `values-in/` |
| D7 | ~~Layar mana yang masuk MVP?~~ **DIPUTUSKAN: 11 layar** | Usulan baru di PRD §12 — Settings **masuk** MVP; Downloads **tidak dipakai** |
| D8 | ~~Palet mana: Kubu A atau Kubu B?~~ **DIPUTUSKAN: Kubu B** | canvas `#09090B` · surface `#141418` · accent `#8B5CF6`; 17 layar Kubu A diremap |
| D9 | ~~Snapshot vs dokumen, mana yang menang?~~ **DIPUTUSKAN 2026-11-10: snapshot menang** | tarik ulang `.stitch/` lalu dokumen ikut; lihat `keputusan.md` |
| D10 | ~~Dua layar `Downloads Management`~~ **DIPUTUSKAN: `b26aa75b`** | versi lama `2f6ecab6` dihapus desainer 2026-11-10; tetap tidak dipakai di v0.1 (D7) |
| D11 | ~~Bentuk nav final~~ **DIPUTUSKAN: PIL MENGAMBANG** | `h-[68px]`, `rounded-full`, `bg-[#121621]/85`, `blur(30px)`, `bottom-4`; kanon `home-screen-243ea0d8` |
| D12 | ~~Tema gelap/terang (F-09)?~~ **DIPUTUSKAN 2026-11-12: v0.1 DARK-ONLY** | F-09 → v0.2; token light belum ada di desain |
| D13 | ~~DI: Hilt atau Koin?~~ **DIPUTUSKAN 2026-11-12: Hilt** | pola sejajar Metrolist/EchoMusic |
| D14 | **Sapaan Home + nama profil** — desain "Good evening, **Alex**", tapi v0.1 tanpa login | sapaan **tanpa nama**; nama + tombol Edit → v0.2 |
| D15 | **Fitur desain yang belum ada di v0.1** (Podcasts, Downloaded, Equalizer, Crossfade, Sleep Timer, Download Offline, …) | **tidak dirender sama sekali** (bukan disabled) |
| D16 | **Lagu duplikat dalam satu playlist** | **tolak duplikat** — `PRIMARY KEY (playlist_id, track_id)` |

---

## Ringkasan yang perlu diketahui siapa pun yang membuka repo ini

1. Aplikasi ini **tidak memblokir** iklan — ia **tidak pernah meminta** iklan. Tidak ada jalur iklan di arsitektur.
2. Bukan modifikasi APK Google. Bukan klien yang menyuntik proses lain.
3. Tidak akan masuk Play Store. Distribusi lewat GitHub Releases, nanti F-Droid.
4. Tidak memakai nama/merek "YouTube" di nama aplikasi, ikon, atau `applicationId`.
5. Risiko terbesar proyek ini bukan uang, tapi **PO token YouTube**: resolver harus diperbarui rutin.
6. Risiko render terbesar: **lapisan blur persisten** (header, nav pil, mini-player) — dan sejak 2026-11-10 nav pil + mini-player hanya terpisah <20px, dua kaca rona terang berdampingan. Lihat `docs/DESIGN.md` §9.
7. Bahasa UI = **English penuh** (D6). Desain memakai satu tab Indonesia ("Buat") → diganti "Create". Semua string wajib masuk resource i18n sejak hari pertama; `docs/I18N-KEYS.md` memuat daftarnya.
8. Palet = **Kubu B** (D8): canvas `#09090B`, surface `#141418`, accent `#8B5CF6`. Per ukuran 2026-11-10, **17 layar** masih memakai palet lama di `tailwind.config`-nya dan wajib diremap — jangan salin hex apa adanya.
9. Bottom nav = **pil mengambang** (D11): `bottom-4`, `rounded-full`, `h-[68px]`, `bg-[#121621]/85`, `blur(30px)`, border putih 1–1.5px + glow. Mini-player `bottom-[88–92px]`.
10. Tema = **dark-only di v0.1** (D12). Tidak ada `values-night/` untuk warna. Tema terang menunggu palet dari desainer.
11. Sebelum memakai dokumen ini sebagai acuan implementasi, jalankan **`python scripts/check-consistency.py`** — harus hijau. Alat itu memeriksa nomor layar, ukuran piksel, hitungan snapshot, status keputusan, dan kunci i18n.
