# I18N-KEYS.md — Inventaris Kunci String (Refy v0.1)

**Status:** dibekukan **2026-11-12**. Ini daftar periksa M3 + gerbang audit M5.
**Aturan sumber:** string diambil **verbatim** dari desain Stitch
(`.stitch/electric-purple/SCREEN-TEXT.txt`). Jangan mengarang teks baru.
Satu pengecualian: tab `Buat` → **`Create`** (**D6**, English penuh).

Mengapa berkas ini ada: `docs/DESIGN.md` §8 hanya memberi 3 contoh kunci. Tanpa
inventaris, Composable akan menumpuk teks literal lalu "diperbaiki nanti" — dan
itu tidak pernah terjadi. **Definisi Selesai M5 memakai berkas ini sebagai daftar
periksa: nol string literal di Composable.**

---

## 1. Aturan penamaan

| Aturan | Contoh |
|---|---|
| Pola `<area>.<elemen>` | `home.greeting_evening`, `queue.clear_all` |
| Kunci **tidak berbau bahasa** | `nav.create`, bukan `beranda.create` |
| Area memakai nama fungsional, bukan nomor layar | `album.`, bukan `screen5.` |
| Aksi berulang masuk `common.` | `common.save`, `common.cancel` |
| Deskripsi aksesibilitas berakhiran `_cd` | `mini.cd_play` |
| Placeholder memakai tipe eksplisit | `%1$s` (string), `%1$d` (angka) |
| Jamak wajib `<plurals>`, bukan `if` di kode | `discover.pinned_count` |

Tema **tidak** memengaruhi teks (D12, dark-only). Bahasa Inggris di `values/`;
`values-in/` adalah slot kosong yang boleh diisi kapan saja tanpa ubah kode.

---

## 2. Kunci umum — semua layar

| Kunci | String English | Catatan |
|---|---|---|
| `nav.home` | Home | tab |
| `nav.discover` | Discover | tab |
| `nav.library` | Library | tab |
| `nav.create` | **Create** | tab — desain menulis `Buat` (D6) |
| `common.cancel` | Cancel | |
| `common.save` | Save | |
| `common.close` | Close | |
| `common.back` | Back | deskripsi aksesibilitas `arrow_back` |
| `common.see_all` | See all | |
| `common.show_all` | Show all | |
| `common.retry` | Retry | |
| `common.search` | Search | juga placeholder field |
| `common.more_options` | More options | deskripsi aksesibilitas `more_vert` / `more_horiz` |
| `common.playing` | Playing | deskripsi `graphic_eq` |
| `common.pause` / `common.play` | Pause / Play | deskripsi tombol |

## 3. Mini-player (komponen persisten)

| Kunci | String English |
|---|---|
| `mini.next_up` | Playing next: %1$s |
| `mini.cd_open_player` | Open player |
| `mini.cd_favorite` | Add to favorites |
| `mini.cd_pause` / `mini.cd_play` | Pause / Play |

## 4. #1 Home Screen

| Kunci | String English | Catatan |
|---|---|---|
| `home.greeting_morning` | Good morning | tanpa nama — lihat D14 |
| `home.greeting_afternoon` | Good afternoon | |
| `home.greeting_evening` | Good evening | desain menulis "Good evening, Alex" |
| `home.filter_all` | All | |
| `home.filter_music` | Music | |
| `home.section_daily_mix` | Daily Mix 1 | judul bagian; angka dari data |
| `home.section_made_for_you` | Made For You | |
| `home.subtitle_made_for_you` | Curated for your late-night rotation | "Alex's" → netral (D14) |
| `home.section_trending_now` | Trending Now | |
| `home.subtitle_trending_now` | Topping the global late-night charts | |
| `home.explore` | Explore | |

> **Bukan i18n — ini data dari provider:** nama kartu (`Synthwave Chill`,
> `Discover Weekly`, `Night Owl Mix`), badge (`WEEKLY`, `RADAR`, `NIGHT`,
> `REWIND`, `#1 TRENDING`), judul/artis/durasi lagu. Jangan masukkan ke
> `strings.xml`.

## 5. #2 Active Search Results

