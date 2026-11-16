"""Scan lanjutan: UI framework, versi komponen, dan identitas build.

Dipakai setelah `verify-dex-libs.py` menemukan anomali (mis. `material3` 0 padahal
aplikasinya Compose). Tujuannya memisahkan "benar tidak dipakai" dari "R8 me-rename".
"""

import re
import sys
import zipfile
from collections import Counter

PROBES = [
    # UI framework — mana yang benar-benar dipakai
    "androidx/compose/material3",
    "androidx/compose/material/",
    "androidx/compose/material/icons",
    "androidx/compose/ui/graphics",
    "androidx/compose/ui/text",
    "androidx/compose/foundation/lazy",
    "androidx/compose/animation",
    "androidx/glance",
    "com/google/android/material/",
    "androidx/recyclerview",
    "androidx/viewpager2",
    "androidx/constraintlayout",
    # DI alternatif (kalau bukan Hilt)
    "androidx/hilt",
    "com/google/dagger",
    "org/koin",
    "kotlinx/coroutines/flow",
    # Media
    "androidx/media3/ui",
    "androidx/media3/datasource",
    "androidx/media3/session/MediaSessionService",
    "androidx/media/",
    "com/google/android/exoplayer2",
    # Widget & layanan
    "androidx/mediarouter",
    "androidx/glance/appwidget",
    # Serialisasi
    "kotlinx/serialization/json",
    "kotlinx/serialization/protobuf",
    # Jaringan
    "io/ktor/client/engine/okhttp",
    "io/ktor/client/engine/android",
    "okhttp3/OkHttpClient",
    "okhttp3/ConnectionPool",
    # Cache gambar
    "coil3/compose",
    "coil3/network",
    # Database
    "androidx/room/RoomDatabase",
    "app/cash/sqldelight",
    # Kriptografi
    "org/bouncycastle/crypto",
    "javax/crypto/Cipher",
    # JSON alternatif
    "kotlinx/serialization/json/Json",
]

IDENTITY = [
    rb"kotlin[- ]?(\d+\.\d+\.\d+)",
    rb"Compose[- ]?(\d+\.\d+\.\d+)",
    rb"kotlinx-coroutines-core[/-](\d+\.\d+\.\d+)",
    rb"kotlinx-serialization[^\"\x00]{0,25}",
    rb"androidx\.media3[^\"\x00]{0,30}",
    rb"coil[^\"\x00]{0,20}",
    rb"ktor[^\"\x00]{0,25}",
    rb"room[^\"\x00]{0,20}",
    rb"onnxruntime[^\"\x00]{0,25}",
    rb"quickjs[^\"\x00]{0,25}",
    rb"kuromoji[^\"\x00]{0,25}",
    rb"lame|libmp3lame|opus|vorbis",
    rb"targetSdk[^\"\x00]{0,10}",
    rb"(YouTube|Google)[^\"\x00]{0,25}Client",
]


def blobs(path):
    with zipfile.ZipFile(path) as z:
        for n in z.namelist():
            if re.fullmatch(r"classes\d*\.dex", n):
                with z.open(n) as f:
                    yield n, f.read()


def main():
    apk = sys.argv[1]
    data = list(blobs(apk))
    total = b"\x00".join(b for _, b in data)

    print("=== UI / framework probe ===")
    print(f"{'pola':<46} {'hits':>8}  verdict")
    print("-" * 70)
    for p in PROBES:
        n = total.count(p.encode())
        print(f"{p:<46} {n:>8,}  {'ADA' if n else 'TIDAK ADA'}")

    print("\n=== identitas / versi ===")
    for pat in IDENTITY:
        found = Counter(re.findall(pat, total))
        if found:
            print(f"  {pat.decode()[:44]:<46} {dict(found.most_common(4))}")

    # versi dari .so (bukan DEX) — library native menyimpan versinya
    print("\n=== versi di native lib ===")
    with zipfile.ZipFile(apk) as z:
        for n in z.namelist():
            if not n.endswith(".so") or "arm64" not in n:
                continue
            with z.open(n) as f:
                head = f.read(4_000_000)
            hits = re.findall(rb"(?:ffmpeg|libav\w+|Lavc|onnxruntime|quickjs)[\w./ -]{0,40}",
                              head, re.I)
            uniq = sorted({h.decode("latin1").strip() for h in hits})[:5]
            if uniq:
                print(f"  {n.split('/')[-1]:<34} {uniq}")


if __name__ == "__main__":
    main()
