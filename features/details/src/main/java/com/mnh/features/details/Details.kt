package com.mnh.features.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mnh.ble.model.CharacteristicInfo
import com.mnh.ble.model.DeviceInfo
import com.napco.utils.DataState


@Composable
fun Details(
    viewModel: DetailsViewModel,
    connectionResult: DataState<DeviceInfo>,
    deviceAddress: String,
) {

    LaunchedEffect(Unit) {
        viewModel.connect(deviceAddress)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.disconnect(deviceAddress)
        }
    }

    when (connectionResult) {
        is DataState.Loading -> {
            Loader()
        }

        is DataState.Success -> {
            ListView(connectionResult.data.deviceInfo)
        }

        is DataState.Error -> {

        }

        else -> {}
    }

}

@Composable
fun ListView(deviceInfo: HashMap<String, List<CharacteristicInfo>>) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {
        deviceInfo.forEach { (service, characteristics) ->

            Text(text = "Service: $service")
            Spacer(modifier = Modifier.height(8.dp))

            characteristics.forEach { characteristic ->
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val characteristicTypes = characteristic.types.toList()
                    val formattedString = characteristicTypes.joinToString(", ") { it.toString() }
                    Column {
                        Text(text = characteristic.uuid)
                        Text(text = formattedString)
                    }

                    if (characteristic.types.isNotEmpty()) {
                        Button(onClick = { }) {
                            Text(">")
                        }
                    }

                }

            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun Loader() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.Start
    ) {
        CircularProgressIndicator()
    }
}