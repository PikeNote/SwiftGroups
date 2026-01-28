package org.swg.swiftgroups_app

import okio.Path
import okio.Path.Companion.toOkioPath

actual fun getCacheDirectory() : Path {
    val ctx = AndroidApp.getContext()
    return ctx.cacheDir.toOkioPath().resolve("image_cache")
}