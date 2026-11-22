package io.github.mochamadabdulrouf.refy.ui.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.exp
import kotlin.math.pow

/**
 * Satu lapis `box-shadow` CSS: [alpha] warna dengan [blurDp] radius blur.
 * Dalam CSS, blur B menghasilkan Gaussian σ = B/2.
 */
data class GlowLayer(val alpha: Float, val blurDp: Float)

/**
 * Profil glow chip aktif — dari HTML `artist-profile-ebadb5ac`
 * (`box-shadow: rgb(168,85,247) 0 0 20px, rgba(168,85,247,0.7) 0 0 35px`),
 * dibuktikan terhadap PNG: α0,63 pada 1px dari tepi, α0,40 pada 8px.
 */
val ActiveChipGlow = listOf(GlowLayer(1f, 20f), GlowLayer(0.7f, 35f))

/**
 * Profil glow pil "Following" — HTML follow-btn
 * (`rgba(168,85,247,0.65) 0 0 16px, rgba(168,85,247,0.35) 0 0 28px`),
 * cocok dengan profil PNG (α0,33 pada 4px).
 */
val FollowPillGlow = listOf(GlowLayer(0.65f, 16f), GlowLayer(0.35f, 28f))

/**
 * Profil glow FAB 56dp — dari PNG `artist-profile-ebadb5ac` (diukur:
 * α0,52 di tepi → 0,27 pada 13px → 0,08 pada 29px; HTML menulis
 * `0 0 24px rgba(208,188,255,0.45)` tetapi render PNG memakai ungu
 * penuh — dua lapis ini cocok dengan kurva terukur).
 */
val FabGlow = listOf(GlowLayer(0.9f, 26f), GlowLayer(0.35f, 56f))

/**
 * Glow kartu artwork #9 — HTML: lapisan gradien `blur-2xl` opacity 0,75
 * (≈ blur 40) + `box-shadow rgba(168,85,247,0.7) 0 0 50px` +
 * `rgba(147,51,234,0.45) 0 0 90px`.
 *
 * Diukur pada PNG 2026-11-22 (kiri kartu, latar ≈ 25): α0,80 pada 4px →
 * 0,63 pada 16px → 0,40 pada 32px → 0,31 pada 40px. Tiga lapis di bawah
 * cocok dengan kurva itu.
 */
val ArtworkCardGlow = listOf(
    GlowLayer(0.9f, 40f),
    GlowLayer(0.85f, 55f),
    GlowLayer(0.6f, 95f),
)

/**
 * Glow tombol putar 72dp #9 — HTML menulis tiga bayangan (28/55/80),
 * tetapi PNG hanya menyala sampai ≈34px: α0,61 di tepi → 0,38 pada 10px →
 * 0,23 pada 18px → 0,14 pada 26px → 0,09 pada 34px. Satu lapis
 * `erfc` dengan blur 24 memberi kurva itu (0,50 · 0,38 · 0,23 · 0,13).
 */
val PlayButtonGlow = listOf(GlowLayer(1f, 24f))

/**
 * Glow thumb scrubber #9 — PNG: α0,53 pada 8px → 0,28 pada 20px → 0,12
 * pada 32px → ≈0 pada 50px (HTML menulis 16/32/48).
 */
val ScrubberThumbGlow = listOf(
    GlowLayer(1f, 16f),
    GlowLayer(0.9f, 30f),
    GlowLayer(0.6f, 48f),
)

/** Glow kartu lirik #9 — HTML: `rgba(168,85,247,0.22) 0 0 30px`. */
val LyricsCardGlow = listOf(GlowLayer(0.22f, 30f))

/**
 * α glow pada jarak [distanceDp] dari tepi bentuk: gabungan beberapa
 * bayangan CSS sepusat — `1 - Π(1 - αᵢ · ½·erfc(d / (σᵢ√2)))`.
 * Setiap bayangan menyumbang setengah nilai puncaknya di tepi bentuk
 * (setengah lainnya jatuh di bawah isi, tertutup latar pil).
 */
