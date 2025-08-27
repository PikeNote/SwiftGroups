package org.swg.swiftgroups_app

import okio.Path
import okio.Path.Companion.toOkioPath

actual fun getCacheDirectory() : Path {
    val ctx = MainActivity.appContext
    return ctx.cacheDir.toOkioPath().resolve("image_cache")
}