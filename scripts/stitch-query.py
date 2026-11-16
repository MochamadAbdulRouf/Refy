"""Query Stitch MCP (read-only): daftar project, layar, design system.

Pakai:
    python scripts/stitch-query.py                 # list projects
    python scripts/stitch-query.py screens <pid>   # list screens
    python scripts/stitch-query.py ds <pid>        # design systems
    python scripts/stitch-query.py project <pid>   # detail project

Butuh STITCH_API_KEY di environment atau .env (lihat .env.example).
"""

import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from stitch_client import call


def main():
    argv = sys.argv[1:]
    cmd = argv[0] if argv else "projects"

    if cmd == "projects":
        data = call("list_projects", {})
        for p in data.get("projects", []):
            print(f"{p.get('title')} | {p.get('name')} | {p.get('projectType')} "
                  f"| {p.get('updateTime')}")
        return

    if len(argv) < 2:
        raise SystemExit(f"butuh project id untuk '{cmd}'")
    pid = argv[1]

    if cmd == "screens":
        data = call("list_screens", {"projectId": pid})
        screens = data.get("screens", [])
        ui = [s for s in screens if (s.get("htmlCode") or {}).get("name")]
        print(f"total entri: {len(screens)} | layar dengan HTML: {len(ui)}")
        for s in screens:
            has_html = "layar" if (s.get("htmlCode") or {}).get("name") else "aset "
            print(f"  [{has_html}] {s.get('title')} | {s.get('name')}")
        return

    if cmd == "ds":
        data = call("list_design_systems", {"projectId": pid})
        print(json.dumps(data, ensure_ascii=False, indent=2)[:4000])
        return

    if cmd == "project":
        data = call("get_project", {"name": "projects/" + pid})
        print(json.dumps(data, ensure_ascii=False, indent=2)[:4000])
        return

    raise SystemExit(f"perintah tidak dikenal: {cmd}")


if __name__ == "__main__":
    main()