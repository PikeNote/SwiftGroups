package org.swg.swiftgroups_app

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.jetbrains.compose.resources.Font
import org.swg.swiftgroups_app.Tabs.*
import swiftgroups.composeapp.generated.resources.Res
import swiftgroups.composeapp.generated.resources.inter
import swiftgroups.composeapp.generated.resources.inter_bold


private var interFontFamily : FontFamily? = null
private var interBoldFontFamily : FontFamily? = null

@Composable
@Preview
fun App() {
    val theme = Colors(
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

    interFontFamily = FontFamily(Font(Res.font.inter))
    interBoldFontFamily = FontFamily(Font(Res.font.inter_bold))

    val interFont = Typography(defaultFontFamily = interFontFamily!!)


    /*
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {

                AnimatedVisibility(showContent) {
                    val greeting = remember { Greeting().greet() }
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting")
                    }
                }

                Column(verticalArrangement = Arrangement.Bottom) {
                    Button(onClick = { showContent = !showContent }) {
                        Text("Click me!")
                    }
                }


        }

    }
    */
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

        };
    }
}


@Composable
private fun RowScope.TabItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    BottomNavigationItem(
        selected = tabNavigator.current == tab,
        onClick = {
            tabNavigator.current = tab;
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