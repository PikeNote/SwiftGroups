package org.swg.swiftgroups_app


import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import org.swg.swiftgroups_app.Screens.Login

@Composable
@Preview
fun App() {

    MaterialTheme() {
        Navigator(Login);
    }
}


