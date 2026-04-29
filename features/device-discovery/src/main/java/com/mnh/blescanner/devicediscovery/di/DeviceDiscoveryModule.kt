package com.mnh.blescanner.devicediscovery.di

import com.mnh.ble.bluetooth.blescanner.BleScanner
import com.mnh.blescanner.devicediscovery.repository.DeviceDiscoveryRepository
import com.mnh.blescanner.devicediscovery.repository.DeviceDiscoveryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DeviceDiscoveryModule {

    @Provides
    @Singleton
    fun provideDeviceDiscoveryRepository(bleScanner: BleScanner): DeviceDiscoveryRepository {
        return DeviceDiscoveryRepositoryImpl(bleScanner)
    }

}
