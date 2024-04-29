package com.peripheral.bledevice.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    when (result) {
        is DataState.Error -> TODO()
        is DataState.Loading -> TODO()
        is DataState.Success -> {
            result.data.deviceInfo.forEach { s, strings ->
                Log.d("Service", "Service $s")

                strings.distinct().forEach {
                    Log.d("Details", "Characteristics $it")
                }
            }
        }

        null -> {}
        is DataState.Characteristic -> TODO()
        is DataState.Service -> TODO()
    }
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Title1")
        Text(text = "Title2")
    }
}