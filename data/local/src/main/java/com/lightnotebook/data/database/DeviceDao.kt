package com.lightnotebook.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lightnotebook.data.database.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lockEntity: DeviceEntity)

    @Query("SELECT * FROM ble_device_table")
    fun getAllLocks(): Flow<List<DeviceEntity>>
}
