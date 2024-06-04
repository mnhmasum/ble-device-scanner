package com.mnh.bledevicescanner.core

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {
    data object Home : Screen("Main")

    @Serializable
    data object Details : Screen("details")
    data object DeviceOperation : Screen("DetailsAction")

    @Serializable
    data class ScreenDeviceOperation(
        val deviceName: String?,
        val service: String?,
        val characteristic: String?,
        val properties: List<String>,
    )

}