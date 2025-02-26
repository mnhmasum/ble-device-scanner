package com.mnh.ble.bluetooth.bleconnection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.os.Build
import com.napco.utils.Constants
import com.napco.utils.DataState
import com.napco.utils.ServerResponseState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.UUID

@SuppressLint("MissingPermission")
class BleConnectionManagerImpl(private val bleGattClient: BLEGattClient) : BleConnectionManager {

    override fun getService(serviceUUID: UUID): BluetoothGattService? {
        return bleGattClient.gatt?.getService(serviceUUID)
    }

    override fun connectionState(): Flow<DataState<DeviceDetails>> =
        bleGattClient.connectionState.asSharedFlow()

    override fun deviceResponse(): SharedFlow<ServerResponseState<ByteArray>> =
        bleGattClient.serverResponse.asSharedFlow()

    override fun connect(address: String) {
        bleGattClient.connect(address)
    }

    override fun disconnect() {
        bleGattClient.disconnect()
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
        val service = bleGattClient.gatt?.getService(serviceUUID)
        val characteristics = service?.getCharacteristic(characteristicUUID)
        bleGattClient.gatt?.readCharacteristic(characteristics)
    }

    override fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, bytes: ByteArray) {
        writeCharacteristic(characteristic, bytes, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
    }

    override fun writeCharacteristicWithNoResponse(serviceUUID: UUID, characteristicUUID: UUID, bytes: ByteArray) {
        val service = bleGattClient.gatt?.getService(serviceUUID)
        val characteristics = service?.getCharacteristic(characteristicUUID) ?: return
        writeCharacteristic(characteristics, bytes, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
    }

    private fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, bytes: ByteArray, writeType: Int) {
        characteristic.writeType = writeType

        if (Build.VERSION.SDK_INT >= 33) {
            bleGattClient.gatt?.writeCharacteristic(characteristic, bytes, writeType)
        } else {
            characteristic.value = bytes
            bleGattClient.gatt?.writeCharacteristic(characteristic)
        }
    }

    private fun enableNotificationOrIndication(serviceUUID: UUID, characteristicUUID: UUID, value: ByteArray) {
        setupCharacteristicNotification(bleGattClient.gatt, serviceUUID, characteristicUUID, value)
    }

    private fun setupCharacteristicNotification(
        bluetoothGatt: BluetoothGatt?,
        serviceUUID: UUID,
        characteristicUUID: UUID,
        value: ByteArray,
    ) {
        val characteristic = getCharacteristic(bluetoothGatt, serviceUUID, characteristicUUID)
        enableNotification(bluetoothGatt, characteristic)
        writeDescriptor(bluetoothGatt, characteristic, value)
    }

    private fun getCharacteristic(
        bluetoothGatt: BluetoothGatt?,
        serviceUUID: UUID,
        characteristicUUID: UUID,
    ): BluetoothGattCharacteristic? {
        return bluetoothGatt?.getService(serviceUUID)?.getCharacteristic(characteristicUUID)
    }

    private fun enableNotification(bluetoothGatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
        bluetoothGatt?.setCharacteristicNotification(characteristic, true)
    }

    private fun writeDescriptor(
        bluetoothGatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        value: ByteArray,
    ) {
        val descriptor = characteristic?.getDescriptor(Constants.DESCRIPTOR_PRE_CLIENT_CONFIG) ?: return

        if (Build.VERSION.SDK_INT >= 33) {
            bluetoothGatt?.writeDescriptor(descriptor, value)
        } else {
            descriptor.value = value
            bluetoothGatt?.writeDescriptor(descriptor)
        }
    }
}
