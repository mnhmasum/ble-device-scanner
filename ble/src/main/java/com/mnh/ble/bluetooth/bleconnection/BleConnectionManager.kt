package com.mnh.ble.bluetooth.bleconnection

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import com.napco.utils.DataState
import com.napco.utils.ServerResponseState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BleConnectionManager {
    fun connect(address: String)
    fun bleGattConnectionResult(): Flow<DataState<DeviceDetails>>
    fun getBluetoothGatt(): BluetoothGatt?
    fun gattServerResponse(): Flow<ServerResponseState<List<ByteArray>>>
    fun enableNotification(serviceUUID: UUID, characteristicUUID: UUID)
    fun enableIndication(serviceUUID: UUID, characteristicUUID: UUID)
    fun readCharacteristic(serviceUUID: UUID, characteristicUUID: UUID)
    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic?, bytes: ByteArray)
    fun writeCharacteristicWithNoResponse(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    )

    fun disconnect()
}