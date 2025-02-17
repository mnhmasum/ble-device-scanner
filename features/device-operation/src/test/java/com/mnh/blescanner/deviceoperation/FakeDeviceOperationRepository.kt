package com.mnh.blescanner.deviceoperation

import com.mnh.blescanner.deviceoperation.respository.DeviceOperationRepository
import com.napco.utils.DataState
import com.napco.utils.ServerResponseState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.UUID

class FakeDeviceOperationRepo : DeviceOperationRepository {
    private val connectionFlow = MutableSharedFlow<DataState<DeviceDetails>>()
    private val serverResponseFlow = MutableSharedFlow<ServerResponseState<ByteArray>>()
    suspend fun emit(value: DataState<DeviceDetails>) = connectionFlow.emit(value)
    suspend fun emit(value: ServerResponseState<ByteArray>) = serverResponseFlow.emit(value)

    override fun connect(address: String) {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun getGattConnectionResult(): Flow<DataState<DeviceDetails>> {
        return connectionFlow
    }

    override fun gattServerResponse(): Flow<ServerResponseState<ByteArray>> {
        return serverResponseFlow
    }

    override fun enableNotification(serviceUUID: UUID, characteristicUUID: UUID) {
        TODO("Not yet implemented")
    }

    override fun enableIndication(serviceUUID: UUID, characteristicUUID: UUID) {
        TODO("Not yet implemented")
    }

    override fun readCharacteristic(service: UUID, characteristic: UUID) {
        TODO("Not yet implemented")
    }

    override fun writeCharacteristic(serviceUUID: UUID, characteristicUUID: UUID, bytes: ByteArray) {
        TODO("Not yet implemented")
    }

    override fun writeCharacteristicWithNoResponse(serviceUUID: UUID, characteristicUUID: UUID, bytes: ByteArray) {
        TODO("Not yet implemented")
    }
}