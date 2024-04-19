package com.lightnotebook.data.repository

import com.lightnotebook.data.database.DeviceDao
import com.lightnotebook.data.database.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeviceRepositoryImp @Inject constructor(private val deviceDao: DeviceDao) :
    DeviceRepository {
    override suspend fun insert(lockEntity: DeviceEntity) {
        deviceDao.insert(lockEntity)
    }

    override fun getAllDevice(): Flow<List<DeviceEntity>> {
        return deviceDao.getAllLocks()
    }
}
