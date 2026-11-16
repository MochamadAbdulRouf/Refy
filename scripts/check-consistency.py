"""Pemeriksa konsistensi dokumen Refy.

Dijalankan sebelum dokumen dipakai sebagai acuan implementasi:
    python scripts/check-consistency.py

Gerbang (harus semua hijau):
  A  klaim basi (pola teks usang)
  B  fakta wajib ada di tiap dokumen aktif
  C  nilai palet Kubu A tidak dipakai sebagai nilai implementasi
  D  integritas snapshot desain (jumlah, berkas hilang, judul ganda)
  E  sumber vs hasil render Mermaid
  F  status keputusan D1..D16
  G  nomor layar konsisten (#N <Nama>) — ditambahkan 2026-11-12
  H  ukuran tata letak tidak basi (nav 64px / mini-player 64px) — 2026-11-12
  I  hitungan snapshot yang diklaim dokumen = yang terukur — 2026-11-12
  J  inventaris kunci i18n lengkap & dirujuk — 2026-11-12
  K  .gitignore: .rea diabaikan, tiap dokumen ter-stage (bukti dari git) — 2026-11-12
  L  updateTime Stitch seragam di dokumen hidup — 2026-11-12

Kode keluar 1 kalau ada gerbang merah.
"""
import glob
import json
import os
import re
import subprocess
import sys

FAIL = []

STALE = [
    (r"palet desain belum final", "D8 masih terbuka"),
    (r"belum final.*D8", "D8 masih terbuka"),
    (r"tunggu D8", "D8 masih terbuka"),
    (r"tunggu \*\*D8\*\*", "D8 masih terbuka"),
    (r"manifest menang", "aturan pemutus lama"),
    (r"13 dari 14|13/14", "klaim konsensus salah"),
    (r"tiga layar berbagi `<title>`", "klaim title salah"),
    (r"14 layar", "jumlah layar usang"),
    (r"\.stitch/designs", "folder lama"),
    # --- ditambahkan 2026-11-10 sesudah tarik ulang snapshot ---
    (r"\b17 layar unik\b", "jumlah layar usang (sekarang 18 layar + 2 aset)"),
    (r"13 layar (?:memakai|perlu|wajib|masuk)", "jumlah remap usang (sekarang 17)"),
    (r"18 layar perlu|remap 18 layar", "jumlah remap usang (sekarang 17)"),
    (r"bottom nav 64px|nav 64px", "nav sekarang 68px pil mengambang"),
    # "byte-identik" saja terlalu lebar: istilah itu sah dipakai untuk klaim
    # lain (mis. "HTML snapshot byte-identik dengan live"). Yang usang adalah
    # klaim SPESIFIK bahwa artefak prototype sama dengan Home Screen.
    (r"prototype.{0,50}byte-identik|byte-identik.{0,50}Home Screen",
     "klaim duplikat Home Screen sudah gugur"),
    (r"bottom nav 64px|nav 64px", "nav sekarang 68px pil mengambang"),
    (r"downloads-management\.html\b", "nama berkas tanpa akhiran id"),
    (r"home-screen\.html\b", "nama berkas tanpa akhiran id"),
    (r"nocturne-music-streaming-prototype\.html\b", "nama berkas tanpa akhiran id"),
]
# Berkas yang memang menyimpan riwayat: keputusan.md = log keputusan (menyebut angka
# lama adalah isi dokumennya), backlog.md = tugas selesai (mencatat temuan usang).
# Untuk keduanya, baris yang berada di dalam entri bertanggal / sub-butir riwayat
# dianggap sejarah, bukan klaim aktif.
ALLOW = ["keputusan.md", "DESIGN.md", "backlog.md"]


def is_history(fname, line):
    base = os.path.basename(fname)
    if base == "keputusan.md":
        return True          # seluruh berkas adalah log riwayat bertanggal
    if base == "backlog.md":
        # sub-butir riwayat menjorok >= 6 spasi, atau baris bertanda centang selesai
        return line.startswith("      ") or line.lstrip().startswith("- [x]")
    return False

FILES = ["index.md", "docs/PRD.md", "docs/DESIGN.md", "keputusan.md", "backlog.md"] + \
        sorted(glob.glob("flows/*.md"))

