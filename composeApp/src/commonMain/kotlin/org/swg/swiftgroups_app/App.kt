package org.swg.swiftgroups_app


import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import okio.FileSystem
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Screens.Event.SingleEventScreen
import org.swg.swiftgroups_app.Screens.Login

@Composable
@Preview
fun App() {

    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context,0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY.resolve("image_cache"))
                    .maxSizePercent(1.0)
                    .build()
            }
            .build()
    }

    val typography = AppFont.InterTypography

    MaterialTheme(
        typography = typography,
        colors = AppTheme.theme
    ) {
        Navigator(Login)
    }


}


