package com.mnhblescanner.servicedetails

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mnh.ble.model.Characteristic
import com.mnh.ble.model.Service
import com.mnh.ble.model.ServiceInfo
import com.napco.utils.DataState

@Composable
fun ServiceDetailsScreen(navController: NavController, deviceAddress: String) {
    Log.d("Details", "Details")

    val detailsViewModel: DetailsViewModel = hiltViewModel()

    val connectionResult by detailsViewModel.bleConnectionResult.collectAsStateWithLifecycle(
        DataState.Loading()
    )

    var serviceInfo: ServiceInfo? by remember { mutableStateOf(null) }

    LaunchedEffect(deviceAddress) {
        Log.d("Details", "Launched ")
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
            serviceInfo = (connectionResult as DataState.Success<ServiceInfo>).data
        }

        is DataState.Error -> {
            Text(
                text = "Disconnected",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }

    serviceInfo?.let { DeviceInfo(serviceInfo = it) }

}

@Composable
fun DeviceInfo(serviceInfo: ServiceInfo) {
    Log.d("Details", "DeviceInfo: ")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.Start
    ) {
        items(
            serviceInfo.serviceInfo.toList(),
            key = { it.first.uuid }) { service ->
            ServiceItem(service)
        }
    }
}

@Composable
fun ServiceItem(
    service: Pair<Service, List<Characteristic>>,
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        BasicText(
            text = service.first.name,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
        )
        Divider(color = Color.Gray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))
        service.second.forEach {
            CharacteristicItem(characteristic = it)
        }
    }
}

@Composable
private fun CharacteristicItem(characteristic: Characteristic) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = characteristic.name)
            Text(
                text = characteristic.acceptedPropertyList, style = TextStyle(
                    fontSize = 13.sp
                )
            )
        }
        if (characteristic.properties.isNotEmpty()) {
            Spacer(modifier = Modifier.weight(1f))
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



