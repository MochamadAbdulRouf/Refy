"""Uji negatif: gate konsistensi WAJIB bisa merah.

Gate yang tidak pernah gagal tidak memverifikasi apa pun. Skrip ini menyisipkan
kesalahan yang dikenal satu per satu, menjalankan pemeriksa, lalu memulihkan
berkas. Keluar 0 kalau semua kesalahan tertangkap.

Jalankan: python scripts/test-gates.py
"""
import io
import os
import shutil
import subprocess
import sys
import tempfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
os.chdir(ROOT)

CHECK = [sys.executable, "scripts/check-consistency.py"]


def run_check():
    r = subprocess.run(CHECK, capture_output=True, text=True)
    return r.returncode, r.stdout + r.stderr


def show_fail(out):
    """Ambil baris pertama yang menunjukkan kenapa pemeriksa gagal."""
    for ln in out.splitlines():
        s = ln.strip()
        if s.startswith("[#") or "!!" in s or s.startswith("- "):
            return s[:70]
    return ""


def with_edit(path, old, new, label, expect_fail=True):
    """Sisipkan satu kesalahan, pastikan pemeriksa gagal, lalu pulihkan."""
    bak = tempfile.mktemp(suffix=".bak")
    shutil.copy2(path, bak)
    try:
        s = io.open(path, encoding="utf-8", newline="").read()
        if old not in s:
            return None, f"LEWATI: pola tidak ditemukan di {path}: {old[:60]}"
        io.open(path, "w", encoding="utf-8", newline="").write(s.replace(old, new, 1))
        rc, out = run_check()
        ok = (rc != 0) == expect_fail
        return ok, f"{label}: exit={rc} {'TERBACA' if ok else 'TIDAK TERBACA'} {show_fail(out)}"
    finally:
        shutil.copy2(bak, path)
        os.remove(bak)


def with_untracked_ignored_doc(label):
    """
    Buat dokumen BARU (belum di-track git), lalu abaikan lewat `.gitignore`.

    Kenapa perlu cara berbeda: `git check-ignore` melaporkan "tidak diabaikan"
    untuk berkas yang SUDAH tercatat di git — aturan `.gitignore` memang tidak
    berlaku pada berkas yang sudah di-track secara sengaja. Jadi satu-satunya
    bentuk nyata dari "dokumen hilang dari commit" adalah dokumen baru yang
    langsung tertutup pola abaikan. Kasus ini karena itu butuh berkas baru.
    """
    doc = "docs/Z-dokumen-uji.md"
    bak = tempfile.mktemp(suffix=".bak")
    shutil.copy2(".gitignore", bak)
    try:
        io.open(doc, "w", encoding="utf-8").write("# dokumen uji\n")
        s = io.open(".gitignore", encoding="utf-8", newline="").read()
        io.open(".gitignore", "w", encoding="utf-8", newline="").write(
            s + "\n# pola uji\n" + doc + "\n")
        rc, out = run_check()
        ok = rc != 0
        return ok, f"{label}: exit={rc} {'TERBACA' if ok else 'TIDAK TERBACA'} {show_fail(out)}"
    finally:
        shutil.copy2(bak, ".gitignore")
        os.remove(bak)
        if os.path.exists(doc):
            os.remove(doc)


