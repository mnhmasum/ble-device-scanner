package com.mnh.ble.model

import com.mnh.ble.utils.Utility
import com.napco.utils.Constants
import java.util.UUID


data class Characteristic(
    val types: List<Constants.CharType>,
    val uuid: String = "",
) {
    val name: String
        get() = Utility.getCharacteristicPurpose(UUID.fromString(uuid))
}

data class Service(val name: String, val uuid: String)
data class ServiceInfo(val serviceInfo: HashMap<Service, List<Characteristic>>)
