"""Inventaris kelas TERDEFINISI di DEX (bukan sekadar referensi string).

Kenapa perlu: string pool DEX memuat dua hal berbeda —
  (a) descriptor kelas yang BENAR-BENAR didefinisikan APK ini, dan
  (b) nama tipe yang hanya muncul sebagai parameter/return di signature method.
`grep` tidak bisa memisahkan keduanya, jadi hitungannya melebih-lebihkan
(mis. Compose tampak "ada" padahal hanya dipakai sebagai tipe parameter).

Script ini membaca `class_defs` langsung: table itu hanya memuat kelas yang
didefinisikan di DEX tersebut. Hasilnya = daftar final pasca-R8.

Pakai: python dex-classes.py <apk>
"""

import re
import struct
import sys
import zipfile
from collections import Counter


def uleb128(buf, off):
    r = 0
    sh = 0
    while True:
        b = buf[off]
        off += 1
        r |= (b & 0x7F) << sh
        if not (b & 0x80):
            return r, off
        sh += 7


class Dex:
    """Pembaca minimal satu file DEX: string pool + class_defs."""

    def __init__(self, buf):
        self.buf = buf
        self.n_strings, self.off_strings = struct.unpack_from("<II", buf, 0x38)
        self.n_types, self.off_types = struct.unpack_from("<II", buf, 0x40)
        self.n_classes, self.off_classes = struct.unpack_from("<II", buf, 0x60)
        self._strings = None

    @property
    def strings(self):
        if self._strings is None:
            out = []
            for i in range(self.n_strings):
                (d,) = struct.unpack_from("<I", self.buf, self.off_strings + 4 * i)
                _, p = uleb128(self.buf, d)
                out.append(self.buf[p:self.buf.index(b"\x00", p)]
                           .decode("utf-8", "replace"))
            self._strings = out
        return self._strings

    def type_name(self, type_idx):
        """type_ids[idx] -> string_idx -> nama tipe.

        Format DEX: `Lpkg/Kelas;`. Diubah ke bentuk Java (`pkg.Kelas`) supaya
        bisa dibandingkan dengan `startswith` paket biasa.
        """
        (si,) = struct.unpack_from("<I", self.buf, self.off_types + 4 * type_idx)
        raw = self.strings[si]
        if raw.startswith("L") and raw.endswith(";"):
            return raw[1:-1].replace("/", ".")
        return raw

    def defined_classes(self):
        """Nama kelas yang didefinisikan DEX ini (class_defs)."""
        out = []
        for i in range(self.n_classes):
            # class_def_item: class_idx(4) access(4) superclass(4) ...
            (ci,) = struct.unpack_from("<I", self.buf, self.off_classes + 32 * i)
            out.append(self.type_name(ci))
        return out


LIB_MARKERS = {
    "Jetpack Compose (runtime)": "androidx.compose.runtime",
    "Jetpack Compose (ui)": "androidx.compose.ui",
    "Compose Material3": "androidx.compose.material3",
    "Compose Material (M2)": "androidx.compose.material.",
    "Compose Foundation": "androidx.compose.foundation",
    "Compose Animation": "androidx.compose.animation",
    "Activity Compose": "androidx.activity.compose",
    "Navigation Compose": "androidx.navigation.compose",
    "View system: AppCompat": "androidx.appcompat",
    "View system: Fragment": "androidx.fragment",
    "View system: RecyclerView": "androidx.recyclerview",
    "View system: ConstraintLayout": "androidx.constraintlayout",
    "View system: ViewPager2": "androidx.viewpager2",
    "View system: ViewBinding": "androidx.viewbinding",
    "View system: DataBinding": "androidx.databinding",
    "Material Components (View)": "com.google.android.material",
    "AndroidX Navigation (View)": "androidx.navigation",
    "AndroidX Lifecycle": "androidx.lifecycle",
    "AndroidX Core": "androidx.core",
    "AndroidX Activity": "androidx.activity",
    "AndroidX Room": "androidx.room",
    "AndroidX DataStore": "androidx.datastore",
    "AndroidX WorkManager": "androidx.work",
    "AndroidX Media3": "androidx.media3",
    "  Media3 ExoPlayer": "androidx.media3.exoplayer",
    "  Media3 Session": "androidx.media3.session",
    "  Media3 UI": "androidx.media3.ui",
    "AndroidX Mediarouter": "androidx.mediarouter",
    "AndroidX WebKit": "androidx.webkit",
    "AndroidX Glance": "androidx.glance",
    "AndroidX Paging": "androidx.paging",
    "Hilt": "dagger.hilt",
    "Dagger": "dagger.",
    "javax.inject": "javax.inject",
    "Koin": "org.koin",
    "Kotlin stdlib": "kotlin.",
    "Kotlin Coroutines": "kotlinx.coroutines",
    "kotlinx.serialization": "kotlinx.serialization",
    "Ktor": "io.ktor",
    "OkHttp": "okhttp3",
    "Okio": "okio",
    "Retrofit": "retrofit2",
    "Gson": "com.google.gson",
    "Moshi": "com.squareup.moshi",
    "Protocol Buffers": "com.google.protobuf",
    "Coil": "coil3",
    "Glide": "com.bumptech.glide",
    "FFmpegKit": "com.arthenica.ffmpegkit",
    "ONNX Runtime (Java)": "ai.onnxruntime",
    "QuickJS (dokar)": "com.dokar.quickjs",
    "Rhino": "org.mozilla.javascript",
    "Kuromoji": "com.atilika.kuromoji",
    "BouncyCastle": "org.bouncycastle",
    "UCrop": "com.yalantis.ucrop",
    "Firebase": "com.google.firebase",
    "Play Services": "com.google.android.gms",
    "  Cast SDK": "com.google.android.gms.cast",
    "  Google Sign-In": "com.google.android.gms.auth",
    "AdMob": "com.google.android.gms.ads",
    "NewPipeExtractor": "org.schabi.newpipe.extractor",
    "InnerTube models": "com.music.innertube",
    "Metrolist": "com.metrolist.innertubex",
    "ShazamKit": "com.music.shazamkit",
    "Aplikasi sendiri": "echo.music.iad1tya",
}

OWN_PREFIX = "echo.music.iad1tya."


def main():
    apk = sys.argv[1]
    all_classes = []
    print("=== DEX di APK ===")
    with zipfile.ZipFile(apk) as z:
        for name in sorted(n for n in z.namelist() if re.fullmatch(r"classes\d*\.dex", n)):
            with z.open(name) as f:
                d = Dex(f.read())
            cs = d.defined_classes()
            all_classes += cs
            print(f"  {name:<14} kelas terdefinisi: {len(cs):>7,}  string: {len(d.strings):>7,}")

    print(f"\ntotal kelas terdefinisi: {len(all_classes):,}")
    uniq = sorted(set(all_classes))

    print(f"\n{'library':<34} {'kelas':>7}  status")
    print("-" * 54)
    for label, prefix in LIB_MARKERS.items():
        n = sum(1 for c in uniq if c.startswith(prefix))
        print(f"{label:<34} {n:>7,}  {'ADA' if n else 'TIDAK ADA'}")

    print("\n=== arsitektur aplikasi sendiri ===")
    own = Counter()
    for c in uniq:
        if c.startswith(OWN_PREFIX):
            rest = c[len(OWN_PREFIX):].split(".")
            own[rest[0] if rest else "?"] += 1
    for k, v in own.most_common(40):
        print(f"  {v:>5,}  {k}")


if __name__ == "__main__":
    main()
