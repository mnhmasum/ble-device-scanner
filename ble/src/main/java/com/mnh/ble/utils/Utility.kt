package com.mnh.ble.utils

import android.bluetooth.BluetoothGattCharacteristic
import com.mnh.ble.model.Characteristic
import com.napco.utils.Constants
import java.util.Formatter
import java.util.UUID


class Utility {

    companion object {
        fun bytesToHexString(bytes: ByteArray): String {
            val sb = java.lang.StringBuilder(bytes.size * 2)
            val formatter = Formatter(sb)
            for (b in bytes) formatter.format("%02x ", b)
            return sb.toString()
        }

        fun getServiceName(serviceUUID: UUID): String {
            return when (serviceUUID) {
                UUID.fromString("00001800-0000-1000-8000-00805F9B34FB") -> "GENERIC ACCESS"
                UUID.fromString("00001801-0000-1000-8000-00805F9B34FB") -> "GENERIC ATTRIBUTE"
                UUID.fromString("00001802-0000-1000-8000-00805F9B34FB") -> "IMMEDIATE ALERT"
                UUID.fromString("00001803-0000-1000-8000-00805F9B34FB") -> "LINK LOSS"
                UUID.fromString("00001804-0000-1000-8000-00805F9B34FB") -> "TX POWER"
                UUID.fromString("00001805-0000-1000-8000-00805F9B34FB") -> "CURRENT TIME SERVICE"
                UUID.fromString("00001806-0000-1000-8000-00805F9B34FB") -> "REFERENCE TIME UPDATE SERVICE"
                UUID.fromString("00001807-0000-1000-8000-00805F9B34FB") -> "NEXT DST CHANGE SERVICE"
                UUID.fromString("00001808-0000-1000-8000-00805F9B34FB") -> "GLUCOSE"
                UUID.fromString("00001809-0000-1000-8000-00805F9B34FB") -> "HEALTH THERMOMETER"
                UUID.fromString("0000180A-0000-1000-8000-00805F9B34FB") -> "DEVICE INFORMATION"
                UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB") -> "HEART RATE"
                UUID.fromString("0000180E-0000-1000-8000-00805F9B34FB") -> "PHONE ALERT STATUS SERVICE"
                UUID.fromString("0000180F-0000-1000-8000-00805F9B34FB") -> "BATTERY SERVICE"
                UUID.fromString("00001810-0000-1000-8000-00805F9B34FB") -> "BLOOD PRESSURE"
                UUID.fromString("00001811-0000-1000-8000-00805F9B34FB") -> "ALERT NOTIFICATION SERVICE"
                UUID.fromString("00001812-0000-1000-8000-00805F9B34FB") -> "HUMAN INTERFACE DEVICE"
                UUID.fromString("00001813-0000-1000-8000-00805F9B34FB") -> "SCAN PARAMETERS"
                UUID.fromString("00001814-0000-1000-8000-00805F9B34FB") -> "RUNNING SPEED AND CADENCE"
                UUID.fromString("00001816-0000-1000-8000-00805F9B34FB") -> "CYCLING SPEED AND CADENCE"
                UUID.fromString("00001818-0000-1000-8000-00805F9B34FB") -> "CYCLING POWER"
                UUID.fromString("00001819-0000-1000-8000-00805F9B34FB") -> "LOCATION AND NAVIGATION"
                UUID.fromString("0000181A-0000-1000-8000-00805F9B34FB") -> "ENVIRONMENTAL SENSING"
                UUID.fromString("0000181B-0000-1000-8000-00805F9B34FB") -> "BODY COMPOSITION"
                UUID.fromString("0000181C-0000-1000-8000-00805F9B34FB") -> "USER DATA"
                UUID.fromString("0000181D-0000-1000-8000-00805F9B34FB") -> "WEIGHT SCALE"
                UUID.fromString("0000181E-0000-1000-8000-00805F9B34FB") -> "BOND MANAGEMENT SERVICE"
                UUID.fromString("0000181F-0000-1000-8000-00805F9B34FB") -> "CONTINUOUS GLUCOSE MONITORING"
                UUID.fromString("00001820-0000-1000-8000-00805F9B34FB") -> "INTERNET PROTOCOL SUPPORT SERVICE"
                UUID.fromString("00001821-0000-1000-8000-00805F9B34FB") -> "INDOOR POSITIONING"
                UUID.fromString("00001822-0000-1000-8000-00805F9B34FB") -> "PULSE OXIMETER SERVICE"
                UUID.fromString("00001823-0000-1000-8000-00805F9B34FB") -> "HTTP PROXY"
                UUID.fromString("00001824-0000-1000-8000-00805F9B34FB") -> "TRANSPORT DISCOVERY"
                UUID.fromString("00001825-0000-1000-8000-00805F9B34FB") -> "OBJECT TRANSFER SERVICE"
                UUID.fromString("00001826-0000-1000-8000-00805F9B34FB") -> "FITNESS MACHINE"
                UUID.fromString("00001827-0000-1000-8000-00805F9B34FB") -> "MESH PROVISIONING SERVICE"
                UUID.fromString("00001828-0000-1000-8000-00805F9B34FB") -> "MESH PROXY SERVICE"
                UUID.fromString("00001829-0000-1000-8000-00805F9B34FB") -> "RECONNECTION CONFIGURATION"
                UUID.fromString("0000183A-0000-1000-8000-00805F9B34FB") -> "INSULIN DELIVERY"

                else -> serviceUUID.toString()
            }
        }

        fun getCharacteristicPurpose(characteristicUuid: UUID): String {
            return when (characteristicUuid) {
                // Device Information Service
                UUID.fromString("00002A00-0000-1000-8000-00805F9B34FB") -> "Device Name"
                UUID.fromString("00002A01-0000-1000-8000-00805F9B34FB") -> "Appearance"
                UUID.fromString("00002A02-0000-1000-8000-00805F9B34FB") -> "Peripheral Privacy Flag"
                UUID.fromString("00002A03-0000-1000-8000-00805F9B34FB") -> "Reconnection Address"
                UUID.fromString("00002A04-0000-1000-8000-00805F9B34FB") -> "Peripheral Preferred Connection Parameters"
                UUID.fromString("00002A05-0000-1000-8000-00805F9B34FB") -> "Service Changed"
                UUID.fromString("00002A06-0000-1000-8000-00805F9B34FB") -> "Alert Level"
                UUID.fromString("00002A07-0000-1000-8000-00805F9B34FB") -> "Tx Power Level"
                UUID.fromString("00002A08-0000-1000-8000-00805F9B34FB") -> "Date Time"
                UUID.fromString("00002A09-0000-1000-8000-00805F9B34FB") -> "Day Of Week"
                UUID.fromString("00002A0A-0000-1000-8000-00805F9B34FB") -> "Day Date Time"
                UUID.fromString("00002A0C-0000-1000-8000-00805F9B34FB") -> "Exact Time 256"
                UUID.fromString("00002A0D-0000-1000-8000-00805F9B34FB") -> "Dst Offset"
                UUID.fromString("00002A0E-0000-1000-8000-00805F9B34FB") -> "Time Zone"
                UUID.fromString("00002A0F-0000-1000-8000-00805F9B34FB") -> "Local Time Information"
                UUID.fromString("00002A11-0000-1000-8000-00805F9B34FB") -> "Time With Dst"
                UUID.fromString("00002A12-0000-1000-8000-00805F9B34FB") -> "Time Accuracy"
                UUID.fromString("00002A13-0000-1000-8000-00805F9B34FB") -> "Time Source"
                UUID.fromString("00002A14-0000-1000-8000-00805F9B34FB") -> "Reference Time Information"
                UUID.fromString("00002A16-0000-1000-8000-00805F9B34FB") -> "Time Update Control Point"
                UUID.fromString("00002A17-0000-1000-8000-00805F9B34FB") -> "Time Update State"
                UUID.fromString("00002A18-0000-1000-8000-00805F9B34FB") -> "Glucose Measurement"
                UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB") -> "Battery Level"
                UUID.fromString("00002A1C-0000-1000-8000-00805F9B34FB") -> "Temperature Measurement"
                UUID.fromString("00002A1D-0000-1000-8000-00805F9B34FB") -> "Temperature Type"
                UUID.fromString("00002A1E-0000-1000-8000-00805F9B34FB") -> "Intermediate Temperature"
                UUID.fromString("00002A21-0000-1000-8000-00805F9B34FB") -> "Measurement Interval"
                UUID.fromString("00002A22-0000-1000-8000-00805F9B34FB") -> "Boot Keyboard Input Report"
                UUID.fromString("00002A23-0000-1000-8000-00805F9B34FB") -> "System Id"
                UUID.fromString("00002A24-0000-1000-8000-00805F9B34FB") -> "Model Number String"
                UUID.fromString("00002A25-0000-1000-8000-00805F9B34FB") -> "Serial Number String"
                UUID.fromString("00002A26-0000-1000-8000-00805F9B34FB") -> "Firmware Revision String"
                UUID.fromString("00002A27-0000-1000-8000-00805F9B34FB") -> "Hardware Revision String"
                UUID.fromString("00002A28-0000-1000-8000-00805F9B34FB") -> "Software Revision String"
                UUID.fromString("00002A29-0000-1000-8000-00805F9B34FB") -> "Manufacturer Name String"
                UUID.fromString("00002A2A-0000-1000-8000-00805F9B34FB") -> "Ieee 11073-20601 Regulatory Certification Data List"
                UUID.fromString("00002A2B-0000-1000-8000-00805F9B34FB") -> "Current Time"
                UUID.fromString("00002A2C-0000-1000-8000-00805F9B34FB") -> "Magnetic Declination"
                UUID.fromString("00002A31-0000-1000-8000-00805F9B34FB") -> "Scan Refresh"
                UUID.fromString("00002A32-0000-1000-8000-00805F9B34FB") -> "Boot Keyboard Output Report"
                UUID.fromString("00002A33-0000-1000-8000-00805F9B34FB") -> "Boot Mouse Input Report"
                UUID.fromString("00002A34-0000-1000-8000-00805F9B34FB") -> "Glucose Measurement Context"
                UUID.fromString("00002A35-0000-1000-8000-00805F9B34FB") -> "Blood Pressure Measurement"
                UUID.fromString("00002A36-0000-1000-8000-00805F9B34FB") -> "Intermediate Cuff Pressure"
                UUID.fromString("00002A37-0000-1000-8000-00805F9B34FB") -> "Heart Rate Measurement"
                UUID.fromString("00002A38-0000-1000-8000-00805F9B34FB") -> "Body Sensor Location"
                UUID.fromString("00002A39-0000-1000-8000-00805F9B34FB") -> "Heart Rate Control Point"
                UUID.fromString("00002A3F-0000-1000-8000-00805F9B34FB") -> "Alert Status"
                UUID.fromString("00002A40-0000-1000-8000-00805F9B34FB") -> "Ringer Control Point"
                UUID.fromString("00002A41-0000-1000-8000-00805F9B34FB") -> "Ringer Setting"
                UUID.fromString("00002A42-0000-1000-8000-00805F9B34FB") -> "Alert Category Id Bit Mask"
                UUID.fromString("00002A43-0000-1000-8000-00805F9B34FB") -> "Alert Category Id"
                UUID.fromString("00002A44-0000-1000-8000-00805F9B34FB") -> "Alert Notification Control Point"
                UUID.fromString("00002A45-0000-1000-8000-00805F9B34FB") -> "Unread Alert Status"
                UUID.fromString("00002A46-0000-1000-8000-00805F9B34FB") -> "New Alert"
                UUID.fromString("00002A47-0000-1000-8000-00805F9B34FB") -> "Supported New Alert Category"
                UUID.fromString("00002A48-0000-1000-8000-00805F9B34FB") -> "Supported Unread Alert Category"
                UUID.fromString("00002A49-0000-1000-8000-00805F9B34FB") -> "Blood Pressure Feature"
                UUID.fromString("00002A4A-0000-1000-8000-00805F9B34FB") -> "Hid Information"
                UUID.fromString("00002A4B-0000-1000-8000-00805F9B34FB") -> "Report Map"
                else -> characteristicUuid.toString()
            }
        }

        fun extractCharacteristicInfo(characteristic: BluetoothGattCharacteristic): Characteristic {
            val properties = mutableListOf<String>()

            if (BluetoothGattCharacteristic.PROPERTY_READ.isPresent(characteristic)) {
                properties.add(Constants.CharType.READABLE.type)
            }

            if (BluetoothGattCharacteristic.PROPERTY_NOTIFY.isPresent(characteristic)) {
                properties.add(Constants.CharType.NOTIFY.type)
            }

            if (BluetoothGattCharacteristic.PROPERTY_WRITE.isPresent(characteristic)) {
                properties.add(Constants.CharType.WRITABLE.type)
            }

            if (BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE.isPresent(characteristic)) {
                properties.add(Constants.CharType.WRITABLE_NO_RESPONSE.type)
            }

            if (BluetoothGattCharacteristic.PROPERTY_INDICATE.isPresent(characteristic)) {
                properties.add(Constants.CharType.INDICATION.type)
            }

            return Characteristic(characteristic.uuid.toString(), properties)
        }

        private fun Int.isPresent(characteristic: BluetoothGattCharacteristic): Boolean {
            return (characteristic.properties and this) != 0
        }
    }
}