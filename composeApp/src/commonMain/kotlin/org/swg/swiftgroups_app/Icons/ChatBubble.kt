package org.swg.swiftgroups_app.Icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ChatBubble: ImageVector
    get() {
        if (_ChatBubbleLeft != null) {
            return _ChatBubbleLeft!!
        }
        _ChatBubbleLeft = ImageVector.Builder(
            name = "ChatBubbleLeft",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(2.25f, 12.7593f)
                curveTo(2.25f, 14.3604f, 3.3734f, 15.754f, 4.9575f, 15.987f)
                curveTo(6.0436f, 16.1467f, 7.1415f, 16.27f, 8.25f, 16.3556f)
                verticalLineTo(21f)
                lineTo(12.326f, 16.924f)
                curveTo(12.6017f, 16.6483f, 12.9738f, 16.4919f, 13.3635f, 16.481f)
                curveTo(15.2869f, 16.4274f, 17.1821f, 16.2606f, 19.0425f, 15.9871f)
                curveTo(20.6266f, 15.7542f, 21.75f, 14.3606f, 21.75f, 12.7595f)
                verticalLineTo(6.74056f)
                curveTo(21.75f, 5.1395f, 20.6266f, 3.7458f, 19.0425f, 3.5129f)
                curveTo(16.744f, 3.175f, 14.3926f, 3f, 12.0003f, 3f)
                curveTo(9.6078f, 3f, 7.2561f, 3.175f, 4.9575f, 3.513f)
                curveTo(3.3734f, 3.7459f, 2.25f, 5.1396f, 2.25f, 6.7406f)
                verticalLineTo(12.7593f)
                close()
            }
        }.build()
        return _ChatBubbleLeft!!
    }

private var _ChatBubbleLeft: ImageVector? = null
