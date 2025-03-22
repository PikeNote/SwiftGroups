package org.swg.swiftgroups_app.Fonts

import androidx.compose.material.Typography
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


    val InterTypography : Typography
        @Composable
        get() {
            return Typography(
                h1 = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                ),
                h2 = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                ),
                h3 = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                ),
                h4 = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
                h5 = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                h6 = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                body1 = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                ),
                body2 = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                ),
                subtitle1 = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF274c85)
                ),
                subtitle2 = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )


            )
        }
}