| Kunci | String English | Catatan |
|---|---|---|
| `search.hint` | Search songs, artists, albums | placeholder |
| `search.chip_top` | Top | |
| `search.chip_songs` | Songs | |
| `search.chip_albums` | Albums | |
| `search.chip_artists` | Artists | |
| `search.chip_playlists` | Playlists | |
| `search.chip_podcasts` | Podcasts | lihat D15 |
| `search.top_result` | Top Result | |
| `search.result_saves` | Over %1$s saves • By %2$s | angka dari data |
| `search.empty` | No results for "%1$s" | baru — desain tidak menggambar keadaan kosong |
| `search.no_connection` | No connection | memenuhi US-01 |
| `search.verified` | Verified | deskripsi badge `verified` |

## 6. #3 Discover (Library & Search Screen)

| Kunci | String English | Catatan |
|---|---|---|
| `discover.title` | Discover | header memakai label tab — `<h1>` desain ("Library And Search Screen") dipakai 3 layar sekaligus, cacat #2 |
| `discover.browse_all` | Browse all | |
| `discover.explore_tags` | Explore tags | |
| `discover.recently_played` | Recently played | |
| `discover.section_favorite_tracks` | Favorite Tracks | |
| `discover.filter_playlists` | Playlists | |
| `discover.filter_albums` | Albums | |
| `discover.filter_artists` | Artists | |
| `discover.filter_downloaded` | Downloaded | lihat D15 |
| `discover.filter_podcasts` | Podcasts | lihat D15 |
| `discover.item_playlist_songs` | Playlist • %1$d songs | `<plurals>` |
| `discover.item_downloaded_songs` | Downloaded • %1$d songs | `<plurals>` |
| `discover.empty` | Nothing here yet | baru |
| `discover.cd_search` | Search | |
| `discover.cd_camera` | Search by image | lihat §10 |

> Nama genre (`Electronic & Synth`, `Hip-Hop & Trap`, `Chill & Lo-Fi`,
> `Rock & Alternative`, `Dark Ambient`, `Dance & EDM`) dan nama playlist =
> **data**, bukan i18n.

## 7. #4 Your Library

| Kunci | String English |
|---|---|
| `library.title` | Your Library |
| `library.recently_played` | Recently Played |
| `library.pinned_count` | %1$d Pinned (`<plurals>`) |
| `library.liked_songs` | Liked Songs |
| `library.filter_*` | sama dengan `discover.filter_*` (pakai ulang) |
| `library.empty` | Your library is empty |
| `library.cd_add` | Create playlist |

## 8. #5 Album Detail & Tracklist

| Kunci | String English | Catatan |
|---|---|---|
| `album.meta` | Album • %1$s • %2$d songs, %3$s | contoh desain: "Album • 2020 • 14 songs, 56 min" |
| `album.badge_lossless` | Lossless Audio | |
| `album.badge_atmos` | Dolby Atmos | |
| `album.recommended_tracks` | Recommended Tracks | |
| `album.recommended_subtitle` | Based on this album & vibe | |
| `album.tracklist_title` | TITLE | kolom |
| `album.tracklist_duration_cd` | Duration | deskripsi `schedule` |
| `album.cd_play` / `album.cd_shuffle` | Play / Shuffle | |
| `album.cd_more` | More | |

> Layar **#6** (`album-detail-after-hours`) ditunda per D7 — varian visual dari
> layar yang sama. Jangan buat kunci terpisah untuknya.

## 9. #8 Artist Profile

| Kunci | String English | Catatan |
|---|---|---|
| `artist.verified` | Verified Artist | |
| `artist.monthly_listeners` | %1$s monthly listeners | angka dari data |
| `artist.streams` | %1$s streams | |
| `artist.following` | Following | |
| `artist.follow` | Follow | baru — keadaan belum mengikuti |
| `artist.popular` | Popular | |
| `artist.discography` | Discography | |
| `artist.see_discography` | See discography | |
| `artist.tab_albums` | Albums | |
| `artist.tab_singles_eps` | Singles & EPs | |

## 10. #9 Now Playing Screen

