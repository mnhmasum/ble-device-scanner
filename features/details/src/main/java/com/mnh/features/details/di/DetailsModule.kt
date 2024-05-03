package com.mnh.features.details.di

import com.mnh.ble.connector.BleConnector
import com.mnh.features.details.repository.PeripheralDetailsRepository
import com.mnh.features.details.repository.PeripheralDetailsRepositoryImp
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
    fun providePeripheralDetailsRepositoryImpl(bleConnector: BleConnector): PeripheralDetailsRepository {
        return PeripheralDetailsRepositoryImp(bleConnector)
    }

}
