package com.mnh.bledevicescanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mnh.blescanner.devicedetails.DeviceOperationScreen
import com.mnh.blescanner.devicedetails.ServiceDetailsScreen
import com.mnh.blescanner.devicelist.DeviceListScreen
import com.napco.utils.DeviceDetailsScreen
import com.napco.utils.DeviceListScreen
import com.napco.utils.DeviceOperationScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = DeviceListScreen) {
        composable<DeviceListScreen> {
            DeviceListScreen(navController)
        }

        composable<DeviceDetailsScreen> { backStackEntry ->
            val address = backStackEntry.toRoute<DeviceDetailsScreen>().macAddress
            ServiceDetailsScreen(navController, address)
        }

        composable<DeviceOperationScreen> {
            val args: DeviceOperationScreen = it.toRoute<DeviceOperationScreen>()
            DeviceOperationScreen(navController, args)
        }
    }
}
