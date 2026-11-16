"""Parser string pool DEX untuk inventaris tech stack — ground truth.

Kenapa bukan sekadar `grep`: R8/ProGuard me-rename kelas aplikasi, dan `grep`
atas DEX mentah bisa salah hitung (descriptor `Lpkg/Kelas;` vs path biasa).
Script ini membaca `string_ids` langsung dari header DEX, jadi daftar nama kelas
yang dikeluarkan adalah isi pool yang sebenarnya.

Pakai: python dex_strings.py <apk>
"""

import re
import struct
import sys
import zipfile
from collections import Counter


def uleb128(buf, off):
    """Baca LEB128 tanpa tanda; kembalikan (nilai, offset_baru)."""
    result = 0
    shift = 0
    while True:
        b = buf[off]
        off += 1
        result |= (b & 0x7F) << shift
        if not (b & 0x80):
            return result, off
        shift += 7


def dex_strings(buf):
    """Ambil semua string dari pool DEX. String disimpan sebagai MUTF-8."""
    if buf[:4] != b"dex\n":
        return []
    string_ids_size, string_ids_off = struct.unpack_from("<II", buf, 0x38)
    out = []
    for i in range(string_ids_size):
        (data_off,) = struct.unpack_from("<I", buf, string_ids_off + 4 * i)
        _, p = uleb128(buf, data_off)
        end = buf.index(b"\x00", p)
        raw = buf[p:end]
        out.append(raw.decode("utf-8", "replace"))
    return out


def dex_header_facts(buf):
    return {
        "file_size": struct.unpack_from("<I", buf, 32)[0],
        "string_ids": struct.unpack_from("<I", buf, 0x38)[0],
        "type_ids": struct.unpack_from("<I", buf, 0x40)[0],
        "method_ids": struct.unpack_from("<I", buf, 0x58)[0],
        "class_defs": struct.unpack_from("<I", buf, 0x60)[0],
    }


# Library yang dicari, dipetakan ke awalan paket (tanpa L dan ;)
LIB_PROBES = {
    "Jetpack Compose — runtime": "androidx/compose/runtime/",
    "Jetpack Compose — UI": "androidx/compose/ui/",
    "Compose Material3": "androidx/compose/material3/",
    "Compose Material (M2)": "androidx/compose/material/",
    "Compose Foundation": "androidx/compose/foundation/",
    "Compose Animation": "androidx/compose/animation/",
    "Compose Material Icons": "androidx/compose/material/icons/",
    "Activity Compose": "androidx/activity/compose/",
    "Navigation Compose": "androidx/navigation/compose/",
    "AndroidX Navigation": "androidx/navigation/",
    "AndroidX Lifecycle": "androidx/lifecycle/",
    "AndroidX Core": "androidx/core/",
    "AndroidX AppCompat": "androidx/appcompat/",
    "AndroidX Fragment": "androidx/fragment/",
    "AndroidX RecyclerView": "androidx/recyclerview/",
    "AndroidX ConstraintLayout": "androidx/constraintlayout/",
    "AndroidX ViewPager2": "androidx/viewpager2/",
    "AndroidX Media3": "androidx/media3/",
    "  Media3 ExoPlayer": "androidx/media3/exoplayer/",
    "  Media3 Session": "androidx/media3/session/",
    "  Media3 UI": "androidx/media3/ui/",
    "  Media3 Datasource": "androidx/media3/datasource/",
    "AndroidX Media (lama)": "androidx/media/",
    "AndroidX Mediarouter (Cast)": "androidx/mediarouter/",
    "AndroidX Room": "androidx/room/",
    "AndroidX DataStore": "androidx/datastore/",
    "AndroidX WorkManager": "androidx/work/",
    "AndroidX Palette": "androidx/palette/",
    "AndroidX WebKit": "androidx/webkit/",
    "AndroidX Preference": "androidx/preference/",
    "AndroidX SplashScreen": "androidx/core/splashscreen/",
    "AndroidX Biometric": "androidx/biometric/",
    "AndroidX Glance (widget)": "androidx/glance/",
    "AndroidX Paging": "androidx/paging/",
    "Material Components (View)": "com/google/android/material/",
    "Hilt": "dagger/hilt/",
    "Dagger": "dagger/",
    "javax.inject": "javax/inject/",
    "Koin": "org/koin/",
    "Kotlin stdlib": "kotlin/",
    "Kotlin Coroutines": "kotlinx/coroutines/",
    "kotlinx.serialization": "kotlinx/serialization/",
    "kotlinx.datetime": "kotlinx/datetime/",
    "Kotlin Immutable Collections": "kotlinx/collections/immutable/",
    "Ktor client": "io/ktor/client/",
    "  Ktor engine OkHttp": "io/ktor/client/engine/okhttp/",
    "Ktor utils": "io/ktor/utils/",
    "OkHttp": "okhttp3/",
    "Okio": "okio/",
    "Retrofit": "retrofit2/",
    "Gson": "com/google/gson/",
    "Moshi": "com/squareup/moshi/",
    "Jackson": "com/fasterxml/jackson/",
    "jsoup": "org/jsoup/",
    "Protocol Buffers": "com/google/protobuf/",
    "Coil (gambar)": "coil3/",
    "Lottie": "com/airbnb/lottie/",
    "Reorderable list": "sh/calvin/reorderable/",
    "Accompanist": "com/google/accompanist/",
    "FFmpegKit": "com/arthenica/ffmpegkit/",
    "ONNX Runtime (Java)": "ai/onnxruntime/",
    "QuickJS (JS engine)": "app/cash/quickjs/",
    "Rhino (JS engine)": "org/mozilla/javascript/",
    "Kuromoji (Jepang)": "com/atilika/kuromoji/",
    "BouncyCastle": "org/bouncycastle/",
    "UCrop": "com/yalantis/ucrop/",
    "Firebase": "com/google/firebase/",
    "Play Services": "com/google/android/gms/",
    "Cast SDK": "com/google/android/gms/cast/",
    "Install Referrer": "com/android/installreferrer/",
    "AdMob / GMS Ads": "com/google/android/gms/ads/",
    "NewPipeExtractor": "org/schabi/newpipe/extractor/",
    "InnerTube models (InnerTune)": "com/music/innertube/",
    "Metrolist fork": "com/metrolist/innertubex/",
    "Zion Huang (InnerTune asli)": "com/zionhuang/",
    "Sentry": "io/sentry/",
    "Crashlytics": "com/google/firebase/crashlytics/",
    "SimpleXML": "org/simpleframework/xml/",
    "ThreeTen/Joda": "org/threeten/bp/",
    "Apache Commons": "org/apache/commons/",
    "ZXing": "com/google/zxing/",
    "AudioBeam / media3 cast": "androidx/media3/cast/",
}


