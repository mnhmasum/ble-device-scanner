package com.mnh.features.details.usecase

import com.mnh.ble.model.ServiceInfo
import com.mnh.features.details.repository.PeripheralDetailsRepository
import com.napco.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class PeripheralDetailsUseCase @Inject constructor(private val peripheralDetailsRepository: PeripheralDetailsRepository) {
    fun connect(device: String) {
        return peripheralDetailsRepository.connect(device)
    }

    fun enableNotification(service: UUID, chara: UUID) {
        peripheralDetailsRepository.enableNotification(service, chara)
    }

    fun enableIndication(service: UUID, chara: UUID) {
        peripheralDetailsRepository.enableIndication(service, chara)
    }

    fun readCharacteristic(service: UUID, chara: UUID) {
        peripheralDetailsRepository.readCharacteristic(service, chara)
    }

    fun writeCharacteristic(service: UUID, chara: UUID) {
        peripheralDetailsRepository.readCharacteristic(service, chara)
    }

    fun writeCharacteristicWithNoResponse(service: UUID, chara: UUID) {
        peripheralDetailsRepository.readCharacteristic(service, chara)
    }

    fun disconnect() {
        peripheralDetailsRepository.disconnect()
    }

    fun bleGattConnectionResult(): Flow<DataState<ServiceInfo>> =
        peripheralDetailsRepository.getGattConnectionResult()
            .flowOn(Dispatchers.IO)
            .map { it }

}