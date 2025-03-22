package org.swg.swiftgroups_app

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color
import org.swg.swiftgroups_app.Fonts.AppFont

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
        0f to Color(0xFF446BA0),
        0.71f to Color(0xFF71AEFF)
    )

    val eventPageImage = arrayOf(
        0f  to Color(0xFFd3d3da),
        0.2f to Color(0xFFb1b5d7),
        0.8f to Color(0xFF3d5483),
        1f to Color(0xFF003B7F)
    )


    val shadowBrush = listOf(
        Color.Black.copy(alpha = 0.25f),  // Darker shadow at the base
        Color.Black.copy(alpha = 0.15f),  // Soft transition
        Color.Black.copy(alpha = 0.05f),  // Light fading effect
        Color.Transparent                 // Full fade-out
    )

}