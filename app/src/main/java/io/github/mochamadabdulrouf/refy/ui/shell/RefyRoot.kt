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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.mochamadabdulrouf.refy.R
import io.github.mochamadabdulrouf.refy.ui.album.AlbumDetailScreen
import io.github.mochamadabdulrouf.refy.ui.album.sampleAlbumDetailUiState
import io.github.mochamadabdulrouf.refy.ui.artist.ArtistProfileScreen
import io.github.mochamadabdulrouf.refy.ui.artist.sampleArtistProfileUiState
import io.github.mochamadabdulrouf.refy.ui.component.BottomNavPill
import io.github.mochamadabdulrouf.refy.ui.discover.DiscoverScreen
import io.github.mochamadabdulrouf.refy.ui.discover.sampleDiscoverUiState
import io.github.mochamadabdulrouf.refy.ui.home.HomeScreen
import io.github.mochamadabdulrouf.refy.ui.home.sampleHomeUiState
import io.github.mochamadabdulrouf.refy.ui.library.LibraryScreen
import io.github.mochamadabdulrouf.refy.ui.library.sampleLibraryUiState
import io.github.mochamadabdulrouf.refy.ui.nowplaying.NowPlayingScreen
import io.github.mochamadabdulrouf.refy.ui.nowplaying.sampleNowPlayingUiState
import io.github.mochamadabdulrouf.refy.ui.search.SearchResultsScreen
import io.github.mochamadabdulrouf.refy.ui.search.sampleSearchUiState
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
 *
 * [showNavigation] = `false` untuk layar penuh seperti #9 Now Playing: pil nav
 * dan mini-player disembunyikan, dan jarak aman 160dp tidak dipakai — desain
 * layar itu tidak punya keduanya (lihat PNG kanon).
 */
@Composable
fun ScaffoldShell(
    selected: RefyDestination,
    onSelect: (RefyDestination) -> Unit,
    modifier: Modifier = Modifier,
    miniPlayer: (@Composable () -> Unit)? = null,
    showNavigation: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize().background(RefyColors.Canvas)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showNavigation) RefyDimens.Offset.contentBottom else 0.dp),
        ) {
            content()
        }

        if (showNavigation) {
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
}

/**
 * Akar navigasi: 4 tab + route non-tab.
 *
 * Isi tab: #1 Home, #3 Discover, #4 Your Library sudah nyata (data contoh).
 * Route non-tab: #2 Active Search Results (`search`, dari kolom cari), #5 Album
 * Detail (`album`, dari kartu Home), #8 Artist Profile (`artist`, dari baris
 * artis di Library). Tab yang menyala saat route non-tab aktif adalah tab
 * tempat route itu dibuka.
 */
@Composable
fun RefyRoot(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val selected = RefyDestination.entries.firstOrNull { it.route == currentRoute }
        ?: when (currentRoute) {
            // Pintu masuk keduanya → tab tempat ia dibuka (peta navigasi flows/00).
            SEARCH_ROUTE -> RefyDestination.Discover
            ARTIST_ROUTE -> RefyDestination.Library
            else -> RefyDestination.Home
        }

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
        // #9 adalah layar penuh: tanpa pil nav & mini-player (PNG kanon).
        showNavigation = currentRoute != NOW_PLAYING_ROUTE,
    ) {
        NavHost(
            navController = navController,
            startDestination = RefyDestination.Home.route,
        ) {
            composable(RefyDestination.Home.route) {
                HomeScreen(
                    state = sampleHomeUiState(),
                    onItemClick = {
                        navController.navigate(ALBUM_ROUTE) { launchSingleTop = true }
                    },
                )
            }
            composable(RefyDestination.Discover.route) {
                DiscoverScreen(
                    state = sampleDiscoverUiState(),
                    onSearchClick = { navController.navigate(SEARCH_ROUTE) { launchSingleTop = true } },
                )
            }
            composable(RefyDestination.Library.route) {
                LibraryScreen(
                    state = sampleLibraryUiState(),
                    onSearchClick = { navController.navigate(SEARCH_ROUTE) { launchSingleTop = true } },
                    onCreatePlaylist = {},
                    onItemClick = { item ->
                        // Data contoh: baris artis → #8, baris album → #5,
                        // baris playlist menunggu #7 (Fase 6).
                        when (item.id) {
                            "lib5" -> navController.navigate(ARTIST_ROUTE) { launchSingleTop = true }
                            "lib6" -> navController.navigate(ALBUM_ROUTE) { launchSingleTop = true }
                        }
                    },
                )
            }
            composable(RefyDestination.Create.route) {
                PlaceholderScreen(RefyDestination.Create)
            }
            composable(SEARCH_ROUTE) {
                SearchResultsScreen(
                    state = sampleSearchUiState(),
                    onCancel = { navController.popBackStack() },
                    onClear = {},
                )
            }
            composable(ALBUM_ROUTE) {
                AlbumDetailScreen(
                    state = sampleAlbumDetailUiState(),
                    onBack = { navController.popBackStack() },
                    // "Play" album → layar pemutar (flows/00: #5 → #9).
                    onPlay = { navController.navigate(NOW_PLAYING_ROUTE) { launchSingleTop = true } },
                )
            }
            composable(ARTIST_ROUTE) {
                ArtistProfileScreen(
                    state = sampleArtistProfileUiState(),
                    onBack = { navController.popBackStack() },
                    // Kartu discography membuka detail album (flows/00: #8 → #5).
                    onAlbumClick = { navController.navigate(ALBUM_ROUTE) { launchSingleTop = true } },
                    // FAB putar → layar pemutar (#8 → #9).
                    onPlay = { navController.navigate(NOW_PLAYING_ROUTE) { launchSingleTop = true } },
                )
            }
            composable(NOW_PLAYING_ROUTE) {
                NowPlayingScreen(
                    state = sampleNowPlayingUiState(),
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

/** Bukan tab: #2 dibuka dari kolom cari, jadi identitasnya route biasa. */
private const val SEARCH_ROUTE = "search"

/** Layar #5 — dibuka dari kartu Home; kembali lewat tombol panah. */
private const val ALBUM_ROUTE = "album"

/** Layar #8 — dibuka dari baris artis di Library; kembali lewat tombol panah. */
private const val ARTIST_ROUTE = "artist"

/** Layar #9 — dibuka dari aksi putar #5/#8; kembali lewat chevron turun. */
private const val NOW_PLAYING_ROUTE = "nowPlaying"

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