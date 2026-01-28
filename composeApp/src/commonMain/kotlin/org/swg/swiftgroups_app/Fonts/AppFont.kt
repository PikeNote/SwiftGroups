package org.swg.swiftgroups_app.Fonts

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import swiftgroups.composeapp.generated.resources.Res
import swiftgroups.composeapp.generated.resources.inter_multi

object AppFont {


    init {

    }

    val InterFontFamily : FontFamily
    @Composable
    get() {
        return FontFamily(

            Font(Res.font.inter_multi, FontWeight.Light),
            Font(Res.font.inter_multi, FontWeight.Normal),
            Font(Res.font.inter_multi, FontWeight.Bold),
            Font(Res.font.inter_multi, FontWeight.Medium),
            Font(Res.font.inter_multi, FontWeight.Black)
        )
    }


    val InterTypography: Typography
        @Composable
        get() {
            return Typography(
                // M2 h1 -> M3 displayLarge (38.sp)
                displayLarge = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                ),
                // M2 h2 -> M3 displayMedium (30.sp)
                displayMedium = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                ),
                // M2 h3 -> M3 headlineLarge (22.sp)
                headlineLarge = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                ),
                // M2 h4 -> M3 headlineMedium (18.sp)
                headlineMedium = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
                // M2 h5 -> M3 titleLarge (16.sp)
                titleLarge = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                // M2 h6 -> M3 titleMedium (14.sp)
                titleMedium = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                // M2 body1 -> M3 bodyLarge (14.sp)
                bodyLarge = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                ),
                // M2 body2 -> M3 bodySmall (11.sp)
                bodySmall = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                ),
                // M2 subtitle1 -> M3 labelLarge (11.sp + Blue Color)
                labelLarge = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF274C85)
                ),
                // M2 subtitle2 -> M3 labelMedium (12.sp)
                labelMedium = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
}