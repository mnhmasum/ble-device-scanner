package com.lightnotebook.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lightnotebook.data.database.entity.DeviceEntity

@Database(entities = [DeviceEntity::class], version = 1, exportSchema = false)
abstract class DeviceDatabase : RoomDatabase() {

    abstract fun lockDao(): DeviceDao
}
