package org.swg.swiftgroups_app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsControllerCompat
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.WHITE,
                darkScrim = android.graphics.Color.WHITE,
            )
        )

        AndroidApp.init(this)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true

        super.onCreate(savedInstanceState)

        appContext = applicationContext

        if (org.koin.core.context.GlobalContext.getOrNull() == null) {
            initKoin {
                androidContext(this@MainActivity)
                modules(commonModule, androidModule)
            }
        }


        setContent {
            App()
        }

    }
}
