package com.mnh.bledevicescanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mnh.bledevicescanner.core.Screen
import com.mnh.features.details.Details
import com.mnh.features.details.DetailsViewModel
import com.mnh.features.home.HomeViewModel
import com.mnh.features.home.MainContent

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()
    val detailsViewModel: DetailsViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(route = Screen.Home.route) {
            MainContent(navController, homeViewModel)
        }

        composable(
            route = "${Screen.Details.route}/{index}",
            arguments = listOf(navArgument("index") { type = NavType.StringType })
        ) { backStackEntry ->
            val deviceAddress = backStackEntry.arguments?.getString("index") ?: ""
            Details(
                detailsViewModel = detailsViewModel,
                deviceAddress = deviceAddress
            )
        }
    }
}


