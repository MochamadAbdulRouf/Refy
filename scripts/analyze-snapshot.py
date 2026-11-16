"""Analisis snapshot Stitch: palet per layar + gaya bottom nav.

READ-ONLY. Membaca .stitch/electric-purple/*.html saja.

Dipakai untuk menjawab dua pertanyaan berulang:
  1. Layar mana yang masih memakai palet Kubu A (perlu remap ke Kubu B)?
  2. Layar mana yang bottom nav-nya masih full-width, bukan pill mengambang?
  3. Apakah kunci tab masih "Buat" alih-alih "Create"?

Pakai: python scripts/analyze-snapshot.py [--md]
"""

import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from stitch_client import SNAP  # noqa: E402

# Penanda palet. Kubu B = prosa brand Nocturne Audio; Kubu A = token manifest.
B_ACCENT = ["#8b5cf6", "#a855f7", "#6366f1"]      # violet terang + gradien
B_SURFACE = ["#09090b", "#141418", "#121621"]     # canvas OLED murni
A_ACCENT = ["#d0bcff", "#a078ff", "#ddb7ff", "#c0c1ff"]  # lavender pucat
A_SURFACE = ["#131315", "#0e0e10", "#201f22", "#1c1b1d", "#353437"]

TAB_RE = re.compile(r'>(Home|Discover|Library|Create|Buat|Search|Profile)<')


def tailwind_config_palette(raw):
    """Ambil blok tailwind.config dan kembalikan (primary, background).

    Ini ukuran paling tegas untuk "layar ini lahir dari kubu mana": nilai seed
    yang dipakai generator saat layar dibuat. Override hex inline di markup
    (mis. `bg-[#8B5CF6]`) tidak terlihat di sini — itulah sebabnya audit di
    bawah menampilkan dua kolom, bukan satu.
    """
    m = re.search(r"tailwind\.config\s*=\s*\{", raw)
    if not m:
        return None, None
    i, depth = m.end(), 1
    while i < len(raw) and depth:
        if raw[i] == "{":
            depth += 1
        elif raw[i] == "}":
            depth -= 1
        i += 1
    cfg = raw[m.end() - 1:i]
    g = lambda k: (re.search(rf'"{k}"\s*:\s*"(#[0-9a-fA-F]{{6}})"', cfg) or [None, None])[1]
    return g("primary"), g("background")


def config_kubu(primary, background):
    if primary is None:
        return "?"
    if primary.lower() in ("#8b5cf6", "#a855f7") or (background or "").lower() == "#09090b":
        return "B"
    return "A"


def stats(path):
    raw = open(path, encoding="utf-8", errors="replace").read()
    low = raw.lower()
    nav = re.search(r"<nav\b.*?</nav>", raw, re.S | re.I)
    navhtml = nav.group(0) if nav else ""

    b = sum(low.count(h) for h in B_ACCENT)
    a = sum(low.count(h) for h in A_ACCENT)
    bs = sum(low.count(h) for h in B_SURFACE)
    as_ = sum(low.count(h) for h in A_SURFACE)

    primary, background = tailwind_config_palette(raw)

    floating = bool(re.search(r"rounded-full", navhtml)) and "inset-x-4" in navhtml
    fullwidth = bool(re.search(r"fixed\s+bottom-0\s+w-full", navhtml))
    tabs = TAB_RE.findall(navhtml)
    mini = bool(re.search(r"fixed\s+bottom-\[?\d", raw))

    return {
        "b_accent": b, "a_accent": a, "b_surface": bs, "a_surface": as_,
        "nav": "pill" if floating else ("full" if fullwidth else "?"),
        "tabs": tabs, "mini": mini,
        "buat": "Buat" in navhtml,
        "create": "Create" in navhtml,
        "nav_bytes": len(navhtml),
        "cfg_primary": primary, "cfg_background": background,
        "cfg_kubu": config_kubu(primary, background),
        "accent_kubu": "B" if b > a else ("A" if a > b else "="),
    }




def main():
    as_md = "--md" in sys.argv
    files = sorted(f for f in os.listdir(SNAP) if f.endswith(".html"))
    rows = []
    for f in files:
        s = stats(os.path.join(SNAP, f))
        rows.append((f[:-5], s))

    if as_md:
        print("| layar | kubucfg | aksen | nav | 'Buat' | primary cfg | bg cfg |")
        print("|---|---|---|---|---|---|---|")
        for name, s in rows:
            print(f"| {name} | {s['cfg_kubu']} | {s['accent_kubu']} | {s['nav']} | "
                  f"{'ya' if s['buat'] else '-'} | `{s['cfg_primary']}` | `{s['cfg_background']}` |")
    else:
        print(f"{'layar':<46} {'kfg':>4} {'aks':>4} {'nav':>5} {'Buat':>5} "
              f"{'cfg primary':>12} {'cfg bg':>10}")
        print("-" * 96)
        for name, s in rows:
            print(f"{name:<46} {s['cfg_kubu']:>4} {s['accent_kubu']:>4} {s['nav']:>5} "
                  f"{'ya' if s['buat'] else '-':>5} {str(s['cfg_primary']):>12} "
                  f"{str(s['cfg_background']):>10}")

    from collections import Counter
    c = Counter(s["cfg_kubu"] for _, s in rows)
    print(f"\ntotal {len(rows)} layar · tailwind.config Kubu A (perlu remap): {c['A']} · "
          f"Kubu B: {c['B']}")
    print(f"bottom nav pill: {sum(1 for _,s in rows if s['nav']=='pill')} · "
          f"full-width: {sum(1 for _,s in rows if s['nav']=='full')} · "
          f"tanpa nav: {sum(1 for _,s in rows if s['nav']=='?')}")
    print(f"tab 'Buat' masih ada: {sum(1 for _,s in rows if s['buat'])} layar · "
          f"'Create': {sum(1 for _,s in rows if s['create'])} layar")


if __name__ == "__main__":
    main()
