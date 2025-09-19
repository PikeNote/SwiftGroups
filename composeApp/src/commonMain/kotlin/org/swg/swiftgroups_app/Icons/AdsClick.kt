package org.swg.swiftgroups_app.Icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Ads_click: ImageVector
    get() {
        if (_Ads_click != null) return _Ads_click!!

        _Ads_click = ImageVector.Builder(
            name = "Ads_click",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(468f, 720f)
                quadToRelative(-96f, -5f, -162f, -74f)
                reflectiveQuadToRelative(-66f, -166f)
                quadToRelative(0f, -100f, 70f, -170f)
                reflectiveQuadToRelative(170f, -70f)
                quadToRelative(97f, 0f, 166f, 66f)
                reflectiveQuadToRelative(74f, 162f)
                lineToRelative(-84f, -25f)
                quadToRelative(-13f, -54f, -56f, -88.5f)
                reflectiveQuadTo(480f, 320f)
                quadToRelative(-66f, 0f, -113f, 47f)
                reflectiveQuadToRelative(-47f, 113f)
                quadToRelative(0f, 57f, 34.5f, 100f)
                reflectiveQuadToRelative(88.5f, 56f)
                close()
                moveToRelative(48f, 158f)
                quadToRelative(-9f, 2f, -18f, 2f)
                horizontalLineToRelative(-18f)
                quadToRelative(-83f, 0f, -156f, -31.5f)
                reflectiveQuadTo(197f, 763f)
                reflectiveQuadToRelative(-85.5f, -127f)
                reflectiveQuadTo(80f, 480f)
                reflectiveQuadToRelative(31.5f, -156f)
                reflectiveQuadTo(197f, 197f)
                reflectiveQuadToRelative(127f, -85.5f)
                reflectiveQuadTo(480f, 80f)
                reflectiveQuadToRelative(156f, 31.5f)
                reflectiveQuadTo(763f, 197f)
                reflectiveQuadToRelative(85.5f, 127f)
                reflectiveQuadTo(880f, 480f)
                verticalLineToRelative(18f)
                quadToRelative(0f, 9f, -2f, 18f)
                lineToRelative(-78f, -24f)
                verticalLineToRelative(-12f)
                quadToRelative(0f, -134f, -93f, -227f)
                reflectiveQuadToRelative(-227f, -93f)
                reflectiveQuadToRelative(-227f, 93f)
                reflectiveQuadToRelative(-93f, 227f)
                reflectiveQuadToRelative(93f, 227f)
                reflectiveQuadToRelative(227f, 93f)
                horizontalLineToRelative(12f)
                close()
                moveToRelative(305f, 22f)
                lineTo(650f, 729f)
                lineTo(600f, 880f)
                lineTo(480f, 480f)
                lineToRelative(400f, 120f)
                lineToRelative(-151f, 50f)
                lineToRelative(171f, 171f)
                close()
            }
        }.build()

        return _Ads_click!!
    }

private var _Ads_click: ImageVector? = null

