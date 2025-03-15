package org.swg.swiftgroups_app.Tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Star
import org.swg.swiftgroups_app.Screens.ScreenEvents

object TabEvents : Tab {
    @Composable
    override fun Content() {
        Navigator(screen = ScreenEvents) { navigator ->
            SlideTransition(navigator = navigator)
        }
    }

    override val options : TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(FontAwesomeIcons.Solid.Star)
            val title = "Events"
            val index: UShort = 1u

            return TabOptions(
                index,title,icon
            )
        }
}