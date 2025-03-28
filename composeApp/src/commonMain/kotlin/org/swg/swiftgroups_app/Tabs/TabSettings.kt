package org.swg.swiftgroups_app.Tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import org.swg.swiftgroups_app.Icons.Gear
import org.swg.swiftgroups_app.Screens.ScreenEvents

object TabSettings : TabWithNavigator {
    override var nav: Navigator? = null

    @Composable
    override fun Content() {
        Navigator(screen = ScreenEvents) { navigator ->
            SlideTransition(navigator = navigator)
            nav = navigator
        }
    }

    override val options : TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Gear)
            val title = "Settings"
            val index: UShort = 1u

            return TabOptions(
                index,title,icon
            )
        }
}