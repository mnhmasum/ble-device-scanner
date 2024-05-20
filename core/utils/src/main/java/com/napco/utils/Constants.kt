package com.napco.utils

import java.util.UUID

class Constants {

    enum class CharType {
        READABLE,
        WRITABLE,
        NOTIFY,
        WRITABLE_NO_RESPONSE,
        INDICATION
    }

    companion object {
        val LOCK_READY = 0x30.toByte()
        val LOCKED = 0x33.toByte()
        val PASSWORD = 0x16.toByte()
        val LOCK_STATUS = 0x12.toByte()

        val SERVICE_ALARM_LOCK_DATA = UUID.fromString("")
        val CHARACTERISTIC_DATA_RX_TYPE: UUID = UUID.fromString("")
        val CHARACTERISTIC_DATA_RX_BUFFER: UUID = UUID.fromString("")
        val CHARACTERISTIC_DATA_RX_CTRL: UUID = UUID.fromString("")
        val CHARACTERISTIC_DATA_TX_TYPE: UUID = UUID.fromString("")
        val CHARACTERISTIC_DATA_TX_BUFFER: UUID = UUID.fromString("")
        val CHARACTERISTIC_DATA_TX_CTRL: UUID = UUID.fromString("")
        val DESCRIPTOR_PRE_CLIENT_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB")

        const val NOTIFICATION_CHANNEL_ID = "BlE_DEVICE_SCANNING_NOTIFICATION_CHANNEL"
    }


}