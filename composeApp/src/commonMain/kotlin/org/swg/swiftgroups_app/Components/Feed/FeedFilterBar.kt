package org.swg.swiftgroups_app.Components.Feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.swg.swiftgroups_app.AppTheme
import org.swg.swiftgroups_app.CGAPI.Feed.Button
import org.swg.swiftgroups_app.Fonts.AppFont

@Composable
fun FilterBar(
    buttonList: List<Button>,
    selectedIndex: Int,
    onFilterSelected: (Int) -> Unit
) {
    val gradient = remember { Brush.horizontalGradient(colorStops = AppTheme.profileColorStops) }

    Row (modifier = Modifier.fillMaxWidth().padding(10.dp).height(66.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(brush = gradient)
        .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.padding(start = 5.dp))
        buttonList.forEachIndexed { index, item ->
            Column (modifier = Modifier.width(80.dp).height(56.dp).clip(RoundedCornerShape(20.dp)).background(
                Color(0xFFF5F5F5)
            ).then(if(index==selectedIndex) Modifier
                .innerShadow(
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                    shadow = Shadow(
                        offset = DpOffset(1.dp, 5.dp),
                        color = Color(0xFFc5c5c5),
                        radius = 5.4.dp,
                        spread = 1.dp
                    )
                )
            else Modifier
            ).clickable {
                if(index != selectedIndex) {
                    onFilterSelected(index)
                }
            }, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(model = "https://community.case.edu${item.icon_url}",item.name, modifier = Modifier.size(25.dp), contentScale = ContentScale.Crop)
                Text(item.name, style = AppFont.InterTypography.body1, maxLines=1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 3.dp))
            }

        }
        Spacer(modifier = Modifier.width(5.dp))
    }
}