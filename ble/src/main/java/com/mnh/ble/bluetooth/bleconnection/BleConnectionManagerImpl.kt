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
import android.os.Build
import com.napco.utils.Constants
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.util.UUID

@SuppressLint("MissingPermission")
class BleConnectionManagerImpl(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val gattConnectionResult: MutableSharedFlow<DataState<DeviceDetails>> = MutableSharedFlow(),
    private val gattServerResponse: MutableSharedFlow<ServerResponseState<ByteArray>> = MutableSharedFlow(),
) : BleConnectionManager, BluetoothGattCallback() {

    private var bluetoothGatt: BluetoothGatt? = null

    fun setBluetoothGatt(gatt: BluetoothGatt) {
        this.bluetoothGatt = gatt
    }

    override fun getBluetoothGatt(): BluetoothGatt? {
        return bluetoothGatt
    }

    override fun bleGattConnectionResult(): Flow<DataState<DeviceDetails>> =
        gattConnectionResult.asSharedFlow()

    override fun gattServerResponse(): SharedFlow<ServerResponseState<ByteArray>> =
        gattServerResponse.asSharedFlow()

    override fun connect(address: String) {
        scope.launch {
            gattConnectionResult.emit(DataState.loading())
        }
        bluetoothAdapter.getRemoteDevice(address).connectGatt(context, false, this)
    }

    override fun disconnect() {
        gattConnectionResult.drop(1)
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
    }

    override fun enableNotification(serviceUUID: UUID, characteristicUUID: UUID) {
        enableNotificationOrIndication(
            serviceUUID, characteristicUUID, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        )
    }

    override fun enableIndication(serviceUUID: UUID, characteristicUUID: UUID) {
        enableNotificationOrIndication(
            serviceUUID, characteristicUUID, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
        )
    }

    override fun readCharacteristic(serviceUUID: UUID, characteristicUUID: UUID) {
        bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)?.let {
            bluetoothGatt?.readCharacteristic(it)
        }
    }

    override fun writeCharacteristic(
        characteristic: BluetoothGattCharacteristic?,
        bytes: ByteArray,
    ) {
        writeCharacteristic(
            characteristic,
            bytes,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        )

    }

    override fun writeCharacteristicWithNoResponse(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    ) {
        bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)?.let {
            writeCharacteristic(it, bytes, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
        }
    }

    private fun writeCharacteristic(
        gattCharacteristic: BluetoothGattCharacteristic?,
        bytes: ByteArray,
        writeType: Int,
    ) {
        gattCharacteristic?.writeType = writeType
        if (Build.VERSION.SDK_INT >= 33) {
            gattCharacteristic?.let { bluetoothGatt?.writeCharacteristic(it, bytes, writeType) }
        } else {
            gattCharacteristic?.value = bytes
            bluetoothGatt?.writeCharacteristic(gattCharacteristic)
        }
    }

    private fun enableNotificationOrIndication(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        value: ByteArray,
    ) {
        setupCharacteristicNotification(bluetoothGatt, serviceUUID, characteristicUUID, value)
    }

    private fun setupCharacteristicNotification(
        bluetoothGatt: BluetoothGatt?,
        serviceUUID: UUID,
        characteristicUUID: UUID,
        value: ByteArray,
    ) {
        val characteristic = getCharacteristic(bluetoothGatt, serviceUUID, characteristicUUID)
        characteristic?.let {
            enableNotification(bluetoothGatt, it)
            writeDescriptor(bluetoothGatt, it, value)
        }
    }

    private fun getCharacteristic(
        bluetoothGatt: BluetoothGatt?,
        serviceUUID: UUID,
        characteristicUUID: UUID,
    ): BluetoothGattCharacteristic? {
        return bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)
    }

    private fun enableNotification(
        bluetoothGatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic,
    ) {
        bluetoothGatt?.setCharacteristicNotification(characteristic, true)
    }

    private fun writeDescriptor(
        bluetoothGatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
    ) {
        characteristic.getDescriptor(Constants.DESCRIPTOR_PRE_CLIENT_CONFIG)?.let { descriptor ->
            if (Build.VERSION.SDK_INT >= 33) {
                bluetoothGatt?.writeDescriptor(descriptor, value)
            } else {
                descriptor.value = value
                bluetoothGatt?.writeDescriptor(descriptor)
            }
        }
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        when (newState) {
            BluetoothProfile.STATE_CONNECTED -> {
                setBluetoothGatt(gatt)
                bluetoothGatt?.discoverServices()
            }

            BluetoothProfile.STATE_DISCONNECTED -> {
                updateConnectionStatus()
                logI("Device disconnected")
            }
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
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

        gattConnectionResult.emit(DataState.success(details))

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
                gattServerResponse.emit(ServerResponseState.writeSuccess(characteristic.value))
            }
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        newValue: ByteArray,
    ) {
        handleCharacteristicChange(characteristic)
    }

    @Deprecated(
        "Used natively in Android 12 and lower",
        ReplaceWith("onCharacteristicChanged(gatt, characteristic, characteristic.value)")
    )
    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic,
    ) {
        handleCharacteristicChange(characteristic)
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        newValue: ByteArray,
        status: Int,
    ) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            handleCharacteristicRead(characteristic)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic,
        status: Int,
    ) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            handleCharacteristicRead(characteristic)
        }
    }

    private fun handleCharacteristicChange(characteristic: BluetoothGattCharacteristic) {
        scope.launch {
            gattServerResponse.emit(ServerResponseState.notifySuccess(characteristic.value))
        }
    }

    private fun handleCharacteristicRead(characteristic: BluetoothGattCharacteristic) {
        scope.launch {
            gattServerResponse.emit(ServerResponseState.readSuccess(characteristic.value))
        }
    }

    private fun updateConnectionStatus() {
        scope.launch {
            val throwable = Throwable("Error: Disconnected")
            gattConnectionResult.emit(DataState.error("Disconnected", throwable))
        }
    }


}
