package com.rakesh.chargingnorch.appFeature

sealed class ChargingEvent {

    data class UpdateChargingState(val isCharging: Boolean): ChargingEvent()

    data class UpdateAnimationProgress(val progress: Float): ChargingEvent()

    data class IsOverlayDisplay(val overlayDisplay: Boolean): ChargingEvent()

    data class UpdateNotchRadius(val radius: Float): ChargingEvent()

    data class UpdateNotchOffsetX(val offsetX: Float): ChargingEvent()

    data class UpdateNotchOffsetY(val offsetY: Float): ChargingEvent()

}