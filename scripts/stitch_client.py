"""Shared Stitch MCP client.

Kunci API TIDAK boleh ditulis di dalam kode. Ambil dari environment:

    export STITCH_API_KEY=...        # git-bash
    setx STITCH_API_KEY "..."        # Windows, permanen

Atau taruh di file `.env` (sudah masuk .gitignore) dengan format:
    STITCH_API_KEY=...

Cara pakai:
    from stitch_client import call
    data = call("list_screens", {"projectId": "..."})
"""

import json
import os
import subprocess

URL = "https://stitch.googleapis.com/mcp"

# Akar repo = satu tingkat di atas scripts/. Semua path snapshot dihitung dari sini
# supaya skrip bisa dipanggil dari direktori mana pun (dulu rusak kalau dijalankan
# dari dalam scripts/: snapshot nyasar ke scripts/.stitch).
REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SNAP = os.path.join(REPO, ".stitch", "electric-purple")


def snap(*parts):
    """Path absolut di dalam folder snapshot."""
    return os.path.join(SNAP, *parts)


def _load_dotenv():
    """Baca .env sederhana kalau ada. Tidak menimpa env yang sudah di-set."""
    here = os.path.dirname(os.path.abspath(__file__))
    for path in (os.path.join(here, "..", ".env"), os.path.join(here, ".env")):
        if not os.path.exists(path):
            continue
        with open(path, encoding="utf-8") as fh:
            for line in fh:
                line = line.strip()
                if not line or line.startswith("#") or "=" not in line:
                    continue
                k, v = line.split("=", 1)
                os.environ.setdefault(k.strip(), v.strip().strip('"').strip("'"))


_load_dotenv()

API_KEY = os.environ.get("STITCH_API_KEY", "").strip()


def call(tool, args, timeout=180, tries=3):
    """Panggil satu tool Stitch MCP. Retry karena server kadang balas kosong."""
    if not API_KEY:
        raise SystemExit(
            "STITCH_API_KEY belum di-set.\n"
            "  git-bash : export STITCH_API_KEY=...\n"
            '  Windows  : setx STITCH_API_KEY "..."\n'
            "  atau taruh di .env (lihat .env.example)"
        )

    last = ""
    for _ in range(tries):
        payload = json.dumps({
            "jsonrpc": "2.0",
            "id": 1,
            "method": "tools/call",
            "params": {"name": tool, "arguments": args},
        })
        r = subprocess.run(
            ["curl", "-s", "--max-time", str(timeout), "-X", "POST", URL,
             "-H", "Content-Type: application/json",
             "-H", "Accept: application/json, text/event-stream",
             "-H", "X-Goog-Api-Key: " + API_KEY, "--data-binary", "@-"],
            input=payload, capture_output=True, text=True, encoding="utf-8")
        out = r.stdout
        if out.lstrip().startswith(("event:", "data:")):
            out = "".join(l[5:].strip() for l in out.splitlines() if l.startswith("data:"))
        if not out.strip():
            last = "(empty response)"
            continue
        try:
            d = json.loads(out)
        except json.JSONDecodeError:
            last = f"(non-JSON: {out[:120]!r})"
            continue
        if "error" in d:
            last = json.dumps(d["error"])[:300]
            continue
        res = d.get("result", {})
        # Error tingkat-tool MCP datang di dalam `result.isError`, BUKAN di
        # `d["error"]` tingkat-atas. Tanpa cabang ini, kegagalan sesaat server
        # kembali sebagai dict biasa, dan pemanggil membaca `.get("screens", [])`
        # sebagai "proyek kosong" — bukan sebagai error.
        # Terbukti 2026-11-12: `verify-docs-vs-stitch.py` melaporkan
        # "live: 0 entri" padahal Stitch memuat 20 entri; panggilan berikutnya
        # normal. Perlakukan sebagai kegagalan supaya loop retry bekerja.
        if isinstance(res, dict) and res.get("isError"):
            last = f"(tool isError: {json.dumps(res)[:200]})"
            continue
        sc = res.get("structuredContent")
        if sc is None and isinstance(res.get("content"), list):
            for it in res["content"]:
                if it.get("type") == "text":
                    try:
                        sc = json.loads(it["text"])
                    except json.JSONDecodeError:
                        sc = None
                    break
            # Teks balasan yang BUKAN JSON = pesan galat/human-readable,
            # bukan payload. Jangan diteruskan sebagai data.
            if sc is None:
                last = f"(non-JSON content: {json.dumps(res.get('content'))[:200]})"
                continue
        return sc if sc is not None else res
    raise RuntimeError(f"{tool} gagal setelah {tries} percobaan: {last}")


def slug(title, screen=None):
    """Slug nama layar -> nama berkas.

    Kalau `screen` diberikan, akhiran 8 karakter pertama id layar ditambahkan.
    Perlu karena Stitch mengizinkan dua layar berjudul sama: pada 2026-11-10 ada
    dua "Downloads Management" sehingga saling menimpa berkas satu sama lain.
    """
    s = "".join(c.lower() if c.isalnum() else "-" for c in title)
    while "--" in s:
        s = s.replace("--", "-")
    s = s.strip("-")[:60]
    if screen:
        s += "-" + screen.rsplit("/", 1)[-1][:8]
    return s
