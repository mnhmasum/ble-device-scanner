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
import android.content.Context
import android.os.Build
import android.util.Log
import com.napco.utils.Constants
import com.napco.utils.DataState
import com.napco.utils.Utility
import com.napco.utils.Utility.Companion.extractCharacteristicInfo
import com.napco.utils.model.Characteristic
import com.napco.utils.model.DeviceDetails
import com.napco.utils.model.DeviceInfo
import com.napco.utils.model.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

@SuppressLint("MissingPermission")
class BleConnectorImp(private val context: Context) : BleConnector, BluetoothGattCallback() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private var _bluetoothGatt: BluetoothGatt? = null
    private val _gattConnectionResult = MutableSharedFlow<DataState<DeviceDetails>>()
    private val _gattServerResponse = MutableSharedFlow<List<ByteArray>>()

    private companion object {
        val TAG: String = BleConnectorImp::class.java.simpleName
    }

    override fun bleGattConnectionResult(): Flow<DataState<DeviceDetails>> =
        _gattConnectionResult.asSharedFlow()

    override fun gattServerResponse(): Flow<List<ByteArray>> = _gattServerResponse.asSharedFlow()

    override fun connect(address: String) {
        _bluetoothGatt?.close()
        getDevice(address)?.connectGatt(context, false, this)
    }

    override fun disconnect() {
        _bluetoothGatt?.disconnect()
    }

    override fun enableNotification(serviceUUID: UUID, characteristicUUID: UUID) {
        enableNotificationOrIndication(serviceUUID, characteristicUUID, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
    }

    override fun enableIndication(serviceUUID: UUID, characteristicUUID: UUID) {
        enableNotificationOrIndication(serviceUUID, characteristicUUID, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)
    }

    override fun readCharacteristic(serviceUUID: UUID, characteristicUUID: UUID) {
        _bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)?.let {
            _bluetoothGatt?.readCharacteristic(it)
        }
    }

    override fun writeCharacteristic(serviceUUID: UUID, characteristicUUID: UUID, bytes: ByteArray) {
        _bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)?.let {
            writeCharacteristic(it, bytes, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
        }
    }

    override fun writeCharacteristicWithNoResponse(serviceUUID: UUID, characteristicUUID: UUID, bytes: ByteArray) {
        _bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)?.let {
            writeCharacteristic(it, bytes, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
        }
    }

    private fun writeCharacteristic(gattCharacteristic: BluetoothGattCharacteristic, bytes: ByteArray, writeType: Int) {
        gattCharacteristic.writeType = writeType
        if (Build.VERSION.SDK_INT >= 33) {
            _bluetoothGatt?.writeCharacteristic(gattCharacteristic, bytes, writeType)
        } else {
            gattCharacteristic.value = bytes
            _bluetoothGatt?.writeCharacteristic(gattCharacteristic)
        }
    }

    private fun provideBluetoothManager(): BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private fun getDevice(address: String): BluetoothDevice? =
        provideBluetoothManager().adapter.getRemoteDevice(address)

    private fun enableNotificationOrIndication(serviceUUID: UUID, characteristicUUID: UUID, value: ByteArray) {
        _bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)?.let { characteristic ->
            _bluetoothGatt?.setCharacteristicNotification(characteristic, true)
            characteristic.getDescriptor(Constants.DESCRIPTOR_PRE_CLIENT_CONFIG)?.let { descriptor ->
                if (Build.VERSION.SDK_INT >= 33) {
                    _bluetoothGatt?.writeDescriptor(descriptor, value)
                } else {
                    descriptor.value = value
                    _bluetoothGatt?.writeDescriptor(descriptor)
                }
            }
        }
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        when (newState) {
            BluetoothProfile.STATE_CONNECTED -> {
                _bluetoothGatt = gatt
                _bluetoothGatt?.discoverServices()
            }
            BluetoothProfile.STATE_DISCONNECTED -> {
                updateConnectionStatus()
                Log.d(TAG, "Device disconnected")
            }
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            scope.launch {
                emitAttributes(gatt)
            }
        }
    }

    private suspend fun emitAttributes(peripheralGatt: BluetoothGatt) {
        val serviceCharacteristicsMap = extractServicesWithCharacteristics(peripheralGatt.services)

        val deviceInfo = DeviceInfo(
            name = peripheralGatt.device.name,
            address = peripheralGatt.device.address,
            generalInfo = "${peripheralGatt.device.bondState}"
        )

        val details = DeviceDetails(deviceInfo = deviceInfo, services = serviceCharacteristicsMap)

        _gattConnectionResult.emit(DataState.success(details))
    }

    private fun extractServicesWithCharacteristics(serviceList: List<BluetoothGattService>): Map<Service, List<Characteristic>> =
        serviceList.associate { service ->
            val characteristics = service.characteristics.map { bleCharacteristic ->
                extractCharacteristicInfo(bleCharacteristic)
            }
            val serviceReadableTitleName = Utility.getServiceName(service.uuid)
            val newService = Service(serviceReadableTitleName, service.uuid.toString())
            newService to characteristics
        }

    override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
        super.onDescriptorWrite(gatt, descriptor, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            // When descriptor is written successfully
        }
    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            // Handle successful characteristic write
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, newValue: ByteArray) {
        super.onCharacteristicChanged(gatt, characteristic, newValue)
        handleCharacteristicChange(characteristic, newValue)
    }

    @Deprecated("Used natively in Android 12 and lower", ReplaceWith("onCharacteristicChanged(gatt, characteristic, characteristic.value)"))
    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        super.onCharacteristicChanged(gatt, characteristic)
        characteristic?.value?.let { newValue ->
            handleCharacteristicChange(characteristic, newValue)
        }
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, newValue: ByteArray, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, newValue, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            handleCharacteristicRead(characteristic, newValue)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            characteristic?.value?.let { newValue ->
                handleCharacteristicRead(characteristic, newValue)
            }
        }
    }

    private fun handleCharacteristicChange(characteristic: BluetoothGattCharacteristic, newValue: ByteArray) {
        Log.d(TAG, "Characteristic Changed: ${characteristic.uuid}")
        scope.launch {
            val existingValues = _gattServerResponse.replayCache.firstOrNull() ?: emptyList()
            val updatedList = existingValues + newValue
            _gattServerResponse.emit(updatedList)
        }
    }

    private fun handleCharacteristicRead(characteristic: BluetoothGattCharacteristic, newValue: ByteArray) {
        Log.d(TAG, "Characteristic Read: ${characteristic.uuid}")
        Log.d(TAG, "Characteristic Value: ${Utility.bytesToHexString(newValue)}")
        scope.launch {
            val existingValues = _gattServerResponse.replayCache.firstOrNull() ?: emptyList()
            val updatedList = existingValues + newValue
            _gattServerResponse.emit(updatedList)
        }
    }

    private fun updateConnectionStatus() {
        scope.launch {
            val throwable = Throwable("Error: Disconnected")
            _gattConnectionResult.emit(DataState.error("Disconnected", throwable))
        }
    }
}
