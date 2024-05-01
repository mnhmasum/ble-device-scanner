package com.mnh.ble.model

import com.napco.utils.Constants


data class CharacteristicInfo(val types: List<Constants.CharType>, val uuid: String)
data class DeviceInfo(val deviceInfo: HashMap<String, List<CharacteristicInfo>>)
