"""Summarise each saved Stitch HTML screen: visible text + structural markers."""
import glob
import html as htmlmod
import os
import re
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from stitch_client import SNAP as OUT

TAG = re.compile(r"<[^>]+>")
SCRIPT = re.compile(r"<(script|style)\b.*?</\1>", re.S | re.I)


def visible(path):
    s = open(path, encoding="utf-8", errors="replace").read()
    s = SCRIPT.sub(" ", s)
    s = re.sub(r"<!--.*?-->", " ", s, flags=re.S)
    s = re.sub(r"<br\s*/?>", " | ", s, flags=re.I)
    s = re.sub(r"</(div|p|h1|h2|h3|h4|li|span|button|a|section)>", "\n", s, flags=re.I)
    s = TAG.sub(" ", s)
    s = htmlmod.unescape(s)
    lines = []
    for ln in s.splitlines():
        ln = re.sub(r"[ \t]+", " ", ln).strip(" |")
        if ln and len(ln) > 1:
            lines.append(ln)
    return lines


def convo(lines):
    """collapse consecutive duplicates"""
    out = []
    for ln in lines:
        if not out or out[-1] != ln:
            out.append(ln)
    return out


for path in sorted(glob.glob(os.path.join(OUT, "*.html"))):
    name = os.path.basename(path)
    lines = convo(visible(path))
    # keep lines that look like content, drop very long prose
    keep = [l for l in lines if len(l) <= 90]
    print("=" * 70)
    print("##", name, f"({len(keep)} text lines)")
    print("=" * 70)
    for l in keep[:120]:
        print(" ", l)
    print()