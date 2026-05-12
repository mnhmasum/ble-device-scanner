package com.mnh.blescanner.deviceoperation.respository

import com.mnh.blescanner.utils.DataState
import com.mnh.blescanner.utils.ServerResponseState
import com.mnh.blescanner.utils.model.BleDevice
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface DeviceOperationRepository {
    fun connect(address: String)
    fun disconnect()
    fun getGattConnectionResult(): Flow<DataState<BleDevice>>
    fun gattServerResponse(): Flow<ServerResponseState<ByteArray>>
    fun enableNotification(serviceUUID: UUID, characteristicUUID: UUID)
    fun enableIndication(serviceUUID: UUID, characteristicUUID: UUID)
    fun readCharacteristic(service: UUID, characteristic: UUID)
    fun writeCharacteristic(serviceUUID: UUID, characteristicUUID: UUID, bytes: ByteArray)
    fun writeCharacteristicWithNoResponse(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    )

}