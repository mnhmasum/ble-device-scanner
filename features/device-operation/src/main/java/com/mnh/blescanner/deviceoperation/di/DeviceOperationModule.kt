package com.mnh.blescanner.deviceoperation.di

import com.mnh.ble.connector.BleConnector
import com.mnh.blescanner.deviceoperation.respository.DeviceOperationRepository
import com.mnh.blescanner.deviceoperation.respository.DeviceOperationRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DeviceOperationModule {

    @Provides
    @Singleton
    fun providePeripheralDetailsRepositoryImpl(bleConnector: BleConnector): DeviceOperationRepository {
        return DeviceOperationRepositoryImp(bleConnector)
    }

}
