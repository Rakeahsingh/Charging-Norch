package com.rakesh.chargingnorch.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object StateManager {
    private val _isScreenOn = MutableStateFlow(false)
    val isScreenOn: StateFlow<Boolean> = _isScreenOn.asStateFlow()


    fun setScreenOn(isOn: Boolean) {
        _isScreenOn.value = isOn
    }


}