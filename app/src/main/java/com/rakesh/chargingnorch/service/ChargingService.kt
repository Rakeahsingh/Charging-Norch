package com.rakesh.chargingnorch.service


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import android.util.Log
import com.rakesh.chargingnorch.R
import com.rakesh.chargingnorch.utils.StateManager
import com.rakesh.chargingnorch.preference.Preferences
import com.rakesh.chargingnorch.utils.ChargingStateManager
import com.rakesh.chargingnorch.utils.Window
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ChargingService : Service() {

    private var window: Window? = null

    @Inject
    lateinit var preferences: Preferences

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

//        coroutineScope.launch {
//            val x = preferences.getX().first()
//            val y = preferences.getY().first()
//            val r = preferences.getR().first()
//            showOverlay(x, y, r)
//        }

    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING

                if (isCharging) {

                    coroutineScope.launch {
                        StateManager.isScreenOn.collectLatest {
                            Log.d("ChargingService", "Screen is on, $it")
                            if (!it) {
                                try {
                                    val x = preferences.getX().first()
                                    val y = preferences.getY().first()
                                    val r = preferences.getR().first()
                                    Log.d("ChargingService", "Displaying overlay at $x, $y, radius: $r")
                                    showOverlay(x, y, r)
                                } catch (e: Exception) {
                                    Log.e("ChargingService", "Error showing overlay: ${e.message}")
                                }

                            }else{
                                Log.d("ChargingService", "Screen is on, removing overlay")
                                removeOverlay()
                            }


                        }
                    }

                } else {
                    Log.d("ChargingService", "Device not charging, removing overlay")
                    removeOverlay()
                }

                // Notify charging state (ensure null safety)
                ChargingStateManager.notifyChargingState(isCharging)
            } catch (e: Exception) {
                Log.e("ChargingService", "Error processing battery status: ${e.message}")
            }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // register for battery status
        try {
            // Register for battery status broadcasts
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            registerReceiver(batteryReceiver, filter)

            // Start foreground service
            startForeground(1, createNotification())
            Log.d("ChargingService", "Service started")
        } catch (e: Exception) {
            Log.e("ChargingService", "Error starting service: ${e.message}")
        }
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        unregisterReceiver(batteryReceiver)
        Log.d("ChargingService", "Service stopped")
        removeOverlay()
        super.onDestroy()
    }


    private fun showOverlay(
        x: Float,
        y: Float,
        r: Float,

        ) {

        if (window == null) {
            Log.d("overlay serivce", "Window started x is $x ,$y ,$r")
            window = Window(
                context = this,
                xOffset = x,
                yOffset = y,
                radius = r,
            )
        }
//        if (window?.isOpen() == false) {
//            window?.open()
//        }
        window?.open()
    }

    private fun removeOverlay() {
        Log.d("overlay serivce", "close overlay called")
        coroutineScope.launch(Dispatchers.Main) {

            window?.clear()
            window?.closeOverlay()
            //window?.close()
            window = null
        }
    }



    private fun createNotification(): Notification {
        val notificationChannelId = "Charging_Service"

        val channel = NotificationChannel(
            notificationChannelId,
            "Charging Service",
            NotificationManager.IMPORTANCE_LOW
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        return Notification.Builder(this, notificationChannelId)
            .setContentTitle("Charging Effect")
            .setContentText("Monitoring charging status...")
            .setSmallIcon(R.drawable.baseline_battery_charging)
            .build()

    }


}