package com.lightnotebook.data.di

import android.content.Context
import androidx.room.Room
import com.lightnotebook.data.database.DeviceDao
import com.lightnotebook.data.database.DeviceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object LocalDataSourceModule {

    @Singleton
    @Provides
    fun provideLockDatabase(@ApplicationContext appContext: Context): DeviceDatabase {
        return Room.databaseBuilder(
            appContext,
            DeviceDatabase::class.java,
            "bleDemoDB"
        ).build()
    }

    @Provides
    @Singleton
    fun provideLockDao(deviceDatabase: DeviceDatabase): DeviceDao {
        return deviceDatabase.lockDao()
    }

}
