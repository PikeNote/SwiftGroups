package org.swg.swiftgroups_app.Screens

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.multiplatform.webview.cookie.Cookie
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.Fonts.AppFont.InterFontFamily
import org.swg.swiftgroups_app.Tabs.TabEvents
import org.swg.swiftgroups_app.Tabs.TabFeed
import org.swg.swiftgroups_app.Tabs.TabGroups
import org.swg.swiftgroups_app.Tabs.TabHome
import org.swg.swiftgroups_app.Tabs.TabSettings
import org.swg.swiftgroups_app.Tabs.TabWithNavigator
import org.swg.swiftgroups_app.getScreenResult

object Home : Screen {

    var profileDataItem : List<ProfileDataItem> = emptyList()
    private var cookies : List<Cookie>? = null

    @Composable
    override fun Content() {

        cookies = getScreenResult("cookies")


        MaterialTheme() {

            TabNavigator(TabHome) {

                Scaffold(
                    bottomBar = {
                        BottomNavigation {
                            TabItem(TabHome)
                            TabItem(TabEvents)
                            TabItem(TabFeed)
                            TabItem(TabGroups)
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
    private fun RowScope.TabItem(tab: TabWithNavigator) {
        val tabNavigator = LocalTabNavigator.current

        BottomNavigationItem(
            selected = tabNavigator.current == tab,
            onClick = {
                if(tabNavigator.current == tab) {
                    tab.nav?.popUntilRoot()
                }
                tabNavigator.current = tab
            },
            icon = {
                tab.options.icon?.let { painter ->
                    Icon(painter, contentDescription = tab.options.title, modifier = Modifier.size(24.dp))
                }
            },
            label = {
                Text(tab.options.title, fontFamily  = InterFontFamily, fontWeight = FontWeight.Bold)
            },
            selectedContentColor = Color(0xFF0279fd),
            unselectedContentColor = Color(0xFF929292)
        )
    }
}