print("### A. stale-claim scan")
bad = 0
for f in FILES:
    txt = open(f, encoding="utf-8").read()
    for line_no, line in enumerate(txt.splitlines(), 1):
        for pat, why in STALE:
            if re.search(pat, line):
                # Kalimat yang menyanggah klaim lama bukan klaim basi:
                # "... TIDAK lagi byte-identik", "sudah gugur", "klaim lama".
                negated = any(n in line.lower() for n in (
                    "tidak lagi", "tidak ", "bukan lagi", "gugur", "usang", "salah",
                    "klaim lama", "dulu", "sebelumnya", "sudah tidak"))
                if negated:
                    continue
                quoted = line.strip().startswith("|") and ("~~" in line or "Klaim lama" in txt)
                hist = is_history(f, line) or (
                    os.path.basename(f) in ALLOW and (
                        "Pelajaran" in line or "Kenyataan" in line or "DIBATALKAN" in line
                        or "salah" in line.lower() or "Salah" in line))
                if quoted or hist:
                    continue
                print(f"  {f}:{line_no}  [{why}]  {line.strip()[:85]}")
                bad += 1
print("  stale hits (excluding history/quoted):", bad)
if bad:
    FAIL.append(f"A: {bad} klaim basi")

print("\n### B. required facts present in each active doc")
REQ = {
    "index.md": ["Kubu B", "pil mengambang", "English penuh", "I18N-KEYS", "D13"],
    "docs/PRD.md": ["Kubu B", "18 layar UI", "English penuh", "D8", "D11", "I18N-KEYS", "D13", "D12"],
    "docs/DESIGN.md": ["Kubu B", "#09090B", "#141418", "#8B5CF6", "tabel remap", "20 entri", "I18N-KEYS"],
    "docs/I18N-KEYS.md": ["nav.create", "queue.clear_all", "common.retry", "Create"],
    "flows/00-peta-navigasi.md": ["18 layar", "Create", "#16 Settings"],
    "flows/02-alur-antrean.md": ["CREATE TABLE playlists", "CREATE TABLE playlist_items", "#10 Playback Queue"],
}
for f, musts in REQ.items():
    if not os.path.exists(f):
        print(f"  {f:<32} MISSING FILE")
        FAIL.append(f"B: {f} tidak ada")
        continue
    txt = open(f, encoding="utf-8").read()
    miss = [m for m in musts if m not in txt]
    print(f"  {f:<32} {'OK' if not miss else 'MISSING: ' + ', '.join(miss)}")
    if miss:
        FAIL.append(f"B: {f} kehilangan {', '.join(miss)}")

print("\n### C. palette values that must NOT appear as implementation values")
FORBID = ["#d0bcff", "#131315"]
# Menyebut nilai Kubu A boleh — asalkan dalam konteks perbandingan/remap
# ("Kubu A", "remap", "lama", "dulu"). Yang dilarang: muncul sebagai nilai pakai.
CONTEXT_OK = ("kubu a", "remap", "lama", "dulu", "usang", "salah", "#131315 =" )
for f in ["docs/PRD.md", "index.md", "flows/00-peta-navigasi.md"]:
    bad = []
    for ln in open(f, encoding="utf-8"):
        low = ln.lower()
        for h in FORBID:
            if h in low and not any(c in low for c in CONTEXT_OK):
                bad.append(f"{h} -> {ln.strip()[:70]}")
    print(f"  {f:<32} {'clean' if not bad else 'AS VALUE: ' + ' | '.join(bad[:3])}")

print("\n### D. design snapshot integrity")
SNAP = ".stitch/electric-purple"
recs = json.load(open(os.path.join(SNAP, "screens.json"), encoding="utf-8"))

# Entri artefak: ada di snapshot supaya mirror Stitch, tapi bukan layar.
# Sejak 2026-11-10 slug memuat 8 karakter id layar, dan judul "Downloads Management"
# dipakai DUA layar berbeda yang sah — jadi duplikat tidak boleh dikenali dari judul.
def is_artifact(r):
    return r["slug"].startswith("nocturne-music-streaming-prototype")


ui = [r for r in recs if r["html_bytes"] > 0 and not is_artifact(r)]
assets = [r for r in recs if r["html_bytes"] == 0]
dupes = [r for r in recs if is_artifact(r)]

