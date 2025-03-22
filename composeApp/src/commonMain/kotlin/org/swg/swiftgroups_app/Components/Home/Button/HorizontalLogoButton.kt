package org.swg.swiftgroups_app.Components.Home.Button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.PencilSquare

@Composable
fun HorizontalLogoButton (
    modifier: Modifier = Modifier,
    logo: ImageVector,
    text: String,
    size: Dp = 20.dp,
    textStyle: TextStyle = AppFont.InterTypography.subtitle2,
    onClick: ()-> Unit,
    backgroundColor : Color = Color(0xFF2850A6),
    textColor : Color = Color.White
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        modifier = Modifier.width(170.dp).height(45.dp).clip(RoundedCornerShape(25.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,

            ) {

            Text(text, style=textStyle, color = textColor)
            Spacer(modifier = Modifier.width(5.dp))
            Icon(
                logo, text,
                modifier = modifier
                    .size(size),
                tint = textColor
            )

        }
    }
}