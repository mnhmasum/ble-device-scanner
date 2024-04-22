package com.peripheral.bledevice.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("Main")
    object Details : Screen("details")

}