def main():
    apk = sys.argv[1]
    all_strings = []
    print("=== header tiap DEX ===")
    with zipfile.ZipFile(apk) as z:
        for name in sorted(n for n in z.namelist() if re.fullmatch(r"classes\d*\.dex", n)):
            with z.open(name) as f:
                buf = f.read()
            h = dex_header_facts(buf)
            s = dex_strings(buf)
            all_strings += s
            print(f"  {name:<14} file={h['file_size']:>10,}  "
                  f"string_ids={h['string_ids']:>7,}  class_defs={h['class_defs']:>7,}  "
                  f"method_ids={h['method_ids']:>7,}")

    # hanya kelas: 'Lpkg/Kelas;' atau 'pkg/Kelas'
    classes = [s for s in all_strings if s.startswith("L") and s.endswith(";")
               and "/" in s]
    plain = [s for s in all_strings if not s.startswith("L") and "/" in s
             and re.fullmatch(r"[A-Za-z0-9_$/.-]+", s)]
    hay = classes + plain
    print(f"\ntotal string={len(all_strings):,}  kelas(desc)={len(classes):,}  "
          f"path={len(plain):,}")

    print(f"\n=== library terdeteksi (kelas cocok) ===")
    print(f"{'library':<34} {'kelas':>8}  status")
    print("-" * 62)
    present, absent = [], []
    for label, prefix in LIB_PROBES.items():
        pat = prefix
        n = sum(1 for s in hay if pat in s)
        status = "ADA" if n else "TIDAK ADA"
        (present if n else absent).append(label)
        print(f"{label:<34} {n:>8,}  {status}")

    print("\n=== tidak ditemukan ===")
    for a in absent:
        print(f"  {a.strip()}")

    print("\n=== awalan paket teratas (depth 3) ===")
    cnt = Counter()
    for s in hay:
        p = s[1:] if s.startswith("L") else s
        parts = p.split("/")
        if len(parts) >= 3:
            cnt["/".join(parts[:3])] += 1
    for k, v in cnt.most_common(30):
        print(f"  {v:>6,}  {k}")


if __name__ == "__main__":
    main()
