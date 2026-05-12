package com.mnh.blescanner.deviceoperation

import com.mnh.blescanner.deviceoperation.respository.DeviceOperationRepository
import com.mnh.blescanner.utils.DataState
import com.mnh.blescanner.utils.ServerResponseState
import com.mnh.blescanner.utils.model.BleDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.UUID

class FakeDeviceOperationRepo : DeviceOperationRepository {
    private val connectionFlow = MutableSharedFlow<DataState<BleDevice>>()
    private val serverResponseFlow = MutableSharedFlow<ServerResponseState<ByteArray>>()
    suspend fun emit(value: DataState<BleDevice>) = connectionFlow.emit(value)
    suspend fun emit(value: ServerResponseState<ByteArray>) = serverResponseFlow.emit(value)

    override fun connect(address: String) {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun getGattConnectionResult(): Flow<DataState<BleDevice>> {
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