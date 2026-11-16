"""Tarik seluruh aset desain dari Stitch (READ-ONLY ke Stitch).

Menyimpan HTML + PNG + manifest + indeks layar ke .stitch/electric-purple/.
Tidak pernah menulis apa pun ke Stitch.

Pakai: python scripts/fetch-electric-purple.py [project_id]
Butuh STITCH_API_KEY (lihat .env.example).
"""

import base64
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from stitch_client import SNAP, call, slug

PID = sys.argv[1] if len(sys.argv) > 1 else "9190295645942987043"


def main():
    os.makedirs(SNAP, exist_ok=True)

    proj = call("get_project", {"name": "projects/" + PID})
    p = proj if isinstance(proj, dict) else proj.get("project", proj)
    print(f"project : {p.get('title')}")
    print(f"update  : {p.get('updateTime')}")

    # design system + manifest
    ds = call("list_design_systems", {"projectId": PID})
    ds_index = []
    for entry in ds.get("designSystems", []):
        d = entry.get("designSystem", {})
        ds_index.append({"displayName": d.get("displayName"), "name": entry.get("name")})
        mj = d.get("manifestJson")
        if mj:
            parsed = json.loads(mj)
            json.dump(parsed, open(os.path.join(SNAP, "DESIGN-manifest.json"), "w",
                                   encoding="utf-8"), ensure_ascii=False, indent=2)
            if parsed.get("designMd"):
                open(os.path.join(SNAP, "DESIGN.md"), "w", encoding="utf-8").write(
                    parsed["designMd"])
            print(f"design system: {d.get('displayName')} -> {entry.get('name')}")
    json.dump(ds_index, open(os.path.join(SNAP, "design-systems.json"), "w",
                             encoding="utf-8"), ensure_ascii=False, indent=2)

    # layar
    screens = call("list_screens", {"projectId": PID}).get("screens", [])
    records = []
    for i, s in enumerate(screens, 1):
        title = s.get("title") or "(untitled)"
        sl = slug(title, s.get("name"))
        html_res = (s.get("htmlCode") or {}).get("name")

        if not html_res:
            # entri aset gambar (avatar dsb), bukan layar
            print(f"  [{i:>2}] aset gambar: {title[:60]}")
            records.append({"title": title, "slug": sl, "screen": s.get("name"),
                            "width": s.get("width"), "height": s.get("height"),
                            "deviceType": s.get("deviceType"), "html_bytes": 0})
            continue

        html = call("download_file", {"name": html_res}).get("html", "")
        open(os.path.join(SNAP, sl + ".html"), "w", encoding="utf-8").write(html)
        print(f"  [{i:>2}] layar: {title[:52]:<54} {len(html):>7}b")

        shot = (s.get("screenshot") or {}).get("name")
        if shot:
            b64 = call("download_file", {"name": shot}).get("image", "")
            if b64:
                open(os.path.join(SNAP, sl + ".png"), "wb").write(base64.b64decode(b64))

        records.append({"title": title, "slug": sl, "screen": s.get("name"),
                        "width": s.get("width"), "height": s.get("height"),
                        "deviceType": s.get("deviceType"), "html_bytes": len(html)})

    json.dump(records, open(os.path.join(SNAP, "screens.json"), "w", encoding="utf-8"),
              ensure_ascii=False, indent=2)
    ui = sum(1 for r in records if r["html_bytes"] > 0)
    print(f"\n{len(records)} entri = {ui} layar UI + {len(records) - ui} aset gambar")


if __name__ == "__main__":
    main()