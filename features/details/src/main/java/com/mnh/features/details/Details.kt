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
import com.mnh.ble.model.Characteristic
import com.mnh.ble.model.Service
import com.mnh.ble.model.ServiceInfo
import com.napco.utils.DataState


@Composable
fun Details(
    viewModel: DetailsViewModel,
    connectionResult: DataState<ServiceInfo>,
    deviceAddress: String,
) {

    var serviceInfo: ServiceInfo? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.connect(deviceAddress)
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("Details", "Disposed !! ")
            viewModel.disconnect()
        }
    }

    when (connectionResult) {
        is DataState.Loading -> {
            Loader()
        }

        is DataState.Success -> {
            serviceInfo = connectionResult.data
            ListView(serviceInfo?.serviceInfo)
        }

        is DataState.Error -> {
            Log.d("Details", "Error and Disconnected !! ")
            ListView(serviceInfo?.serviceInfo)
            Text(text = "Disconnected")
        }

    }

}

@Composable
fun ListView(deviceInfo: HashMap<Service, List<Characteristic>>?) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {
        deviceInfo?.forEach { (service, characteristics) ->

            Text(text = "Service: ${service.uuid}")
            Spacer(modifier = Modifier.height(8.dp))

            characteristics.forEach { characteristic ->
                CharacteristicComponent(characteristic)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CharacteristicComponent(characteristic: Characteristic) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        val typeList = characteristic.types.toList()
        val types = typeList.joinToString(", ") { it.toString() }
        Column {
            Text(text = characteristic.uuid)
            Text(text = types)
        }

        if (characteristic.types.isNotEmpty()) {
            Button(onClick = { }) {
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