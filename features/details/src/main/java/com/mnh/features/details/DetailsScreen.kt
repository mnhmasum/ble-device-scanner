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
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mnh.ble.model.Characteristic
import com.mnh.ble.model.Service
import com.mnh.ble.model.ServiceInfo
import com.napco.utils.DataState

@Composable
fun Details(navController: NavController, deviceAddress: String) {
    Log.d("Details", "Details")

    val detailsViewModel: DetailsViewModel = hiltViewModel()

    val connectionResult by detailsViewModel.bleConnectionResult.collectAsStateWithLifecycle(
        DataState.Loading()
    )

    LaunchedEffect(deviceAddress) {
        Log.d("Details", "Launched ")
        detailsViewModel.connect(deviceAddress)
    }

    DisposableEffect(Unit) {
        onDispose {
            detailsViewModel.disconnect()
        }
    }

    var serviceInfo: ServiceInfo? by remember { mutableStateOf(null) }

    when (connectionResult) {
        is DataState.Loading -> Loader()
        is DataState.Success -> {
            serviceInfo = (connectionResult as DataState.Success<ServiceInfo>).data
        }

        is DataState.Error -> {
            Text(text = "Disconnected")
        }
    }

    DeviceInfo(serviceInfo = serviceInfo)

}

@Composable
fun DeviceInfo(serviceInfo: ServiceInfo?) {
    Log.d("Details", "DeviceInfo: ")
    if (serviceInfo == null) {
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.Start
    ) {
        items(
            serviceInfo.serviceInfo.toList(),
            key = { it.first.uuid }) { map ->
            ServiceItem(map)
        }
    }
}

@Composable
fun ServiceItem(
    characteristics: Pair<Service, List<Characteristic>>,
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        BasicText(text = characteristics.first.uuid)
        Spacer(modifier = Modifier.height(20.dp))
        characteristics.second.forEach {
            CharacteristicItem(characteristic = it)
        }
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



