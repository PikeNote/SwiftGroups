package org.swg.swiftgroups_app.Screens

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BottomTabVisibilityManager {

    private val _bottomBarVisible = MutableStateFlow(true)
    val bottomBarVisible: StateFlow<Boolean> = _bottomBarVisible.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Main) // Use Dispatchers.Main for UI updates

    fun setBottomBarVisibility(visible: Boolean) {
        _bottomBarVisible.value = visible
    }

    fun observeBottomBarVisibility(observer: (Boolean) -> Unit) {
        coroutineScope.launch {
            bottomBarVisible.collect { isVisible ->
                observer(isVisible)
            }
        }
    }
}