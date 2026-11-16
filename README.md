# Refy

Klien pemutar musik Android, open source, **tanpa iklan**, **tanpa login**.
Musik dari katalog YouTube Music lewat extractor komunitas.

Lisensi: **GPL-3.0-or-later** (lihat `LICENSE`) · Bahasa UI: **English** (D6) ·
Tema: **dark-only** untuk v0.1 (D12)

> **Status: M0 (fondasi) selesai, M1 belum mulai.** Aplikasi sudah bisa
> dikompilasi dan dipasang, tapi belum bisa memutar apa pun — belum ada resolver
> maupun pemutar. Lihat `PLAN.md` untuk urutan kerja UI dan `backlog.md` untuk
> daftar tugas.

---

## Apa yang sudah ada

| Bagian | Keadaan |
|---|---|
| Scaffold Gradle + Compose + Material 3 + Hilt | ada, `assembleDebug` hijau |
| Tema `NocturneTheme` (token Kubu B) + font Plus Jakarta Sans | ada — `ui/theme/` |
| 1 Activity Compose, DI Hilt aktif | ada (layar masih penanda tema, bukan UI asli) |
| Resolver stream (M1) | belum |
| Pemutar / MediaSession (M2) | belum |
| Layar UI (M3) | belum — 17 layar desain ada di `.stitch/`, snapshot read-only |

---

## Cara build

**Prasyarat**

| Alat | Versi | Catatan |
|---|---|---|
| JDK | 17 | AGP 9 minimum JDK 17 |
| Android SDK | platform **android-35**, build-tools **36.0.0** | AGP 9 minimum build-tools 36.0.0 |
| Gradle | tidak perlu dipasang | pakai wrapper yang ikut repo |

**Langkah**

```bash
# 1. Arahkan ke Android SDK Anda (berkas ini lokal, tidak di-commit)
printf 'sdk.dir=C\\:\\\\Users\\\\<nama>\\\\AppData\\\\Local\\\\Android\\\\Sdk\n' > local.properties

# 2. Build APK debug
./gradlew :app:assembleDebug          # hasil: app/build/outputs/apk/debug/app-debug.apk

# 3. Pemeriksaan yang dipakai CI
./gradlew lintDebug testDebugUnitTest assembleDebug

# 4. Pasang ke perangkat/emulator
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Kalau build gagal dengan "Gradle build daemon disappeared unexpectedly"**

Mesin ini punya RAM terbatas (7,4 GB). `gradle.properties` sudah disetel
konservatif (`-Xmx1280m`, tanpa parallel, `--workers.max=1`) supaya build tidak
membunuh daemon. Kalau RAM bebas < 2 GB, tutup aplikasi lain dulu. Bukti
kegagalan aslinya: `hs_err_pid*.log` (tidak di-commit).

---

## Batasan hukum & etika

Refy adalah proyek independen yang **tidak berafiliasi** dengan Google LLC,
YouTube, atau YouTube Music. Nama merek tersebut hanya dipakai untuk menyebut
interoperabilitas.

- Refy **tidak** membundel, memodifikasi, atau mendistribusikan ulang kode Google.
- Refy **tidak** memblokir iklan di aplikasi resmi. Ia tidak pernah meminta iklan
  sejak awal — model datanya tidak punya tempat untuk iklan.
- Tidak akan dirilis di Google Play Store. Distribusi lewat GitHub Releases, nanti
  F-Droid.
- Tanggung jawab kepatuhan terhadap syarat layanan pihak ketiga ada pada pengguna.
  Proyek ini tidak memberi jaminan apa pun.

Kredit pihak ketiga ada di `NOTICE`.

---

## Berkembang

Repo ini memuat dua hal: **dokumen** (`docs/`, `flows/`, `index.md`,
`keputusan.md`, `backlog.md`) dan **kode** (`app/`, `gradle/`).

Aturan kerja:

1. Sebelum menjadikan dokumen sebagai acuan implementasi, jalankan
   `python scripts/check-consistency.py` — harus exit 0.
2. Keputusan teknis dicatat di `keputusan.md`: tanggal, pilihan, alasan,
   alternatif yang ditolak.
3. Tugas selesai dicentang di `backlog.md`, jangan dihapus.
4. Urutan kerja UI + status per fase ada di `PLAN.md`.
