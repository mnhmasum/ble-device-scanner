package com.peripheral.bledevice.di

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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
class NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationBuilder(@ApplicationContext appContext: Context): Notification {
        val stopIntent = Intent(appContext, BluetoothScanService::class.java)
            .apply { action = BluetoothScanService.ACTION_STOP_SERVICE }

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

}