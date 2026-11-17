package io.github.mochamadabdulrouf.refy.ui.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.mochamadabdulrouf.refy.R
import io.github.mochamadabdulrouf.refy.ui.component.BottomNavPill
import io.github.mochamadabdulrouf.refy.ui.discover.DiscoverScreen
import io.github.mochamadabdulrouf.refy.ui.discover.sampleDiscoverUiState
import io.github.mochamadabdulrouf.refy.ui.home.HomeScreen
import io.github.mochamadabdulrouf.refy.ui.home.sampleHomeUiState
import io.github.mochamadabdulrouf.refy.ui.theme.NocturneTheme
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Kerangka aplikasi: konten + mini-player (opsional) + pil nav mengambang.
 *
 * Jarak yang dipakai **bukan angka baru** — berasal dari token:
 * pil nav duduk [RefyDimens.Offset.navBarBottom] (20dp) dari dasar, tingginya
 * [RefyDimens.Size.navBar] (68dp), sehingga mini-player yang ditumpuk tepat di
 * atasnya otomatis berada di **88dp** dari dasar (sama dengan `bottom-[88px]`
 * kanon). Kalau salah satu nilai diubah, offset 88 itu ikut bergeser — itulah
 * sebabnya tidak ada konstanta 88 terpisah di shell.
 *
 * Konten diberi jarak aman bawah [RefyDimens.Offset.contentBottom] (160dp)
 * supaya baris terakhir tidak tertutup mini-player + nav.
 */
@Composable
fun ScaffoldShell(
    selected: RefyDestination,
    onSelect: (RefyDestination) -> Unit,
    modifier: Modifier = Modifier,
    miniPlayer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(RefyColors.Canvas)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = RefyDimens.Offset.contentBottom),
        ) {
            content()
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = RefyDimens.Margin.mobile)
                .padding(bottom = RefyDimens.Offset.navBarBottom)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (miniPlayer != null) {
                miniPlayer()
                Spacer(Modifier.height(RefyDimens.Offset.gapNavToMiniPlayer))
            }
            BottomNavPill(selected = selected, onSelect = onSelect)
        }
    }
}

/**
 * Akar navigasi: 4 tab, masing-masing satu route.
 *
 * Isi tab masih [PlaceholderScreen] — Fase 3 mengisinya satu per satu
 * (#1 Home → #3 Discover → #4 Library → #2 Search).
 */
@Composable
fun RefyRoot(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val selected = RefyDestination.entries.firstOrNull { it.route == currentRoute }
        ?: RefyDestination.Home

    ScaffoldShell(
        selected = selected,
        onSelect = { destination ->
            if (destination != selected) {
                navController.navigate(destination.route) {
                    // Simpan/mulihkan keadaan tiap tab, dan jangan menumpuk riwayat.
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
        modifier = modifier,
    ) {
        NavHost(
            navController = navController,
            startDestination = RefyDestination.Home.route,
        ) {
            RefyDestination.entries.forEach { destination ->
                composable(destination.route) {
                    when (destination) {
                        RefyDestination.Home -> HomeScreen(state = sampleHomeUiState())
                        RefyDestination.Discover -> DiscoverScreen(state = sampleDiscoverUiState())
                        else -> PlaceholderScreen(destination)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(destination: RefyDestination) {
    Column(
        modifier = Modifier.fillMaxSize().padding(RefyDimens.Margin.mobile),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(destination.labelRes),
            style = RefyType.headlineLgMobile,
            color = RefyColors.TextPrimary,
        )
        Spacer(Modifier.height(RefyDimens.Space.xs))
        Text(
            text = stringResource(R.string.common_coming_soon),
            style = RefyType.bodyMd,
            color = RefyColors.TextMuted,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScaffoldShellPreview() {
    NocturneTheme {
        ScaffoldShell(selected = RefyDestination.Home, onSelect = {}) {
            PlaceholderScreen(RefyDestination.Home)
        }
    }
}