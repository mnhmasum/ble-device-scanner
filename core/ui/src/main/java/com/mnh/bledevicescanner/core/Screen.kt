package com.mnh.bledevicescanner.core

sealed class Screen(val route: String) {
    data object Home : Screen("Main")
    data object Details : Screen("details")
    data object DeviceOperation : Screen("DetailsAction")

}