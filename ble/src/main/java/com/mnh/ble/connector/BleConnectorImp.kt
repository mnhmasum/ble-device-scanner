package com.mnh.ble.connector

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import com.mnh.ble.model.CharacteristicInfo
import com.mnh.ble.model.Device
import com.mnh.ble.model.DeviceInfo
import com.mnh.ble.utils.Utility
import com.napco.utils.Constants
import com.napco.utils.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException


@SuppressLint("MissingPermission")
class BleConnectorImp(private val context: Context) : BleConnector {
    private var lockFullStatus: ByteArray = ByteArray(4)
    private var seqLB: Int = -1
    private var hasEncryptionKey = false
    private lateinit var encryptByte: ByteArray

    private val device1: Device = Device()

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    companion object {
        val TAG: String = BleConnectorImp::class.java.simpleName
    }

    private val _bleGattConnectionResult =
        MutableStateFlow<DataState<DeviceInfo>>(DataState.loading())

    override fun bleGattConnectionResult(): Flow<DataState<DeviceInfo>> =
        _bleGattConnectionResult.asStateFlow()

    override fun connect(address: String) {
        //showLoading()
        val device = getDevice(address)
        device?.connectGatt(context, false, gattCallback)
    }

    private fun showLoading() {
        scope.launch {
            _bleGattConnectionResult.emit(DataState.loading())
        }
    }

    override fun disconnect() {
        scope.launch {
            _bleGattConnectionResult.emit(
                DataState.error(
                    "Disconnected",
                    Throwable("Error: Disconnected ")
                )
            )
        }
    }

    private fun provideBluetoothManager(): BluetoothManager {
        return context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private fun getDevice(address: String): BluetoothDevice? {
        val adapter = provideBluetoothManager().adapter
        return adapter.getRemoteDevice(address)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    gatt.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    disconnect()
                    Log.d(TAG, "Device disconnected")
                }
            }
        }

        override fun onServicesDiscovered(peripheralGatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(peripheralGatt, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {

                Log.d(TAG, "onServicesDiscovered: Enable Notification")

                //enableTxTypeNotification(gatt)

                val peripheralDeviceInfo = getPeripheralInfo(peripheralGatt.services)

                scope.launch {
                    _bleGattConnectionResult.emit(DataState.success(peripheralDeviceInfo))

                }

            }


        }

        fun getPeripheralInfo(services: List<BluetoothGattService>): DeviceInfo {
            val peripheralServices = HashMap<String, List<CharacteristicInfo>>()

            for (service in services) {

                val characteristics = ArrayList<CharacteristicInfo>()

                for (characteristic in service.characteristics) {
                    Log.d(TAG, "getPeripheralInfo char : ${characteristic.uuid.toString()}")
                    val characteristicInfo = getCharacteristicInfo(characteristic)
                    characteristics.add(characteristicInfo)

                }

                peripheralServices[service.uuid.toString()] = characteristics

            }

            return DeviceInfo(peripheralServices)
        }


        fun getCharacteristicInfo(characteristic: BluetoothGattCharacteristic): CharacteristicInfo {
            val types = ArrayList<Constants.CharType>()
            val isReadable = isCharacteristicReadable(characteristic)
            val isNotify = isCharacteristicNotify(characteristic)
            val isWritable = isCharacteristicWritable(characteristic)
            val isWriteNoResponse = isCharacteristicWritableNoResponse(characteristic)

            if (isReadable) {
                types.add(Constants.CharType.READABLE)
            }
            if (isNotify) {
                types.add(Constants.CharType.NOTIFY)
            }
            if (isWritable) {
                types.add(Constants.CharType.WRITABLE)
            }
            if (isWriteNoResponse) {
                types.add(Constants.CharType.WRITABLE_NO_RESPONSE)
            }

            return CharacteristicInfo(types, characteristic.uuid.toString())
        }

        fun isCharacteristicReadable(pChar: BluetoothGattCharacteristic): Boolean {
            return (pChar.properties and BluetoothGattCharacteristic.PROPERTY_READ) != 0
        }

        fun isCharacteristicWritable(pChar: BluetoothGattCharacteristic): Boolean {
            return (pChar.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0
        }

        fun isCharacteristicNotify(pChar: BluetoothGattCharacteristic): Boolean {
            return (pChar.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0
        }

        fun isCharacteristicWritableNoResponse(pChar: BluetoothGattCharacteristic): Boolean {
            return (pChar.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor?,
            status: Int,
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
            gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int,
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(Constants.SERVICE_ALARM_LOCK_DATA)
                val charRxType = service.getCharacteristic(Constants.CHARACTERISTIC_DATA_RX_TYPE)

                if (characteristic == charRxType) {
                    val charRxBuffer =
                        service.getCharacteristic(Constants.CHARACTERISTIC_DATA_RX_BUFFER)
                    writePassword(charRxBuffer, gatt)
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
            characteristic: BluetoothGattCharacteristic?,
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            val characteristicsValue: ByteArray? = characteristic?.value

            Log.d(TAG, "Characteristics Read value2: ${characteristicsValue?.contentToString()}")
            Log.d(TAG, "Characteristics Read value2: ${characteristic?.uuid.toString()}")

            processCharacteristicChangedData(gatt, characteristic)

        }


        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            processCharacteristicChangedData(gatt, characteristic)
        }


        /* override fun onCharacteristicRead(
             gatt: BluetoothGatt,
             characteristic: BluetoothGattCharacteristic,
             value: ByteArray,
             status: Int
         ) {
             super.onCharacteristicRead(gatt, characteristic, value, status)
             //processCharacteristicChangedData(null, null)
             if (status == BluetoothGatt.GATT_SUCCESS) {
                 Log.d(TAG, "Characteristics Read NEW: ${characteristic?.uuid.toString()}")
                 Log.d(TAG, "Characteristics Read Value: $value")
                 Log.d(TAG, "Characteristics Read Status: $status")
             }
         }*/

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

        /*val bt2TxTypeDesc = charTxType.getDescriptor(Constants.DESCRIPTOR_PRE_CLIENT_CONFIG)
        bt2TxTypeDesc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        gatt.writeDescriptor(bt2TxTypeDesc)*/
    }

    fun processCharacteristicChangedData(
        gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?,
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