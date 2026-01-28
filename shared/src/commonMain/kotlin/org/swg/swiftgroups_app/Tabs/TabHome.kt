package org.swg.swiftgroups_app.Tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.CalendarWeek
import org.swg.swiftgroups_app.Screens.ScreenHome

object  TabHome : TabWithNavigator {
    override var nav: Navigator? = null

    @Composable
    override fun Content() {
        Navigator(screen = ScreenHome) { navigator ->
            SlideTransition(navigator = navigator)
            nav = navigator
        }
    }

    override val options : TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(FontAwesomeIcons.Solid.CalendarWeek)
            val title = "My Day"
            val index: UShort = 0u

            return TabOptions(
                index,title,icon
            )
        }
}