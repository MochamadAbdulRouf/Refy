package io.github.mochamadabdulrouf.refy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyShapesTokens

/**
 * Satu-satunya jalan membuat permukaan kaca (header, pil nav, mini-player, overlay).
 *
 * **Keadaan sekarang: JALUR MUNDUR, bukan blur.** Latar belakang di balik kaca
 * TIDAK diblur. Compose tidak menyediakan backdrop blur bawaan — `Modifier.blur`
 * memblur isi Composable itu sendiri, bukan yang ada di belakangnya. Untuk blur
 * nyata perlu pustaka pihak ketiga (mis. `haze`) atau RenderNode kustom; itu
 * **belum** diputuskan. Lihat `PLAN.md` §6 dan `keputusan.md` 2026-11-17.
 *
 * Struktur mengikuti `docs/DESIGN.md` §9 langkah 1, dalam urutan yang benar:
 * 1. `clip` bentuk — supaya tidak ada sudut bocor;
 * 2. `background(canvas)` — **penyangga opaque**; tanpa ini sampling tile memori
 *    GPU bisa menghasilkan pita warna (bug Mali);
 * 3. `background(warna kaca)` — lapisan transparan di atas penyangga;
 * 4. `border` 1dp.
 *
 * Konsekuensi yang harus diingat: karena tidak ada blur, warna kaca dengan alfa
 * <100 % memperlihatkan isi di belakangnya **tajam**. Kalau itu mengganggu
 * keterbacaan, pakai [RefyColors.GlassFallback].
 */
@Composable
fun GlassSurface(
    color: Color,
    border: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RefyShapesTokens.medium,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(RefyColors.Canvas)
            .background(color)
            .border(width = 1.dp, color = border, shape = shape),
        content = content,
    )
}