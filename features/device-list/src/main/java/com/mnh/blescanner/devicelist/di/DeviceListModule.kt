package com.mnh.blescanner.devicelist.di

import com.mnh.ble.bluetooth.scanner.BleScanner
import com.mnh.blescanner.devicelist.repository.DeviceListRepository
import com.mnh.blescanner.devicelist.repository.DeviceListRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DeviceListModule {

    @Provides
    @Singleton
    fun provideDeviceListRepository(bleScanner: BleScanner): DeviceListRepository {
        return DeviceListRepositoryImpl(bleScanner)
    }

}