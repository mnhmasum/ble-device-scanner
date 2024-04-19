package com.mnh.ble.di

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mnh.ble.scanner.BleScanner
import com.mnh.ble.scanner.BleScannerImp
import com.mnh.service.BluetoothScanService
import com.napco.utils.Constants
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
        @ApplicationContext appContext: Context
    ): BleScanner {

        return BleScannerImp(bluetoothLeScanner, sharedPreferences, appContext)
    }

   /* @Provides
    fun provideBleRepository(bleDataSource: BleDataSource): BleRepository {
        return BleRepositoryImp(bleDataSource)
    }*/

    @Provides
    @Singleton
    fun provideNotificationChannel(): NotificationChannel {
        val channel = NotificationChannel(
            Constants.channelId,
            "Bluetooth Scan Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Channel for Bluetooth Scan Service" }
        return channel
    }

    @Provides
    @Singleton
    fun provideNotificationBuilder(@ApplicationContext appContext: Context): Notification {
        val stopIntent = Intent(appContext, com.mnh.service.BluetoothScanService::class.java)
            .apply { action = com.mnh.service.BluetoothScanService.ACTION_STOP_SERVICE }

        val stopPendingIntent = PendingIntent.getService(
            appContext,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(appContext, Constants.channelId)
            .apply {
                setContentTitle("Lock Background Scan Service")
                setContentText("Scanning for BLE devices")
                setSmallIcon(androidx.core.R.drawable.notification_bg)
                setColor(
                    ContextCompat.getColor(
                        appContext,
                        androidx.core.R.color.notification_icon_bg_color
                    )
                )
                setOngoing(true)
                addAction(
                    com.google.android.material.R.drawable.notify_panel_notification_icon_bg,
                    "Stop",
                    stopPendingIntent
                )
            }.build()
        return notificationBuilder
    }


}
