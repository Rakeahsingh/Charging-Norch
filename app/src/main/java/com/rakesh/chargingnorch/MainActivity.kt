package com.rakesh.chargingnorch

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rakesh.chargingnorch.appFeature.ChargingEvent
import com.rakesh.chargingnorch.appFeature.ChargingScreen
import com.rakesh.chargingnorch.appFeature.ChargingViewModel
import com.rakesh.chargingnorch.appFeature.OverlayChargingScreen
import com.rakesh.chargingnorch.service.ChargingService
import com.rakesh.chargingnorch.ui.theme.ChargingNorchTheme
import com.rakesh.chargingnorch.utils.ChargingStateManager
import com.rakesh.chargingnorch.utils.StateManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        StateManager.setScreenOn(true)

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        // start service
        startChargingService()

        setContent {
            ChargingNorchTheme {

                val viewModel: ChargingViewModel = hiltViewModel()

                ChargingStateManager.onChargingStateChanged = { isCharging ->
                    viewModel.onEvent(ChargingEvent.UpdateChargingState(isCharging))
                    Log.d("Charging start", "is charging: $isCharging")
                }

                val state by viewModel.state.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    OverlayChargingScreen(x =state.notchOffsetX,y=state.notchOffsetY, r = state.notchRadius)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ){

                        ChargingScreen(
                            modifier = Modifier.padding(top = 16.dp),
                            state = state,
                            onEvent = viewModel::onEvent
                        )

                    }

                }
            }
        }
    }


    private fun startChargingService(){
        val serviceIntent = Intent(this, ChargingService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(serviceIntent)
        }else{
            startService(serviceIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        StateManager.setScreenOn(false)
    }

    override fun onResume() {
        super.onResume()
        StateManager.setScreenOn(true)
    }
    override fun onDestroy() {
        super.onDestroy()
        StateManager.setScreenOn(false)
        stopService(Intent(this, ChargingService::class.java))
    }



}

