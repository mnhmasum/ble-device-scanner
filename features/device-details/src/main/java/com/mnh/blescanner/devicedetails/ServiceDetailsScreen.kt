package com.mnh.blescanner.devicedetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.napco.utils.DataState
import com.napco.utils.DeviceOperationScreen
import com.napco.utils.model.Characteristic
import com.napco.utils.model.DeviceDetails
import com.napco.utils.model.Service


@Composable
fun ServiceDetailsScreen(navController: NavController, deviceName: String, deviceAddress: String) {
    val isAlreadyConnected = rememberSaveable { mutableStateOf(false) }

    val detailsViewModel: DetailsViewModel = hiltViewModel()

    val connectionResult by detailsViewModel.bleConnectionResult.collectAsStateWithLifecycle(
        initialValue = DataState.Loading()
    )

    LaunchedEffect(deviceAddress) {
        detailsViewModel.connect(deviceAddress)
        isAlreadyConnected.value = true

    }

    DisposableEffect(Unit) {
        onDispose {
            //detailsViewModel.disconnect()
        }
    }

    BackHandler {
        navController.navigateUp()
    }

    Scaffold(topBar = {
        TopBar(deviceName = deviceName, onNavigationIconClick = { navController.navigateUp() })
    }) { paddingValues ->

        val contentPadding = Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = paddingValues.calculateTopPadding(),
            bottom = paddingValues.calculateBottomPadding()
        )

        Box(modifier = contentPadding) {
            ServiceDetails(navController, connectionResult, onClickReconnect = {
                detailsViewModel.connect(deviceAddress)
            })
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(deviceName: String, onNavigationIconClick: () -> Unit) {
    TopAppBar(title = { Text(deviceName) }, navigationIcon = {
        IconButton(onClick = onNavigationIconClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
    })
}

@Composable
private fun ServiceDetails(
    navController: NavController,
    connectionResult: DataState<DeviceDetails>,
    onClickReconnect: () -> Unit,
) {
    when (connectionResult) {
        is DataState.Loading -> Loader()
        is DataState.Success -> DeviceDetailsContent(navController, connectionResult.data)
        is DataState.Error -> DisconnectedMessage(onClickReconnect)
    }
}

@Composable
fun DeviceDetailsContent(navController: NavController, deviceDetails: DeviceDetails) {
    val services = deviceDetails.services.toList()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        items(services, key = { it.first.uuid }) { service ->
            ServiceItem(service, onNavigateCharacteristic = { characteristic ->
                navController.navigate(
                    DeviceOperationScreen(
                        deviceAddress = deviceDetails.deviceInfo.address,
                        serviceUUID = service.first.uuid,
                        characteristicName = characteristic.name,
                        characteristicUUID = characteristic.uuid,
                        properties = characteristic.properties
                    )
                )
            })
        }
    }
}

@Composable
fun ServiceItem(
    service: Pair<Service, List<Characteristic>>,
    onNavigateCharacteristic: (Characteristic) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        BasicText(
            text = service.first.name,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
        )
        Divider(color = Color.Gray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))

        service.second.forEach { characteristic ->
            CharacteristicItem(characteristic = characteristic) {
                onNavigateCharacteristic(characteristic)
            }
        }
    }
}

@Composable
private fun CharacteristicItem(
    characteristic: Characteristic,
    onSelectCharacteristic: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelectCharacteristic() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(3f)) {
            Text(text = characteristic.name)
            Text(
                text = characteristic.acceptedPropertyList, style = TextStyle(fontSize = 13.sp)
            )
        }
        if (characteristic.properties.isNotEmpty()) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )

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

@Composable
fun DisconnectedMessage(onClickReconnect: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Disconnected", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center
        )
        IconButton(onClick = onClickReconnect) {
            Icon(Icons.Default.Refresh, contentDescription = "Reconnect")
        }

    }

}

