package com.peripheral.bledevice.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mnh.features.details.Details
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


