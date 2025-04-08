package com.mnh.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.napco.utils.DataState
import com.napco.utils.ServerResponseState
import com.napco.utils.Utility
import com.napco.utils.Utility.Companion.extractCharacteristicInfo
import com.napco.utils.Utility.Companion.logI
import com.napco.utils.model.Characteristic
import com.napco.utils.model.DeviceDetails
import com.napco.utils.model.DeviceInfo
import com.napco.utils.model.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class FakeBLEGattClient(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) : BluetoothGattCallback() {
    var connectionState: MutableSharedFlow<DataState<DeviceDetails>> = MutableSharedFlow()

    val serverResponse: MutableSharedFlow<ServerResponseState<ByteArray>> = MutableSharedFlow()

    suspend fun emit(value: DataState<DeviceDetails>) = connectionState.emit(value)
    suspend fun emit(value: ServerResponseState<ByteArray>) = serverResponse.emit(value)

    var gatt: BluetoothGatt? = null

    fun connect(address: String) {
        bluetoothAdapter.getRemoteDevice(address).connectGatt(context, false, this)
    }

    fun disconnect() {
        connectionState.drop(1)
        gatt?.disconnect()
        gatt?.close()
    }

    private fun setBluetoothGatt(gatt: BluetoothGatt) {
        this.gatt = gatt
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        when (newState) {
            BluetoothProfile.STATE_CONNECTING -> {
                scope.launch {
                    connectionState.emit(DataState.loading())
                }
            }

            BluetoothProfile.STATE_CONNECTED -> {
                setBluetoothGatt(gatt)
                gatt.discoverServices()
            }

            BluetoothProfile.STATE_DISCONNECTED -> {
                scope.launch {
                    connectionState.emit(DataState.error("Disconnected", Throwable("Error: Disconnected")))
                }
                logI("Device disconnected")
            }
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            scope.launch {
                val serviceCharacteristicsMap = extractServicesWithCharacteristics(gatt.services)
                val deviceInfo = DeviceInfo(gatt.device.name, gatt.device.address)
                val details = DeviceDetails(deviceInfo = deviceInfo, services = serviceCharacteristicsMap)
                connectionState.emit(DataState.success(details))
            }
        }
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        status: Int,
    ) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            // When descriptor is written successfully
        }
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int,
    ) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            scope.launch {
                serverResponse.emit(ServerResponseState.writeSuccess(characteristic.value))
            }
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        newValue: ByteArray,
    ) {
        scope.launch {
            serverResponse.emit(ServerResponseState.notifySuccess(characteristic.value))
        }
    }

    @Deprecated(
        "Used natively in Android 12 and lower",
        ReplaceWith("onCharacteristicChanged(gatt, characteristic, characteristic.value)")
    )
    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic,
    ) {
        scope.launch {
            serverResponse.emit(ServerResponseState.notifySuccess(characteristic.value))
        }
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        newValue: ByteArray,
        status: Int,
    ) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            scope.launch {
                serverResponse.emit(ServerResponseState.readSuccess(characteristic.value))
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic,
        status: Int,
    ) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            scope.launch {
                serverResponse.emit(ServerResponseState.readSuccess(characteristic.value))
            }
        }
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
}