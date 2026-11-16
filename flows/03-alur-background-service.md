# 03 — Alur Background Service

Bagian dari PRD Refy v0.1. Inti kualitas aplikasi ada di berkas ini.

## Siklus hidup

```mermaid
stateDiagram-v2
    [*] --> TidakAda: app dibuka, belum ada lagu
    TidakAda --> Aktif: play(track) → startForegroundService
    Aktif --> Aktif: putar/jeda/next/prev (service tetap hidup)
    Aktif --> Aktif: app di-swipe dari recents (tetap hidup)
    Aktif --> BerhentiSementara: audio focus hilang (telepon)
    BerhentiSementara --> Aktif: fokus kembali
    Aktif --> HentiDiri: jeda > 15 menit tanpa interaksi
    HentiDiri --> TidakAda: stopSelf()
    Aktif --> TidakAda: pengguna tekan tutup di notifikasi
```

## Urutan startup service

```mermaid
sequenceDiagram
    autonumber
    participant UC as PlayTrackUseCase
    participant Ctx as Context
    participant Svc as MediaSessionService
    participant Exo as ExoPlayer
    participant N as NotificationManager
    participant MS as MediaSession

    UC->>Ctx: ContextCompat.startForegroundService(intent)
    Ctx->>Svc: onCreate()
    Svc->>Svc: buat channel "pemutaran" (IMPORTANCE_LOW)
    Svc->>Exo: build() + setAudioAttributes(musik)
    Svc->>MS: setSessionActivity(PendingIntent → NowPlaying)
    Svc->>MS: setCallback(play/pause/next/prev/seek/queue)
    UC->>Svc: onAddMediaItems / setMediaItems
    Svc->>Exo: prepare() + play()
    Svc->>N: startForeground(ID, mediaNotification)
    Note over Svc,N: Wajib memanggil startForeground ≤ 5 detik setelah startForegroundService, jika tidak → ForegroundServiceDidNotStartInTimeException
```

## Penanganan niat dari luar

```mermaid
flowchart TD
    A[Aksi masuk ke MediaSession] --> B{Jenis aksi}
    B -->|play / pause / toggle| C[Exo.play/pause]
    B -->|next / previous| D[QueueRepository.next/prev → resolve → setMediaItem]
    B -->|seekTo| E[Exo.seekTo]
    B -->|stop| F["Simpan posisi, stopSelf"]
    B -->|setMediaItems dari Android Auto| G[Ganti antrean penuh]

    C --> H{Pemutaran berubah dari jeda ke jalan?}
    H -->|Ya| I[Dapatkan audio focus]
    H -->|Tidak| J[Update notifikasi]

    K[Headphone dicabut / AudioDeviceCallback] --> L[Pause + tampilkan notifikasi resumable]
    M[Telepon masuk - AUDIOFOCUS_LOSS_TRANSIENT] --> N["Pause, ingat status"]
    N --> O{Audio focus kembali dalam 15 menit?}
    O -->|Ya| P[Resume]
    O -->|Tidak| Q["Tetap jeda, simpan posisi"]
```

## Persyaratan Android modern (wajib, sering bikin app gagal terbit)

| Syarat | Implementasi |
|---|---|
| `FOREGROUND_SERVICE` + `FOREGROUND_SERVICE_MEDIA_PLAYBACK` di manifest | Wajib untuk API 34+ |
| `POST_NOTIFICATIONS` runtime permission (API 33+) | Minta saat pertama tekan play, bukan saat app dibuka |
| Channel notifikasi `IMPORTANCE_LOW` | Agar tidak berbunyi & tidak muncul heads-up |
| `android:foregroundServiceType="mediaPlayback"` | Tanpa ini, service ditolak di API 34+ |
| `setSessionActivity` | Tanpa ini, tap notifikasi tidak membuka app |
| Batere: jangan minta `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Kebijakan Play menolak; di luar Play tetap berisiko. Dokumentasikan sebagai batasan |
| Media3 `MediaSessionService` | Jangan pakai `Service` mentah; sesi otomatis menangani Bluetooth & Android Auto |

## Kasus tepi & perilaku yang diharapkan

| Kasus | Perilaku yang benar |
|---|---|
| App di-swipe dari recents | Audio **tetap** jalan (perilaku yang diminta pengguna) |
| Pengguna tekan "tutup" di notifikasi | Audio berhenti, posisi disimpan, service `stopSelf` |
| Jeda > 15 menit | Service berhenti sendiri; antrean tetap tersimpan di Room |
| Bluetooth terputus | Pause; notifikasi tetap resumable |
| Jaringan hilang saat buffering | Status `Buffering` maksimum 30 detik, lalu `Gagal` + aksi coba lagi |
| Dua instance app dibuka | Tetap satu `MediaSessionService`; tidak ada dua audio bersamaan |
| Layar mati 60 menit | Putar terus; konsumsi baterai dipantau (KPI §2.2 PRD) |
