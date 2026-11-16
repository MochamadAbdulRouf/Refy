"""Verifikasi silang: cari byte-pattern di DEX mentah, tanpa preprocessing.

Dipakai untuk memisahkan "benar-benar tidak ada" dari "ada tapi ter-obfuscate/R8
me-rename". Setiap pola dilaporkan dengan bentuk descriptor (`Lpkg/Kelas;`) dan
bentuk path biasa, supaya salah satu pasti kena.
"""

import re
import sys
import zipfile
from collections import Counter

CANDIDATES = [
    "androidx/compose/material3",
    "androidx/compose/ui",
    "androidx/compose/runtime",
    "androidx/compose/foundation",
    "androidx/activity/compose",
    "androidx/navigation",
    "androidx/media3",
    "androidx/room",
    "androidx/datastore",
    "androidx/work",
    "androidx/core",
    "androidx/lifecycle",
    "dagger/hilt",
    "dagger/internal",
    "javax/inject",
    "okhttp3/internal",
    "retrofit2",
    "io/ktor/client",
    "kotlinx/coroutines",
    "kotlinx/serialization",
    "kotlinx/collections/immutable",
    "com/google/gson",
    "org/jsoup",
    "com/google/protobuf",
    "com/google/firebase",
    "com/google/android/gms/ads",
    "com/google/android/gms/cast",
    "coil3",
    "coil/",
    "sh/calvin/reorderable",
    "com/airbnb/lottie",
    "org/bouncycastle",
    "org/mozilla/javascript",
    "com/atilika/kuromoji",
    "com/arthenica/ffmpegkit",
    "ai/onnxruntime",
    "com/yalantis/ucrop",
    "androidx/media3/exoplayer",
    "androidx/media3/session",
    "org/schabi/newpipe/extractor",
    "com/music/innertube",
    "com/metrolist/innertubex",
    "com/zionhuang",
    "org/jetbrains/compose",
]


def dex_blobs(path):
    with zipfile.ZipFile(path) as z:
        for name in z.namelist():
            if re.fullmatch(r"classes\d*\.dex", name):
                with z.open(name) as f:
                    yield name, f.read()


def main():
    apk = sys.argv[1]
    blobs = list(dex_blobs(apk))
    print(f"DEX: {[n for n, _ in blobs]}")

    print(f"\n{'pola':<42} {'descriptor':>10} {'path':>8}  verdict")
    print("-" * 78)
    for cand in CANDIDATES:
        desc = ("L" + cand + ";").encode()
        rawp = cand.encode()
        d = sum(b.count(desc) for _, b in blobs)
        r = sum(b.count(rawp) for _, b in blobs)
        verdict = "ADA" if (d or r) else "TIDAK ADA"
        print(f"{cand:<42} {d:>10,} {r:>8,}  {verdict}")

    # petunjuk versi dari string yang biasanya tidak di-obfuscate
    allblob = b"\x00".join(b for _, b in blobs)
    print("\n=== petunjuk versi/identitas ===")
    for pat in [rb"okhttp/(\d+\.\d+\.\d+)", rb"okio/(\d+\.\d+\.\d+)",
                rb"ktor[/-](\d+\.\d+\.\d+)", rb"newpipe[^\"\x00]{0,30}",
                rb"metrolist[^\"\x00]{0,30}", rb"innertube[^\"\x00]{0,30}",
                rb"(\d+\.\d+\.\d+)-release", rb"ExoPlayerLib/(\d+\.\d+\.\d+)",
                rb"ffmpeg[-_]kit[^\"\x00]{0,20}"]:
        found = Counter(re.findall(pat, allblob, re.I))
        if found:
            top = dict(list(found.most_common(5)))
            print(f"  {pat.decode():<40} {top}")


if __name__ == "__main__":
    main()
