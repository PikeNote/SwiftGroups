package org.swg.swiftgroups_app.Icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Person: ImageVector
    get() {
        if (_Person != null) {
            return _Person!!
        }
        _Person = ImageVector.Builder(
            name = "Person",
            defaultWidth = 15.dp,
            defaultHeight = 15.dp,
            viewportWidth = 15f,
            viewportHeight = 15f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(7.5f, 0.875f)
                curveTo(5.498f, 0.875f, 3.875f, 2.498f, 3.875f, 4.5f)
                curveTo(3.875f, 6.1529f, 4.9812f, 7.5474f, 6.4937f, 7.9835f)
                curveTo(5.2997f, 8.129f, 4.2756f, 8.5513f, 3.5041f, 9.3117f)
                curveTo(2.5222f, 10.2794f, 2.025f, 11.72f, 2.025f, 13.5999f)
                curveTo(2.025f, 13.8623f, 2.2377f, 14.0749f, 2.5f, 14.0749f)
                curveTo(2.7624f, 14.0749f, 2.975f, 13.8623f, 2.975f, 13.5999f)
                curveTo(2.975f, 11.8799f, 3.4279f, 10.7206f, 4.1709f, 9.9883f)
                curveTo(4.9154f, 9.2546f, 6.0267f, 8.875f, 7.5f, 8.875f)
                curveTo(8.9732f, 8.875f, 10.0846f, 9.2546f, 10.8291f, 9.9883f)
                curveTo(11.5721f, 10.7206f, 12.025f, 11.8799f, 12.025f, 13.5999f)
                curveTo(12.025f, 13.8623f, 12.2376f, 14.0749f, 12.5f, 14.0749f)
                curveTo(12.7623f, 14.075f, 12.975f, 13.8623f, 12.975f, 13.6f)
                curveTo(12.975f, 11.72f, 12.4778f, 10.2794f, 11.4959f, 9.3117f)
                curveTo(10.7244f, 8.5513f, 9.7003f, 8.129f, 8.5062f, 7.9835f)
                curveTo(10.0187f, 7.5474f, 11.125f, 6.1529f, 11.125f, 4.5f)
                curveTo(11.125f, 2.498f, 9.502f, 0.875f, 7.5f, 0.875f)
                close()
                moveTo(4.825f, 4.5f)
                curveTo(4.825f, 3.0226f, 6.0226f, 1.825f, 7.5f, 1.825f)
                curveTo(8.9774f, 1.825f, 10.175f, 3.0226f, 10.175f, 4.5f)
                curveTo(10.175f, 5.9774f, 8.9774f, 7.175f, 7.5f, 7.175f)
                curveTo(6.0226f, 7.175f, 4.825f, 5.9774f, 4.825f, 4.5f)
                close()
            }
        }.build()
        return _Person!!
    }

private var _Person: ImageVector? = null
