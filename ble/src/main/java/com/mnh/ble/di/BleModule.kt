package com.mnh.ble.di

import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.SharedPreferences
import com.mnh.ble.connector.BleConnectionManager
import com.mnh.ble.connector.BleConnectionManagerImp
import com.mnh.ble.scanner.BleScanner
import com.mnh.ble.scanner.BleScannerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class BleModule {

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences("threshold_key", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext appContext: Context): NotificationManager {
        return appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    @Singleton
    fun provideBluetoothManager(@ApplicationContext appContext: Context): BluetoothManager {
        return appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    @Provides
    @Singleton
    fun provideBluetoothAdapter(bluetoothManager: BluetoothManager): BluetoothAdapter {
        return bluetoothManager.adapter
    }

    @Provides
    @Singleton
    fun provideBluetoothLeScanner(bluetoothAdapter: BluetoothAdapter): BluetoothLeScanner {
        return bluetoothAdapter.bluetoothLeScanner
    }

    @Provides
    @Singleton
    fun provideBleScannerDataSourceImp(
        bluetoothLeScanner: BluetoothLeScanner,
        sharedPreferences: SharedPreferences,
    ): BleScanner {

        return BleScannerImpl(bluetoothLeScanner)
    }

    @Provides
    @Singleton
    fun provideBleConnector(@ApplicationContext appContext: Context): BleConnectionManager {
        return BleConnectionManagerImp(appContext)
    }

}
