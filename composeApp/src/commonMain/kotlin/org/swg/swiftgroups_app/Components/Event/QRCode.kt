package org.swg.swiftgroups_app.Components.Event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.swg.swiftgroups_app.Fonts.AppFont

class QRCode(private val code : String,
             private val name : String,
             private val time : String,
             private val ticketName : String ) {
    @Composable
    fun Content() {
        Spacer(modifier = Modifier.height(7.dp))

        Column (modifier = Modifier.shadow(
            elevation = 3.dp,
            shape = RoundedCornerShape(20.dp)
        ).clip(RoundedCornerShape(20.dp))) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(325.dp)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(name, style = AppFont.InterTypography.h3)
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    modifier = Modifier.height(200.dp).fillMaxWidth(),
                    painter = rememberQrCodePainter(code),
                    contentDescription = "QR code displaying ${code}"
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(ticketName , style = AppFont.InterTypography.h5)
                Text(time, style = AppFont.InterTypography.h5)
            }
        }
    }
}