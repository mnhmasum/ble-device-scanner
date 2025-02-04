package com.mnh.blescanner.deviceoperation

import com.mnh.blescanner.deviceoperation.respository.DeviceOperationRepository
import com.napco.utils.DataState
import com.napco.utils.ServerResponseState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.UUID

class FakeDeviceOperationRepo : DeviceOperationRepository {
    private val flow = MutableSharedFlow<DataState<DeviceDetails>>()
    private val flow1 = MutableSharedFlow<ServerResponseState<ByteArray>>()
    suspend fun emit(value: DataState<DeviceDetails>) = flow.emit(value)
    suspend fun emit1(value: ServerResponseState<ByteArray>) = flow1.emit(value)
    //override fun scores(): Flow<Int> = flow

    override fun connect(address: String) {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun getGattConnectionResult(): Flow<DataState<DeviceDetails>> {
        return flow
    }

    override fun gattServerResponse(): Flow<ServerResponseState<ByteArray>> {
        return flow1
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