package com.mnh.blescanner.devicedetails.repository

import com.napco.utils.DataState
import com.napco.utils.ServerResponseState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface DeviceDetailsRepository {
    fun connect(address: String)
    fun disconnect()
    fun getGattConnectionResult(): Flow<DataState<DeviceDetails>>
    fun gattServerResponse(): Flow<ServerResponseState<List<ByteArray>>>
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