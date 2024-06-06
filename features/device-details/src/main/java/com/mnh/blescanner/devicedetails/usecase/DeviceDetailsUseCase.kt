package com.mnh.blescanner.devicedetails.usecase

import com.napco.utils.model.DeviceDetails
import com.mnh.blescanner.devicedetails.repository.DeviceDetailsRepository
import com.napco.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class DeviceDetailsUseCase @Inject constructor(private val deviceDetailsRepository: DeviceDetailsRepository) {
    fun connect(device: String) {
        return deviceDetailsRepository.connect(device)
    }

    fun enableNotification(service: UUID, chara: UUID) {
        deviceDetailsRepository.enableNotification(service, chara)
    }

    fun enableIndication(service: UUID, chara: UUID) {
        deviceDetailsRepository.enableIndication(service, chara)
    }

    fun readCharacteristic(service: UUID, chara: UUID) {
        deviceDetailsRepository.readCharacteristic(service, chara)
    }

    fun writeCharacteristic(service: UUID, chara: UUID) {
        deviceDetailsRepository.readCharacteristic(service, chara)
    }

    fun writeCharacteristicWithNoResponse(service: UUID, chara: UUID) {
        deviceDetailsRepository.readCharacteristic(service, chara)
    }

    fun disconnect() {
        deviceDetailsRepository.disconnect()
    }

    fun bleGattConnectionResult(): Flow<DataState<DeviceDetails>> =
        deviceDetailsRepository.getGattConnectionResult()
            .flowOn(Dispatchers.IO)
            .map { it }

    fun gattServerResponse(): Flow<List<ByteArray>> =
        deviceDetailsRepository.gattServerResponse().flowOn(Dispatchers.IO).map { it }

}