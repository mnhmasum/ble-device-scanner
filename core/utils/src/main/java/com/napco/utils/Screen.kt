package com.napco.utils

import kotlinx.serialization.Serializable

@Serializable
object DeviceListScreen

@Serializable
data class DeviceDetailsScreen(val deviceName: String, val macAddress: String)

@Serializable
data class DeviceOperationScreen(
    val deviceMacAddress: String,
    val serviceUUID: String,
    val characteristicName: String,
    val characteristicUUID: String,
    val properties: List<String>,
)
