package com.mnh.ble.model

import com.napco.utils.Constants


data class Characteristic(
    val types: List<Constants.CharType>,
    val uuid: String,
    val name: String = "",
)

data class Service(val name: String, val uuid: String)
data class ServiceInfo(val serviceInfo: HashMap<Service, List<Characteristic>>)
