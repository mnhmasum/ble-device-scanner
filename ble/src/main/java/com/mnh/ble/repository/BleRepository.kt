package com.mnh.ble.repository

import android.bluetooth.le.ScanResult
import kotlinx.coroutines.flow.Flow

interface BleRepository {
    fun getScannedDeviceList(): Flow<List<ScanResult>>

    fun stopScanning()

}