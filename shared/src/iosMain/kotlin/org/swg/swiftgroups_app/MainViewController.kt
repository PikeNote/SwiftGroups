package org.swg.swiftgroups_app

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController { App()

fun initKoinIos() = initKoin {
    modules(commonModule, iosModule)
}}