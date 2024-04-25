package com.mnh.ble.connector

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import com.mnh.ble.model.Device
import com.mnh.ble.utils.Utility
import com.napco.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.UnsupportedEncodingException

@SuppressLint("MissingPermission")
class BleConnectorImp(private val context: Context) : BleConnector {
    private var lockFullStatus: ByteArray = ByteArray(4)
    private var seqLB: Int = -1
    private var descWriteCount = 0
    private var hasEncryptionKey = false
    private lateinit var encryptByte: ByteArray

    private val device1: Device = Device()


    private val _bleGattConnectionResult = MutableStateFlow<String?>("")


    companion object {
        val TAG: String = BleConnectorImp::class.java.simpleName
    }

    override fun connect(device: BluetoothDevice) {
        device.connectGatt(context, false, gattCallback)
    }

    override fun bleGattConnectionResult(): Flow<String?> = _bleGattConnectionResult


    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    gatt.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Device disconnected")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onServicesDiscovered: Enable Notification")
                //enableTxTypeNotification(gatt)

                val services = gatt.services

                for (service in services) {
                    Log.d(TAG, "Service: ${service.uuid}")
                    Log.d(TAG, "------------------------- ")
                    for (characteristic in service.characteristics) {
                        Log.d(TAG, "Characteristic: ${characteristic.uuid}")
                    }
                    Log.d(TAG, "============================= ")
                }

            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor?, status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            val string = Utility.bytesToHexString(descriptor?.value!!)
            Log.d(TAG, "onDescriptorWrite: $string")
            Log.d(TAG, "onDescriptorWrite: Status $status")

            if (status == BluetoothGatt.GATT_SUCCESS) {

                /*descWriteCount++
                when (descWriteCount) {
                    1 -> enableTxBufferNotification(gatt)
                    2 -> enableRxCtrlNotification(gatt)
                }*/

            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(Constants.SERVICE_ALARM_LOCK_DATA)
                val charRxType =
                    service.getCharacteristic(Constants.CHARACTERISTIC_DATA_RX_TYPE)

                if (characteristic == charRxType) {
                    val charRxBuffer =
                        service.getCharacteristic(Constants.CHARACTERISTIC_DATA_RX_BUFFER)
                    //writePassword(charRxBuffer, gatt)
                }
            }
        }

        @Suppress("DEPRECATION")
        @Deprecated(
            "Used natively in Android 12 and lower",
            ReplaceWith("onCharacteristicChanged(gatt, characteristic, characteristic.value)")
        )
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            val characteristicsValue: ByteArray? = characteristic?.value

            Log.d(TAG, "Characteristics Read value2: ${characteristicsValue?.contentToString()}")
            Log.d(TAG, "Characteristics Read value2: ${characteristic?.uuid.toString()}")

            //processCharacteristicChangedData(gatt, characteristic)

        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            //processCharacteristicChangedData(gatt, characteristic)
        }

    }

    private fun enableRxCtrlNotification(gatt: BluetoothGatt) {
        val service = gatt.getService(Constants.SERVICE_ALARM_LOCK_DATA)
        val charRxCtrl = service.getCharacteristic(Constants.CHARACTERISTIC_DATA_RX_CTRL)
        gatt.setCharacteristicNotification(charRxCtrl, true)
        val bt2RxCtrlDesc = charRxCtrl.getDescriptor(Constants.DESCRIPTOR_PRE_CLIENT_CONFIG)
        bt2RxCtrlDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        gatt.writeDescriptor(bt2RxCtrlDesc)
    }

    private fun enableTxBufferNotification(gatt: BluetoothGatt) {
        val service = gatt.getService(Constants.SERVICE_ALARM_LOCK_DATA)
        val charTxBuffer = service.getCharacteristic(Constants.CHARACTERISTIC_DATA_TX_BUFFER)
        gatt.setCharacteristicNotification(charTxBuffer, true)

        val bt2TxBufferDesc = charTxBuffer.getDescriptor(Constants.DESCRIPTOR_PRE_CLIENT_CONFIG);
        bt2TxBufferDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(bt2TxBufferDesc)
    }

    private fun enableTxTypeNotification(gatt: BluetoothGatt) {
        val service = gatt.getService(Constants.SERVICE_ALARM_LOCK_DATA)
        val charTxType = service.getCharacteristic(Constants.CHARACTERISTIC_DATA_TX_TYPE)
        gatt.setCharacteristicNotification(charTxType, true)

        val bt2TxTypeDesc = charTxType.getDescriptor(Constants.DESCRIPTOR_PRE_CLIENT_CONFIG)
        bt2TxTypeDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        gatt.writeDescriptor(bt2TxTypeDesc)
    }

    fun processCharacteristicChangedData(
        gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?
    ) {
        val characteristicBytes = characteristic?.value ?: byteArrayOf()

        when (characteristic?.uuid) {
            Constants.CHARACTERISTIC_DATA_RX_CTRL -> { /*Handle CHARACTERISTIC_DATA_RX_CTRL*/
            }

            Constants.CHARACTERISTIC_DATA_TX_TYPE -> handleTxType(characteristicBytes)
            Constants.CHARACTERISTIC_DATA_TX_BUFFER -> handleTxBuffer(characteristicBytes, gatt)
        }
    }


    private fun handleTxType(characteristicBytes: ByteArray) {
        writeLog("TxType:", characteristicBytes)
        lockFullStatus[0] = characteristicBytes.getOrNull(0) ?: 0
        seqLB = characteristicBytes.getOrNull(1)?.toInt() ?: 0
    }

    private fun handleTxBuffer(characteristicBytes: ByteArray, gatt: BluetoothGatt?) {
        writeLog("TxBuffer:", characteristicBytes)
        lockFullStatus[1] = characteristicBytes.getOrNull(0) ?: 0
        lockFullStatus[2] = characteristicBytes.getOrNull(1) ?: 0

        if (characteristicBytes.contentEquals(byteArrayOf(0x30, 0x34))) {
            // Unlock Success
            return
        }

        if (lockFullStatus[0].toInt() == 0x15) {
            encryptByte = characteristicBytes
            hasEncryptionKey = true
        }

        if (hasEncryptionKey) {
            writeRxType(gatt)
        }
    }


    private fun writePassword(charRxBuffer: BluetoothGattCharacteristic, gatt: BluetoothGatt) {
        try {
            val devicePassword = device1.lockPassword
            val passData = Utility.passData(devicePassword)
            val password = Utility.encryptData(passData, encryptByte)

            if (password.size <= 20) {
                charRxBuffer.setValue(password)
                gatt.writeCharacteristic(charRxBuffer)
            }

            writeLog("Password", password)

        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    private fun writeLog(message: String, characteristicBytes: ByteArray) {
        val logMessage = "$message: ${Utility.bytesToHexString(characteristicBytes)}"
        Log.d(TAG, logMessage)
        Utility.logToFile(context, logMessage)
    }

    private fun writeRxType(gatt: BluetoothGatt?) {
        val isLockStatus = lockFullStatus[0] == Constants.LOCK_STATUS
        val isLockReady = lockFullStatus[1] == Constants.LOCK_READY
        val isLockLocked = lockFullStatus[2] == Constants.LOCKED

        if (isLockStatus && isLockReady && isLockLocked) {
            lockFullStatus = byteArrayOf(0, 0, 0, 0)
            val writeRxType = byteArrayOf(Constants.PASSWORD, (seqLB + 1).toByte())
            val service = gatt?.getService(Constants.SERVICE_ALARM_LOCK_DATA)
            val charRxType = service?.getCharacteristic(Constants.CHARACTERISTIC_DATA_RX_TYPE)
            charRxType?.value = writeRxType
            gatt?.writeCharacteristic(charRxType)
        }
    }

    private fun showLogCat(device: BluetoothDevice, result: ScanResult) {
        Log.d(TAG, "Device found: ${device.name}, Address: ${device.address}")
        Log.d(TAG, "Device RSS: ${result.rssi}")
    }

}