package org.swg.swiftgroups_app.Screens.Groups

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Times
import org.swg.swiftgroups_app.Fonts.AppFont

object GroupScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { GroupScreenViewModel() }
        val naivgator = LocalNavigator.currentOrThrow
        var searchText by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "SwiftGroups",
                style = AppFont.InterTypography.h2,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = searchText,
                textStyle = AppFont.InterTypography.h4,
                onValueChange = {
                    searchText = it
                    viewModel.fetchGroups(searchText)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = { Text("Search clubs...", style=AppFont.InterTypography.h4) },
                leadingIcon = {
                    Icon(
                        FontAwesomeIcons.Solid.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchText = ""
                            }
                        ) {
                            Icon(
                                FontAwesomeIcons.Solid.Times,
                                contentDescription = "Clear search",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn (verticalArrangement = Arrangement.spacedBy(5.dp), userScrollEnabled = true) {
                viewModel.groupList.forEach {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(92.dp).padding(horizontal = 8.dp)
                                .shadow(2.dp, shape = RoundedCornerShape(20.dp))
                                .padding(2.dp)
                                .clickable {
                                    naivgator.push(GroupPage(groupID = it.clubID))
                                }



                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(90.dp).padding(horizontal = 2.dp)
                                    .clip(
                                        RoundedCornerShape(20.dp)
                                    )
                            ) {
                                AsyncImage(
                                    model = "https://community.case.edu${it.clubBanner}",
                                    "", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    Color(0xFF003B7F),
                                                    Color(0xAA6C65F0)
                                                ) // Example with some transparency
                                            )
                                        )
                                )

                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    AsyncImage(
                                        model = "https://community.case.edu${it.clubLogo}",
                                        "",
                                        modifier = Modifier.height(60.dp).clip(RoundedCornerShape(20.dp))
                                    )


                                    Text(
                                        it.clubName,
                                        color = Color.White,
                                        style = AppFont.InterTypography.h4,
                                        modifier = Modifier.padding(horizontal = 10.dp).width(190.dp)
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        it.clubCategories.split(",").filter { it != "" }.forEach {
                                            Text(
                                                it,
                                                modifier = Modifier.widthIn(min = 80.dp).clip(
                                                    RoundedCornerShape(10.dp)
                                                ).background(Color(0xFFD9D9D9)).padding(horizontal = 1.dp),
                                                style = AppFont.InterTypography.body1,
                                                textAlign = TextAlign.Center, maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // Loading indicator
                if (viewModel.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                // Load more when reaching the end
                if (viewModel.hasMoreClubs && !viewModel.isLoading) {
                    item {
                        viewModel.loadMoreClubs()
                    }
                }
            }
        }
    }
}