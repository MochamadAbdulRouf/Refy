package io.github.mochamadabdulrouf.refy.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mochamadabdulrouf.refy.ui.shell.RefyDestination
import io.github.mochamadabdulrouf.refy.ui.theme.NocturneTheme
import io.github.mochamadabdulrouf.refy.ui.theme.RefyColors
import io.github.mochamadabdulrouf.refy.ui.theme.RefyDimens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyShapesTokens
import io.github.mochamadabdulrouf.refy.ui.theme.RefyType

/**
 * Bottom navigation **pil mengambang** — keputusan D11, bentuk mengikat.
 *
 * Nilai dari kanon `home-screen-243ea0d8` (diukur ulang dari PNG pada Fase 2):
 * tinggi [RefyDimens.Size.navBar] (68dp), `inset-x-4` → margin 16dp, `max-w-sm`
 * → lebar maksimum 384dp, radius pil, latar `#121621`/85 %, garis putih 60 %
 * (lihat catatan koreksi di [RefyColors.NavBorder]), tab aktif bersorot
 * accent 6 %.
 *
 * Label memakai [RefyType.labelXs] **tanpa** `uppercase()`: desain menulis
 * "Home / Discover / Library / Create" dengan huruf kapital di awal saja
 * (`docs/DESIGN.md` §2 menyebut HURUF BESAR, PNG kanon tidak).
 */
@Composable
fun BottomNavPill(
    selected: RefyDestination,
    onSelect: (RefyDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassSurface(
        color = RefyColors.GlassNav,
        border = RefyColors.NavBorder,
        shape = RefyShapesTokens.pill,
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 384.dp)
            .height(RefyDimens.Size.navBar),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            RefyDestination.entries.forEach { destination ->
                NavItem(
                    destination = destination,
                    selected = destination == selected,
                    onSelect = onSelect,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    destination: RefyDestination,
    selected: Boolean,
    onSelect: (RefyDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (selected) RefyColors.TextPrimary else RefyColors.TextMuted
    val label = stringResource(destination.labelRes)

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 6.dp)
            .clip(RefyShapesTokens.pill)
            .then(
                if (selected) {
                    Modifier.background(RefyColors.TabActiveTint)
                } else {
                    Modifier
                }
            )
            .selectable(
                selected = selected,
                role = Role.Tab,
                onClick = { onSelect(destination) },
            )
            .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = null, // label teks di bawah sudah menjelaskan
            tint = contentColor,
            modifier = Modifier.size(RefyDimens.Size.navIcon),
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = RefyType.labelXs,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavPillPreview() {
    NocturneTheme {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
            BottomNavPill(selected = RefyDestination.Home, onSelect = {})
        }
    }
}