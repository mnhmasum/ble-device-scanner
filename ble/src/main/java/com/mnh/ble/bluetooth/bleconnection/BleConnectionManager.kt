package com.mnh.ble.bluetooth.bleconnection

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.napco.utils.DataState
import com.napco.utils.ServerResponseState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BleConnectionManager {
    fun connect(address: String)
    fun getService(serviceUUID: UUID): BluetoothGattService?
    fun connectionState(): Flow<DataState<DeviceDetails>>
    fun deviceResponse(): Flow<ServerResponseState<ByteArray>>
    fun enableNotification(serviceUUID: UUID, characteristicUUID: UUID)
    fun enableIndication(serviceUUID: UUID, characteristicUUID: UUID)
    fun readCharacteristic(serviceUUID: UUID, characteristicUUID: UUID)
    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, bytes: ByteArray)
    fun writeCharacteristicWithNoResponse(serviceUUID: UUID, characteristicUUID: UUID, bytes: ByteArray, )
    fun disconnect()
}