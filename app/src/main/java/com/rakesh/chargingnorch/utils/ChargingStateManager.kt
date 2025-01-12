package com.rakesh.chargingnorch.utils

import android.util.Log

object ChargingStateManager {

    var onChargingStateChanged: ((Boolean) -> Unit)? = null

    fun notifyChargingState(isCharging: Boolean) {
        onChargingStateChanged?.invoke(isCharging)
        Log.d( "Charging state","Callback invoked for isCharging: $isCharging")
    }

}