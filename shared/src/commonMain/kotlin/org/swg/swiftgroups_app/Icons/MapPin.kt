package org.swg.swiftgroups_app.Icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val MapPin: ImageVector
    get() {
        if (_MapPin != null) {
            return _MapPin!!
        }
        _MapPin = ImageVector.Builder(
            name = "MapPin",
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
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15f, 10.5f)
                curveTo(15f, 12.1569f, 13.6569f, 13.5f, 12f, 13.5f)
                curveTo(10.3431f, 13.5f, 9f, 12.1569f, 9f, 10.5f)
                curveTo(9f, 8.8431f, 10.3431f, 7.5f, 12f, 7.5f)
                curveTo(13.6569f, 7.5f, 15f, 8.8431f, 15f, 10.5f)
                close()
            }
            path(
                fill = null,
                fillAlpha = 1.0f,
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeAlpha = 1.0f,
                strokeLineWidth = 2.0f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(19.5f, 10.5f)
                curveTo(19.5f, 17.6421f, 12f, 21.75f, 12f, 21.75f)
                curveTo(12f, 21.75f, 4.5f, 17.6421f, 4.5f, 10.5f)
                curveTo(4.5f, 6.3579f, 7.8579f, 3f, 12f, 3f)
                curveTo(16.1421f, 3f, 19.5f, 6.3579f, 19.5f, 10.5f)
                close()
            }
        }.build()
        return _MapPin!!
    }

private var _MapPin: ImageVector? = null

