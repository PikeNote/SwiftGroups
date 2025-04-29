package org.swg.swiftgroups_app.Screens.Profile

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.swg.swiftgroups_app.CGAPI.Profile.ProfileDataItem

class MyUserProfileViewModel(initProfileDataItem : ProfileDataItem?) :ScreenModel {
    val _profileData = MutableStateFlow(initProfileDataItem)
    val profileData : StateFlow<ProfileDataItem?> = _profileData.asStateFlow()
}