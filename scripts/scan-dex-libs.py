"""Pindai string library di DEX APK.

Dipakai untuk membaca tech stack dari artifact, bukan dari asumsi: DEX menyimpan
nama paket kelas, jadi library yang benar-benar dibundel akan terlihat namanya.

Pakai: python scan_dex_libs.py <apk> [--extract-dir DIR]
Hasil: tabel library+versi yang terdeteksi, frekuensi kemunculan, dan bukti mentah.
"""

import argparse
import os
import re
import zipfile
from collections import Counter

# Marker: (label, pola regex yang dicari di string pool DEX)
MARKERS = [
    # --- Fondasi Android ---
    ("AndroidX Core", rb"androidx/core/app/"),
    ("AndroidX Lifecycle", rb"androidx/lifecycle/"),
    ("AndroidX Navigation", rb"androidx/navigation/"),
    ("AndroidX WorkManager", rb"androidx/work/"),
    ("AndroidX Media3", rb"androidx/media3/"),
    ("AndroidX Room", rb"androidx/room/"),
    ("AndroidX DataStore", rb"androidx/datastore/"),
    ("AndroidX Paging", rb"androidx/paging/"),
    ("AndroidX Preference", rb"androidx/preference/"),
    ("Activity Compose", rb"androidx/activity/compose/"),
    ("Splash Screen", rb"androidx/core/splashscreen/"),
    ("Palette", rb"androidx/palette/"),
    ("Biometric", rb"androidx/biometric/"),
    # --- UI ---
    ("Jetpack Compose", rb"androidx/compose/"),
    ("Compose Material3", rb"androidx/compose/material3/"),
    ("Compose Foundation", rb"androidx/compose/foundation/"),
    ("Compose Runtime", rb"androidx/compose/runtime/"),
    ("Coil (gambar)", rb"coil3?/"),
    ("Lottie", rb"com/airbnb/lottie/"),
    ("Reorderable", rb"sh/calvin/reorderable"),
    ("Pager / accompanist", rb"com/google/accompanist/"),
    ("Material Components", rb"com/google/android/material/"),
    # --- DI ---
    ("Hilt", rb"dagger/hilt/"),
    ("Dagger", rb"^dagger/"),
    ("javax.inject", rb"javax/inject/"),
    ("Koin", rb"org/koin/"),
    # --- Async / Bahasa ---
    ("Kotlin stdlib", rb"kotlin/"),
    ("Kotlin Coroutines", rb"kotlinx/coroutines/"),
    ("kotlinx.serialization", rb"kotlinx/serialization/"),
    ("kotlinx.datetime", rb"kotlinx/datetime/"),
    ("Kotlin Reflect", rb"kotlin/reflect/"),
    ("Kotlin Immutable Collections", rb"kotlinx/collections/immutable/"),
    # --- Jaringan ---
    ("OkHttp", rb"okhttp3/"),
    ("Ktor", rb"io/ktor/"),
    ("Retrofit", rb"retrofit2/"),
    ("Okio", rb"okio/"),
    ("jsoup (HTML)", rb"org/jsoup/"),
    ("Chucker / okhttp intercept", rb"com/chuckerteam/"),
    # --- Serialisasi ---
    ("Gson", rb"com/google/gson/"),
    ("Moshi", rb"com/squareup/moshi/"),
    ("Jackson", rb"com/fasterxml/jackson/"),
    ("Protobuf", rb"com/google/protobuf/"),
    # --- Penyimpanan ---
    ("SQLite (framework)", rb"android/database/sqlite/"),
    ("SQLCipher", rb"net/sqlcipher/"),
    # --- Media ---
    ("Media3 ExoPlayer", rb"androidx/media3/exoplayer/"),
    ("Media3 Session", rb"androidx/media3/session/"),
    ("Media3 Download", rb"androidx/media3/datasource/"),
    ("FFmpegKit", rb"com/arthenica/ffmpegkit/"),
    ("FFmpeg native", rb"libffmpegkit"),
    ("ONNX Runtime", rb"ai/onnxruntime/"),
    ("QuickJS", rb"libquickjs|app/cash/quickjs"),
    ("Rhino (JS)", rb"org/mozilla/javascript/"),
    ("Kuromoji (Jepang)", rb"com/atilika/kuromoji/"),
    ("BouncyCastle", rb"org/bouncycastle/"),
    # --- Firebase / Google ---
    ("Firebase Analytics", rb"com/google/firebase/analytics/"),
    ("Firebase Installations", rb"com/google/firebase/installations/"),
    ("Firebase Storage", rb"com/google/firebase/storage/"),
    ("Firebase Crashlytics", rb"com/google/firebase/crashlytics/"),
    ("Google Play Services (base)", rb"com/google/android/gms/common/"),
    ("Play Services Cast", rb"com/google/android/gms/cast/"),
    ("Play Services Auth", rb"com/google/android/gms/auth/"),
    ("Play Services Location", rb"com/google/android/gms/location/"),
    ("Play Install Referrer", rb"com/android/installreferrer/"),
    # --- Iklan (harus 0) ---
    ("AdMob / GMS Ads", rb"com/google/android/gms/ads/"),
    ("Unity Ads", rb"com/unity3d/ads/"),
    ("AppLovin", rb"com/applovin/"),
    ("Facebook Audience", rb"com/facebook/ads/"),
    ("IronSource", rb"com/ironsource/"),
    # --- Observabilitas ---
    ("Sentry", rb"io/sentry/"),
    ("Bugsnag", rb"com/bugsnag/"),
    # --- Lain ---
    ("UCrop (crop gambar)", rb"com/yalantis/ucrop/"),
    ("ZXing (QR)", rb"com/google/zxing/"),
    ("Apache Commons", rb"org/apache/commons/"),
    ("Joda / ThreeTen", rb"org/threeten/bp/"),
    ("SimpleXML", rb"org/simpleframework/xml/"),
    ("AndroidX WebKit", rb"androidx/webkit/"),
    ("Chrome Custom Tabs", rb"androidx/browser/customtabs/"),
    ("NewPipeExtractor", rb"org/schabi/newpipe/extractor/"),
    ("InnerTube (asal InnerTune)", rb"com/music/innertube/"),
    ("Metrolist (nama paket)", rb"com/metrolist/innertubex/"),
]

