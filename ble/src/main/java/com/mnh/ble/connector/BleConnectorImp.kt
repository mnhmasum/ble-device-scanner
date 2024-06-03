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
import java.util.UUID


@SuppressLint("MissingPermission")
class BleConnectorImp(private val context: Context) : BleConnector, BluetoothGattCallback() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private var _bluetoothGatt: BluetoothGatt? = null
    private val _gattConnectionResult = MutableSharedFlow<DataState<ServiceInfo>>()
    private val _gattServerResponse = MutableSharedFlow<List<ByteArray>>()

    private companion object {
        val TAG: String = BleConnectorImp::class.java.simpleName
    }

    override fun bleGattConnectionResult(): Flow<DataState<ServiceInfo>> =
        _gattConnectionResult.asSharedFlow()

    override fun gattServerResponse(): Flow<List<ByteArray>> {
        return _gattServerResponse.asSharedFlow()
    }

    override fun connect(address: String) {
        val device = getDevice(address)
        if (_bluetoothGatt != null) {
            _bluetoothGatt?.close()
        }
        device?.connectGatt(context, false, this)
    }

    override fun disconnect() {
        _bluetoothGatt?.disconnect()
    }

    private fun updateConnectionStatus() {
        scope.launch {
            val throwable = Throwable("Error: Disconnected ")
            _gattConnectionResult.emit(DataState.error("Disconnected", throwable))
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
        val bluetoothGattService = _bluetoothGatt?.getService(serviceUUID)
        val gattCharacteristic = bluetoothGattService?.getCharacteristic(characteristicUUID)
        _bluetoothGatt?.readCharacteristic(gattCharacteristic)
    }

    override fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    ) {
        val bluetoothGattService = _bluetoothGatt?.getService(serviceUUID)
        val gattCharacteristic = bluetoothGattService?.getCharacteristic(characteristicUUID)
        writeCharacteristic(gattCharacteristic, bytes, WRITE_TYPE_DEFAULT)
    }

    override fun writeCharacteristicWithNoResponse(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    ) {
        val bluetoothGattService = _bluetoothGatt?.getService(serviceUUID)
        val gattCharacteristic = bluetoothGattService?.getCharacteristic(characteristicUUID)
        writeCharacteristic(gattCharacteristic, bytes, WRITE_TYPE_NO_RESPONSE)
    }

    private fun writeCharacteristic(
        gattCharacteristic: BluetoothGattCharacteristic?,
        bytes: ByteArray,
        writeType: Int,
    ) {
        if (Build.VERSION.SDK_INT < 33) {
            _bluetoothGatt?.writeCharacteristic(gattCharacteristic)
        } else {
            gattCharacteristic?.let {
                _bluetoothGatt?.writeCharacteristic(it, bytes, writeType)
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

    private fun enableNotificationOrIndication(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        value: ByteArray,
    ) {
        val bluetoothGattService = _bluetoothGatt?.getService(serviceUUID)
        val gattCharacteristic = bluetoothGattService?.getCharacteristic(characteristicUUID)

        if (bluetoothGattService == null || gattCharacteristic == null) {
            return
        }

        _bluetoothGatt?.setCharacteristicNotification(gattCharacteristic, true)

        val descriptor = gattCharacteristic.getDescriptor(Constants.DESCRIPTOR_PRE_CLIENT_CONFIG)

        if (Build.VERSION.SDK_INT < 33) {
            descriptor.value = value
            _bluetoothGatt?.writeDescriptor(descriptor)
        } else {
            _bluetoothGatt?.writeDescriptor(descriptor, value)
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

    override fun onServicesDiscovered(peripheralGatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(peripheralGatt, status)

        if (status == BluetoothGatt.GATT_SUCCESS) {
            scope.launch {
                val serviceDetails: ServiceInfo = extractServiceInfo(peripheralGatt.services)
                _gattConnectionResult.emit(DataState.success(serviceDetails))
            }
        }
    }

    private fun extractServiceInfo(serviceList: List<BluetoothGattService>): ServiceInfo {
        val services = serviceList.associate { service ->
            val characteristics = service.characteristics.map { bleCharacteristic ->
                extractCharacteristicInfo(bleCharacteristic)
            }

            val serviceName = Utility.getServiceName(service.uuid)
            val newService = Service(name = serviceName, uuid = service.getUUID())

            newService to characteristics
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
        val newValue: ByteArray? = characteristic?.value

        Log.d(
            TAG, "Characteristics Reading from deprecated function: `" +
                    "Characteristic UUID ${characteristic?.uuid.toString()} " +
                    "Response: ${Utility.bytesToHexString(newValue!!)}`"
        )
        scope.launch {
            val existingValues = _gattServerResponse.replayCache.firstOrNull() ?: emptyList()
            val updatedList = existingValues + newValue
            _gattServerResponse.emit(updatedList)
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        newValue: ByteArray,
    ) {
        super.onCharacteristicChanged(gatt, characteristic, newValue)
        Log.d(TAG, "Characteristics Read value new: ${characteristic.uuid}")
        scope.launch {
            val existingValues = _gattServerResponse.replayCache.firstOrNull() ?: emptyList()
            val updatedList = existingValues + newValue
            _gattServerResponse.emit(updatedList)
        }

    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        newValue: ByteArray,
        status: Int,
    ) {
        super.onCharacteristicRead(gatt, characteristic, newValue, status)

        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "Characteristics Read NEW: ${characteristic.uuid}")
            Log.d(TAG, "Characteristics Read New Value: ${Utility.bytesToHexString(newValue)}")

            scope.launch {
                val existingValues = _gattServerResponse.replayCache.firstOrNull() ?: emptyList()
                val updatedList = existingValues + newValue
                _gattServerResponse.emit(updatedList)
            }

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int,
    ) {
        super.onCharacteristicRead(gatt, characteristic, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "Characteristics Read C: ${characteristic?.uuid}")
            Log.d(
                TAG,
                "Characteristics Read Value: ${Utility.bytesToHexString(characteristic!!.value)}"
            )

            scope.launch {
                val existingValues = _gattServerResponse.replayCache.firstOrNull() ?: emptyList()
                val updatedList = existingValues + characteristic.value
                _gattServerResponse.emit(updatedList)
            }

        }
    }

}