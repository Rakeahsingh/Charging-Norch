package com.rakesh.chargingnorch.appFeature



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChargingScreen(
    modifier: Modifier = Modifier,
    state: ChargingState,
    onEvent: (ChargingEvent) -> Unit
) {



    Column(
        modifier = modifier
            .fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 4.dp, end = 4.dp)

        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp))
                    .background(
                        Color.White,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {

                Column {

                    Text(
                        text = "Size",
                        fontSize = 16.sp
                    )

                    Slider(
                        value = state.notchRadius,
                        onValueChange = { onEvent(ChargingEvent.UpdateNotchRadius(it)) },
                        valueRange = 0f..100f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "x-axis",
                        fontSize = 16.sp
                    )

                    Slider(
                        value = state.notchOffsetX,
                        onValueChange = { onEvent(ChargingEvent.UpdateNotchOffsetX(it)) },
                        valueRange = -500f..500f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "y-axis",
                        fontSize = 16.sp
                    )

                    Slider(
                        value = state.notchOffsetY,
                        onValueChange = { onEvent(ChargingEvent.UpdateNotchOffsetY(it)) },
                        valueRange = 0f..300f,
                        modifier = Modifier.fillMaxWidth()
                    )

                }
            }
        }


    }


}