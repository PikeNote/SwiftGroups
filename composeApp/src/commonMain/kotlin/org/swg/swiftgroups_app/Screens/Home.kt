package org.swg.swiftgroups_app.Screens

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.multiplatform.webview.cookie.Cookie
import org.jetbrains.compose.resources.Font
import org.swg.swiftgroups_app.Tabs.TabEvents
import org.swg.swiftgroups_app.Tabs.TabFeed
import org.swg.swiftgroups_app.Tabs.TabGroups
import org.swg.swiftgroups_app.Tabs.TabHome
import org.swg.swiftgroups_app.Tabs.TabSettings
import org.swg.swiftgroups_app.getScreenResult
import swiftgroups.composeapp.generated.resources.Res
import swiftgroups.composeapp.generated.resources.inter
import swiftgroups.composeapp.generated.resources.inter_bold


object Home : Screen {

    private var interFontFamily : FontFamily? = null
    private var interBoldFontFamily : FontFamily? = null
    private var cookies : List<Cookie>? = null
    private val theme = Colors(
        primary = Color(0xffffffff),
        primaryVariant = Color(0xFF0279fd),
        secondary = Color(0xFF0279fd),
        secondaryVariant = Color(0xFF0279fd),
        background = Color(0xffffffff),
        surface = Color(0xffffffff),
        error = Color(0xFFB00020),
        onPrimary = Color(0xff000000),
        onSecondary = Color(0xff000000),
        onBackground = Color(0xff000000),
        onSurface = Color(0xff000000),
        onError = Color(0xffffffff),
        isLight = true
    )

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        cookies = getScreenResult("cookies")

        interFontFamily = FontFamily(Font(Res.font.inter))
        interBoldFontFamily = FontFamily(Font(Res.font.inter_bold))

        val interFont = Typography(defaultFontFamily = interFontFamily!!)

        MaterialTheme(colors = theme, typography = interFont) {

            TabNavigator(TabHome) {

                Scaffold(
                    bottomBar = {
                        BottomNavigation {
                            TabItem(TabHome)
                            TabItem(TabEvents)
                            TabItem(TabGroups)
                            TabItem(TabFeed)
                            TabItem(TabSettings)

                        }
                    }
                ) {
                    CurrentTab()
                }

            }
        }
    }

    @Composable
    private fun RowScope.TabItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        BottomNavigationItem(
            selected = tabNavigator.current == tab,
            onClick = {
                tabNavigator.current = tab
            },
            icon = {
                tab.options.icon?.let { painter ->
                    Icon(painter, contentDescription = tab.options.title, modifier = Modifier.size(24.dp))
                }
            },
            label = {
                Text(tab.options.title, fontFamily  = interBoldFontFamily, fontWeight = FontWeight.Bold)
            },
            selectedContentColor = Color(0xFF0279fd),
            unselectedContentColor = Color(0xFF929292)
        )
    }
}