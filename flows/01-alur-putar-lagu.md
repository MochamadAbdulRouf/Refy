# 01 — Alur Putar Lagu

Bagian dari PRD Refy v0.2. Layar & ukuran mengikuti `docs/DESIGN.md` dan desain Stitch *Electric Purple Music App*.

| Elemen | Nilai dari desain |
|---|---|
| Layar pencarian | **#2** Active Search Results (masuk dari tab Discover **#3**) |
| Mini-player | 56px, melayang **88–92px** di atas dasar (di atas pil nav, D11), progress bar 2px di tepi bawah |
| Header hasil | `Top Result` + chip `Top / Songs / Albums / Artists / Playlists` |
| Baris hasil | 56px, artwork 44px, judul putih satu baris, artis muted satu baris |
| Keadaan sedang diputar | judul ungu `#8B5CF6` + glyph equalizer 3 batang |

## Alur utama: cari → putar

```mermaid
sequenceDiagram
    autonumber
    actor U as Pengguna
    participant UI as CariScreen
    participant VM as SearchViewModel
    participant UC as PlayTrackUseCase
    participant Repo as MusicRepository
    participant P as YtMusicProvider
    participant Svc as MediaSessionService
    participant Exo as ExoPlayer

    U->>UI: ketik "tulus monokrom" + enter
    UI->>VM: onQuerySubmit(q)
    VM->>Repo: search(q, SONGS)
    Repo->>P: search(q, SONGS)
    P-->>Repo: List<Track>
    Repo-->>VM: List<Track>
    VM-->>UI: state = Success(list)   [≤ 2 dtk]
    U->>UI: tap lagu
    UI->>UC: play(track, queue = hasilPencarian)
    UC->>Repo: getStreamInfo(track.id)
    Repo->>P: getStreamInfo(track.id)
    P-->>Repo: StreamInfo(url, mime, bitrate, expiresAt)
    Repo-->>UC: StreamInfo
    UC->>Svc: setQueue(tracks, index)
    UC->>Exo: setMediaItem(url) + prepare()
    Exo-->>U: audio berbunyi   [≤ 2 dtk dari tap]
    Svc->>Svc: publish MediaStyle notification
    UI-->>U: mini-player tampil
```

## Diagram keputusan saat memutar

```mermaid
flowchart TD
    A[Pengguna tap lagu] --> B{StreamInfo ter-cache & belum kedaluwarsa?}
    B -->|Ya| F[Pakai URL cache]
    B -->|Tidak| C[Minta StreamInfo ke provider]
    C --> D{Berhasil?}
    D -->|Ya| E[Simpan di cache dengan expiresAt]
    E --> F
    D -->|Tidak, jaringan| G[Status ERROR_JARINGAN]
    D -->|Tidak, provider rusak| H[Status ERROR_PROVIDER]
    F --> I[ExoPlayer.setMediaItem + prepare]
    I --> J{Player siap?}
    J -->|Ya| K[Play + update mini-player + notifikasi]
    J -->|Tidak, format tidak didukung| L["Turunkan kualitas, coba sekali lagi"]
    L --> J2{Gagal lagi?}
    J2 -->|Ya| M["Lewati lagu, tampilkan toast sekali"]
    G --> N["Tombol Coba lagi, antrean tetap utuh"]
    H --> O[Lewati lagu + banner 'Sumber sedang bermasalah']
```

## Status pemutar (state machine)

```mermaid
stateDiagram-v2
    [*] --> Idle
    Idle --> Menyiapkan: play(track)
    Menyiapkan --> Memutar: player ready
    Menyiapkan --> Gagal: resolve/prepare gagal
    Memutar --> Jeda: pause / audio focus hilang
    Jeda --> Memutar: play / audio focus kembali
    Memutar --> Buffering: jaringan lambat
    Buffering --> Memutar: buffer cukup
    Buffering --> Gagal: timeout 30s
    Memutar --> Selesai: track selesai
    Selesai --> Menyiapkan: autoplay berikutnya
    Gagal --> Idle: lewati / berhenti
```

## Aturan

1. **Audio focus wajib dihormati.** Dapatkan fokus sebelum play; pause saat fokus hilang permanen (telepon masuk, app lain ambil alih); resume saat fokus kembali jika `AUDIOFOCUS_LOSS_TRANSIENT`.
2. **Menjadi noisy receiver.** Cabut headphone → pause, jangan auto-play.
3. **Kegagalan tidak boleh menutup aplikasi.** Semua jalur gagal berakhir di state dengan aksi yang bisa dilakukan pengguna.
4. **URL stream tidak boleh di-log.** Bisa dipakai orang lain; anggap sebagai rahasia sesi.
5. **Autoplay** memakai `getRelated(trackId)` provider; mati secara default di v0.1 sampai perilakunya stabil.