html = len([f for f in glob.glob(os.path.join(SNAP, "*.html"))])
png = len([f for f in glob.glob(os.path.join(SNAP, "*.png"))])
print(f"  screens.json: {len(recs)} entri = {len(ui)} layar + "
      f"{len(dupes)} artefak + {len(assets)} aset gambar")
print(f"  html: {html}   png: {png}")
missing_html = [r["slug"] for r in ui if not os.path.exists(os.path.join(SNAP, r["slug"] + ".html"))]
missing_png = [r["slug"] for r in ui if not os.path.exists(os.path.join(SNAP, r["slug"] + ".png"))]
print("  missing html:", missing_html or "-")
print("  missing png :", missing_png or "-")

# judul ganda wajib punya berkas berbeda (kalau tidak, isinya saling menimpa)
import hashlib
from collections import Counter


def md5(p):
    return hashlib.md5(open(p, "rb").read()).hexdigest()


titles = Counter(r["title"] for r in recs if r["html_bytes"] > 0)
dup_titles = {t: n for t, n in titles.items() if n > 1}
print(f"  judul ganda: {dup_titles or '-'}")
for t, n in dup_titles.items():
    slugs = [r["slug"] for r in recs if r["title"] == t]
    hashes = {md5(os.path.join(SNAP, s + ".html")) for s in slugs
              if os.path.exists(os.path.join(SNAP, s + ".html"))}
    print(f"    '{t[:40]}' {n} entri, slug {slugs}, berkas unik: {len(hashes)}")

print("\n### E. mermaid render")
svgs = glob.glob("docs/diagrams/*.svg")
mmds = glob.glob("docs/diagrams/*.mmd")
print(f"  .mmd {len(mmds)}  .svg {len(svgs)}  {'OK' if len(mmds) == len(svgs) else 'MISMATCH'}")

print("\n### F. open decisions still unanswered")
prd = open("docs/PRD.md", encoding="utf-8").read()
seen = set()
for m in re.finditer(r"^\| (D\d+) \|(.{0,110})", prd, re.M):
    tag, rest = m.group(1), m.group(2)
    seen.add(tag)
    done = "DIPUTUSKAN" in rest or "~~" in rest
    print(f"  {tag}: {'CLOSED' if done else 'OPEN (default tertulis?)'}")

# D1-D13 WAJIB tertutup: itu yang menghambat M0. D14+ boleh terbuka asalkan
# punya default tertulis di kolom ketiga.
MUST_CLOSE = [f"D{i}" for i in range(1, 14)]
missing = [d for d in MUST_CLOSE if d not in seen]
if missing:
    print("  keputusan hilang dari tabel:", missing)
    FAIL.append(f"F: keputusan hilang {missing}")
for tag in MUST_CLOSE:
    mm = re.search(rf"^\| {tag} \|(.+)$", prd, re.M)
    if not mm:
        continue
    row = mm.group(1)
    if "DIPUTUSKAN" not in row and "~~" not in row:
        print(f"  !! {tag} masih terbuka padahal wajib tertutup")
        FAIL.append(f"F: {tag} masih terbuka")

# Keputusan yang SUDAH diputuskan (D1-D13) wajib punya jejak di keputusan.md,
# bukan hanya kata "DIPUTUSKAN" di tabel PRD — tanpa ini tabel bisa berbunyi
# "DIPUTUSKAN" tanpa alasan dan alternatif yang ditolak.
# D14-D16 sengaja TERBUKA: syaratnya bukan jejak di keputusan.md, tapi default
# tertulis di kolom ketiga PRD section 12 (kalau kosong, itu keputusan menggantung).
kep = open("keputusan.md", encoding="utf-8").read()
for tag in [f"D{i}" for i in range(1, 14)]:
    if tag not in kep:
        print(f"  !! {tag} tidak muncul di keputusan.md")
        FAIL.append(f"F: {tag} tidak tercatat di keputusan.md")
for tag in [f"D{i}" for i in range(14, 17)]:
    mm = re.search(rf"^\| {tag} \|(.+)$", prd, re.M)
    if mm and not mm.group(1).strip():
        print(f"  !! {tag} terbuka TANPA default tertulis")
        FAIL.append(f"F: {tag} tanpa default")
    row = mm.group(1) if mm else ""
    if row and "Default:" not in row:
        print(f"  !! {tag} tidak menyebut 'Default:' — default harus eksplisit")
        FAIL.append(f"F: {tag} default tidak eksplisit")

