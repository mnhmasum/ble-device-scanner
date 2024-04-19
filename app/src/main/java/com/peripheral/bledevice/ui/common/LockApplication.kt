package com.peripheral.bledevice.ui.common;

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LockApplication : Application() {

    companion object {
        //val channelId = "BluetoothScanServiceChannel"
    }

}