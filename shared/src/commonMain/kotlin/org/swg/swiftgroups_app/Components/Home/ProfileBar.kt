package org.swg.swiftgroups_app.Components.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.User
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.swg.swiftgroups_app.AppTheme
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem
import org.swg.swiftgroups_app.Components.Home.Button.VerticalLogoButton
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Screens.Profile.MyUserProfile

class ProfileBar ( private val profileData : ProfileDataItem ) {

    @Composable
    fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        val currentTime = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())


        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 10.dp)
                .dropShadow(
                    shape = RoundedCornerShape(15.dp),
                    shadow = Shadow(
                        radius = 0.dp,
                        color = Color.Black.copy(alpha = 0.2f),
                        offset = DpOffset(1.dp, 2.dp)
                    )
                )
                .clip(shape = RoundedCornerShape(15.dp))
                .background(Brush.horizontalGradient(colorStops = AppTheme.profileColorStops))
                ,



            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.weight(0.6f))
            AsyncImage(
                model = "https://community.case.edu${profileData.photoUrl}",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(shape = CircleShape)

            )

            val greetingText = when (currentTime.hour) {
                in 5..11 -> "morning"
                in 12..18 -> "afternoon"
                else -> "evening"
            }

            Spacer(Modifier.weight(1f))
            Column {
                Text("Good ${greetingText}, ", style = AppFont.InterTypography.headlineLarge, fontWeight = FontWeight.Medium)
                Text("${profileData.firstName}!", style = AppFont.InterTypography.headlineLarge)
            }

            VerticalLogoButton(logo = FontAwesomeIcons.Solid.User, text = "My Profile", onClick = {
                navigator.push(MyUserProfile())
            }, size = 25.dp, textStyle = AppFont.InterTypography.titleMedium, modifier = Modifier.width(110.dp))
        }
    }
}