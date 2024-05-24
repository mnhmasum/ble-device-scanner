package com.mnh.features.details

import android.util.Log
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mnh.ble.model.Characteristic
import com.mnh.ble.model.ServiceInfo
import com.napco.utils.DataState


@Composable
fun Details(
    detailsViewModel: DetailsViewModel,
    deviceAddress: String,
) {
    Log.d("MyCompose", "Details ")
    val connectionResult by detailsViewModel.bleConnectionResult.collectAsStateWithLifecycle(
        DataState.loading()
    )
    var serviceInfo: ServiceInfo? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        detailsViewModel.connect(deviceAddress)
    }

    DisposableEffect(Unit) {
        onDispose {
            detailsViewModel.disconnect()
        }
    }

    when (connectionResult) {
        is DataState.Loading -> {
            Loader()
        }

        is DataState.Success -> {
            serviceInfo = (connectionResult as DataState.Success<ServiceInfo>).data
            DeviceInfo(detailsViewModel, serviceInfo)
        }

        is DataState.Error -> {
            DeviceInfo(detailsViewModel, serviceInfo)
            Text(text = "Disconnected")
        }
    }
}

@Composable
fun DeviceInfo(detailsViewModel: DetailsViewModel, serviceInfo: ServiceInfo?) {
    val deviceInfo = serviceInfo?.serviceInfo
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {

        if (deviceInfo == null) return

        for ((service, characteristics) in deviceInfo) {
            Text(text = service.name)
            Divider(thickness = 1.dp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))

            if (characteristics.isEmpty()) {
                Text(text = "No characteristic available")
            }

            for (characteristic in characteristics) {
                CharacteristicInfo(characteristic, onClickArrow = { characteristicUUIDString ->
                    //detailsViewModel.enableNotification(service.uuid, characteristicUUIDString)
                    //detailsViewModel.readCharacteristic(service.uuid, characteristicUUIDString)
                })
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CharacteristicInfo(
    characteristic: Characteristic,
    onClickArrow: (uuid: String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        val types = characteristic.types.joinToString(", ")

        Column {
            Text(text = characteristic.name)
            Text(text = types)
        }

        if (characteristic.types.isNotEmpty()) {
            Button(onClick = { onClickArrow(characteristic.uuid) }) {
                Text(">")
            }
        }

    }
}

@Composable
private fun Loader() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.Start
    ) {
        CircularProgressIndicator()
    }
}