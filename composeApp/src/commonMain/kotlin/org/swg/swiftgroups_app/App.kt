package org.swg.swiftgroups_app


import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.swg.swiftgroups_app.DataStore.UserSettingsPreferences
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Screens.BottomTabVisibilityManager
import org.swg.swiftgroups_app.Screens.Login

val commonModule = module {
    single { UserSettingsPreferences(get(), get()) }
    single<BottomTabVisibilityManager> { BottomTabVisibilityManager() }
    //factory { SettingsViewModel(get()) }
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule)
    }

@Composable
@Preview
fun App() {
    val typography = AppFont.InterTypography


    MaterialTheme(
        typography = typography,
        colors = AppTheme.theme
    ) {
        val lifecycle = remember { LifecycleRegistry() }

        Navigator(Login, onBackPressed = null) {
            CurrentScreen()
        }
    }
}