fun glowAlphaAt(distanceDp: Float, layers: List<GlowLayer>): Float {
    var inv = 1f
    for (layer in layers) {
        val sigma = layer.blurDp / 2f
        inv *= 1f - layer.alpha * 0.5f * erfc(distanceDp / (sigma * 1.4142135f))
    }
    return 1f - inv
}

/**
 * Glow pil di belakang isi node: cincin sepusat setiap [ringWidthDp]
 * dari tepi bentuk, α per cincin dari [glowAlphaAt]. `Modifier.shadow`
 * Compose jauh lebih lemah dari box-shadow CSS (terukur α0,16 vs 0,63),
 * jadi digambar manual (2026-11-20).
 *
 * Setiap cincin digambar **tumpang tindih 3×** lebar langkah (2026-11-22):
 * cincin yang hanya bersentuhan menyisakan jahitan antialias yang terlihat
 * sebagai garis-garis halus pada glow besar (tombol putar, kartu artwork).
 * Dengan tumpang tindih, tiap piksel tertutup ~3 cincin, sehingga α per
 * cincin diturunkan `1 − (1 − α)^(1/3)` agar totalnya tetap [glowAlphaAt].
 *
 * [clipVerticalDp] memotong glow pada jarak itu dari tepi atas/bawah —
 * meniru clip `overflow` container desain (`py-2` chip = 8dp). Default =
 * [maxReachDp] (tidak memotong); horizontal selalu tidak dipotong di
 * sini — clip LazyRow (contentPadding penuh) yang membatasi sampai tepi
 * layar.
 *
 * Bentuk node: default pil (sudut = setengah tinggi) — beri [cornerRadiusDp]
 * untuk bentuk lain, mis. kartu artwork `rounded-2xl` (#9).
 */
fun Modifier.pillGlow(
    color: Color,
    layers: List<GlowLayer>,
    maxReachDp: Float = 32f,
    ringWidthDp: Dp = 0.5.dp,
    clipVerticalDp: Dp = maxReachDp.dp,
    cornerRadiusDp: Dp? = null,
): Modifier = this.drawBehind {
    val step = ringWidthDp.toPx()
    // Tumpang tindih: lebar goresan = 3 langkah; α per goresan = akar
    // pangkat tiga dari α cincin supaya akumulasi 3 lapis = profil asli.
    val strokeWidth = step * 3f
    val clip = clipVerticalDp.toPx()
    val radius = cornerRadiusDp?.toPx() ?: (size.height / 2f)
    val maxReach = maxReachDp * density
    clipRect(
        left = -1e7f,
        top = -clip,
        right = size.width + 1e7f,
        bottom = size.height + clip,
    ) {
        var d = 0f
        while (d <= maxReach) {
            val alpha = glowAlphaAt(d / density, layers)
            val perStroke = 1f - (1f - alpha).pow(1f / 3f)
            if (perStroke >= 0.004f) {
                drawRoundRect(
                    color = color.copy(alpha = perStroke),
                    topLeft = Offset(-d, -d),
                    size = Size(size.width + 2f * d, size.height + 2f * d),
                    cornerRadius = CornerRadius(radius + d),
                    style = Stroke(width = strokeWidth),
                )
            }
            d += step
        }
    }
}

/**
 * erfc(x) untuk x ≥ 0 lewat aproksimasi erf Abramowitz & Stegun 7.1.26
 * (galat < 1,5·10⁻⁷) — cukup untuk profil glow.
 */
private fun erf(x: Float): Float {
    val t = 1f / (1f + 0.3275911f * x)
    val poly = (((((1.061405429f * t - 1.453152027f) * t) + 1.421413741f) * t
        - 0.284496736f) * t + 0.254829592f) * t
    return 1f - poly * exp(-x * x)
}

private fun erfc(x: Float): Float = 1f - erf(x)
