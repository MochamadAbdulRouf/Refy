package io.github.mochamadabdulrouf.refy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import io.github.mochamadabdulrouf.refy.ui.shell.RefyRoot
import io.github.mochamadabdulrouf.refy.ui.theme.NocturneTheme

/**
 * Activity tunggal. Seluruh UI adalah Compose.
 *
 * Fase 2: kerangka tab (pil nav + `NavHost`). Isi tiap tab masih placeholder —
 * Fase 3 mengisinya satu per satu.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NocturneTheme {
                RefyRoot()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RefyRootPreview() {
    NocturneTheme {
        RefyRoot()
    }
}