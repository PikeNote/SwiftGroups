package org.swg.swiftgroups_app

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

object AppTheme {
    val theme = Colors(
        primary = Color(0xffffffff),
        primaryVariant = Color(0xFF0279fd),
        secondary = Color(0xFF0279fd),
        secondaryVariant = Color(0xFF0279fd),
        background = Color(0xffffffff),
        surface = Color(0xffffffff),
        error = Color(0xFFB00020),
        onPrimary = Color(0xff000000),
        onSecondary = Color(0xff000000),
        onBackground = Color(0xff000000),
        onSurface = Color(0xff000000),
        onError = Color(0xffffffff),
        isLight = true
    )

    val profileColorStops = arrayOf(
        0f to Color(0xFF6b86ac),
        0.4f to Color(0xFF6b9ee4)
    )
}