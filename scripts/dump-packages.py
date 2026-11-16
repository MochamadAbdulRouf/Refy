"""Dump daftar kelas untuk paket tertentu (verifikasi akhir sebelum menulis dokumen)."""

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


TARGETS = [
    r"Landroidx/compose/",
    r"Lokhttp3/",
    r"Lcoil3?/",
    r"Landroidx/room/",
    r"Lcom/music/shazamkit/[^/;]+;",
    r"Lecho/music/iad1tya/(utils|cipher|potoken|sabr|playback)/[^/;]+/",
    r"Lecho/music/iad1tya/[A-Za-z]+/[A-Za-z]+;",
    r"Lcom/google/android/gms/(cast|auth|location|ads)/",
    r"Lcom/google/firebase/[a-z]+/",
]


def main():
    apk = sys.argv[1]
    strings = []
    with zipfile.ZipFile(apk) as z:
        for n in sorted(x for x in z.namelist() if re.fullmatch(r"classes\d*\.dex", x)):
            with z.open(n) as f:
                strings += dex_strings(f.read())
    uniq = sorted(set(strings))

    for pat in TARGETS:
        rx = re.compile(pat)
        hits = sorted({s for s in uniq if rx.search(s)})
        print(f"\n=== {pat}  ({len(hits)}) ===")
        for h in hits[:45]:
            print(f"  {h}")
        if len(hits) > 45:
            print(f"  ... (+{len(hits)-45})")


if __name__ == "__main__":
    main()
