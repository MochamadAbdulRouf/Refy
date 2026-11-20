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
 * [clipVerticalDp] memotong glow pada jarak itu dari tepi atas/bawah —
 * meniru clip `overflow` container desain (`py-2` chip = 8dp). Default =
 * [maxReachDp] (tidak memotong); horizontal selalu tidak dipotong di
 * sini — clip LazyRow (contentPadding penuh) yang membatasi sampai tepi
 * layar.
 *
 * Bentuk pil: sudut = setengah tinggi node (node harus berupa pil).
 */
fun Modifier.pillGlow(
    color: Color,
    layers: List<GlowLayer>,
    maxReachDp: Float = 32f,
    ringWidthDp: Dp = 0.5.dp,
    clipVerticalDp: Dp = maxReachDp.dp,
): Modifier = this.drawBehind {
    val ring = ringWidthDp.toPx()
    val clip = clipVerticalDp.toPx()
    val radius = size.height / 2f
    val maxReach = maxReachDp * density
    clipRect(
        left = -1e7f,
        top = -clip,
        right = size.width + 1e7f,
        bottom = size.height + clip,
    ) {
        var d = ring / 2f
        while (d <= maxReach) {
            val alpha = glowAlphaAt(d / density, layers)
            if (alpha >= 0.004f) {
                drawRoundRect(
                    color = color.copy(alpha = alpha),
                    topLeft = Offset(-d, -d),
                    size = Size(size.width + 2f * d, size.height + 2f * d),
                    cornerRadius = CornerRadius(radius + d),
                    style = Stroke(width = ring),
                )
            }
            d += ring
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
