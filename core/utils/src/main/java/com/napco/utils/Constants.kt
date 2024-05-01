package com.napco.utils

import java.util.UUID

class Constants {

    enum class CharType {
        READABLE,
        WRITABLE,
        NOTIFY,
        WRITABLE_NO_RESPONSE
    }

    companion object {
        val LOCK_READY = 0x30.toByte()
        val LOCKED = 0x33.toByte()
        val PASSWORD = 0x16.toByte()
        val LOCK_STATUS = 0x12.toByte()
        val SERVICE_ALARM_LOCK_DATA = UUID.fromString("EE88DBB3-5EE0-4ED5-BE48-0F520407EC74")
        val CHARACTERISTIC_DATA_RX_TYPE: UUID =
            UUID.fromString("74102D3D-0300-4E1B-ABB3-2D491AC12833")
        val CHARACTERISTIC_DATA_RX_BUFFER: UUID =
            UUID.fromString("305B0556-78FC-4885-9567-E64C78D06467")
        val CHARACTERISTIC_DATA_RX_CTRL: UUID =
            UUID.fromString("588AB4F2-612C-40B5-8169-83A13B632520")
        val CHARACTERISTIC_DATA_TX_TYPE: UUID =
            UUID.fromString("BA67C742-3E49-4E0F-AD0D-141E0020F2A2")
        val CHARACTERISTIC_DATA_TX_BUFFER: UUID =
            UUID.fromString("A8C993EB-1322-4A8E-9CB1-4501B9AE539B")
        val CHARACTERISTIC_DATA_TX_CTRL: UUID =
            UUID.fromString("BCD6678B-55D0-47BC-AD27-EDB6EC455255")
        val DESCRIPTOR_PRE_CLIENT_CONFIG: UUID =
            UUID.fromString("00002902-0000-1000-8000-00805F9B34FB")

        val channelId = "BluetoothScanServiceChannel"
    }


}