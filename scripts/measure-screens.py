"""Extract precise component metrics from the saved Electric Purple screens."""
import glob
import os
import re
import sys
from collections import Counter

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from stitch_client import SNAP as D

PX = {"0": 0, "0.5": 2, "1": 4, "1.5": 6, "2": 8, "2.5": 10, "3": 12, "3.5": 14,
      "4": 16, "5": 20, "6": 24, "7": 28, "8": 32, "9": 36, "10": 40, "11": 44,
      "12": 48, "14": 56, "16": 64, "18": 72, "20": 80, "24": 96, "28": 112,
      "32": 128, "36": 144, "40": 160, "44": 176, "48": 192, "56": 224,
      "64": 256, "72": 288, "80": 320, "96": 384}


def h(class_str):
    m = re.search(r"\bh-(\d+(?:\.\d+)?)\b", class_str)
    return m.group(1) if m else None


def report(path):
    s = open(path, encoding="utf-8").read()
    classes = re.findall(r'class="([^"]*)"', s)
    hc = Counter()
    for c in classes:
        for m in re.finditer(r"\bh-(\d+(?:\.\d+)?)\b", c):
            hc[m.group(1)] += 1
    r_ = Counter()
    for m in re.finditer(r"\brounded-(none|sm|md|lg|xl|2xl|3xl|full)\b", s):
        r_[m.group(1)] += 1
    blur = Counter(m.group(0) for m in re.finditer(r"backdrop-blur-[a-z0-9]+", s))
    return hc, r_, blur


print("### heights (px) per file — top 8\n")
for p in sorted(glob.glob(os.path.join(D, "*.html"))):
    hc, r_, blur = report(p)
    top = ", ".join(f"h-{k}={PX.get(k,'?')}px×{v}" for k, v in hc.most_common(8))
    print(os.path.basename(p))
    print("   heights:", top)
    print("   radius :", dict(r_.most_common(6)))
    print("   blur   :", dict(blur))
    print()

print("\n### artwork / thumbnail sizes (w-N h-N pairs, square)\n")
pat = re.compile(r'class="[^"]*\bw-(\d+(?:\.\d+)?)\s+h-(\d+(?:\.\d+)?)\b[^"]*"')
for p in sorted(glob.glob(os.path.join(D, "*.html"))):
    s = open(p, encoding="utf-8").read()
    pairs = Counter(m.group(0) for m in pat.finditer(s))
    sq = Counter()
    for m in pat.finditer(s):
        w, hh = m.group(1), m.group(2)
        if w == hh and w in PX:
            sq[f"{PX[w]}px"] += 1
    if sq:
        print(f"{os.path.basename(p):<42}", dict(sq.most_common(6)))

print("\n### fixed/sticky persistent chrome\n")
for p in sorted(glob.glob(os.path.join(D, "*.html"))):
    s = open(p, encoding="utf-8").read()
    found = []
    for m in re.finditer(r'class="([^"]*)"', s):
        c = m.group(1)
        if ("fixed" in c or "sticky" in c) and ("bottom" in c or "top" in c):
            tag = "nav" if "nav" in c[:0] else ""
            found.append(c[:110])
    if found:
        print(f"--- {os.path.basename(p)}")
        for f in found[:6]:
            print("   ", f)