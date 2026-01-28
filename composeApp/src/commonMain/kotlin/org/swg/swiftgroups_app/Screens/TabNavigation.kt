package org.swg.swiftgroups_app.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.multiplatform.webview.cookie.Cookie
import org.koin.compose.koinInject
import org.swg.swiftgroups_app.Fonts.AppFont.InterFontFamily
import org.swg.swiftgroups_app.Tabs.*
import org.swg.swiftgroups_app.getScreenResult

object TabNavigation : Screen {
    private var cookies : List<Cookie>? = null

    @Composable
    override fun Content() {


        val bottomBarVisibilityManager: BottomTabVisibilityManager = koinInject()
        val isBottomBarVisible = remember { mutableStateOf(true) }

        bottomBarVisibilityManager.observeBottomBarVisibility { isVisible ->
            isBottomBarVisible.value = isVisible
        }

        cookies = getScreenResult("cookies")


        TabNavigator(TabHome) {
            Scaffold(
                bottomBar = {
                    if (isBottomBarVisible.value) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            windowInsets = WindowInsets.navigationBars
                        ) {
                            TabItem(TabHome)
                            TabItem(TabEvents)
                            TabItem(TabFeed)
                            TabItem(TabGroups)
                            TabItem(TabSettings)

                        }
                    }
                },
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
            ) {
                CurrentTab()
            }
        }

    }

    @Composable
    private fun RowScope.TabItem(tab: TabWithNavigator) {
        val tabNavigator = LocalTabNavigator.current

        NavigationBarItem(
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
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = Color(0xFF929292),
                unselectedTextColor = Color(0xFF929292),
                indicatorColor = Color(0xFFD1E4FF)
            )
        )
    }
}