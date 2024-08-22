package com.mnh.ble.bluetooth.bleconnection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
import com.napco.utils.Utility.Companion.logD
import com.napco.utils.Utility.Companion.logI
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
class BleConnectionManagerImpl(private val context: Context, private val bluetoothAdapter: BluetoothAdapter) :
    BleConnectionManager,
    BluetoothGattCallback() {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private var bluetoothGatt: BluetoothGatt? = null
    private val gattConnectionResult = MutableSharedFlow<DataState<DeviceDetails>>()
    private val gattServerResponse = MutableSharedFlow<ServerResponseState<List<ByteArray>>>()
    private val readCharacteristicResponseBytes = ArrayList<ByteArray>()
    private val writeCharacteristicResponseBytes = ArrayList<ByteArray>()

    override fun bleGattConnectionResult(): Flow<DataState<DeviceDetails>> =
        gattConnectionResult.asSharedFlow()

    override fun gattServerResponse(): Flow<ServerResponseState<List<ByteArray>>> =
        gattServerResponse.asSharedFlow()

    override fun connect(address: String) {
        readCharacteristicResponseBytes.clear()
        writeCharacteristicResponseBytes.clear()
        getDevice(address)?.connectGatt(context, false, this)
    }

    override fun disconnect() {
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
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    ) {
        bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)?.let {
            writeCharacteristic(it, bytes, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
        }
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
        gattCharacteristic: BluetoothGattCharacteristic,
        bytes: ByteArray,
        writeType: Int,
    ) {
        gattCharacteristic.writeType = writeType
        if (Build.VERSION.SDK_INT >= 33) {
            bluetoothGatt?.writeCharacteristic(gattCharacteristic, bytes, writeType)
        } else {
            gattCharacteristic.value = bytes
            bluetoothGatt?.writeCharacteristic(gattCharacteristic)
        }
    }

    private fun getDevice(address: String): BluetoothDevice? =
        bluetoothAdapter.getRemoteDevice(address)

    private fun enableNotificationOrIndication(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        value: ByteArray,
    ) {
        bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)
            ?.let { characteristic ->
                bluetoothGatt?.setCharacteristicNotification(characteristic, true)
                characteristic.getDescriptor(Constants.DESCRIPTOR_PRE_CLIENT_CONFIG)
                    ?.let { descriptor ->
                        if (Build.VERSION.SDK_INT >= 33) {
                            bluetoothGatt?.writeDescriptor(descriptor, value)
                        } else {
                            descriptor.value = value
                            bluetoothGatt?.writeDescriptor(descriptor)
                        }
                    }
            }
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        when (newState) {
            BluetoothProfile.STATE_CONNECTED -> {
                bluetoothGatt = gatt
                bluetoothGatt?.discoverServices()
            }

            BluetoothProfile.STATE_DISCONNECTED -> {
                updateConnectionStatus()
                logI("Device disconnected")
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
        super.onDescriptorWrite(gatt, descriptor, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            // When descriptor is written successfully
        }
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int,
    ) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            scope.launch {
                writeCharacteristicResponseBytes.add(characteristic.value)
                gattServerResponse.emit(
                    ServerResponseState.writeSuccess(
                        writeCharacteristicResponseBytes
                    )
                )
            }
        }
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        newValue: ByteArray,
    ) {
        super.onCharacteristicChanged(gatt, characteristic, newValue)
        handleCharacteristicChange(characteristic, newValue)
    }

    @Deprecated(
        "Used natively in Android 12 and lower",
        ReplaceWith("onCharacteristicChanged(gatt, characteristic, characteristic.value)")
    )
    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
    ) {
        super.onCharacteristicChanged(gatt, characteristic)
        characteristic?.value?.let { newValue ->
            handleCharacteristicChange(characteristic, newValue)
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
            handleCharacteristicRead(characteristic, newValue)
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
            characteristic?.value?.let { newValue ->
                handleCharacteristicRead(characteristic, newValue)
            }
        }
    }

    private fun handleCharacteristicChange(
        characteristic: BluetoothGattCharacteristic,
        newValue: ByteArray,
    ) {
        logD("Characteristic Changed: ${Utility.bytesToHexString(newValue)}")
        scope.launch {
            readCharacteristicResponseBytes.add(newValue)
            gattServerResponse.emit(
                ServerResponseState.notifySuccess(
                    readCharacteristicResponseBytes
                )
            )
        }
    }

    private fun handleCharacteristicRead(
        characteristic: BluetoothGattCharacteristic,
        newValue: ByteArray,
    ) {
        logD("Characteristic Read: ${characteristic.uuid}")
        logD("Characteristic Value: ${Utility.bytesToHexString(newValue)}")
        scope.launch {
            readCharacteristicResponseBytes.add(newValue)
            gattServerResponse.emit(ServerResponseState.readSuccess(readCharacteristicResponseBytes))
        }
    }

    private fun updateConnectionStatus() {
        scope.launch {
            val throwable = Throwable("Error: Disconnected")
            gattConnectionResult.emit(DataState.error("Disconnected", throwable))
        }
    }
}
