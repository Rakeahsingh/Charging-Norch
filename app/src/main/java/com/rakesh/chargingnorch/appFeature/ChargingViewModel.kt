package com.rakesh.chargingnorch.appFeature

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakesh.chargingnorch.preference.Preferences
import com.rakesh.chargingnorch.utils.ChargingStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChargingViewModel @Inject constructor(
    private val preferences: Preferences
): ViewModel() {

    private val _state = MutableStateFlow(ChargingState())
    val state = _state.asStateFlow()

    init {
        observeChargingState()

    }

    private fun observeChargingState() {
        ChargingStateManager.onChargingStateChanged = { isCharging ->
            _state.update { it.copy(isCharging = isCharging) }
            Log.d("ChargingViewModel", "Charging state observed: $isCharging")
        }
    }

    fun onEvent(event: ChargingEvent){
        when(event){

            is ChargingEvent.UpdateAnimationProgress -> {
                _state.update {
                    it.copy(
                        animationProgress = event.progress
                    )
                }
            }

            is ChargingEvent.UpdateChargingState -> {
                _state.update {
                    it.copy(
                        isCharging = event.isCharging
                    )
                }
            }

            is ChargingEvent.IsOverlayDisplay -> {
                _state.update {
                    it.copy(
                        isOverlayDisplay = event.overlayDisplay
                    )
                }
            }

            is ChargingEvent.UpdateNotchOffsetX -> {
                _state.update {
                    it.copy(
                        notchOffsetX = event.offsetX
                    )
                }

                viewModelScope.launch {
                    preferences.saveX(event.offsetX)
                }
                Log.d("ChargingViewModel", "Updated Notch Offset X: ${event.offsetX}")
            }

            is ChargingEvent.UpdateNotchOffsetY -> {
                _state.update {
                    it.copy(
                        notchOffsetY = event.offsetY
                    )
                }
                viewModelScope.launch {
                    preferences.saveY(event.offsetY)
                }
                Log.d("ChargingViewModel", "Updated Notch Offset Y: ${event.offsetY}")
            }

            is ChargingEvent.UpdateNotchRadius -> {
                _state.update {
                    it.copy(
                        notchRadius = event.radius
                    )
                }
                viewModelScope.launch {
                    preferences.saveR(event.radius)
                }
                Log.d("ChargingViewModel", "Updated Notch Radius: ${event.radius}")
            }

        }
    }

}