| Kunci | String English | Catatan |
|---|---|---|
| `np.playing_from_playlist` | Playing from playlist | |
| `np.badge_lossless` | Lossless | |
| `np.audio_output` | Audio Output | |
| `np.live_lyrics` | Live Lyrics | |
| `np.expand` / `np.collapse` | Expand / Collapse | |
| `np.share` | Share | |
| `np.remaining` | -%1$s | hitung mundur |
| `np.cd_favorite` / `np.cd_queue` | Add to favorites / Open queue | |
| `np.cd_output` / `np.cd_lyrics` | Change audio output / Show lyrics | |

> Teks lirik yang terlihat di desain = **data**. Lirik penuh adalah v0.2
> (`expanded-live-lyrics` ditunda per D7) — di v0.1 bagian lirik **disembunyikan**.

## 11. #10 Playback Queue

| Kunci | String English | Catatan |
|---|---|---|
| `queue.title` | Playback Queue | |
| `queue.clear_all` | Clear all | |
| `queue.now_playing` | Now Playing | |
| `queue.badge_lossless` | Lossless 24-bit | |
| `queue.badge_master` | Master Audio | |
| `queue.next_from` | Next from: %1$s | |
| `queue.autoplay_on` / `queue.autoplay_off` | Autoplay Similar: ON / OFF | |
| `queue.summary` | Queue: %1$d tracks · %2$s remaining | `<plurals>` |
| `queue.save_as_playlist` | Save as Playlist | |
| `queue.tap_to_resume` | Tap to resume playback | |
| `queue.cd_check` / `queue.cd_drag` | Select track / Reorder | deskripsi aksesibilitas |
| `queue.cd_remove` | Remove from queue | baru |

## 12. #12 Track Options Bottom Sheet

| Kunci | String English | Catatan |
|---|---|---|
| `options.playing_from_playlist` | PLAYING FROM PLAYLIST | huruf kapital di desain |
| `options.add_to_playlist` | Add to Playlist... | |
| `options.add_to_queue` | Add to Queue | |
| `options.next_up` | Next Up | |
| `options.go_to_album` | Go to Album | |
| `options.go_to_artist` | Go to Artist | |
| `options.share_track` | Share Track | |
| `options.close` | Close | |

> `options.download_offline` (**Download for Offline**) dan `options.sleep_timer`
> (**Sleep Timer**) ada di desain tapi **fiturnya tidak ada di v0.1** — jangan
> render barisnya (bukan disembunyikan, tapi tidak dibuat).

## 13. #14 Create Playlist

| Kunci | String English | Catatan |
|---|---|---|
| `create.title` | Create Playlist | |
| `create.upload_artwork` | Upload Artwork | |
| `create.tap_to_browse` | Tap to browse | |
| `create.name_label` | Playlist Name | batas 60 karakter |
| `create.counter` | %1$d/%2$d | |
| `create.description_label` | Description | |
| `create.description_optional` | Optional | |
| `create.save` | Save | tombol utama |
| `create.upload_broken` | Couldn't load that image | baru |

> `create.make_public` (**Make Public on Profile**) + subtitelnya **tidak dipakai
> di v0.1** — publikasi playlist butuh akun (D2), dan playlist v0.1 lokal saja.
> Tombol upload artwork: **aktif**, tulis ke penyimpanan aplikasi (tidak perlu
> `READ_EXTERNAL_STORAGE`; pakai Photo Picker).

## 14. #15 User Profile & Audio Settings

| Kunci | String English | Catatan |
|---|---|---|
| `profile.title` | User Profile & Audio Settings | |
| `profile.edit` | Edit | profil lokal, lihat D14 |
| `profile.listening_insights` | Listening Insights | |
| `profile.live_metrics` | LIVE METRICS | |
| `profile.monthly_listening` | Monthly Listening | |
| `profile.minutes` | mins | |
| `profile.top_resonance` | Top Resonance | |
| `profile.audio_engine` | Audio Engine & Playback | |
| `profile.audio_quality` | Audio Quality | |
| `profile.offline_storage` | Offline Storage | lihat D15 |
| `profile.manage` | MANAGE | |
| `profile.normalize_volume` | Normalize Volume | lihat D15 |
| `profile.connected_devices` | Connected Devices & Services | |
| `profile.system_settings` | System & Settings | |
| `profile.app_settings` | App Settings | pintu ke layar #16 |
| `profile.app_settings_subtitle` | Playback, notifications, data & storage, about | |

