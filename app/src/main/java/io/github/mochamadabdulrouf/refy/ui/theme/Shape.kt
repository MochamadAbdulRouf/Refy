package io.github.mochamadabdulrouf.refy.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Bentuk Refy — `docs/DESIGN.md` §4.
 *
 * Nilai diambil dari `borderRadius` di `tailwind.config` HTML (yang benar-terukur),
 * bukan dari prosa manifest yang keliru menulis "`rounded-lg` = 16px".
 */
object RefyShapesTokens {

    /** 8px — artwork di baris lagu, chip kecil, badge. */
    val small = RoundedCornerShape(8.dp)

    /** 12px — kartu, mini-player, panel sheet. */
    val medium = RoundedCornerShape(12.dp)

    /** 16px — artwork besar, modal, hero player. */
    val large = RoundedCornerShape(16.dp)

    /** 24px — sheet tinggi penuh (DESIGN §6.9 menyebut 16–24px; diambil ujung atas). */
    val extraLarge = RoundedCornerShape(24.dp)

    /** Pil — tombol putar, chip filter, tab aktif, pil nav, scrub thumb. */
    val pill = RoundedCornerShape(percent = 50)
}

val RefyShapes = Shapes(
    extraSmall = RefyShapesTokens.small,
    small = RefyShapesTokens.small,
    medium = RefyShapesTokens.medium,
    large = RefyShapesTokens.large,
    extraLarge = RefyShapesTokens.extraLarge,
)
