package com.peripheral.bledevice.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mnh.ble.model.CharacteristicInfo
import com.mnh.ble.model.DeviceInfo
import com.napco.utils.DataState
import com.peripheral.bledevice.ui.main.MainActivityViewModel
import com.peripheral.bledevice.ui.main.MainContent

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val mainActivityViewModel: MainActivityViewModel = viewModel()
    val bleDeviceList by mainActivityViewModel.bleDeviceList.collectAsState(initial = null)
    val gattResult by mainActivityViewModel.bleGattState.collectAsState(initial = null)

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            MainContent(navController, bleDeviceList, mainActivityViewModel)
        }

        composable(route = Screen.Details.route) {
            Details(gattResult)
        }
    }
}

@Composable
fun Details(result: DataState<DeviceInfo>?) {
    var isLoading by remember { mutableStateOf(true) }

    if (isLoading) {
        Loader()
    }

    when (result) {
        is DataState.Error -> TODO()
        is DataState.Loading -> {
            Loader()
        }

        is DataState.Success -> {
            isLoading = false
            DeviceInfoScreen(result.data.deviceInfo)
        }

        null -> {}
        is DataState.Characteristic -> TODO()
        is DataState.Service -> TODO()
    }
}

@Composable
fun DeviceInfoScreen(deviceInfo: HashMap<String, List<CharacteristicInfo>>) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {
        deviceInfo.forEach { (service, characteristics) ->

            Text(text = service)
            Spacer(modifier = Modifier.height(8.dp))

            characteristics.forEach { characteristic ->
                Text(text = "${characteristic.types.toList()}")
                Text(text = characteristic.uuid)
                Spacer(modifier = Modifier.height(4.dp))
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
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        CircularProgressIndicator()
    }
}