> `profile.equalizer` (**Equalizer & Sound Effects**) dan
> `profile.crossfade_gapless` (**Crossfade & Gapless**) **tidak dirender di
> v0.1** — ekualiser & crossfade adalah v0.3/v0.2 (§5 PRD). Statistik
> (`4,820 mins`, `Synthwave & Dark Electro`) dihitung dari riwayat lokal, bukan
> dikarang; label `DSP 4.2 HIGH-RES` **mock desain, jangan dipakai**.

## 15. #16 Settings

| Kunci | String English | Catatan |
|---|---|---|
| `settings.title` | Settings | |
| `settings.section_playback` | Playback | |
| `settings.streaming_quality` | Streaming quality | |
| `settings.streaming_quality_value_auto` | Auto (Adaptive Bitrate) | |
| `settings.download_quality` | Download quality | **tidak dirender v0.1** (D7) |
| `settings.downloads_mobile_data` | Downloads on mobile data | **tidak dirender v0.1** |
| `settings.normalize_volume` | Normalize volume | |
| `settings.normalize_volume_subtitle` | Consistent loudness across tracks | |
| `settings.crossfade` | Crossfade | **tidak dirender v0.1** |
| `settings.gapless` | Gapless playback | |
| `settings.gapless_subtitle` | Continuous listening experience | |
| `settings.section_audio_engine` | Audio Engine | |
| `settings.equalizer` | Equalizer & effects | **tidak dirender v0.1** |
| `settings.audio_output` | Audio output | |
| `settings.section_notifications` | Notifications | |
| `settings.playback_controls` | Playback controls | |
| `settings.playback_controls_subtitle` | Show active media player in drawer | |
| `settings.new_releases` | New releases | |
| `settings.section_data_storage` | Data & Storage | |
| `settings.app_cache` | App cache | |
| `settings.app_cache_subtitle` | Temporary wave audio snippets | |
| `settings.clear_cache` | Clear cache | |
| `settings.section_about` | About & Legal | |
| `settings.app_version` | App version | |
| `settings.licenses` | Licenses | |
| `settings.source_code` | Source code | |
| `settings.privacy` | Privacy policy | |
| `settings.terms` | Terms of use | |
| `settings.footer` | Refy – version %1$s – GPL-3.0 | desain menulis "Nocturne Audio"; produk ini **Refy** |

> `settings.new_releases` (**New releases**) butuh notifikasi push → **tidak
> dirender di v0.1** (tidak ada server, tidak ada Firebase).

## 16. Galat & keadaan jaringan (dipakai lintas layar)

| Kunci | String English | Catatan |
|---|---|---|
| `error.no_connection` | No connection | memenuhi US-01 |
| `error.retry` | Retry | |
| `error.provider_broken` | This track isn't available right now | ditampilkan **sekali per sesi** (`flows/00` aturan 3) |
| `error.playback_failed` | Couldn't play that track | |

---

## 17. Gerbang M5 — audit i18n

1. `rg '"[A-Z][a-z]+ ' app/src/main/java` → **nol** hasil di dalam Composable.
2. Setiap kunci di berkas ini ada di `app/src/main/res/values/strings.xml`.
3. Setiap kunci di `strings.xml` **dirujuk** minimal sekali (tidak ada kunci mati).
4. Jamak memakai `<plurals>`; angka lewat `NumberFormat` locale default.
5. `values-in/` ada (boleh kosong) dan `Locale` switch tidak mengubah kode.

Aplikasi ini tidak pernah menampilkan teks desain yang berbau merek pihak lain:
`Nocturne Audio`, `Nocturne Core Engine`, `Nocturne Editorial`, `DSP 4.2
HIGH-RES` adalah **mock desainer** — ganti dengan `Refy` atau buang (aturan R4
§7.2 PRD: nol merek Google, dan nol merek desainer).
