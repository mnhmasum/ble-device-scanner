package com.mnh.bledevicescanner.ui.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mnh.blescanner.devicedetails.ServiceDetailsScreen
import com.mnh.blescanner.devicelist.DeviceListScreen
import com.mnh.blescanner.deviceoperation.DeviceOperationScreen
import com.napco.utils.DeviceDetailsScreen
import com.napco.utils.DeviceListScreen
import com.napco.utils.DeviceOperationScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    BackHandler(navController)
    NavHost(navController = navController, startDestination = DeviceListScreen) {
        composable<DeviceListScreen> {
            DeviceListScreen(navController)
        }

        composable<DeviceDetailsScreen> { backStackEntry ->
            val deviceName = backStackEntry.toRoute<DeviceDetailsScreen>().deviceName
            val deviceAddress = backStackEntry.toRoute<DeviceDetailsScreen>().macAddress
            ServiceDetailsScreen(navController, deviceName, deviceAddress)
        }

        composable<DeviceOperationScreen> {
            val args: DeviceOperationScreen = it.toRoute<DeviceOperationScreen>()
            DeviceOperationScreen(navController, args)
        }
    }
}

@Composable
fun BackHandler(navController: NavController) {
    val context = LocalContext.current
    val activity = (context as? Activity)

    androidx.activity.compose.BackHandler {
        if (!navController.popBackStack()) {
            activity?.finish()
        }
    }
}
