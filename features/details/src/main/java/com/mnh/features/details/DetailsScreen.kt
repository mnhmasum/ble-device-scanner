package com.mnh.features.details

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mnh.ble.model.Characteristic
import com.mnh.ble.model.Service
import com.mnh.ble.model.ServiceInfo
import com.napco.utils.DataState

@Composable
fun Details(
    detailsViewModel: DetailsViewModel,
    deviceAddress: String,
) {
    Log.d("MyCompose", "Details")

    val connectionResult by detailsViewModel.bleConnectionResult.collectAsStateWithLifecycle(
        DataState.Loading()
    )

    //var serviceInfo: ServiceInfo? by remember { mutableStateOf(null) }

    LaunchedEffect(deviceAddress) {
        detailsViewModel.connect(deviceAddress)
    }

    DisposableEffect(Unit) {
        onDispose {
            detailsViewModel.disconnect()
        }
    }

    when (connectionResult) {
        is DataState.Loading -> Loader()
        is DataState.Success -> {
            //serviceInfo = (connectionResult as DataState.Success<ServiceInfo>).data
            DeviceInfo(detailsViewModel,  (connectionResult as DataState.Success<ServiceInfo>).data)
        }

        is DataState.Error -> {
            //DeviceInfo(detailsViewModel, serviceInfo)
            Text(text = "Disconnected")
        }

    }
}

@Composable
fun DeviceInfo(detailsViewModel: DetailsViewModel, serviceInfo: ServiceInfo?) {
    if (serviceInfo == null) {
        Text(text = "No device information available")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.Start
    ) {
        items(
            serviceInfo.serviceInfo.toList(),
            key = { it.first.uuid }) { (service, characteristics) ->
            ServiceItem(detailsViewModel, service, characteristics)
        }
    }
}

@Composable
fun ServiceItem(
    detailsViewModel: DetailsViewModel,
    service: Service,
    characteristics: List<Characteristic>,
) {
    Column {
        Text(text = service.name)
        Divider(thickness = 1.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))

        if (characteristics.isEmpty()) {
            Text(text = "No characteristic available")
            return
        }

        LazyColumn(modifier = Modifier.height((100 * characteristics.size).dp)) {
            items(characteristics, key = { it.uuid }) { characteristic ->
                CharacteristicItem(characteristic)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun CharacteristicItem(characteristic: Characteristic) {
    Column {
        Text(text = characteristic.name)
        Text(text = characteristic.joinProperties)
        if (characteristic.properties.isNotEmpty()) {
            Button(onClick = {}) {
                Text("►")
            }
        }
    }
}

@Composable
fun Loader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}



