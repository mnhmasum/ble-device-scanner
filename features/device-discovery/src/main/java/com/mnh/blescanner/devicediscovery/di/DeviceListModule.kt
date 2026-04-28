package com.mnh.blescanner.devicediscovery.di

import com.mnh.ble.bluetooth.blescanner.BleScanner
import com.mnh.blescanner.devicelist.repository.DeviceDiscoveryRepository
import com.mnh.blescanner.devicelist.repository.DeviceDiscoveryRepositoryImpl
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
    fun provideDeviceListRepository(bleScanner: BleScanner): DeviceDiscoveryRepository {
        return DeviceDiscoveryRepositoryImpl(bleScanner)
    }

}