CASES = [
    # G — nomor layar. Lima bentuk yang PERNAH salah di PRD section 5, dua di
    # antaranya memakai prosa Indonesia ("Profil artis", "opsi per lagu").
    ("docs/PRD.md", "(desain layar **#3**)", "(desain layar **#4**)", "G Discover #3->#4"),
    ("docs/PRD.md", "(desain layar **#5**; varian **#6** ditunda",
     "(desain layar **#6**; varian **#6** ditunda", "G album #5->#6"),
    ("docs/PRD.md", "Profil artis | Header artis, lagu populer, diskografi (desain layar **#8**)",
     "Profil artis | Header artis, lagu populer, diskografi (desain layar **#7**)", "G artis #8->#7"),
    ("docs/PRD.md", "(desain layar **#12**)", "(desain layar **#11**)", "G opsi #12->#11"),
    ("docs/DESIGN.md", "| 12 | Track Options Bottom Sheet |", "| 13 | Track Options Bottom Sheet |",
     "G options DESIGN #12->#13"),
    ("docs/DESIGN.md", "| 14 | Create Playlist |", "| 12 | Create Playlist |", "G playlist DESIGN #14->#12"),
    ("flows/02-alur-antrean.md", "Layar acuan: **#10 Playback Queue**",
     "Layar acuan: **#9 Playback Queue**", "G queue #10->#9"),
    # H — ukuran basi
    ("docs/PRD.md", "| Bottom nav | **68px — pil mengambang, D11**",
     "| Bottom nav | 64px", "H bottom nav 64px"),
    ("flows/01-alur-putar-lagu.md", "melayang **88–92px** di atas dasar",
     "melayang 64px di atas dasar", "H mini-player 64px"),
    # F — keputusan wajib tertutup (D1-D13). Baris harus kehilangan "DIPUTUSKAN"
    # DAN tanda coret, karena keduanya menandai keputusan tertutup.
    ("docs/PRD.md",
     "| D4 | ~~Native Android (Kotlin) atau Flutter?~~ **DIPUTUSKAN 2026-11-12: Kotlin + Jetpack Compose.** | Mengikat |",
     "| D4 | Native Android (Kotlin) atau Flutter? | Kotlin + Compose |", "F D4 dibuka lagi"),
    # J — inventaris i18n
    ("docs/I18N-KEYS.md", "| `nav.create` | **Create** |", "| `nav.buat` | Buat |", "J nav.create hilang"),
    # K — gitignore
    (".gitignore", ".rea/\n", "", "K .rea tidak di-ignore"),
    # L — updateTime harus seragam di dokumen hidup
    ("index.md", "`updateTime` `2026-11-10T18:29:53Z`", "`updateTime` `2026-11-10T07:00:00Z`",
     "L updateTime beda di index.md"),
    # A — klaim usang. Target harus di berkas HIDUP dan tidak diawali kalimat
    # penyangkalan, karena baris riwayat ("Klaim lama ...") memang sengaja
    # dilewati oleh gate A. Pola `byte-identik` dipersempit 2026-11-12 supaya
    # tidak lagi menangkap klaim sah "HTML snapshot byte-identik dengan live".
    ("index.md", "## Dokumen\n", "## Dokumen\n\nKlaim uji: aplikasi ini disusun oleh 14 layar saja.\n",
     "A klaim usang (14 layar)"),
]

print("=" * 62)
print("UJI NEGATIF GATE — setiap kesalahan harus membuat pemeriksa GAGAL")
print("=" * 62)

fails = 0
skipped = 0
for path, old, new, label in CASES:
    ok, msg = with_edit(path, old, new, label)
    if ok is None:
        skipped += 1
        print(f"  SKIP  {msg}")
        fails += 1
    else:
        print(f"  {'OK   ' if ok else 'GAGAL'} {msg}")
        if not ok:
            fails += 1

# Kasus yang butuh berkas baru, bukan suntingan. Lihat with_untracked_ignored_doc.
extra = [with_untracked_ignored_doc("K dokumen baru ter-ignore")]
for ok, msg in extra:
    print(f"  {'OK   ' if ok else 'GAGAL'} {msg}")
    if not ok:
        fails += 1

rc, _ = run_check()
print("\n  keadaan akhir: check-consistency exit", rc, "(harap 0)")
if rc != 0:
    fails += 1

print("=" * 62)
if fails:
    print(f"HASIL: {fails} masalah — gate tidak bisa gagal ATAU case tidak cocok")
    sys.exit(1)
print(f"HASIL: {len(CASES) + len(extra)} gate terbukti bisa gagal, berkas bersih setelah dipulihkan")
sys.exit(0)