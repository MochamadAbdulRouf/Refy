# 05 — Alur Offline Cache (v0.2, Should)

Bagian dari PRD Refy v0.1. Belum masuk MVP; ditulis sekarang supaya skema DB dan batas penyimpanan sudah benar sejak awal.

## Kebijakan cache

```mermaid
flowchart TD
    A[Lagu selesai diputar] --> B{Kebijakan cache?}
    B -->|Mati - default v0.1| C[Tidak simpan apa pun]
    B -->|Otomatis - v0.2| D{Cache < batas?}
    B -->|Simpan manual - v0.2| E[Unduh sekarang + progress bar]

    D -->|Ya| F[Unduh berkas audio]
    D -->|Tidak| G[Evict LRU sampai ada ruang]
    G --> F
    F --> H[Simpan ke filesDir/cache_audio/<br/>+ catatan di DB]
    H --> I[Tersedia offline]

    I --> J{Pemutaran diminta}
    J --> K{Cache ada?}
    K -->|Ya| L[Putar dari berkas lokal<br/>tanpa panggilan jaringan]
    K -->|Tidak| M[Resolver jaringan seperti biasa]
```

## Keputusan yang harus dijaga

| Item | Keputusan |
|---|---|
| Lokasi | `filesDir/cache_audio/` (internal), bukan penyimpanan eksternal |
| Batas default | 2 GB, diatur pengguna (500 MB – 10 GB) |
| Kebijakan eviksi | LRU berdasarkan waktu putar terakhir; tidak pernah menghapus item Favorit |
| Hak cipta | Fitur cache **tidak** dipromosikan sebagai "unduh" dan tidak ada tombol "ekspor"; berkas tidak dapat dibuka aplikasi lain di v1 |
| Ekstensi berkas | Tanpa ekstensi audio yang bisa diputar pemutar lain; metadata disimpan di DB |
| Manual clear | Tersedia di Pengaturan: "Bersihkan cache" + "Bersihkan semua" |
| Hitungan ukuran | Ditampilkan di Pengaturan dengan pembaruan setelah setiap penulisan/penghapusan |"

## Dampak ke skema (siapkan kolom sekarang, walau fitur belum ada)

```sql
ALTER TABLE provider_cache ADD COLUMN local_path TEXT;      -- null = belum ter-cache
ALTER TABLE provider_cache ADD COLUMN size_bytes INTEGER;
ALTER TABLE provider_cache ADD COLUMN last_played_at INTEGER;
```

## Urutan preferensi pemutaran

```mermaid
flowchart LR
    A[Permintaan putar] --> B{Berkas lokal ada?}
    B -->|Ya| C["Putar lokal - hemat data, offline OK"]
    B -->|Tidak| D{StreamInfo cache valid?}
    D -->|Ya| E[Putar dari URL]
    D -->|Tidak| F[Resolver ulang]
```
