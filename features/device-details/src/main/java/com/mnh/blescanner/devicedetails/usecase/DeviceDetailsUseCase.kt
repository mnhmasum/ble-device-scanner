package com.mnh.blescanner.devicedetails.usecase

import com.mnh.blescanner.devicedetails.repository.DeviceDetailsRepository
import com.napco.utils.DataState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeviceDetailsUseCase @Inject constructor(private val deviceDetailsRepository: DeviceDetailsRepository) {
    fun connect(device: String) {
        return deviceDetailsRepository.connect(device)
    }

    fun disconnect() {
        deviceDetailsRepository.disconnect()
    }

    fun bleGattConnectionResult(): Flow<DataState<DeviceDetails>> =
        deviceDetailsRepository.getGattConnectionResult()
            .flowOn(Dispatchers.IO)
            .map { it }

}