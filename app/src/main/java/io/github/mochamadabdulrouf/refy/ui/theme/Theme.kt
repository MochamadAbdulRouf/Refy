package io.github.mochamadabdulrouf.refy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Tema Refy — **NocturneTheme**, dark-only (D12).
 *
 * Tidak ada `darkColorScheme`/`lightColorScheme` bercabang dan tidak ada
 * `values-night/`: desain tidak punya token terang, jadi v0.1 hanya gelap.
 * Tema terang menunggu palet dari desainer (`docs/PRD.md` F-09 → v0.2).
 *
 * Pemetaan warna: seluruh peran Material 3 diarahkan ke token Kubu B di
 * [RefyColors] dan [RefyType]. Composable **tidak boleh** menulis nilai hex.
 */
private val NocturneColorScheme = darkColorScheme(
    primary = RefyColors.Accent,
    onPrimary = RefyColors.OnAccent,
    primaryContainer = RefyColors.AccentBright,
    onPrimaryContainer = RefyColors.OnAccent,

    secondary = RefyColors.AccentBright,
    onSecondary = RefyColors.OnAccent,
    secondaryContainer = RefyColors.AccentDeep,
    onSecondaryContainer = RefyColors.OnAccent,

    tertiary = RefyColors.TextMuted,
    onTertiary = RefyColors.Canvas,

    background = RefyColors.Canvas,
    onBackground = RefyColors.TextPrimary,

    surface = RefyColors.Surface,
    onSurface = RefyColors.TextPrimary,
    surfaceVariant = RefyColors.SurfaceElevated,
    onSurfaceVariant = RefyColors.TextMuted,

    surfaceContainerLowest = RefyColors.Canvas,
    surfaceContainerLow = RefyColors.Surface,
    surfaceContainer = RefyColors.SurfaceElevated,
    surfaceContainerHigh = RefyColors.SurfaceHighest,
    surfaceContainerHighest = RefyColors.SurfaceHighest,

    outline = RefyColors.TextFaint,
    outlineVariant = RefyColors.Border,

    error = RefyColors.Error,
    onError = RefyColors.ErrorContainer,
    errorContainer = RefyColors.ErrorContainer,
    onErrorContainer = RefyColors.Error,

    scrim = RefyColors.Canvas,
)

@Composable
fun NocturneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NocturneColorScheme,
        typography = RefyTypography,
        shapes = RefyShapes,
        content = content,
    )
}
