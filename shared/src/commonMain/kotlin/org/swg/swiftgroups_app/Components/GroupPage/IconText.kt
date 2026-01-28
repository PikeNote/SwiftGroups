package org.swg.swiftgroups_app.Components.GroupPage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.swg.swiftgroups_app.Fonts.AppFont

class IconText (val icon : ImageVector, val contentDesc : String = "", val text : String) {


    @Composable
    fun Content() {
        Column (modifier = Modifier.width(120.dp).height(80.dp).clip(
            RoundedCornerShape(20.dp)
        ).background(Color(0xFFD9D9D9)), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(
                icon, contentDesc,
                modifier = Modifier
                    .size(30.dp),
                tint = Color.Black
            )
            Text(text, style= AppFont.InterTypography.titleMedium, textAlign = TextAlign.Center)
        }
    }
}