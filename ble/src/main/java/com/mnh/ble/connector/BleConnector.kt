package com.mnh.ble.connector

import com.napco.utils.model.DeviceDetails
import com.napco.utils.DataState
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BleConnector {
    fun connect(address: String)
    fun bleGattConnectionResult(): Flow<DataState<DeviceDetails>>
    fun gattServerResponse(): Flow<List<ByteArray>>
    fun enableNotification(serviceUUID: UUID, characteristicUUID: UUID)
    fun enableIndication(serviceUUID: UUID, characteristicUUID: UUID)
    fun readCharacteristic(serviceUUID: UUID, characteristicUUID: UUID)
    fun writeCharacteristic(serviceUUID: UUID, characteristicUUID: UUID, bytes: ByteArray)
    fun writeCharacteristicWithNoResponse(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    )

    fun disconnect()
}