package org.swg.swiftgroups_app

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object AppTheme {
    val theme = lightColorScheme(
        primary = Color(0xff446ba0),
        onPrimaryFixedVariant = Color(0xFF0279fd),
        secondary = Color(0xFF0279fd),
        onSecondaryFixedVariant = Color(0xFF0279fd),
        background = Color(0xfff5f5f5),
        surface = Color(0xffffffff),
        error = Color(0xFFB00020),
        onPrimary = Color(0xff000000),
        onSecondary = Color(0xff000000),
        onBackground = Color(0xff000000),
        onSurface = Color(0xff000000),
        onError = Color(0xffffffff),
        surfaceContainerHighest = Color(0xFFe5e5e6)
    )

    val profileColorStops = arrayOf(
        0f to Color(0xFF446BA0),
        0.71f to Color(0xFFCCCCCC)
    )

    val eventPageImage = arrayOf(
        0f  to Color(0xFFd3d3da),
        0.2f to Color(0xFFb1b5d7),
        0.8f to Color(0xFF3d5483),
        1f to Color(0xFF003B7F)
    )


    val profilePageColorStop = arrayOf(
        0.0f to Color(0xFFFFFDFD),
        0.8077f to Color(0xFF697CB3),
    )


}