package org.swg.swiftgroups_app.Icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val PencilSquare: ImageVector
	get() {
		if (_PencilSquare != null) {
			return _PencilSquare!!
		}
		_PencilSquare = ImageVector.Builder(
            name = "PencilSquare",
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
				moveTo(16.8617f, 4.48667f)
				lineTo(18.5492f, 2.79917f)
				curveTo(19.2814f, 2.0669f, 20.4686f, 2.0669f, 21.2008f, 2.7992f)
				curveTo(21.9331f, 3.5314f, 21.9331f, 4.7186f, 21.2008f, 5.4508f)
				lineTo(10.5822f, 16.0695f)
				curveTo(10.0535f, 16.5981f, 9.4014f, 16.9868f, 8.6849f, 17.2002f)
				lineTo(6f, 18f)
				lineTo(6.79978f, 15.3151f)
				curveTo(7.0132f, 14.5986f, 7.4018f, 13.9465f, 7.9305f, 13.4178f)
				lineTo(16.8617f, 4.48667f)
				close()
				moveTo(16.8617f, 4.48667f)
				lineTo(19.5f, 7.12499f)
				moveTo(18f, 14f)
				verticalLineTo(18.75f)
				curveTo(18f, 19.9926f, 16.9926f, 21f, 15.75f, 21f)
				horizontalLineTo(5.25f)
				curveTo(4.0074f, 21f, 3f, 19.9926f, 3f, 18.75f)
				verticalLineTo(8.24999f)
				curveTo(3f, 7.0073f, 4.0074f, 6f, 5.25f, 6f)
				horizontalLineTo(10f)
			}
		}.build()
		return _PencilSquare!!
	}

private var _PencilSquare: ImageVector? = null