# Versi: pola string yang biasanya menyimpan versi library
VERSION_HINTS = [
    rb"okhttp/(\d+\.\d+\.\d+)",
    rb"exoplayer/(\d+\.\d+\.\d+)",
    rb"ffmpeg-kit/(\w+)-(\S+)",
    rb"onnxruntime[/-](\d+\.\d+\.\d+)",
    rb"okio/(\d+\.\d+\.\d+)",
    rb"ktor[/-](\d+\.\d+\.\d+)",
    rb"compose[/-](\d+\.\d+\.\d+)",
]


def read_dex_strings(zpath, max_bytes=None):
    """Baca potongan DEX mentah. String pool DEX memuat nama kelas dalam UTF-8."""
    with zipfile.ZipFile(zpath) as z:
        for name in z.namelist():
            if re.fullmatch(r"classes\d*\.dex", name):
                with z.open(name) as f:
                    yield name, f.read(max_bytes) if max_bytes else f.read()


def count_pattern(blob, pattern):
    return len(re.findall(pattern, blob, re.I | re.M))


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("apk")
    ap.add_argument("--min-hits", type=int, default=1)
    args = ap.parse_args()

    blobs = {}
    print("=== memuat DEX ===")
    for name, blob in read_dex_strings(args.apk):
        blobs[name] = blob
        print(f"  {name}: {len(blob):,} byte")

    combined = b"\n".join(blobs.values())
    # String pool DEX dipisahkan NUL; sisipkan newline supaya pola ^...$ bekerja
    combined = combined.replace(b"\x00", b"\n")

    print("\n=== library terdeteksi (jumlah kemunculan) ===")
    hits = {}
    for label, pat in MARKERS:
        n = count_pattern(combined, pat)
        hits[label] = n

    for label, n in sorted(hits.items(), key=lambda kv: -kv[1]):
        if n >= args.min_hits:
            print(f"  {n:>7,}  {label}")

    print("\n=== TIDAK ditemukan (0 kemunculan) ===")
    zero = [l for l, n in hits.items() if n == 0]
    for l in zero:
        print(f"  {l}")

    print("\n=== petunjuk versi ===")
    for pat in VERSION_HINTS:
        found = Counter(re.findall(pat, combined, re.I))
        if found:
            print(f"  {pat.decode()}: {dict(found)}")


if __name__ == "__main__":
    main()
