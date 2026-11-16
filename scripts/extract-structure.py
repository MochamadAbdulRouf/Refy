"""Extract nav tabs, section headers, and mini-player presence per screen."""
import glob
import html as H
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from stitch_client import SNAP as D
SCRIPT = re.compile(r"<(script|style)\b.*?</\1>", re.S | re.I)


def text_of(frag):
    frag = re.sub(r"<[^>]+>", " ", frag)
    return re.sub(r"\s+", " ", H.unescape(frag)).strip()


for p in sorted(glob.glob(os.path.join(D, "*.html"))):
    s = SCRIPT.sub(" ", open(p, encoding="utf-8").read())
    name = os.path.basename(p)

    # nav tabs: anchors inside <nav>. Label diambil dari <span> terakhir di anchor
    # (kelas label berubah antar-generasi layar: text-label-xs lalu text-[10px]/
    # text-[11px], jadi jangan bergantung pada nama kelas).
    nav = re.search(r"<nav\b.*?</nav>", s, re.S | re.I)
    tabs = []
    if nav:
        for a in re.finditer(r"<a\b[^>]*>(.*?)</a>", nav.group(0), re.S | re.I):
            frag = a.group(1)
            span = re.findall(r"<span\b[^>]*>([^<]+)</span>", frag, re.S | re.I)
            # ambil span terakhir yang bukan nama ikon material (home, explore, ...)
            named = [x.strip() for x in span
                     if x.strip() and not re.fullmatch(r"[a-z_]+", x.strip())]
            cur = "ACTIVE" if 'aria-current="page"' in a.group(0) else ""
            if named:
                tabs.append(f"{named[-1]}{'*' if cur else ''}")
    # section headers h2/h3 with their tag
    heads = []
    for m in re.finditer(r"<h([1-4])\b[^>]*>(.*?)</h\1>", s, re.S | re.I):
        t = text_of(m.group(2))
        if t and len(t) < 70:
            heads.append(f"h{m.group(1)}:{t}")
    # mini player
    mini = "MINI-PLAYER" if re.search(r"fixed\s+bottom-1[0-9]", s) else ""
    print("=" * 78)
    print(name, "|", mini)
    print("  tabs:", " / ".join(tabs) if tabs else "-")
    print("  heads:", " ; ".join(heads[:14]) if heads else "-")
    print()
