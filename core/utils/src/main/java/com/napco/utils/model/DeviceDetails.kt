package com.napco.utils.model

import com.napco.utils.Utility
import java.util.UUID

data class Characteristic(
    val uuid: String = "",
    val properties: List<String>,
    val acceptedPropertyList: String = properties.joinToString(", "),
) {
    val name: String
        get() = Utility.getCharacteristicPurpose(UUID.fromString(uuid))
}
data class Service(val name: String, val uuid: String)

data class BleDevice(
    val name: String,
    val macAddress: String,
    val services: Map<Service, List<Characteristic>>,
)
