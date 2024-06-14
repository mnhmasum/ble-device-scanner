package com.mnh.blescanner.deviceoperation.usecase

import com.mnh.blescanner.deviceoperation.respository.DeviceOperationRepository
import com.napco.utils.DataState
import com.napco.utils.ServerResponseState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DeviceOperationUseCase @Inject constructor(private val deviceOperationRepository: DeviceOperationRepository) {
    fun connect(device: String) {
        return deviceOperationRepository.connect(device)
    }

    fun enableNotification(service: UUID, chara: UUID) {
        deviceOperationRepository.enableNotification(service, chara)
    }

    fun enableIndication(service: UUID, chara: UUID) {
        deviceOperationRepository.enableIndication(service, chara)
    }

    fun readCharacteristic(service: UUID, chara: UUID) {
        deviceOperationRepository.readCharacteristic(service, chara)
    }

    fun writeCharacteristic(service: UUID, chara: UUID, bytes: ByteArray) {
        deviceOperationRepository.writeCharacteristic(service, chara, bytes)
    }

    fun writeCharacteristicWithNoResponse(service: UUID, chara: UUID, bytes: ByteArray) {
        deviceOperationRepository.writeCharacteristicWithNoResponse(service, chara, bytes)
    }

    fun disconnect() {
        deviceOperationRepository.disconnect()
    }

    fun bleGattConnectionResult(): Flow<DataState<DeviceDetails>> =
        deviceOperationRepository.getGattConnectionResult()
            .flowOn(Dispatchers.IO)
            .map { it }

    fun gattServerResponse(): Flow<ServerResponseState<List<ByteArray>>> =
        deviceOperationRepository.gattServerResponse().flowOn(Dispatchers.IO).map { it }

}