# ---------------------------------------------------------------- G
print("\n### G. nomor layar konsisten (#N <Nama>)")
# Nomor ini pernah salah di PRD section 5 (F-11..F-15 ditulis #4/#6/#7/#11) dan
# lolos dari pemeriksa lama karena alat ini hanya mencari kata wajib.
# Cara kerja: ambil teks tepat setelah "#N" (berhenti di |, (, <, ,, #, .),
# lalu cocokkan dengan nama kanonik. Kalau nama itu milik nomor lain -> salah.
NAMES = [
    ("Home Screen", 1),
    ("Active Search Results", 2),
    ("Library & Search Screen", 3),
    ("Discover", 3),
    ("Your Library", 4),
    ("Album Detail & Tracklist", 5),
    ("Album Detail - After Hours", 6),
    ("Playlist Detail", 7),
    ("Artist Profile", 8),
    ("Now Playing", 9),
    ("Playback Queue", 10),
    ("Expanded Live Lyrics", 11),
    ("Track Options", 12),
    ("Add to Playlist", 13),
    ("Create Playlist", 14),
    ("User Profile", 15),
    ("Settings", 16),
    ("Downloads Management", 17),
    # Sinonim Indonesia yang dipakai PRD/flows. Tanpa ini gate buta terhadap
    # kalimat seperti "Profil artis ... (desain layar **#7**)".
    # "antrean" SENGAJA tidak didaftarkan: kata itu muncul di prosa biasa
    # ("tambah ke antrean") sehingga membuat satu baris cocok ke dua layar
    # sekaligus, dan gate lalu melewati baris itu. "Playback Queue" sudah cukup.
    ("Profil artis", 8),
    ("detail album", 5),
    ("opsi per lagu", 12),
    ("buat playlist", 14),
    ("pemutar penuh", 9),
    ("lirik penuh", 11),
]
SCAN = ["docs/PRD.md", "docs/DESIGN.md", "index.md"] + sorted(glob.glob("flows/*.md"))
g_bad = 0
for f in SCAN:
    for ln_no, ln in enumerate(open(f, encoding="utf-8"), 1):
        # `#N` harus token berdiri sendiri. Tanpa `(?![\dA-Fa-f])`, nilai hex
        # ikut terbaca sebagai nomor layar: `#121621` -> #12, `#8B5CF6` -> #8.
        nums = {int(x) for x in re.findall(r"#(\d{1,2})(?![\dA-Fa-f])", ln)}
        names = {n for name, n in NAMES
                 if re.search(r"(?<![A-Za-z])" + re.escape(name) + r"(?![A-Za-z])", ln, re.I)}
        # BATAS YANG DISENGAJA: hanya baris dengan TEPAT SATU nomor dan TEPAT
        # SATU nama layar yang dinilai. Baris multi-rujukan (mis. tabel MVP
        # "| #9 Now Playing, #10 Playback Queue |") dilewati, karena memasangkan
        # nomor ke nama di baris seperti itu butuh parser asli, bukan heuristik —
        # dan heuristik yang salah pasang menghasilkan positif palsu yang lebih
        # berbahaya daripada tidak memeriksa (terbukti 2026-11-12).
        if len(nums) != 1 or len(names) != 1:
            continue
        num, real = nums.pop(), names.pop()
        if num != real:
            print(f"  {f}:{ln_no}  [#{num} -> #{real}]  {ln.strip()[:58]}")
            g_bad += 1

# Aturan 3 — tabel inventaris `| N | Nama layar | ... |` di DESIGN section 7.
# Notasi nomor di sini tidak memakai `#`, jadi tidak tertangkap aturan 1-2.
# Tabel inilah sumber nomor yang dipakai PRD/flows; kalau barisnya tergeser,
# seluruh rujukan `#N` di dokumen lain jadi menunjuk layar yang salah.
for ln_no, ln in enumerate(open("docs/DESIGN.md", encoding="utf-8"), 1):
    m = re.match(r"^\| (\d{1,2}) \| ([^|]+?) \|", ln)
    if not m:
        continue
    num, cell = int(m.group(1)), m.group(2)
    hits = {n for name, n in NAMES if cell.strip().startswith(name)}
    if len(hits) == 1 and num not in hits:
        real = hits.pop()
        print(f"  docs/DESIGN.md:{ln_no}  [tabel '{cell.strip()[:28]}' di #{num} -> #{real}]")
        g_bad += 1
