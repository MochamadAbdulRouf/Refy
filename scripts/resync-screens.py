"""Sinkronisasi ulang snapshot desain dengan Stitch hidup.

Mendeteksi layar BARU / BERUBAH / DIHAPUS, lalu menyimpan HTML + PNG.
READ-ONLY ke Stitch.

Pakai: python scripts/resync-screens.py [project_id] [--force-png]

Jalan dari direktori mana pun (path snapshot dihitung dari akar repo lewat
stitch_client.SNAP). Butuh STITCH_API_KEY (lihat .env.example).

Catatan 2026-11-10:
- Nama berkas memakai slug + 8 karakter pertama id layar. Wajib, karena Stitch
  mengizinkan dua layar berjudul sama (dua "Downloads Management").
- Layar yang berubah otomatis menarik ulang PNG-nya, bukan hanya kalau PNG hilang.
"""

import base64
import json
import os
import sys

from stitch_client import SNAP, call, snap, slug

PID = sys.argv[1] if len(sys.argv) > 1 and not sys.argv[1].startswith("-") else "9190295645942987043"
FORCE_PNG = "--force-png" in sys.argv


def main():
    os.makedirs(SNAP, exist_ok=True)

    proj = call("get_project", {"name": "projects/" + PID})
    p = proj if isinstance(proj, dict) else proj.get("project", proj)
    print(f"project    : {p.get('title')}")
    print(f"updateTime : {p.get('updateTime')}")

    index_path = snap("screens.json")
    old = json.load(open(index_path, encoding="utf-8")) if os.path.exists(index_path) else []

    live = call("list_screens", {"projectId": PID}).get("screens", [])
    print(f"\nLIVE: {len(live)} entri   snapshot lama: {len(old)}\n")
    print(f"{'#':<3} {'status':<11} {'judul':<46} {'byte':>8} {'png':>5}")
    print("-" * 78)

    records, added, updated = [], [], []
    for i, s in enumerate(live, 1):
        title = s.get("title") or "(untitled)"
        sid = s.get("name", "").rsplit("/", 1)[-1]
        sl = slug(title, s.get("name"))
        html_res = (s.get("htmlCode") or {}).get("name")
        shot = (s.get("screenshot") or {}).get("name")
        png_path = snap(sl + ".png")

        if not html_res:
            # entri aset gambar (avatar, image.png), bukan layar UI
            png_note = "ada" if os.path.exists(png_path) else "-"
            print(f"{i:<3} {'ASET':<11} {title[:45]:<46} {'-':>8} {png_note:>5}")
            if shot and (FORCE_PNG or not os.path.exists(png_path)):
                b64 = call("download_file", {"name": shot}).get("image", "")
                if b64:
                    open(png_path, "wb").write(base64.b64decode(b64))
            records.append({"title": title, "slug": sl, "screen": s.get("name"),
                            "width": s.get("width"), "height": s.get("height"),
                            "deviceType": s.get("deviceType"), "html_bytes": 0})
            continue

        html = call("download_file", {"name": html_res}).get("html", "")
        path = snap(sl + ".html")
        prev = open(path, encoding="utf-8").read() if os.path.exists(path) else None

        if prev is None:
            status = "BARU"
            added.append(title)
        elif prev.strip() != html.strip():
            status = "BERUBAH"
            updated.append((title, len(prev), len(html)))
        else:
            status = "tetap"

        open(path, "w", encoding="utf-8").write(html)

        # PNG ikut disegarkan kalau layar berubah / belum ada / dipaksa
        png_note = "ada" if os.path.exists(png_path) else "-"
        if shot and (FORCE_PNG or status != "tetap" or not os.path.exists(png_path)):
            b64 = call("download_file", {"name": shot}).get("image", "")
            if b64:
                open(png_path, "wb").write(base64.b64decode(b64))
                png_note = "BARU"

        print(f"{i:<3} {status:<11} {title[:45]:<46} {len(html):>8} {png_note:>5}")

        records.append({"title": title, "slug": sl, "screen": s.get("name"),
                        "width": s.get("width"), "height": s.get("height"),
                        "deviceType": s.get("deviceType"), "html_bytes": len(html),
                        "updated": status})

    json.dump(records, open(index_path, "w", encoding="utf-8"),
              ensure_ascii=False, indent=2)

    live_slugs = {r["slug"] for r in records}
    gone = [r["title"] for r in old if r["slug"] not in live_slugs]
    ui = sum(1 for r in records if r["html_bytes"] > 0)

    print("\nBARU    :", added or "-")
    print("BERUBAH :", [u[0] for u in updated] or "-")
    print("DIHAPUS :", gone or "-")
    print(f"TOTAL   : {len(records)} entri = {ui} layar UI + {len(records) - ui} aset")


if __name__ == "__main__":
    main()
