package org.swg.swiftgroups_app

import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSUserDomainMask

actual fun getCacheDirectory(): Path {
    val baseDir = NSFileManager.defaultManager
        .URLsForDirectory(NSCachesDirectory, NSUserDomainMask)
    val url = baseDir.firstOrNull() as? NSURL
        ?: error("No caches directory found")
    return url.path?.toPath()?.resolve("image_cache") ?: error("Failed to resolve cache directory path")
}