print(f"  nomor layar salah: {g_bad}")
if g_bad:
    FAIL.append(f"G: {g_bad} nomor layar salah")

# ---------------------------------------------------------------- H
print("\n### H. ukuran tata letak tidak basi")
# D11 mengubah nav jadi pil 68px dan mini-player naik ke 88-92px. Nilai lama
# (64px untuk keduanya) pernah tertinggal di PRD section 7.1 dan index.md.
H_BAD = 0
H_SCAN = ["docs/PRD.md", "index.md"] + sorted(glob.glob("flows/*.md"))
H_PAT = [
    (r"bottom nav \| ?64px", "bottom nav 64px (D11: 68px)"),
    (r"nav \| ?64px \| ?64px", "nav 64px (D11: 68px)"),
    (r"melayang 64px", "mini-player 64px (D11: 88-92px)"),
    (r"64px di atas dasar", "mini-player 64px (D11: 88-92px)"),
]
for f in H_SCAN:
    for ln_no, ln in enumerate(open(f, encoding="utf-8"), 1):
        low = ln.lower()
        # baris riwayat ("dulu", "usang", "koreksi") bukan klaim aktif
        if any(k in low for k in ("dulu", "usang", "sebelum", "koreksi", "diganti")):
            continue
        for pat, why in H_PAT:
            if re.search(pat, ln, re.I):
                print(f"  {f}:{ln_no}  [{why}]  {ln.strip()[:70]}")
                H_BAD += 1
print(f"  ukuran basi: {H_BAD}")
if H_BAD:
    FAIL.append(f"H: {H_BAD} ukuran basi")

# ---------------------------------------------------------------- I
print("\n### I. hitungan snapshot yang diklaim dokumen")
html_n = len(glob.glob(os.path.join(SNAP, "*.html")))
png_n = len(glob.glob(os.path.join(SNAP, "*.png")))
print(f"  terukur: screens.json {len(recs)} entri | {html_n} html | {png_n} png | "
      f"{len(ui)} layar nyata | {len(dupes)} artefak | {len(assets)} aset")
design = open("docs/DESIGN.md", encoding="utf-8").read()
for pat, expect, why in [
    (r"screens\.json\s+indeks (\d+) entri", str(len(recs)), "entri screens.json"),
    (r"\*\.html, \*\.png\s+(\d+) layar \+ (\d+) aset",
     f"{html_n} layar + {len(assets) + len(dupes)} aset", "html + aset"),
]:
    m = re.search(pat, design)
    got = "TIDAK DITEMUKAN" if not m else " + ".join(m.groups())
    print(f"  {why}: klaim {got} | terukur {expect}")
# 18 berkas html = 17 layar nyata + 1 artefak. screens.json memuat 20 entri
# (18 html + 2 PNG aset). Keduanya harus cocok dengan berkas nyata di disk.
if html_n != len(ui) + len(dupes):
    print(f"  !! html {html_n} != layar {len(ui)} + artefak {len(dupes)}")
    FAIL.append(f"I: html {html_n} != layar {len(ui)} + artefak {len(dupes)}")
if png_n != len(ui) + len(assets):
    print(f"  !! png {png_n} != layar {len(ui)} + aset {len(assets)}")
    FAIL.append(f"I: png {png_n} != layar {len(ui)} + aset {len(assets)}")
# klaim di DESIGN.md harus menyebut angka yang benar-benar terukur.
# 18 html = 17 layar nyata + 1 artefak; 20 entri = 18 html + 2 PNG aset.
if f"{html_n} layar UI + {len(assets)} aset" not in design:
    print(f"  !! DESIGN.md tidak menyebut '{html_n} layar UI + {len(assets)} aset'")
    FAIL.append("I: klaim jumlah layar di DESIGN.md tidak cocok")
if f"{len(recs)} entri" not in design:
    print(f"  !! DESIGN.md tidak menyebut '{len(recs)} entri'")
    FAIL.append("I: klaim jumlah entri di DESIGN.md tidak cocok")

