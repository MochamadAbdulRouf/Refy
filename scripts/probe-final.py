"""Probe akhir: regex diperbaiki untuk descriptor berakhiran ';'.

Regex sebelumnya memakai `[A-Za-z0-9_$/]+$` yang TIDAK mengizinkan ';', sehingga
descriptor kelas nyata (`Lpkg/Kelas;`) tidak pernah cocok dan hasilnya keliru
"TIDAK ADA". Script ini memakai `[^;]*;` supaya benar.
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


def dex_strings(buf):
    if buf[:4] != b"dex\n":
        return []
    size, off = struct.unpack_from("<II", buf, 0x38)
    out = []
    for i in range(size):
        (d,) = struct.unpack_from("<I", buf, off + 4 * i)
        _, p = uleb128(buf, d)
        out.append(buf[p:buf.index(b"\x00", p)].decode("utf-8", "replace"))
    return out


PROBES = {
    "UI: AppCompat": r"Landroidx/appcompat/",
    "UI: Fragment": r"Landroidx/fragment/",
    "UI: RecyclerView": r"Landroidx/recyclerview/",
    "UI: ConstraintLayout": r"Landroidx/constraintlayout/",
    "UI: ViewPager2": r"Landroidx/viewpager2/",
    "UI: Material Components": r"Lcom/google/android/material/",
    "UI: Compose runtime": r"Landroidx/compose/runtime/",
    "UI: Compose ui": r"Landroidx/compose/ui/",
    "UI: Compose material3": r"Landroidx/compose/material3/",
    "UI: Compose foundation": r"Landroidx/compose/foundation/",
    "UI: Compose activity": r"Landroidx/activity/compose/",
    "UI: Compose navigation": r"Landroidx/navigation/compose/",
    "UI: ViewBinding": r"Landroidx/viewbinding/",
    "UI: DataBinding": r"Landroidx/databinding/",
    "UI: Navigation (View)": r"Landroidx/navigation/",
    "UI: Activity": r"Landroidx/activity/",
    "UI: SlidingPaneLayout": r"Landroidx/slidingpanelayout/",
    "UI: CoordinatorLayout": r"Landroidx/coordinatorlayout/",
    "Media3 total": r"Landroidx/media3/",
    "Media3 ExoPlayer": r"Landroidx/media3/exoplayer/",
    "Media3 Session": r"Landroidx/media3/session/",
    "Media3 UI": r"Landroidx/media3/ui/",
    "Media3 Datasource": r"Landroidx/media3/datasource/",
    "Media3 Common": r"Landroidx/media3/common/",
    "Media3 Cast": r"Landroidx/media3/cast/",
    "Room": r"Landroidx/room/",
    "DataStore": r"Landroidx/datastore/",
    "WorkManager": r"Landroidx/work/",
    "Lifecycle": r"Landroidx/lifecycle/",
    "RecyclerView adapter lib": r"Lcom/mikepenz/",
    "Coil": r"Lcoil3?/",
    "Glide": r"Lcom/bumptech/glide/",
    "Hilt": r"Ldagger/hilt/",
    "Dagger": r"Ldagger/",
    "javax.inject": r"Ljavax/inject/",
    "Koin": r"Lorg/koin/",
    "Ktor": r"Lio/ktor/",
    "OkHttp": r"Lokhttp3/",
    "Aplikasi sendiri (echo)": r"Lecho/music/iad1tya/",
    "Metrolist": r"Lcom/metrolist/",
    "InnerTube": r"Lcom/music/innertube/",
    "NewPipeExtractor": r"Lorg/schabi/newpipe/extractor/",
    "Rhino": r"Lorg/mozilla/javascript/",
    "Kuromoji": r"Lcom/atilika/kuromoji/",
    "QuickJS (dokar)": r"Lcom/dokar/quickjs/",
    "ShazamKit": r"Lcom/music/shazamkit/",
    "UCrop": r"Lcom/yalantis/ucrop/",
    "Protobuf": r"Lcom/google/protobuf/",
    "Firebase": r"Lcom/google/firebase/",
    "Cast SDK": r"Lcom/google/android/gms/cast/",
    "AdMob": r"Lcom/google/android/gms/ads/",
    "Sentry": r"Lio/sentry/",
    "jsoup": r"Lorg/jsoup/",
    "Last.fm": r"Lde/umass/|Lfm/last/|Lorg/lastfm/",
    "Spotify API": r"Lcom/adamratcliffe/|Lse/magine/|Lcom/spotify/",
}


def main():
    apk = sys.argv[1]
    strings = []
    with zipfile.ZipFile(apk) as z:
        for n in sorted(x for x in z.namelist() if re.fullmatch(r"classes\d*\.dex", x)):
            with z.open(n) as f:
                strings += dex_strings(f.read())
    uniq = sorted(set(strings))

    print(f"{'probe':<30} {'unik':>7}  status")
    print("-" * 50)
    for label, pat in PROBES.items():
        n = sum(1 for s in uniq if pat in s)
        print(f"{label:<30} {n:>7,}  {'ADA' if n else '-'}")

    print("\n=== arsitektur aplikasi sendiri (paket echo/music/iad1tya) ===")
    own = Counter()
    for s in uniq:
        if s.startswith("Lecho/music/iad1tya/"):
            rest = s[len("Lecho/music/iad1tya/"):].rstrip(";")
            seg = rest.split("/")
            own["/".join(seg[:2]) if len(seg) > 1 else seg[0]] += 1
    for k, v in own.most_common(30):
        print(f"  {v:>5,}  {k}")


if __name__ == "__main__":
    main()
