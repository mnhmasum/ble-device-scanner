package com.mnh.ble.bluetooth.bleconnection

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
import com.napco.utils.model.BleDevice
import com.napco.utils.model.Characteristic
import com.napco.utils.model.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class BLEGattClient(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) : BluetoothGattCallback() {
    var connectionState: MutableSharedFlow<DataState<BleDevice>> = MutableSharedFlow()
    val serverResponse: MutableSharedFlow<ServerResponseState<ByteArray>> = MutableSharedFlow()
    var gatt: BluetoothGatt? = null

    fun connect(address: String) {
        scope.launch {
            connectionState.emit(DataState.loading())
        }.also {
            bluetoothAdapter.getRemoteDevice(address).connectGatt(context, false, this)
        }
    }

    fun disconnect() {
        connectionState.drop(1)
        gatt?.disconnect()
    }

    private fun setBluetoothGatt(gatt: BluetoothGatt) {
        this.gatt = gatt
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        when (newState) {
            BluetoothProfile.STATE_CONNECTED -> {
                setBluetoothGatt(gatt)
                gatt.discoverServices()
            }

            BluetoothProfile.STATE_DISCONNECTED -> {
                scope.launch {
                    connectionState.emit(DataState.error("Disconnected", Throwable("Error: Disconnected")))
                }
                gatt.close()
                logI("Device disconnected")
            }
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            scope.launch {
                val bleServicesWithCharacteristics = extractServicesWithCharacteristics(gatt.services)
                val bleDevice = BleDevice(
                    name = gatt.device.name,
                    macAddress = gatt.device.address,
                    services = bleServicesWithCharacteristics
                )
                connectionState.emit(DataState.success(bleDevice))
            }
        }
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        status: Int,
    ) {
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
        serviceList.associate { service: BluetoothGattService ->
            val characteristics = service.characteristics.map { bleCharacteristic ->
                extractCharacteristicInfo(bleCharacteristic)
            }
            val serviceReadableTitleName = Utility.getServiceName(service.uuid)
            val newService = Service(serviceReadableTitleName, service.uuid.toString())
            newService to characteristics
        }
}