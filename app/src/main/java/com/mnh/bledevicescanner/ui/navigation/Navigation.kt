package com.mnh.bledevicescanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mnh.bledevicescanner.core.Screen
import com.mnh.blescanner.devicedetails.DeviceOperationScreen
import com.mnh.blescanner.devicedetails.ServiceDetailsScreen
import com.mnh.blescanner.devicelist.DeviceListScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.DeviceList) {
        composable<Screen.DeviceList> {
            DeviceListScreen(navController)
        }

        composable<Screen.DeviceDetails> { backStackEntry ->
            val address = backStackEntry.toRoute<Screen.DeviceDetails>()
            ServiceDetailsScreen(navController, address.macAddress)
        }

        composable<Screen.DeviceOperation> {
            val args: Screen.DeviceOperation = it.toRoute<Screen.DeviceOperation>()
            DeviceOperationScreen(navController, args)
        }
    }
}



