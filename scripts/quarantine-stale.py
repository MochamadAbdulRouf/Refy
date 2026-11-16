"""Move removed-from-Stitch artifacts out of the active snapshot dir."""
import os
import shutil
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from stitch_client import SNAP

OLD = os.path.join(SNAP, "_removed")
os.makedirs(OLD, exist_ok=True)

moved = []
for f in ["home-screen-high-density-dark-mode.html",
          "home-screen-high-density-dark-mode.png"]:
    src = os.path.join(SNAP, f)
    if os.path.exists(src):
        shutil.move(src, os.path.join(OLD, f))
        moved.append(f)

print("moved to _removed/:", moved or "nothing")
print("\nactive html:", len([f for f in os.listdir(SNAP) if f.endswith(".html")]))
print("active png :", len([f for f in os.listdir(SNAP) if f.endswith(".png")]))
print("_removed   :", os.listdir(OLD))

# write a README in _removed
open(os.path.join(OLD, "README.md"), "w", encoding="utf-8").write(
    "# Artefak usang\n\n"
    "Berkas di folder ini **sudah dihapus dari Stitch** oleh desainer\n"
    "(project `9190295645942987043`), ditemukan saat resync 2026-11-09 12:34 UTC.\n\n"
    "Disimpan hanya sebagai jejak historis. **Jangan dipakai sebagai acuan implementasi.**\n\n"
    "Layar: `home-screen-high-density-dark-mode` (30.147 byte)\n"
    "Pengganti: `home-screen.html` (layar Home yang dipilih pengguna).\n")
print("README written")