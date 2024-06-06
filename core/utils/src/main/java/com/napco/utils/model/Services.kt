package com.napco.utils.model

import com.napco.utils.Utility
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Characteristic(
    val uuid: String = "",
    val properties: List<String>,
    val acceptedPropertyList: String = properties.joinToString(", "),
) {
    val name: String
        get() = Utility.getCharacteristicPurpose(UUID.fromString(uuid))
}

@Serializable
data class Service(val name: String, val uuid: String)

@Serializable
data class DeviceInfo(
    val name: String,
    val address: String,
    val generalInfo: String,
)

@Serializable
data class DeviceDetails(
    val deviceInfo: DeviceInfo,
    val services: Map<Service, List<Characteristic>>,
)
