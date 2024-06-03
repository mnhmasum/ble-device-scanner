package com.mnhblescanner.servicedetails

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.mnh.bledevicescanner.core.Screen
import com.napco.utils.DataState

@Composable
fun ServiceDetailsScreen(navController: NavController, deviceAddress: String) {
    val effectTriggered = rememberSaveable { mutableStateOf(false) }

    val detailsViewModel: DetailsViewModel = hiltViewModel()
    val connectionResult by detailsViewModel.bleConnectionResult.collectAsStateWithLifecycle(
        DataState.Loading()
    )

    LaunchedEffect(deviceAddress) {
        if (!effectTriggered.value) {
            detailsViewModel.connect(deviceAddress)
            effectTriggered.value = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (effectTriggered.value) {
                detailsViewModel.disconnect()
            }
        }
    }

    BackHandler {
        navController.navigateUp()
    }

    ServiceDetails(navController, connectionResult)

}

@Composable
private fun ServiceDetails(
    navController: NavController,
    connectionResult: DataState<ServiceInfo>,
) {
    var serviceInfo: ServiceInfo? by remember { mutableStateOf(null) }

    when (connectionResult) {
        is DataState.Loading -> Loader()
        is DataState.Success -> {
            serviceInfo = connectionResult.data
        }

        is DataState.Error -> {
            Text(
                text = "Disconnected",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }

    serviceInfo?.let { serviceDetails ->
        DeviceDetailsContent(navController, serviceInfo = { serviceDetails })
    }
}

@Composable
fun DeviceDetailsContent(navController: NavController, serviceInfo: () -> ServiceInfo) {
    Log.d("Details", "DeviceInfo: ")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.Start
    ) {
        items(serviceInfo().serviceInfo.toList(), key = { it.first.uuid }) { service ->
            ServiceItem(service, navController)
        }
    }
}

@Composable
fun ServiceItem(
    service: Pair<Service, List<Characteristic>>,
    navController: NavController,
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        BasicText(
            text = service.first.name,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )
        Divider(color = Color.Gray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))

        service.second.forEach { characteristic ->
            CharacteristicItem(
                characteristic = characteristic,
                onClickCharacteristic = {
                    val deviceOperationScreen =
                        "${Screen.DeviceOperation.route}/${service.first.uuid}/${characteristic.uuid}"
                    navController.navigate(deviceOperationScreen)
                })
        }
    }
}

@Composable
private fun CharacteristicItem(characteristic: Characteristic, onClickCharacteristic: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = characteristic.name)
            Text(
                text = characteristic.acceptedPropertyList,
                style = TextStyle(fontSize = 13.sp)
            )
        }
        if (characteristic.properties.isNotEmpty()) {
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onClickCharacteristic) {
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
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}



