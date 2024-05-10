package com.mnh.features.details.usecase

import com.mnh.ble.model.DeviceInfo
import com.mnh.features.details.repository.PeripheralDetailsRepository
import com.napco.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PeripheralDetailsUseCase @Inject constructor(private val peripheralDetailsRepository: PeripheralDetailsRepository) {
    fun connect(device: String) {
        return peripheralDetailsRepository.connect(device)
    }

    fun disconnect() {
        peripheralDetailsRepository.disconnect()
    }

    fun bleGattConnectionResult(): Flow<DataState<DeviceInfo>> {
        peripheralDetailsRepository.getGattConnectionResult()
            .flowOn(Dispatchers.IO)
            .map { dataState ->
                when (dataState) {
                    is DataState.Success -> dataState
                    is DataState.Error -> dataState
                    is DataState.Loading -> dataState
                }

            }
        return peripheralDetailsRepository.getGattConnectionResult()
    }

}