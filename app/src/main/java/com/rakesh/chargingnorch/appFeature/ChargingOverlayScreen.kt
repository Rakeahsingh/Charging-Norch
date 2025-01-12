package com.rakesh.chargingnorch.appFeature


import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun OverlayChargingScreen(
    modifier: Modifier = Modifier,
    x : Float,
    y: Float,
    r : Float
) {

    Log.d("OverlayChargingScreen", "State: Radius=${r}, X=${x}, Y=${y}")

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {




        val cameraBaseX = size.width / 2
        val cameraBaseY = 0f

        // Calculate center with user offsets
        val centerX = (cameraBaseX + x).coerceIn(r, size.width - r)
        val centerY = (cameraBaseY + y + r).coerceIn(r, size.height - r)


        drawCircle(
            color = Color.Green.copy(alpha = 0.5f),
            style = Stroke(width = 4.dp.toPx()),
            center = Offset(centerX, centerY),
            radius = r
        )


    }

}
