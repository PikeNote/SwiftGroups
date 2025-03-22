package org.swg.swiftgroups_app.Components.Home.Button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.swg.swiftgroups_app.Fonts.AppFont

@Composable
fun VerticalLogoButton (
    modifier: Modifier = Modifier,
    logo: ImageVector,
    text: String,
    size: Dp = 12.dp,
    textStyle: TextStyle = AppFont.InterTypography.subtitle2,
    onClick: ()-> Unit)
{
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        Icon(
            logo,  text,
            modifier = Modifier
                .size(size))
        Text(text, style = textStyle, fontWeight = FontWeight.Bold)
    }
}
