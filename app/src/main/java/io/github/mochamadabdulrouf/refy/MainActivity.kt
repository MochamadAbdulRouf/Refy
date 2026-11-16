package io.github.mochamadabdulrouf.refy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import io.github.mochamadabdulrouf.refy.ui.theme.NocturneTheme
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Activity tunggal. Seluruh UI adalah Compose.
 *
 * SEMENTARA (akhir Fase 1): hanya menampilkan kanvas + nama aplikasi memakai token
 * tema, sebagai bukti tema benar-benar terpasang (font, skala, warna). Fase 2
 * menggantinya dengan kerangka tab (nav pil + mini-player).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NocturneTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    TokenCheck()
                }
            }
        }
    }
}

@Composable
private fun TokenCheck() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(RefyDimens.Margin.mobile),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = RefyType.displayHero,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.app_name),
            style = RefyType.labelXs,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

// Latar pratinjau tidak disetel di sini: `Surface` di dalamnya sudah memakai
// `colorScheme.background` (canvas), jadi tidak perlu hex kedua di luar ui/theme.
@Preview(showBackground = true)
@Composable
private fun TokenCheckPreview() {
    NocturneTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            TokenCheck()
        }
    }
}
