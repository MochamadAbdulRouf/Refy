"""Tarik PNG untuk layar yang belum punya pasangan gambar.

Pakai: python scripts/fetch-missing-png.py [project_id]
Butuh STITCH_API_KEY (lihat .env.example).
"""

import base64
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from stitch_client import SNAP, call, slug

PID = sys.argv[1] if len(sys.argv) > 1 else "9190295645942987043"


def main():
    os.makedirs(SNAP, exist_ok=True)
    screens = call("list_screens", {"projectId": PID}).get("screens", [])
    fetched = []
    for s in screens:
        sl = slug(s.get("title") or "untitled", s.get("name"))
        if not (s.get("htmlCode") or {}).get("name"):
            continue
        png = os.path.join(SNAP, sl + ".png")
        if os.path.exists(png):
            continue
        shot = (s.get("screenshot") or {}).get("name")
        if not shot:
            continue
        b64 = call("download_file", {"name": shot}).get("image", "")
        if b64:
            open(png, "wb").write(base64.b64decode(b64))
            fetched.append((sl, os.path.getsize(png)))

    print("PNG ditarik:", len(fetched))
    for n, sz in fetched:
        print(f"  {n}.png  {sz} byte")
    print("total PNG:", len([f for f in os.listdir(SNAP) if f.endswith(".png")]))


if __name__ == "__main__":
    main()