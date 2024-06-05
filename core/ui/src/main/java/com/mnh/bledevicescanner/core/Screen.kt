package com.mnh.bledevicescanner.core

import kotlinx.serialization.Serializable

sealed class Screen() {
    @Serializable
    object DeviceList

    @Serializable
    data class DeviceDetails(val macAddress: String)

    @Serializable
    data class DeviceOperation(
        val deviceName: String?,
        val service: String?,
        val characteristic: String?,
        val properties: List<String>,
    )

}