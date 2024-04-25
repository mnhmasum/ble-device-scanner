package com.peripheral.bledevice.di

import com.lightnotebook.data.database.DeviceDao
import com.lightnotebook.data.repository.DeviceRepository
import com.lightnotebook.data.repository.DeviceRepositoryImp
import com.mnh.ble.connector.BleConnector
import com.mnh.ble.repository.BleRepository
import com.mnh.ble.repository.BleRepositoryImp
import com.mnh.ble.scanner.BleScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideDeviceRepository(deviceDao: DeviceDao): DeviceRepository {
        return DeviceRepositoryImp(deviceDao)
    }

    @Provides
    @Singleton
    fun provideBleRepository(bleScanner: BleScanner, bleConnector: BleConnector): BleRepository {
        return BleRepositoryImp(bleScanner, bleConnector)
    }

}