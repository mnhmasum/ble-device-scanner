package com.mnh.bledevicescanner.core

sealed class Screen(val route: String) {
    object Home : Screen("Main")
    object Details : Screen("details")

}