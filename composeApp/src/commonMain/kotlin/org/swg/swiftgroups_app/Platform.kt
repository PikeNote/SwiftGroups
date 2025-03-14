package org.swg.swiftgroups_app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform