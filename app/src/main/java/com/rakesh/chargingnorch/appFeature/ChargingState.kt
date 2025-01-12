package com.rakesh.chargingnorch.appFeature

data class ChargingState(
    val isCharging: Boolean = false,
    val animationProgress: Float = 0f,
    val isOverlayDisplay: Boolean = false,
    val notchRadius: Float = 40f,
    val notchOffsetX: Float = 5f,
    val notchOffsetY: Float = 0.02f
)
