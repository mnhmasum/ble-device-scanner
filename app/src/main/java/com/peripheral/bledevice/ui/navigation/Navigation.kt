package com.peripheral.bledevice.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mnh.features.details.Details
import com.mnh.features.details.DetailsViewModel
import com.peripheral.bledevice.ui.main.MainActivityViewModel
import com.peripheral.bledevice.ui.main.MainContent

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val mainActivityViewModel: MainActivityViewModel = viewModel()
    val bleDeviceList by mainActivityViewModel.bleDeviceList.collectAsState(initial = null)

    val detailsViewModel: DetailsViewModel = viewModel()
    val gattResult by detailsViewModel.bleGattState.collectAsState(initial = null)

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            MainContent(navController, bleDeviceList)
        }

        composable(
            route = "${Screen.Details.route}/{index}",
            arguments = listOf(navArgument("index") { type = NavType.StringType })
        ) {
            val address = it.arguments?.getString("index") ?: ""
            //val device = Gson().fromJson(index, BluetoothDevice::class.java) ?: return@composable

            gattResult?.let { gattState ->
                Details(
                    viewModel = detailsViewModel,
                    connectionResult = gattState,
                    deviceAddress = address
                )
            }
        }
    }
}


