package com.mnh.blescanner.devicedetails.di

import com.mnh.ble.connector.BleConnectionManager
import com.mnh.blescanner.devicedetails.repository.DeviceDetailsRepository
import com.mnh.blescanner.devicedetails.repository.DeviceDetailsRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DeviceDetailsModule {

    @Provides
    @Singleton
    fun provideDeviceDetailsRepositoryImpl(bleConnectionManager: BleConnectionManager): DeviceDetailsRepository {
        return DeviceDetailsRepositoryImp(bleConnectionManager)
    }

}
