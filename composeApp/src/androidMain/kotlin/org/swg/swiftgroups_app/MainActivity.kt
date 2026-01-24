package org.swg.swiftgroups_app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appContext = applicationContext

        initKoin {
            androidContext(this@MainActivity)
            modules(commonModule, androidModule)
        }

        setContent {
            App()
        }

    }
}