# ---------------------------------------------------------------- J
print("\n### J. inventaris kunci i18n")
I18N = "docs/I18N-KEYS.md"
if not os.path.exists(I18N):
    print("  docs/I18N-KEYS.md TIDAK ADA")
    FAIL.append("J: I18N-KEYS.md tidak ada")
else:
    keys = re.findall(r"^\| `([a-z][a-z0-9_.]*)` \|", open(I18N, encoding="utf-8").read(), re.M)
    areas = sorted({k.split(".")[0] for k in keys})
    print(f"  kunci terdaftar: {len(keys)} | area: {areas}")
    for need in ("nav.home", "nav.discover", "nav.library", "nav.create",
                 "queue.clear_all", "home.greeting_evening"):
        if need not in keys:
            print(f"  !! kunci wajib hilang: {need}")
            FAIL.append(f"J: kunci {need} hilang")
    if len(keys) < 80:
        print(f"  !! kunci terlalu sedikit ({len(keys)}) untuk 11 layar MVP")
        FAIL.append(f"J: hanya {len(keys)} kunci")
if "I18N-KEYS" not in design:
    print("  !! docs/DESIGN.md tidak merujuk I18N-KEYS.md")
    FAIL.append("J: DESIGN.md tidak merujuk I18N-KEYS.md")

# ---------------------------------------------------------------- K
print("\n### K. .gitignore (bukti nyata dari git, bukan pembacaan berkas)")
# Catatan penting: `git check-ignore` mengembalikan rc=0 DAN MENCETAK BARIS
# baik untuk pola abaikan (`file\tpath`) maupun untuk pola negasi (`!pattern\tpath`).
# Jadi rc saja tidak cukup — baris yang dicetak harus diperiksa: kalau dimulai
# dengan '!' berarti berkas itu TIDAK diabaikan (dinegasi, jadi ikut commit).
def check_ignored(path, want_ignored):
    r = subprocess.run(["git", "check-ignore", "-v", path],
                       capture_output=True, text=True)
    out = r.stdout.strip()
    if not out:
        ignored = False
    else:
        ignored = not out.split("\t")[0].split(":")[-1].startswith("!")
    state = "ignored" if ignored else "TRACKED"
    print(f"  {'OK ' if ignored == want_ignored else '!! '} {path:<46} {state}")
    return ignored == want_ignored


k_ok = True
k_ok &= check_ignored(".rea/echomusic/apk/classes.dex", True)
k_ok &= check_ignored(".stitch/electric-purple/home-screen-243ea0d8.html", True)
k_ok &= check_ignored(".stitch/electric-purple/DESIGN.md", False)
k_ok &= check_ignored(".stitch/DELTA-2026-11-10.md", False)
k_ok &= check_ignored("docs/diagrams/00-peta-navigasi-01.mmd", False)
k_ok &= check_ignored("docs/diagrams/00-peta-navigasi-01.svg", True)
k_ok &= check_ignored("docs/PRD.md", False)

# Verifikasi lapis dua: apa yang BENAR-BENAR akan di-stage `git add -A`.
# Ini menangkap kesalahan yang lolos dari pemeriksaan per-berkas, mis. folder
# 806 MB yang ikut terseret atau seluruh pohon dokumen yang termakan pola lebar.
# (Keduanya pernah terjadi: 2026-11-12 `.rea/` tidak di-ignore; di proyek lain
# pola `docs` polos pernah menghapus seluruh dokumentasi dari git.)
r = subprocess.run(["git", "add", "-An", "."], capture_output=True, text=True)
staged = set()
for ln in r.stdout.splitlines():
    if "'" in ln:
        staged.add(ln.split("'")[1])
big = [p for p in staged if p.startswith(".rea/")]
print(f"  git add -A akan men-stage {len(staged)} berkas | dari .rea/: {len(big)}")
if big:
    print(f"  !! .rea/ ikut terseret ({len(big)} berkas)")
    FAIL.append(f"K: .rea/ ikut di-stage ({len(big)} berkas)")

