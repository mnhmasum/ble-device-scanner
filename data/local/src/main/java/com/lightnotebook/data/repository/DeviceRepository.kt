package com.lightnotebook.data.repository

import com.lightnotebook.data.database.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    suspend fun insert(lockEntity: DeviceEntity)
    fun getAllDevice(): Flow<List<DeviceEntity>>
}