package com.mnh.ble.model

import com.mnh.ble.utils.Utility
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
data class ServiceInfo(val serviceInfo: Map<Service, List<Characteristic>>)
