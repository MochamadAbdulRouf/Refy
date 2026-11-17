package io.github.mochamadabdulrouf.refy.ui.shell

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.mochamadabdulrouf.refy.R

/**
 * Empat tab bawah — mengikuti desain dan D6 (`Create`, bukan `Buat`).
 *
 * **Penyimpangan yang dicatat:** desain memakai Material Symbols (Home, Explore,
 * Library/queue, Add-circle). Proyek ini belum membundel Material Symbols, jadi
 * ikon sementara memakai set inti `androidx.compose.material.icons` supaya tidak
 * menambah dependensi besar. Diganti saat ikon final disiapkan (M5) atau saat
 * Material Symbols diunduh sebagai aset. Rincian: `PLAN.md` §5.
 */
enum class RefyDestination(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Home("home", R.string.nav_home, Icons.Filled.Home),
    Discover("discover", R.string.nav_discover, Icons.Filled.Search),
    Library("library", R.string.nav_library, Icons.Filled.List),
    Create("create", R.string.nav_create, Icons.Filled.Add),
}