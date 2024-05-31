package com.mnh.bledevicescanner.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mnh.bledevicescanner.core.Screen
import com.mnhblescanner.devicelist.DeviceListScreen
import com.mnhblescanner.servicedetails.DeviceOperationScreen
import com.mnhblescanner.servicedetails.ServiceDetailsScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            Log.d("Navigation", "Main navigate")
            DeviceListScreen(navController)
        }

        composable(
            route = "${Screen.Details.route}/{index}",
            arguments = listOf(navArgument("index") { type = NavType.StringType })
        ) { backStackEntry ->
            Log.d("Navigation", "Details navigate")
            val deviceAddress = backStackEntry.arguments?.getString("index") ?: ""
            ServiceDetailsScreen(navController, deviceAddress)
        }

        composable(
            route = "${Screen.DeviceOperation.route}/{service}/{characteristic}",
            arguments = listOf(
                navArgument("service") { type = NavType.StringType },
                navArgument("characteristic") { type = NavType.StringType })
        ) { backStackEntry ->
            Log.d("Navigation", "Details navigate")
            val service = backStackEntry.arguments?.getString("service") ?: ""
            val characteristic = backStackEntry.arguments?.getString("characteristic") ?: ""
            DeviceOperationScreen(navController, service, characteristic)
        }
    }
}


