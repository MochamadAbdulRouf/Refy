"""Uji penentu: apakah aplikasi ini benar-benar Compose atau view system?

Kenapa perlu uji khusus: menghitung nama paket tidak cukup, karena string pool
DEX memuat tipe yang hanya dipakai sebagai parameter. Sinyal yang TIDAK bisa
dipalsukan:

  1. `Landroidx/compose/runtime/Composer;` sebagai PARAMETER method. Setiap
     fungsi @Composable menerima Composer. Aplikasi Compose nyata punya ribuan;
     sisa dependensi hanya punya satu-dua.
  2. Jumlah entri `layout/` di tabel resource. Aplikasi view-system punya
     puluhan-ratusan; aplikasi Compose murni hampir nol.

Pakai: python probe-compose-vs-view.py <apk>
"""

import re
import struct
import subprocess
import sys
import zipfile
from collections import Counter

SIGNATURES = {
    "Composer param (@Composable)": rb"Landroidx/compose/runtime/Composer;",
    "MutableState": rb"Landroidx/compose/runtime/MutableState;",
    "remember (runtime)": rb"androidx/compose/runtime/RememberKt",
    "Composable annotation": rb"Landroidx/compose/runtime/Composable;",
    "setContent (activity compose)": rb"Landroidx/activity/compose/ComponentActivityKt",
    "Modifier (compose ui)": rb"Landroidx/compose/ui/Modifier;",
    "Column/Row/Lazy (foundation)": rb"androidx/compose/foundation/layout/",
    "Material3 theme": rb"androidx/compose/material3/MaterialTheme",
    "PreviewActivity (tooling)": rb"androidx/compose/ui/tooling/PreviewActivity",
    "RecyclerView.Adapter (view)": rb"Landroidx/recyclerview/widget/RecyclerView\$Adapter;",
    "Fragment (view)": rb"Landroidx/fragment/app/Fragment;",
    "AppCompatActivity (view)": rb"Landroidx/appcompat/app/AppCompatActivity;",
    "LayoutInflater (view)": rb"Landroid/view/LayoutInflater;",
    "R.layout (view)": rb"Landroid/R\$layout;",
    "R.id (view)": rb"Landroid/R\$id;",
    "findViewById (view)": rb"findViewById",
    "ViewBinding (view)": rb"Landroidx/viewbinding/ViewBinding;",
}


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
    size, off = struct.unpack_from("<II", buf, 0x38)
    out = []
    for i in range(size):
        (d,) = struct.unpack_from("<I", buf, off + 4 * i)
        _, p = uleb128(buf, d)
        out.append(buf[p:buf.index(b"\x00", p)].decode("utf-8", "replace"))
    return out


def main():
    apk = sys.argv[1]
    strings = []
    with zipfile.ZipFile(apk) as z:
        for n in sorted(x for x in z.namelist() if re.fullmatch(r"classes\d*\.dex", x)):
            with z.open(n) as f:
                strings += dex_strings(f.read())
    blob = "\u0000".join(strings).encode("utf-8", "replace")

    print("=== sinyal di string pool DEX (kemunculan mentah) ===")
    print(f"{'sinyal':<34} {'hits':>9}  baca")
    print("-" * 62)
    for label, pat in SIGNATURES.items():
        n = len(re.findall(pat, blob))
        print(f"{label:<34} {n:>9,}  {'kuat' if n > 20 else ('ada' if n else 'tidak ada')}")

    print("\n=== tabel resource: berapa entri tipe 'layout'? ===")
    try:
        out = subprocess.run(["aapt2", "dump", "resources", apk],
                             capture_output=True, text=True, encoding="utf-8",
                             errors="replace", timeout=300)
        text = out.stdout
        kinds = Counter(re.findall(r"^\s+type (\w+)", text, re.M))
        # aapt2 menulis "type <nama>" sebelum daftar entri
        for k, v in kinds.most_common(12):
            print(f"  {k:<24} {v}")
        if not kinds:
            layouts = len(re.findall(r"\blayout/", text))
            print(f"  (fallback) entri 'layout/': {layouts}")
            print(f"  ukuran output aapt2: {len(text):,} char")
    except FileNotFoundError:
        print("  aapt2 tidak ada di PATH — lewati")
    except subprocess.TimeoutExpired:
        print("  aapt2 timeout")


if __name__ == "__main__":
    main()
