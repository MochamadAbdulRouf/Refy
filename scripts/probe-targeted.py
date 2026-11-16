"""Probe bertarget: cetak string yang BENAR-BENAR ada, bukan hanya hitungan.

Dipanggil sesudah `dex-strings.py`. Fungsinya menjawab pertanyaan spesifik yang
masih ambigu: framework UI mana yang dipakai, DI apa, image loader apa, dan
paket Java untuk library native (yang namanya sering di-rename R8).
"""

import re
import struct
import sys
import zipfile
from collections import Counter


def uleb128(buf, off):
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
    if buf[:4] != b"dex\n":
        return []
    size, off = struct.unpack_from("<II", buf, 0x38)
    out = []
    for i in range(size):
        (d,) = struct.unpack_from("<I", buf, off + 4 * i)
        _, p = uleb128(buf, d)
        out.append(buf[p:buf.index(b"\x00", p)].decode("utf-8", "replace"))
    return out


QUESTIONS = {
    "Compose: kelas apa saja?": r"^L?androidx/compose/[A-Za-z0-9_$/]+;?$",
    "Compose ViewBinding Interop": r"^L?androidx/[A-Za-z0-9_$]*compose[A-Za-z0-9_$/]*;?$",
    "AppCompat/Fragment/RecyclerView aktif": r"^L?androidx/(appcompat|fragment|recyclerview)/[A-Za-z0-9_$/]+$",
    "DI: paket apa": r"^L?(dagger|javax/inject|org/koin|com/google/dagger)[A-Za-z0-9_$/]*;?$",
    "Image loader": r"^L?.*(coil|glide|picasso|fresco|coil3)[A-Za-z0-9_$/]*;?$",
    "ONNX Java binding": r"^L?[A-Za-z0-9_$/]*onnx[A-Za-z0-9_$/]*;?$",
    "QuickJS Java binding": r"^L?[A-Za-z0-9_$/]*quickjs[A-Za-z0-9_$/]*;?$",
    "ShazamKit / audio recognition": r"^L?com/music/shazamkit[A-Za-z0-9_$/]*;?$",
    "Ktor modul": r"^L?io/ktor/(openapi|client|server|websocket|sse|serialization)[A-Za-z0-9_$/]*$",
    "Cronet / Chromium net": r"^L?org/chromium/net[A-Za-z0-9_$/]*$",
    "Media3 kelas": r"^L?androidx/media3/[A-Za-z0-9_$/]+$",
    "Room kelas": r"^L?androidx/room[A-Za-z0-9_$/]*$",
    "DataStore kelas": r"^L?androidx/datastore/[A-Za-z0-9_$/]+$",
    "Komponen AndroidX utama": r"^L?androidx/[A-Za-z0-9_$]+/[A-Za-z0-9_$]+$",
}


def main():
    apk = sys.argv[1]
    strings = []
    with zipfile.ZipFile(apk) as z:
        for n in sorted(x for x in z.namelist() if re.fullmatch(r"classes\d*\.dex", x)):
            with z.open(n) as f:
                strings += dex_strings(f.read())

    # buang duplikat, tapi simpan yang descriptor kelas
    uniq = sorted(set(strings))

    for title, pat in QUESTIONS.items():
        rx = re.compile(pat)
        hits = [s for s in uniq if rx.match(s)]
        print(f"\n=== {title}  ({len(hits)} unik) ===")
        for h in hits[:24]:
            print(f"  {h}")
        if len(hits) > 24:
            print(f"  ... (+{len(hits) - 24} lainnya)")

    # tebakan nama paket hasil rename: cari kelas berisi kata kunci di path pendek
    print("\n=== paket pendek yang mirip hasil rename (2-3 segmen, diawali huruf kecil) ===")
    short = Counter()
    for s in uniq:
        if not s.startswith("L") or not s.endswith(";"):
            continue
        p = s[1:-1]
        seg = p.split("/")
        if 2 <= len(seg) <= 3 and len(seg[0]) <= 3:
            short["/".join(seg[:2])] += 1
    for k, v in short.most_common(20):
        print(f"  {v:>5,}  {k}")


if __name__ == "__main__":
    main()
