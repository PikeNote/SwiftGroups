package org.swg.swiftgroups_app.Screens.DBLoading

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.swg.swiftgroups_app.Fonts.AppFont
import org.swg.swiftgroups_app.Icons.Database
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color

object DatabaseLoading : Screen {

    @Composable
    override fun Content() {
        val nav = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel { DatabaseLoadingViewModel(nav) }
        val infiniteTransition = rememberInfiniteTransition()
        val opacity by
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.3f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        keyframes {
                            durationMillis = 1500
                        }
                    ,
                    repeatMode = RepeatMode.Reverse
                )
        )

        Box() {
            Column (modifier = Modifier.fillMaxSize().offset(y= (-50).dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Database, "Database Icon", modifier = Modifier.size(200.dp).alpha(opacity)
                )
                Text("Compiling database..", style = AppFont.InterTypography.displayMedium)
                Text("This is a one time process to ensure the app runs smoothly!", style = AppFont.InterTypography.headlineMedium, textAlign = TextAlign.Center)

                Spacer(modifier=Modifier.height(50.dp))

                if (viewModel.showButton) {
                    TextButton(
                        onClick = {
                            viewModel.showButton = false
                            viewModel.failed = false
                            viewModel.fetchAPIBatch()
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Retry Request", 
                            style = AppFont.InterTypography.headlineMedium,
                            color = Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .offset(y = 600.dp)
                    .padding(horizontal = 10.dp)
            ) {
                item {
                    Text("Fancy Logs", style = AppFont.InterTypography.headlineLarge)
                }
                items(viewModel.logs) { log ->
                    Text(log, style = AppFont.InterTypography.bodyLarge)
                }
            }
        }

    }

}