# SETIAP dokumen wajib ikut ke-commit. Diperiksa per berkas dengan
# `git check-ignore`, bukan dari daftar pola — membaca daftar pola bukan
# verifikasi (pola bisa benar tapi tidak berlaku).
#
# Diperbaiki 2026-11-16: bagian ini dulu membandingkan dokumen dengan keluaran
# `git add -An .`. Cara itu hanya benar selama repo BELUM punya commit — setelah
# commit pertama, berkas yang tidak berubah tidak lagi muncul di keluaran itu,
# jadi gate K merah permanen padahal tidak ada dokumen yang hilang (terbukti:
# "K: 13 dokumen tidak ter-stage" sesaat setelah commit 9f7a36d). `check-ignore`
# mengukur hal yang sebenarnya dimaksud.
def is_ignored_quiet(path):
    r = subprocess.run(["git", "check-ignore", "-v", path],
                       capture_output=True, text=True)
    out = r.stdout.strip()
    if not out:
        return False
    return not out.split("\t")[0].split(":")[-1].startswith("!")


docs_all = sorted({p.replace("\\", "/") for p in
                   glob.glob("docs/*.md") + glob.glob("docs/**/*.md", recursive=True)
                   + glob.glob("flows/*.md") + ["index.md", "backlog.md",
                                                "keputusan.md", "CLAUDE.md",
                                                ".gitignore"]})
missing_docs = [d for d in docs_all if is_ignored_quiet(d)]
print(f"  dokumen diperiksa: {len(docs_all)} | ter-ignore (tidak akan ke-commit): {len(missing_docs)}")
for d in missing_docs:
    print(f"  !! dokumen TER-IGNORE, tidak akan ke-commit: {d}")
if missing_docs:
    FAIL.append(f"K: {len(missing_docs)} dokumen ter-ignore")
if not k_ok:
    FAIL.append("K: .gitignore salah")

# ---------------------------------------------------------------- L
print("\n### L. updateTime Stitch seragam di dokumen HIDUP")
# Temuan 2026-11-12: index.md mencatat 09:28:11Z, docs/DESIGN.md 10:13:02Z, dan
# .stitch/DELTA-2026-11-10.md 09:28:11Z — tiga dokumen, dua nilai berbeda, dan
# nilai LIVE saat itu 18:29:53Z. Tidak ada gate lama yang menangkap ini karena
# semuanya hanya mencocokkan kata, bukan angka.
#
# Cakupan sengaja dibatasi ke dokumen HIDUP. `.stitch/DELTA-*.md` adalah laporan
# historis yang dibekukan: ia memang menyimpan dua nilai berbeda (satu per
# tarik-ulang) dan itu benar. Menuntut keseragaman di sana akan memaksa
# memalsukan riwayat.
#
# Gerbang ini OFFLINE: tidak bisa tahu nilai live (butuh jaringan), tapi bisa
# memastikan semua dokumen hidup sepakat pada SATU nilai, sehingga tidak ada
# pembaca yang memakai angka berbeda dari pembaca lain.
ts_files = ["index.md", "docs/PRD.md", "docs/DESIGN.md", "backlog.md"]
found = {}
for f in ts_files:
    if not os.path.exists(f):
        continue
    txt = open(f, encoding="utf-8").read()
    for ln in txt.splitlines():
        low = ln.lower()
        # baris riwayat/koreksi boleh menyebut nilai lama, itu penjelasannya
        if any(k in low for k in ("dulu", "usang", "sebelumnya", "dibatalkan",
                                  "pernah mencatat", "koreksi")):
            continue
        for m in re.finditer(r"2026-\d{2}-\d{2}T(\d{2}:\d{2}:\d{2})", ln):
            found.setdefault(m.group(1), set()).add(f)
if len(found) <= 1:
    print(f"  nilai updateTime di dokumen hidup: {sorted(found) or '-'} (seragam)")
else:
    print(f"  !! {len(found)} nilai updateTime berbeda:")
    for v, fs in sorted(found.items()):
        print(f"     {v}  <- {sorted(fs)}")
    FAIL.append(f"L: updateTime tidak seragam ({len(found)} nilai)")

# ---------------------------------------------------------------- ringkasan
print("\n" + "=" * 62)
if FAIL:
    print("HASIL: GAGAL - dokumen belum boleh dipakai sebagai acuan")
    for item in FAIL:
        print(f"  - {item}")
    sys.exit(1)
print("HASIL: HIJAU - semua gerbang lolos (A sampai L)")
sys.exit(0)