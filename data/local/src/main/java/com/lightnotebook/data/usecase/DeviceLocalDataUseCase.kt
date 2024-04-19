package com.lightnotebook.data.usecase

import com.lightnotebook.data.database.entity.DeviceEntity
import com.lightnotebook.data.repository.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeviceLocalDataUseCase @Inject constructor(private val deviceRepository: DeviceRepository) {
    fun getDeviceList() = deviceRepository.getAllDevice()
        .flowOn(Dispatchers.IO)

    suspend fun insert(deviceEntity: DeviceEntity) {
        deviceRepository.insert(deviceEntity)
    }

}