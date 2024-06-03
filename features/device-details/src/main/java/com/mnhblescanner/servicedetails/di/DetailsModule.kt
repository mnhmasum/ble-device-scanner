package com.mnhblescanner.servicedetails.di

import com.mnh.ble.connector.BleConnector
import com.mnhblescanner.servicedetails.repository.DeviceDetailsRepository
import com.mnhblescanner.servicedetails.repository.DeviceDetailsRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DetailsModule {

    @Provides
    @Singleton
    fun providePeripheralDetailsRepositoryImpl(bleConnector: BleConnector): DeviceDetailsRepository {
        return DeviceDetailsRepositoryImp(bleConnector)
    }

}
