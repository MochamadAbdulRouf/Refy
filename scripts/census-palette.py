"""Sensus palet per layar: berapa banyak warna Kubu A vs Kubu B.

Dipakai untuk memverifikasi berapa layar yang masih perlu diremap ke Kubu B
setelah design system Nocturne Audio terbukti memuat dua palet.

Pakai: python scripts/census-palette.py [project_id]
"""

import os
import sys
from collections import Counter

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from stitch_client import SNAP, call, slug

PID = sys.argv[1] if len(sys.argv) > 1 else "9190295645942987043"

PROSE = ["#09090b", "#141418", "#8b5cf6", "#a855f7", "#6366f1", "#a1a1aa", "#27272a"]
TOKENS = ["#131315", "#d0bcff", "#a078ff", "#201f22", "#e5e1e4", "#cbc3d7", "#494454"]


def ensure_snapshot():
    """Pastikan HTML lokal ada; kalau tidak, tarik dulu (read-only)."""
    screens = call("list_screens", {"projectId": PID}).get("screens", [])
    os.makedirs(SNAP, exist_ok=True)
    for s in screens:
        html_res = (s.get("htmlCode") or {}).get("name")
        if not html_res:
            continue  # entri aset gambar, bukan layar
        path = os.path.join(SNAP, slug(s.get("title") or "untitled", s.get("name")) + ".html")
        if os.path.exists(path):
            continue
        html = call("download_file", {"name": html_res}).get("html", "")
        if html:
            open(path, "w", encoding="utf-8").write(html)
            print(f"  ditarik: {os.path.basename(path)}")


def main():
    ensure_snapshot()
    files = sorted(f for f in os.listdir(SNAP) if f.endswith(".html"))
    print(f"\n{'layar':<46} {'prosa':>6} {'token':>6}  verdict")
    print("-" * 74)
    tally = Counter()
    for f in files:
        s = open(os.path.join(SNAP, f), encoding="utf-8").read().lower()
        ps = sum(s.count(h) for h in PROSE)
        ts = sum(s.count(h) for h in TOKENS)
        v = "token" if ts > ps else "prosa"
        tally[v] += 1
        print(f"{f[:-5]:<46} {ps:>6} {ts:>6}  {v}")
    print("-" * 74)
    print(f"perlu diremap ke Kubu B: {tally['token']}   sudah Kubu B: {tally['prosa']}")


if __name__ == "__main__":
    main()