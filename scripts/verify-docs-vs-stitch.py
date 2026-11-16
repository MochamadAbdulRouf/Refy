"""Bandingkan klaim dokumen dengan keadaan Stitch yang HIDUP.

Read-only. Mencetak laporan selisih: judul project, design system,
jumlah layar, layar baru/hilang, dan HTML yang berubah.

Pakai: python scripts/verify-docs-vs-stitch.py [project_id]
Butuh STITCH_API_KEY (lihat .env.example).
"""

import glob
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from stitch_client import SNAP, call, slug

PID = sys.argv[1] if len(sys.argv) > 1 else "9190295645942987043"


def main():
    print("=" * 72)
    print("1. Keadaan project (live)")
    print("=" * 72)
    proj = call("get_project", {"name": "projects/" + PID})
    p = proj if isinstance(proj, dict) else proj.get("project", proj)
    print("title      :", p.get("title"))
    print("updateTime :", p.get("updateTime"))
    print("type       :", p.get("projectType"))

    # Dulu skrip ini membandingkan judul, dan judul bukan identitas.
    # Bukti dari snapshot ini sendiri: TIGA layar berbagi `<h1>` yang sama, dan
    # Stitch mengizinkan dua layar berjudul identik (`Downloads Management`
    # 2f6ecab6 vs b26aa75b). Karena itu identitas dinilai dari slug (judul +
    # 8 char id layar) dan dari resource name layar, bukan dari judul.
    print("\n" + "=" * 72)
    print("2. Design system")
    print("=" * 72)
    ds = call("list_design_systems", {"projectId": PID})
    ds_ids = []
    for entry in ds.get("designSystems", []):
        d = entry.get("designSystem", {})
        ds_ids.append(entry.get("name", ""))
        print("name:", d.get("displayName"), "|", entry.get("name"))
    if not ds_ids:
        print("!! tidak ada design system dilaporkan — perlakukan sebagai gagal, bukan 'kosong'")

    print("\n" + "=" * 72)
    print("3. Layar: live vs snapshot")
    print("=" * 72)
    live = call("list_screens", {"projectId": PID}).get("screens", [])
    idx = os.path.join(SNAP, "screens.json")
    saved = json.load(open(idx, encoding="utf-8")) if os.path.exists(idx) else []

    live_ui = [s for s in live if (s.get("htmlCode") or {}).get("name")]
    saved_ui = [r for r in saved if r.get("html_bytes", 0) > 0]

    live_slugs = {slug(s.get("title") or "(untitled)", s.get("name")) for s in live}
    saved_slugs = {r["slug"] for r in saved}
    live_names = {s.get("name") for s in live}
    saved_names = {r.get("screen") for r in saved}

    print(f"live : {len(live)} entri = {len(live_ui)} layar + "
          f"{len(live) - len(live_ui)} aset")
    print(f"snap : {len(saved)} entri = {len(saved_ui)} layar + "
          f"{len(saved) - len(saved_ui)} aset")
    if not live:
        print("!! live kosong — ini kegagalan panggilan, bukan proyek kosong. "
              "Jangan simpulkan selisih dari hasil ini.")
    print("hanya di live (slug) :", sorted(live_slugs - saved_slugs) or "-")
    print("hanya di snap (slug) :", sorted(saved_slugs - live_slugs) or "-")
    print("hanya di live (id)   :", sorted(live_names - saved_names) or "-")
    print("hanya di snap (id)   :", sorted(saved_names - live_names) or "-")

    print("\n" + "=" * 72)
    print("4. HTML di disk masih sama dengan live?")
    print("=" * 72)
    beda, kosong, tidak_ada = [], [], []
    for s in live_ui:
        title = s.get("title")
        path = os.path.join(SNAP, slug(title, s.get("name")) + ".html")
        if not os.path.exists(path):
            print(f"  {'TIDAK ADA':<24} {title}")
            tidak_ada.append(title)
            continue
        res = (s.get("htmlCode") or {}).get("name")
        html = ""
        # Server sesekali membalas tanpa kunci "html" — coba ulang sebelum
        # menyimpulkan "BEDA live=0", yang sebenarnya cuma respons kosong.
        for _ in range(3):
            html = call("download_file", {"name": res}, tries=1).get("html", "")
            if html.strip():
                break
        disk = open(path, encoding="utf-8").read()
        if not html.strip():
            status = "KOSONG (server tidak membalas html)"
            kosong.append(title)
        elif html.strip() == disk.strip():
            status = "SAMA"
        else:
            status = f"BEDA live={len(html)} disk={len(disk)}"
            beda.append(title)
        print(f"  {status:<24} {title}")

    print("\n" + "=" * 72)
    print("5. Integritas snapshot")
    print("=" * 72)
    html_files = len(glob.glob(os.path.join(SNAP, "*.html")))
    png_files = len(glob.glob(os.path.join(SNAP, "*.png")))
    print(f"  html: {html_files}   png: {png_files}")

    print("\n" + "=" * 72)
    print("6. Ringkasan")
    print("=" * 72)
    print(f"  BERBEDA isi      : {len(beda)}", beda or "")
    print(f"  HTML kosong      : {len(kosong)}", kosong or "")
    print(f"  HTML tidak ada   : {len(tidak_ada)}", tidak_ada or "")
    print(f"  updateTime live  : {p.get('updateTime')}")
    print("  updateTime yang tercatat di dokumen: lihat index.md / docs/DESIGN.md")
    print("  Sinkron = 0 berbeda, 0 kosong, 0 tidak ada, dan set slug sama.")
    if beda or kosong or tidak_ada or live_slugs != saved_slugs:
        print("\n  HASIL: TIDAK SINKRON — jalankan scripts/resync-screens.py")
        sys.exit(1)
    print("\n  HASIL: SINKRON")


if __name__ == "__main__":
    main()