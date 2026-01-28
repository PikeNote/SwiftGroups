package org.swg.swiftgroups_app.Tabs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

interface TabWithNavigator : Tab {

    var nav: Navigator?

    @Composable
    override fun Content()
    override val options: TabOptions
        @Composable get
}