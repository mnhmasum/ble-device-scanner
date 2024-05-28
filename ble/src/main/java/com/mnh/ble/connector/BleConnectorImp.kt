package com.mnh.ble.connector

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.util.Log
import com.mnh.ble.model.Characteristic
import com.mnh.ble.model.Device
import com.mnh.ble.model.Service
import com.mnh.ble.model.ServiceInfo
import com.mnh.ble.utils.Utility
import com.mnh.ble.utils.Utility.Companion.extractCharacteristicInfo
import com.napco.utils.Constants
import com.napco.utils.DataState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException
import java.util.UUID


@SuppressLint("MissingPermission")
class BleConnectorImp(private val context: Context) : BleConnector {
    private var lockFullStatus: ByteArray = ByteArray(4)
    private var seqLB: Int = -1
    private var hasEncryptionKey = false
    private lateinit var encryptByte: ByteArray

    private val device1: Device = Device()

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private var bluetoothGatt: BluetoothGatt? = null

    companion object {
        val TAG: String = BleConnectorImp::class.java.simpleName
    }

    private val _bleGattConnectionResult =
        MutableSharedFlow<DataState<ServiceInfo>>()

    override fun bleGattConnectionResult(): Flow<DataState<ServiceInfo>> =
        _bleGattConnectionResult.asSharedFlow()

    override fun connect(address: String) {
        showLoading()

        val device = getDevice(address)
        device?.connectGatt(context, false, gattCallback)
    }

    private fun showLoading() {
        scope.launch {
            _bleGattConnectionResult.emit(DataState.loading())
        }
    }

    override fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    private fun updateConnectionStatus() {
        scope.launch {
            val throwable = Throwable("Error: Disconnected ")
            _bleGattConnectionResult.emit(DataState.error("Disconnected", throwable))
        }
    }

    override fun enableNotification(serviceUUID: UUID, characteristicUUID: UUID) {
        enableNotificationOrIndication(
            serviceUUID,
            characteristicUUID,
            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        )
    }

    override fun enableIndication(serviceUUID: UUID, characteristicUUID: UUID) {
        enableNotificationOrIndication(
            serviceUUID,
            characteristicUUID,
            BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
        )
    }

    override fun readCharacteristic(serviceUUID: UUID, characteristicUUID: UUID) {
        val bluetoothGattService = bluetoothGatt?.getService(serviceUUID)
        val gattCharacteristic = bluetoothGattService?.getCharacteristic(characteristicUUID)
        bluetoothGatt?.readCharacteristic(gattCharacteristic)
    }

    override fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    ) {
        val bluetoothGattService = bluetoothGatt?.getService(serviceUUID)
        val gattCharacteristic = bluetoothGattService?.getCharacteristic(characteristicUUID)
        writeCharacteristic(gattCharacteristic, bytes, WRITE_TYPE_DEFAULT)
    }

    override fun writeCharacteristicWithNoResponse(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    ) {
        val bluetoothGattService = bluetoothGatt?.getService(serviceUUID)
        val gattCharacteristic = bluetoothGattService?.getCharacteristic(characteristicUUID)
        writeCharacteristic(gattCharacteristic, bytes, WRITE_TYPE_NO_RESPONSE)
    }

    private fun writeCharacteristic(
        gattCharacteristic: BluetoothGattCharacteristic?,
        bytes: ByteArray,
        writeType: Int,
    ) {
        if (Build.VERSION.SDK_INT < 33) {
            bluetoothGatt?.writeCharacteristic(gattCharacteristic)
        } else {
            gattCharacteristic?.let {
                bluetoothGatt?.writeCharacteristic(it, bytes, writeType)
            }
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
                    bluetoothGatt = gatt
                    bluetoothGatt?.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    updateConnectionStatus()
                    Log.d(TAG, "Device disconnected")
                }
            }
        }

        override fun onServicesDiscovered(peripheralGatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(peripheralGatt, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {

                Log.d(TAG, "onServicesDiscovered: Enable Notification")

                //enableTxTypeNotification(gatt)

                scope.launch {
                    val serviceDetails: ServiceInfo = parseServiceDetails(peripheralGatt.services)
                    _bleGattConnectionResult.emit(DataState.success(serviceDetails))
                }
            }
        }

        fun parseServiceDetails(serviceList: List<BluetoothGattService>): ServiceInfo {
            val services = HashMap<Service, List<Characteristic>>()

            for (service in serviceList) {

                val characteristics = ArrayList<Characteristic>()

                for (bleCharacteristic in service.characteristics) {
                    val characteristic = extractCharacteristicInfo(bleCharacteristic)
                    characteristics.add(characteristic)
                    Log.d(TAG, "getPeripheralInfo char : ${bleCharacteristic.uuid}")
                }

                val serviceName = Utility.getServiceName(service.uuid)
                val newService = Service(name = serviceName, uuid = service.getUUID())

                services[newService] = characteristics
            }

            return ServiceInfo(services)
        }

        private fun BluetoothGattService.getUUID(): String {
            return this.uuid.toString()
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor?,
            status: Int,
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                //When descriptor is written successfully
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
            Log.d(
                TAG,
                "Characteristics Read value2: ${Utility.bytesToHexString(characteristic?.value!!)}"
            )

            processCharacteristicChangedData(gatt, characteristic)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            Log.d(TAG, "Characteristics Read value new: ${characteristic.uuid}")
        }


        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int,
        ) {
            super.onCharacteristicRead(gatt, characteristic, value, status)
            //processCharacteristicChangedData(null, null)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristics Read NEW: ${characteristic.uuid.toString()}")
                Log.d(TAG, "Characteristics Read Value: ${Utility.bytesToHexString(value)}")
            }
        }

    }

    fun processCharacteristicChangedData(
        gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?,
    ) {
        val characteristicBytes = characteristic?.value ?: byteArrayOf()

        when (characteristic?.uuid) {
            Constants.CHARACTERISTIC_DATA_TX_TYPE -> handleTxType(characteristicBytes)
            Constants.CHARACTERISTIC_DATA_TX_BUFFER -> handleTxBuffer(characteristicBytes, gatt)
        }
    }

    private fun handleTxType(characteristicBytes: ByteArray) {
        lockFullStatus[0] = characteristicBytes.getOrNull(0) ?: 0
        seqLB = characteristicBytes.getOrNull(1)?.toInt() ?: 0
    }

    private fun handleTxBuffer(characteristicBytes: ByteArray, gatt: BluetoothGatt?) {
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

        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
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

    private fun enableNotificationOrIndication(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        descriptorValue: ByteArray,
    ) {
        val bluetoothGattService = bluetoothGatt?.getService(serviceUUID)
        val gattCharacteristic = bluetoothGattService?.getCharacteristic(characteristicUUID)

        if (bluetoothGattService == null || gattCharacteristic == null) {
            return
        }

        bluetoothGatt?.setCharacteristicNotification(gattCharacteristic, true)

        val descriptor =
            gattCharacteristic.getDescriptor(Constants.DESCRIPTOR_PRE_CLIENT_CONFIG) ?: return

        if (Build.VERSION.SDK_INT < 33) {
            descriptor.value = descriptorValue
            bluetoothGatt?.writeDescriptor(descriptor)
        } else {
            bluetoothGatt?.writeDescriptor(descriptor, descriptorValue)
        }
    }


}