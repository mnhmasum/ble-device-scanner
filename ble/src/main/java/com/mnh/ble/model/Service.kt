package com.mnh.ble.model


open class Gatt(open val uuid: String)

data class Service(override val uuid: String) : Gatt(uuid) {
}

data class Characteristic(override var uuid: String) : Gatt(uuid)
