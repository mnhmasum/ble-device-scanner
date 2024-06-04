package com.mnh.bledevicescanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.mnh.bledevicescanner.core.Screen
import com.mnh.blescanner.devicedetails.DeviceOperationScreen
import com.mnh.blescanner.devicedetails.ServiceDetailsScreen
import com.mnh.blescanner.devicelist.DeviceListScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            DeviceListScreen(navController)
        }

        composable(
            route = "${Screen.Details.route}/{index}",
            arguments = listOf(navArgument("index") { type = NavType.StringType })
        ) { backStackEntry ->
            val deviceAddress = backStackEntry.arguments?.getString("index") ?: ""
            ServiceDetailsScreen(navController, deviceAddress)
        }

        composable<Screen.ScreenDeviceOperation> {
            val args: Screen.ScreenDeviceOperation = it.toRoute<Screen.ScreenDeviceOperation>()
            DeviceOperationScreen(navController, args)
        }
    }
}



